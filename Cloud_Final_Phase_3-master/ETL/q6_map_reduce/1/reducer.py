#!/usr/bin/python

import sys

# Main Function
previous_tweetid = ''

for line in sys.stdin:

    try:
        line_list = line.split('\t')
        item_list = line_list[1].split('<15619NoLife>')

        if line_list[0] != previous_tweetid:
            previous_tweetid = line_list[0]
            print item_list[0] + '\t' + item_list[1]
        else:
            continue

    except ValueError as e:
        continue

