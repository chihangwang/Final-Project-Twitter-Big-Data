#!/usr/bin/python
import sys


dictionary = {}

def return_tuple(a):
	return(a[1],a[0])

for line in sys.stdin:
	line_list = line[:-1].split('<15619nolife>')
	temp_list = line_list[1].split('<15619nolife>')

	if temp_list[0] in dictionary:
		continue
	else:
		dictionary[(temp_list[0],temp_list[1])] = 1

for i in dictionary:
	if return_tuple(i) in dictionary:
		dictionary[i] = i + (1,)
	else:
		dictionary[i] = i + (0,)
	print str(dictionary[i][0])+"<15619nolife>"+str(dictionary[i][1])+"<15619nolife>"+str(dictionary[i][2])
