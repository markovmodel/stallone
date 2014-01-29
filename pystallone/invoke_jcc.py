import sys
import subprocess

def createSourceDistribution(basename, version):
    stallone_api_jar = basename + '-api.jar'
    stallone_whole_in_one_jar = basename + '-jar-with-dependencies-proguard.jar'
    args = [sys.executable, '-m', 'jcc',
            '--jar', stallone_api_jar,
         '--package', 'stallone.mc',
         '--package', 'stallone.algebra',
         '--package', 'stallone.coordinates',
         '--package', 'stallone.cluster',
         '--package', 'stallone.doubles',
         '--package', 'stallone.ints',
         '--package', 'stallone.io',
         '--include', stallone_whole_in_one_jar,
         '--python', 'stallone',
         '--version', version,
         '--reserved', 'extern',
         #'--output', 'src',
         '--files', '2',
	#'--use_full_names',
         '--sdist',
	'java.util.List',
	'java.util.Collection',
    'java.io.File',
    'stallone.ints.PrimitiveIntArray'
         #'--extra-setup-arg', 'sdist',
         #'--egg-info' # needed to force creation of source code without compiling it
    ] 
    subprocess.call(args)


if __name__ == '__main__':
    basename_i = sys.argv.index('--basename') + 1
    version_i = sys.argv.index('--version') + 1
    createSourceDistribution(sys.argv[basename_i], sys.argv[version_i])
