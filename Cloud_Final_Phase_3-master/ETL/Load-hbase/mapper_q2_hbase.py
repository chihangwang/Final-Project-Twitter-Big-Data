#!/usr/bin/python

# Author: Wei-Lin Tsai weilints@andrew.cmu.edu
# Info:
#   This file is a step of mapper-reduce mapper part
#   In this case, there is only maapper but no reducer
#   This file simply parse input from standard input
#   and then insert the result to hbase database

import sys
import happybase

# global variable
EMR_MASTER_DNS = 'localhost'
TMP_TABLE_NAME = 'twitter-q2'

# functions
def open_connection(dns_name):
    connection = happybase.Connection(dns_name)
    connection.open()
    return connection

def print_tables(in_conn):
    print in_conn.tables()

def create_table(in_conn, tb_name):
    in_conn.create_table(
        tb_name,
        {'user': dict(),  # use defaults
        }
    )

# Note: all filter functions must called in specific order
# That is. There are assumptions for some functions
def main():
    hb_conn = open_connection(EMR_MASTER_DNS)
#    create_table(hb_conn, TMP_TABLE_NAME)
#    print_tables(hb_conn)

    my_table = hb_conn.table(TMP_TABLE_NAME)

    i = 0
    with my_table.batch(batch_size=5000) as b:
        for aLine in sys.stdin:
            i = i + 1
            listOfALine = aLine.split("<15619nolife>")
            # 0: tId;
            # 1: uID;
            # 2: time-stamp;
            # 3: after_text;
            # 4: score;
            score = listOfALine[4][:-2]
            b.put (listOfALine[1] + listOfALine[2] + listOfALine[0],
                    {'user:tID': listOfALine[0],
                     'user:Score': score,
                     'user:Text': listOfALine[3]} )

# program entry
if __name__ == '__main__':
	main()
