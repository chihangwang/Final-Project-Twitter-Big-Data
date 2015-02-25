#!/usr/bin/python
import sys
import time


def sort_tweetid(tweetid_str):
    tweetid_list = tweetid_str.split('<15619nolife>')

    if (len(tweetid_list) == 2):
        return tweetid_str
    sorted_list = []
    output_str = ''
    for i in range(0, len(tweetid_list), 2):
        record = [tweetid_list[i], tweetid_list[i+1]]
        sorted_list.append(record)

    sorted_list.sort(key=lambda x: x[0], reverse=False)

    for s in sorted_list:
        if (output_str == ''):
            output_str = s[0]+'<15619nolife>'+s[1]
        else:
            output_str += '<15619nolife>'+s[0]+'<15619nolife>'+s[1]
    return output_str

def print_sorted_line(key, counter, tweetid_str):
    date, location, hashtag = key.split('<15619nolife>', 2)
    sorted_tweetid_str = sort_tweetid(tweetid_str)

    print str(date)+'<15619nolife>'+str(location)+'<15619nolife>'+str(counter).zfill(10)+'\t'+sorted_tweetid_str+'<15619nolife>'+hashtag



# Main Function
counter = 0
previous_key = ''
tweetid_str = ''
previuse_line = ''
for line in sys.stdin:
    if (line == previuse_line or line == '\n' or line == '\t\n'):
        continue
    try:
        date_location_tag, tweetid_index = line.split('\t', 1)
        tweetid_index = tweetid_index[:-1]
        if (date_location_tag == previous_key):
            tweet_id, idx = tweetid_index.split('<15619nolife>')
            start_idx = tweetid_str.find(tweet_id)
            if (start_idx >= 0):
                org = tweetid_str

                i = start_idx+len(tweet_id)+len('<15619nolife>')
                org_idx = tweetid_str[i:i+5]
                if (org_idx > idx):
                    tweetid_str = tweetid_str[:i]+idx+tweetid_str[i+5:]

                previuse_line = line
                continue
            tweetid_str += '<15619nolife>'+tweetid_index
            counter += 1
        else:
            if (previous_key != ''):
                print_sorted_line(previous_key, counter, tweetid_str)
            tweetid_str = tweetid_index
            counter = 1
        previous_key = date_location_tag
        previuse_line = line

    except ValueError:
        continue

print_sorted_line(previous_key, counter, tweetid_str)
