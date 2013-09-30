#!/usr/bin/env python
import subprocess
import shlex

# prefer setuptools in favour of distutils
try:
    from setuptools.core import setup
    from setuptools.command.build import build as _build_clib
except ImportError:
    from distutils.core import setup
    from distutils.command.build import build as _build_clib
    

stallone_api_jar = '../target/'
classpath = []

class my_install(_build_clib):
    def run(self):
        #install.run(self)
        _build_clib.run(self)
        # Custom stuff here
        # distutils.command.install actually has some nice helper methods
        # and interfaces. I strongly suggest reading the docstrings.
        
        call ="python -m jcc --jar " + stallone_api_jar + \
            "--classpath lib/arpack-combo-0.1.jar --python stallone " + \
            "--version 2.7.0 --build --reserved extern"
        
        print "huso"
        subprocess.call(shlex.split(call))
    
setup(name='stallone',
      version='git',
      cmdclass=dict(build = my_install),
      # runtime dependencies
      requires=['jcc (>=1.6)']
      )