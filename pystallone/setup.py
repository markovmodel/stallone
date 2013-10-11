#!/usr/bin/env python
from os import listdir, rmdir
from os.path import isfile, join
import shlex
import shutil
import subprocess
import sys
from tempfile import mkdtemp
from zipfile import ZipFile


# prefer setuptools in favor of distutils
try:
    from setuptools.core import setup
    from setuptools.command.build import build
    from setuptools.command.install import install
    from setuptools.command.clean import clean
except ImportError:
    from distutils.core import setup
    from distutils.command.build import build
    from distutils.command.install import install
    from distutils.command.clean import clean

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
    """
    invokes apache jcc to build a wrapper for the public api of stallone 
    """
    def run(self):
        build.run(self)
        classpath = create_class_path()
        includepath = classpath.replace('--classpath', '--include')
        
        call = sys.executable + " -m jcc --jar " + lib_dir + stallone_api_jar \
             + classpath + includepath \
             + " --python " + __name__ + \
            " --version " + __version__ + " --build --reserved extern" + \
            " --module util/ArrayWrapper --files 4 --bdist"
        
        subprocess.call(shlex.split(call))

class myinstall(install):
        def run(self):
            dest = self.install_usersite
            zfile = ZipFile('dist/' + listdir('dist')[0])
            tmp = mkdtemp()
            zfile.extractall(path=tmp)
            self.copy_tree(tmp, dest)
            shutil.rmtree(tmp)
            
class myclean(clean):
    def run(self):
        shutil.rmtree('build')
    
setup(name=__name__,
      version=__version__,
      cmdclass=dict(build=mybuild, install=myinstall, clean=myclean),
      # runtime dependencies
      requires=['jcc (>=1.6)'],
      )
