#!/usr/bin/python
import sys
import time


def print_one_line(record_list, key):
    date, location, counter = key.split('<15619nolife>', 2)

    t = record_list

    record_list.sort()
    output_str = date+'<15619nolife>'+location+'\t'+counter

    for r in record_list:
        j = 0
        tweet_str = ''
        id_index_tag = r.split('<15619nolife>')
        size = len(id_index_tag)
        for i in id_index_tag:

            if (((j & 1) == 0) and j < size - 1):
                if (tweet_str == ''):
                    tweet_str = i
                else:
                    tweet_str += ','+i
            j += 1
        tag_tweetid_str = id_index_tag[size - 1]+':'+tweet_str

        output_str += '<15619nolife>'+tag_tweetid_str
    print output_str


# Main Function
record_list = []
previous_key = ''
for line in sys.stdin:
    try:
        if (line == '\n' or line == '\t\n'):
            continue
        counter, tweetid_index_tag = line.split('\t', 1)
        tweetid_index_tag = tweetid_index_tag[:-1]
        if (counter == previous_key):
            record_list.append(tweetid_index_tag)
        else:
            if (previous_key != ''):
                print_one_line(record_list, previous_key)
            record_list = []
            record_list.append(tweetid_index_tag)
        previous_key = counter

    except ValueError:
        continue

print_one_line(record_list, previous_key)
