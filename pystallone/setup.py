#!/usr/bin/env python
import subprocess
import shlex
import sys
from os import listdir, pathsep
from os.path import isfile, join

# prefer setuptools in favor of distutils
try:
    from setuptools.core import setup
    from setuptools.command.build import build
except ImportError:
    from distutils.core import setup
    from distutils.command.build import build

stallone_api_jar = 'stallone-1.0-SNAPSHOT-api.jar'

__version__ = '1.0'
__name__ = 'pystallone'
lib_dir = 'libs/'

def create_class_path():
    jars = [ f for f in listdir('libs/') if isfile(join('libs/', f)) ]
    cp_string = ''.join(" --classpath " + lib_dir + "%s " \
                        % ''.join(map(str, x)) for x in jars)
    return cp_string

class mybuild(build):
    def run(self):
        classpath = create_class_path()
        includepath = classpath.replace('--classpath', '--include')
        
        call = sys.executable + " -m jcc --jar " + lib_dir + stallone_api_jar \
             + classpath + includepath \
             + " --python " + __name__ + \
            " --version " + __version__ + " --build --reserved extern" + \
            " --module util/ArrayWrapper --files 4"
        
        return subprocess.call(shlex.split(call))

def list_files():
    """
    maps files in package build directory for installation
    """
    package_dir = 'build/' + __name__ + '/'
    files = [ package_dir + f for f in listdir(package_dir)]
    return [(__name__ + '/', files)]
    
setup(name=__name__,
      version=__version__,
      cmdclass=dict(build=mybuild),
      package='build/pystallone',
      data_files=list_files(),
      # runtime dependencies
      requires=['jcc (>=1.6)'],
      )
