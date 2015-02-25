#!/usr/bin/python

import sys
import time

# Main Function
previous_key = ''
tweetid_list = []
orginal_userid_list = []

for line in sys.stdin:
    
    try:
        # First Check Blank Line
        if (line == '\n' or line == '\t\n'):
            continue

        line = line[:-1]
        line_list = line.split('\t')
        userid = line_list[0]
        tweetid = line_list[1]
        orginal_userid = line_list[2]

        if tweetid in tweetid_list:
            continue

        if (userid != previous_key and previous_key != ''):
        
            # Print Out Map Reduce Results
            for userid_item in orginal_userid_list:
                print str(userid_item)+'\t'+previous_key+'\t'+str(len(tweetid_list))

            previous_key = userid
            orginal_userid_list = [orginal_userid]
            tweetid_list = [tweetid]

        else:
            previous_key = userid
            orginal_userid_list.append(orginal_userid)
            tweetid_list.append(tweetid)
                
    except ValueError as e:
        continue

# Print Out Map Reduce Results (Check for the last line)
for userid_item in orginal_userid_list:
    print str(userid_item)+'\t'+previous_key+'\t'+str(len(tweetid_list))
