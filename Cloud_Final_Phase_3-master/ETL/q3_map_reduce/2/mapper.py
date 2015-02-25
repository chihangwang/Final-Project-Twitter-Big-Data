#!/usr/bin/python
import sys


def return_nonblank_lines(stdin):
	for char in stdin:
		line = char.rstrip()
		if line:
			yield line


# Main Function
for line in return_nonblank_lines(sys.stdin):
	result = line.split('<15619nolife>')
	print result[0]+'<15619nolife>'+result[1]+'<15619nolife>'+result[2]
