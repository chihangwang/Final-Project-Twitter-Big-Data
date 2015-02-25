#!/usr/bin/python
import sys
import operator


dictionary = {}


for line in sys.stdin:
	line_list = line.split('<15619nolife>')
	temp_list = line_list[1][:-1].split('<15619nolife>')

	temp_list[0] = temp_list[0].zfill(20)

	if temp_list[1] == '1':
		temp_list[0] = temp_list[0]+')'

	if line_list[0] in dictionary:
		dictionary[line_list[0]].append(temp_list[0])
	else:
		dictionary[line_list[0]] = [temp_list[0]]

for i in dictionary:
	sys.stdout.write(i+'<15619nolife>')
	for j in sorted(dictionary[i]):
		if ')' in j:
			j = int(j[:-1])
			sys.stdout.write('('+str(j)+')\\n')
		else:
			j = int(j)
			sys.stdout.write(str(j)+'\\n')
	sys.stdout.write('\n')
