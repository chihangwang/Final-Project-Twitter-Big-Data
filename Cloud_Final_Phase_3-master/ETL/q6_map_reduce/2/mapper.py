#!/usr/bin/python

import sys

# Main Function
for line in sys.stdin:
    try:
        print line

    except ValueError as e:
        continue

