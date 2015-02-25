This is CMU Cloud Computing Final Project Phase 2.

[MySQL] ---------------------------------------------------------
mysql-server/bin/Server.java   ======>   Java based Vert.x server

[To run the program: sudo ./vertx run Server.java -instances 50]

[HBase] ---------------------------------------------------------
hbase-server/bin/Server.java   ======>   Java based Vert.x server
import-data-to-hbase/mapper_q2_hbase.py ==> load Q2 data into hbase
import-data-to-hbase/mapper_q3_hbase.py ==> load Q3 data into hbase
import-data-to-hbase/mapper_q4_hbase.py ==> load Q4 data into hbase

[To run the program: sudo ./vertx run Server.java -instances 50]

[ETL] -----------------------------------------------------------
ETL/q2_map_reduce ====> for Q2 dataset
ETL/q3_map_reduce ====> for Q3 dataset
ETL/q4_map_reduce ====> for Q4 dataset