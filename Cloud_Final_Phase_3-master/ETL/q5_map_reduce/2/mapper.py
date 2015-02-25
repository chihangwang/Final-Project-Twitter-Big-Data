#!/usr/bin/python

import sys

for line in sys.stdin:
    if (line == '\n' or line == '\t\n'):
        continue;
    print line[:-1]
