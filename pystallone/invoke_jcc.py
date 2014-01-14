import sys
import subprocess

def createSourceDistribution(basename, version):
    stallone_api_jar = basename + '-api.jar'
    stallone_whole_in_one_jar = basename + '-jar-with-dependencies-proguard.jar'
    args = [sys.executable, '-m', 'jcc',
            '--jar', stallone_api_jar,
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
         '--python', 'stallone',
         '--version', version,
         '--reserved', 'extern',
         #'--output', 'build_stallone',
         '--files', '2',
         '--sdist'
         #'--extra-setup-arg', 'sdist',
         #'--egg-info' # needed to force creation of source code without compiling it
    ] 
    subprocess.call(args)


if __name__ == '__main__':
    basename_i = sys.argv.index('--basename') + 1
    version_i = sys.argv.index('--version') + 1
    createSourceDistribution(sys.argv[basename_i], sys.argv[version_i])
