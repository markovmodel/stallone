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
from setuptools.command.install import install
"""
################################################################################
    Stallone Python Wrapper Setup
################################################################################
"""

import glob
# TODO: what if the cwd is poluted with older versions?
stallone_api_jar = glob.glob('stallone*api.jar')[0]
stallone_whole_in_one_jar = glob.glob('stallone*jar-with-dependencies-proguard.jar')[0]
stallone_module_name = 'stallone'

def getVersionFromManifest(jarFile):
    from zipfile import ZipFile
    import re
    
    z = ZipFile(jarFile)
    f = z.open('META-INF/MANIFEST.MF')
    content = f.read()
    
    splitted = re.split('^(.*)\:.', content, flags=re.MULTILINE)
    ind = splitted.index('version')
    version = splitted[ind+1]
    version = version.strip() # strip whitespaces
    return version

version = getVersionFromManifest(stallone_whole_in_one_jar)

def jcc_args(additional_args = []):
    """
    default arguments to build stallone wrapper.
    Note:
    * All of the public class/interface content of the --jar option is wrapped.
    * All of the public class/interface content of additional packages passed via
     --package option is wrapped.
    """
    args = ['--jar', stallone_api_jar,
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
         '--version', version,
         '--reserved', 'extern',
         '--output', 'build_stallone',
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
        print "Something went wrong:\n", cpe
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
      version = version,
      cmdclass=dict(build=mybuild,
                    install=myinstall),
      # build time dependencies
      setup_requires = ['JCC >=2.18'],
      install_requires = ['JCC >=2.18']
)