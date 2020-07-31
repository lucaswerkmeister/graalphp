import os
from os.path import join

from bench import BenchMeasurement, Bench, verify_files

DIR = os.path.dirname(os.path.realpath(__file__))
SRC_FOLDER = os.path.join(DIR, 'bench/fannkuchredux')

# php
SRC_PHP = join(SRC_FOLDER, "fannkuchredux.php-1.php")

# gphp
SRC_GPHP = join(SRC_FOLDER, "fannkuchredux.php-1.graalphp")

TEST = 'fannkuchredux-1'

verify_files([SRC_PHP, SRC_GPHP])


class BenchmarkFannkuch(Bench):

    def run(self):
        prefix = self.get_test_prefix()
        res = []

        res.append(self.run_php(TEST, prefix, SRC_PHP, ''))
        res.append(self.run_graalphp(TEST, prefix, SRC_GPHP, ''))

        self.extract_and_store_data_array(res)

    def _import_data_manually(self):
        pref = '2020-07-31T01:08:00.473402-binary-trees'
        pref = pref + '-binarytrees.php-3-ref.'
        path = 'saved-measurements/20-07-31-graal-20.0.0-binary-trees/' + pref

        self.import_data(path + 'php-php.txt',
                         test_name=TEST,
                         prefix=pref,
                         comment='graal 20.0.0',
                         binary='php')


if __name__ == '__main__':
    bm = BenchmarkFannkuch()
    bm.run()

    pass
