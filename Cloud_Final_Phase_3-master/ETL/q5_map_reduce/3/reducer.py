#!/usr/bin/python

import sys

score_dict = {0: 0, 1: 0, 2: 0}
previous_key = ''

def accumulate_score(scoreA, scoreB, scoreC, score_dict):
    if (scoreA >= 0):
        score_dict[0] = scoreA
    if (scoreB >= 0):
        score_dict[1] = scoreB
    if (scoreC >= 0):
        score_dict[2] = scoreC

# Main Function
for line in sys.stdin:
    
    try:
        # First Check Blank Line
        if (line == '\n' or line == '\t\n'):
            continue

        line = line[:-1]
        line_list = line.split('\t')
        userid = line_list[0]
        scoreA = int(line_list[1])
        scoreB = int(line_list[2])
        scoreC = int(line_list[3])

        if (userid != previous_key and previous_key != ''):
            # Print Out Map Reduce Results
            print str(previous_key)+'\t'+str(score_dict[0])+'\t'+str(score_dict[1])+'\t'+str(score_dict[2])
        
            # Reinitialize Score Dict
            score_dict = {0: 0, 1: 0, 2: 0}
            accumulate_score(scoreA, scoreB, scoreC, score_dict)
            previous_key = userid
        else:
            accumulate_score(scoreA, scoreB, scoreC, score_dict)
            previous_key = userid
                
    except ValueError as e :
        continue

# Print Out Map Reduce Results (Check for the last line)
print str(previous_key)+'\t'+str(score_dict[0])+'\t'+str(score_dict[1])+'\t'+str(score_dict[2])
