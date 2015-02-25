#!/usr/bin/python

import sys

# Main Function
temp = ''
counter = 0

for line in sys.stdin:
    try:
        line_list = line.split('\t')

        if temp != '':
            if line_list[0] == temp:
                counter += int(line_list[1])
            else:
                print temp + '\t' + str(counter)
                    temp = line_list[0]
                    counter = int(line_list[1])
        
        else:
            temp = line_list[0]
            counter += int(line_list[1])

    except ValueError as e:
        continue


print temp + '\t' + str(counter)

