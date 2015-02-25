#!/usr/bin/python

import sys
import simplejson as json
import time

month_table = {
    'Jan': '01',
    'Feb': '02',
    'Mar': '03',
    'Apr': '04',
    'May': '05',
    'Jun': '06',
    'Jul': '07',
    'Aug': '08',
    'Sep': '09',
    'Oct': '10',
    'Nov': '11',
    'Dec': '12',
}


# Main Function
for line in sys.stdin:

    if (line == '\n'):
        continue;

    tag_list = []
    data = json.loads(line)
    location = data['place']

    if (location != None):
        location = data['place']['name']


    if (location == None or location == ''):
        location = data['user']['time_zone']

        if (location == 'time'):
            location = None
    if (location == None):
        continue


    hashtag_list = data['entities']['hashtags']
    for t in hashtag_list:
        tag_list.append([t['indices'][0], t['text']])

    if (len(tag_list) == 0):
        continue

    tweet_id = data['id']
    tweet_timestamp = data['created_at']
    weekday, month, day, time, zone, year = tweet_timestamp.split()
    date = year+'-'+month_table[month]+'-'+day


    for t in tag_list:
        print str(date)+'<15619nolife>'+str(location.encode('utf-8'))+'<15619nolife>'+str(t[1].encode('utf-8'))+'\t'+str(tweet_id)+'<15619nolife>'+str(t[0]).zfill(5)
