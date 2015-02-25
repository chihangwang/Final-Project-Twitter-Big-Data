#!/usr/bin/python

#
# ---Reducer---
#

import sys


for line in sys.stdin:
	line = line.strip()
	
	try:
		key, value = line.split('\t')
	except ValueError:
		continue
	
	print '%s' % (value)
