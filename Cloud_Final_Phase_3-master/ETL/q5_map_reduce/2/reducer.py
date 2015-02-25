#!/usr/bin/python

import sys


# Main Function
previous_key = ''
retweet_userid_dict = {}
counter = 0

for line in sys.stdin:
    
    try:
        # First Check Blank Line
        if (line == '\n' or line == '\t\n'):
            continue
    
        line = line[:-1]
        line_list = line.split('\t')
        userid = line_list[0]
        retweet_userid = line_list[1]
        scoreA = line_list[2]

        # Use "-9" as a temporary label
        print str(retweet_userid)+'\t'+str(scoreA)+'\t'+str(-9)+'\t'+str(-9)

        if (userid == '#'):
            previous_key = userid
            continue

        if (previous_key != '' and previous_key != '#' and userid != previous_key):
            # Print Out Map Reduce Results
            print str(previous_key)+'\t'+str(-9)+'\t'+str(counter*3)+'\t'+str(len(retweet_userid_dict)*10)
        
            retweet_userid_dict = {}
            retweet_userid_dict[retweet_userid] = 1
            previous_key = userid
            counter = 1

        else:
            retweet_userid_dict[retweet_userid] = 1
            previous_key = userid
            counter += 1

    except ValueError as e:
        continue

# Print Out Map Reduce Results (Check for the last line)
if (userid != '#'):
    print str(previous_key)+'\t'+str(-9)+'\t'+str(counter*3)+'\t'+str(len(retweet_userid_dict)*10)

