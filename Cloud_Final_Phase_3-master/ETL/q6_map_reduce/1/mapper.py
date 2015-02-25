#!/usr/bin/python

import simplejson as json
import sys


# Main Function
for line in sys.stdin:
    data = []

    try:
        data = json.loads(line)
        counter = 0
        
        if 'media' in data['entities']:
            tweetid = data['id']
                for i in range(len(data['entities']['media'])):
                    if data['entities']['media'][i]['type'] == 'photo':
                        counter += 1
                print str(tweetid)+'\t'+str(data['user']['id'])+'<15619NoLife>'+str(counter)

    except ValueError as e:
        continue
