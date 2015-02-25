This is CMU Cloud Computing Final Project Phase 3.

[TCP/IP sys setting]
run my_sys_setting.sh

[MySQL] ---------------------------------------------------------
mysql-server/bin/Server.java   ======>   Java based Vert.x server

[To run the program: sudo ./vertx run Server.java -instances 50]

[HBase] ---------------------------------------------------------
hbase-server/bin/hbase_server/hbase_server.java   ======>   Java based Vert.x server
ETL/Load-hbase/mapper_q2_hbase.py  ==> load Q2 data into hbase
ETL/Load-hbase/mapper_q3_hbase.py  ==> load Q3 data into hbase
ETL/Load-hbase/mapper_q4_hbase.py  ==> load Q4 data into hbase
ETL/Load-hbase/mapper_q5_hbase.py  ==> load Q5 data into hbase
ETL/Load-hbase/mapper_q6_hbase.py  ==> load Q6 data into hbase
ETL/Load-hbase/mapper_q6r_hbase.py ==> load Q6 reverse data into hbase

[To run the program: sudo ./vertx run ./hbase_server hbase_servre.java -instances 50]

[hbase configuration]
hbase_server/conf/hbase-site.xml

[ETL] -----------------------------------------------------------
ETL/q2_map_reduce ====> for Q2 dataset
ETL/q3_map_reduce ====> for Q3 dataset
ETL/q4_map_reduce ====> for Q4 dataset
ETL/q5_map_reduce ====> for Q3 dataset
ETL/q6_map_reduce ====> for Q4 dataset
