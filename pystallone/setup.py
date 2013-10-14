#!/usr/bin/env python
from os import listdir, rmdir
from os.path import isfile, join
import shlex
import shutil
import subprocess
import sys
from tempfile import mkdtemp
from zipfile import ZipFile
import time

__version__ = '1.0-' + time.strftime('%d-%m-%y--%H-%M')
__name__ = 'pystallone'

# prefer setuptools in favor of distutils
try:
    from setuptools.core import setup
    from setuptools.command.build import compile
    from setuptools.command.install import install
    from setuptools.command.clean import clean
    print "using setuptools"
except ImportError:
    from distutils.core import setup
    from distutils.command.build import build   
    from distutils.command.install import install
    from distutils.command.clean import clean
    print "using distutils"

stallone_api_jar = 'stallone-1.0-SNAPSHOT-api.jar'

lib_dir = 'libs/'

def create_include_jar_list():
    jars = [ f for f in listdir('libs/') if isfile(join('libs/', f)) ]
    cp_string = ''.join("--include " + lib_dir + "%s " \
                        % ''.join(map(str, x)) for x in jars)
    return cp_string

def create_packages(packages):
    cp_string = ''.join("--package %s " \
                        % ''.join(map(str, x)) for x in packages)
    return cp_string

def jcc_run(extra_args):
        """
            will generate a string containing the invocation of apache jcc
            to build a wrapper for the given stallone api jar. All depedencies
            of stallone will be included in the distribution 
            (added to java classpath).
        """
        call = sys.executable + ' -m jcc --jar ' + stallone_api_jar \
             + ' ' + create_include_jar_list() \
             + " --python " + __name__ + ' ' \
             + " --version " + __version__ + " --reserved extern" \
             + " --module util/ArrayWrapper" \
             + ' --files 2 '\
             + ' ' + extra_args 
        return call
    
class mybuild(build):
    """
    invokes apache jcc to build a wrapper for the public api of stallone 
    """
    def run(self):
        call = jcc_run('--build')
        print 'invoking: ', call
        return subprocess.call(shlex.split(call))

class myinstall(install):
    def run(self):
        # install to users home directory, because we know its writeable
        call = jcc_run('--install --extra-setup-arg --user')
        print 'invoking: ', call
        return subprocess.call(shlex.split(call))
            
class myclean(clean):
    def run(self):
        shutil.rmtree('build', ignore_errors=True)
        shutil.rmtree('dist', ignore_errors=True)
        shutil.rmtree('libs', ignore_errors=True)
        shutil.rmtree(__name__ + ".egg-info", ignore_errors=True)
    
setup(name=__name__,
      version=__version__,
      cmdclass=dict(build=mybuild,
                    install=myinstall,
                    clean=myclean),
      # runtime dependencies
      requires=['jcc (>=1.6)'])
