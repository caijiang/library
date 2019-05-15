#!/usr/bin/env bash

docker run --name mysql5.7 -p 3307:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:5.7

#再执行构建指令
#docker run -it --rm mysql:5.7 mysql -h docker.for.mac.localhost -u root -p
mysql -u root -h 127.0.0.1 --port 3307 -p'root' <<EOF
select 1;
CREATE USER 'library'@'%' IDENTIFIED BY 'library';
GRANT ALL on library.* to 'library'@'%';
CREATE DATABASE IF NOT EXISTS library CHARACTER SET = utf8;
delete from mysql.user where user='' or user is null;
flush privileges;
exit
EOF

#docker rm -f mysql5.7
