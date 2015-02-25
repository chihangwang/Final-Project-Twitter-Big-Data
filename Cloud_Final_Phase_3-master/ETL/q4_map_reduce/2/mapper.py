#!/usr/bin/python

import sys

# Main Function
for line in sys.stdin:
    if (line == '\n' or line == '\t\n'):
        continue;
    print line[:-1]
