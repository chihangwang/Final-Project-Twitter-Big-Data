#!/bin/bash

# copy from website
# Increase size of file handles and inode cache
sudo sysctl -w fs.file-max=2097152
# Do less swapping
sudo sysctl -w vm.swappiness=10
sudo sysctl -w vm.dirty_ratio=60
sudo sysctl -w vm.dirty_background_ratio=2
 
### GENERAL NETWORK SECURITY OPTIONS ###
 
# Number of times SYNACKs for passive TCP connection.
sudo sysctl -w net.ipv4.tcp_synack_retries=2

# Allowed local port range
sudo sysctl -w net.ipv4.ip_local_port_range='2000 65535'
# Protect Against TCP Time-Wait
sudo sysctl -w net.ipv4.tcp_rfc1337=1
# Decrease the time default value for tcp_fin_timeout connection
sudo sysctl -w net.ipv4.tcp_fin_timeout=15
 
# Decrease the time default value for connections to keep alive
sudo sysctl -w net.ipv4.tcp_keepalive_time=300
sudo sysctl -w net.ipv4.tcp_keepalive_probes=5
sudo sysctl -w net.ipv4.tcp_keepalive_intvl=15

### TUNING NETWORK PERFORMANCE ###

# Default Socket Receive Buffer
sudo sysctl -w net.core.rmem_default=31457280
# Maximum Socket Receive Buffer
sudo sysctl -w net.core.rmem_max=12582912
# Default Socket Send Buffer
sudo sysctl -w net.core.wmem_default=31457280
# Maximum Socket Send Buffer
sudo sysctl -w net.core.wmem_max=12582912
# Increase number of incoming connections
sudo sysctl -w net.core.somaxconn=65536
# Increase number of incoming connections backlog
sudo sysctl -w net.core.netdev_max_backlog=65536
# Increase the maximum amount of option memory buffers
sudo sysctl -w net.core.optmem_max=25165824
# Increase the maximum total buffer-space allocatable
# This is measured in units of pages (4096 bytes)
sudo sysctl -w net.ipv4.tcp_mem='65536 131072 262144'
sudo sysctl -w net.ipv4.udp_mem='65536 131072 262144'
# Increase the read-buffer space allocatable
sudo sysctl -w net.ipv4.tcp_rmem='8192 87380 16777216'
sudo sysctl -w net.ipv4.udp_rmem_min=16384
# Increase the write-buffer-space allocatable
sudo sysctl -w net.ipv4.tcp_wmem='8192 65536 16777216'
sudo sysctl -w net.ipv4.udp_wmem_min=16384
# Increase the tcp-time-wait buckets pool size to prevent simple DOS attacks
sudo sysctl -w net.ipv4.tcp_max_tw_buckets=1440000
sudo sysctl -w net.ipv4.tcp_tw_recycle=1
sudo sysctl -w net.ipv4.tcp_tw_reuse=1

