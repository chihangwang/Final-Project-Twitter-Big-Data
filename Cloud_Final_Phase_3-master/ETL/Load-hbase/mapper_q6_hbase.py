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
TMP_TABLE_NAME = 'twitter-q6'

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
    my_table = hb_conn.table(TMP_TABLE_NAME)

    i = 0
    with my_table.batch(batch_size=5000) as b:
        for aLine in sys.stdin:
    	    i = i + 1
    	    aLine = aLine.strip()

     	    s = aLine.split('<15619nolife>')

     	    U_request = s[0]
            value = s[1]+'_'+s[2]
    	 
    	    b.put(str(U_request),{'user:value':str(value)})


# program entry
if __name__ == '__main__':
	main()
