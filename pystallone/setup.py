#!/usr/bin/env python
'''
Created on 08.01.2014

@author: marscher
'''
import os
import sys

if len(sys.argv) < 2:
    """
        in case we have no args let distutils setup show a basic error message
    """
    from distutils.core import setup
    setup()

"""
we are using setuptools via the bootstrapper ez_setup
"""
from ez_setup import use_setuptools
use_setuptools(version="2.0.2")
from setuptools import __version__ as st_version
print "Using setuptools version: ", st_version
from setuptools import setup, Extension, find_packages
#used for custom commands on stallone jcc setup
from distutils.command.build import build
#from setuptools.command.build_py import build_py
from setuptools.command.install import install
#from setuptools.command.sdist import sdist
"""
################################################################################
    Stallone Python Wrapper Setup
################################################################################
"""
def jcc_args(additional_args = []):
    """
    default arguments to build stallone wrapper.
    """
    from os.path import abspath
    #TODO: make these path relative to __file__, so setup may be executed with prepended relative paths.
    # if this should be also used in stallone maven builds, make sure relative path is correct
    stallone_api_jar = abspath('stallone-1.0-SNAPSHOT-api.jar')
    stallone_whole_in_one_jar = abspath(
        'stallone-1.0-SNAPSHOT-jar-with-dependencies.jar')
    
    stallone_module_name = 'stallone'
    
    args = ['--jar', stallone_api_jar,
            #TODO: read packages from manifest
         '--package', 'stallone.api.coordinates',
         '--package', 'stallone.mc',
         '--package', 'stallone.algebra',
         '--package', 'stallone.coordinates',
         '--package', 'stallone.cluster',
         '--package', 'java.lang',
         '--package', 'java.util',
         '--sequence', 'stallone.api.datasequence.IDataSequence', 'size:()I',
         'get:(I)Lstallone.api.doubles.IDoubleArray;',
         '--sequence', 'stallone.api.IDoubleArray', 'size:()I',
         'get:(I)jdouble;',
         '--include', stallone_whole_in_one_jar,
         #'--use_full_names', # does not work...
         '--python', stallone_module_name,
         '--version', '1.0', #TODO: read from manifest
         '--reserved', 'extern',
         # TODO: this is ignored for some build files.
         '--output', 'build_stallone',
         #'--output', 'target', # output directory, name 'build' is buggy in
                               # case of setup.py sdist, which does not include stuff from this dirs
         '--files', '2']

    if isinstance(additional_args, list):
        args.extend(additional_args)
    else:
        args.append(additional_args)

    return args

def jcc_run(args):
    """
    Invokes python with jcc module to perform the setup. This method fails, if
    JCC is not installed. But since it is required via setup(), it should always
    be available. The actual jcc call is performed via subprocess, because currently
    jcc only allows one jvm instance per process and we call it multiple times.
    
    Note: JCC is itself a setuptools wrapper.
    """
    import subprocess
    # reuse environment of current process to ensure runtime dependencies to be
    # on the python path.
    env = os.environ
    env['PYTHONPATH'] = ':'.join(sys.path)
    # invoke python with module jcc (if not already present)
    if args[0] != sys.executable:
        jcc_invocation = [sys.executable, '-m', 'jcc']
        for arg in reversed(jcc_invocation):
            args.insert(0, arg)
    try:
        print args
        subprocess.check_call(args,
                          stderr = subprocess.STDOUT, env=env)
        print "==================================================="
        print "Finished STALLONE build step:"
        print args
        print "==================================================="
    except subprocess.CalledProcessError as cpe:
        print "Something went wrong:\n", cpe.output
        raise
    return args

class mybuild(build):
    """
    invokes apache jcc to build a wrapper for the public api of stallone 
    """
    def run(self):
        print "==================================================="
        print "Building the STALLONE wrapper"
        print "==================================================="
        args = jcc_args(['--build'])
        
        jcc_run(args)

class myinstall(install):
    def run(self):
        print "==================================================="
        print "Installing the STALLONE wrapper"
        print "==================================================="
        args = jcc_args('--install')
        if '--user' in sys.argv:
            args.append('--extra-setup-arg')
            args.append('--user')
        jcc_run(args)

setup(name = 'stallone',
      # TODO: compare version of stallone stored in manifest of jar.
      # may be set this to version stored in manifest and rely on several things:
      # 1. the version is a git tag (hash id's are not incremental)
      # 2. the setup routine will update properly, when a higher version is found
      version = '9999', # insane build number to ensure local version always wins over release version
      cmdclass=dict(build=mybuild,
                    install=myinstall),
      # build time dependencies
      setup_requires = ['JCC >=2.18'],
      install_requires = ['JCC >=2.18']
)