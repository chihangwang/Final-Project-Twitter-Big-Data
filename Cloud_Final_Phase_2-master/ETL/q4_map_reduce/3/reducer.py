#!/usr/bin/python
import sys
import time


def add_record(result_list, counter_tag):
    counter, tag_str = counter_tag.split('<15619nolife>', 1)
    out_list = []

    out_list.append(str(counter))
    tag_list = tag_str.split('<15619nolife>')
    for t in tag_list:
        out_list.append(t)
    result_list.append(out_list)

def print_date_location(result_list, key):
    rank = 1
    date, location = key.split('<15619nolife>', 1)

    result_list.sort(key=lambda x: x[0], reverse=True)
    prefix_str = date+'#'+location+'#'

    for item in result_list:
        for i in range(len(item)):
            if (i == 0):
                continue
            print prefix_str+str(rank)+'#'+item[i]
            rank += 1


# Main Function
result_list = []
previous_key = ''
for line in sys.stdin:
    try:
        if (line == '\n' or line == '\t\n'):
            continue
        date_location, counter_tag = line.split('\t', 1)
        counter_tag = counter_tag[:-1]
        if (date_location == previous_key):
            add_record(result_list, counter_tag)
        else:
            if (previous_key != ''):
                print_date_location(result_list, previous_key)

            result_list = []
            add_record(result_list, counter_tag)
        previous_key = date_location

    except ValueError:
        continue

print_date_location(result_list, previous_key)
