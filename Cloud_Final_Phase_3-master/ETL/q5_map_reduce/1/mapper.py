#!/usr/bin/python

import simplejson as json
import sys


for line in sys.stdin:
    
    if (line == '\n' or line == '\t\n'):
        continue

    data = json.loads(line)
    tweet_id = data['id']
    user_id = data['user']['id']

    original_user_id = '#'

    if 'retweeted_status' in data:
        original_user_id = data['retweeted_status']['user']['id']


    print str(user_id)+'\t'+str(tweet_id)+'\t'+str(original_user_id)
