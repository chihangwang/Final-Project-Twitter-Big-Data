# Author: Wei-Lin Tsai weilints@andrew.cmu.edu
# Info:
#   To open an table named 'user_tb' on AWS hbase

#!/usr/bin/python

import sys
import happybase

# global variable 
EMR_MASTER_DNS = 'TBD'
TMP_TABLE_NAME = 'user_tb'
FILE_NAME_PART = 'part-00000'

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
    create_table(hb_conn, TMP_TABLE_NAME)

# program entry
if __name__ == '__main__':
	main()
