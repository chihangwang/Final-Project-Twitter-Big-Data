#!/usr/bin/python

import simplejson as json
import sys


# Main Function
for line in sys.stdin:
	try:
		data = json.loads(line)

		if 'retweeted_status' in data:
			retweed_user_id = data['user']['id']
			original_user_id = data['retweeted_status']['user']['id']
			print str(sorted([retweed_user_id,original_user_id]))+"<15619nolife>"+str(original_user_id)+"<15619nolife>"+str(retweed_user_id)

	except ValueError as e:
		continue
