#!/bin/bash

#定时清理tomcat,dubbo,zookeeper的日志  #清理14天以前的
#clean tomcat logs
echo "开始清理tomcat日志..."
find /app/apache-tomcat-8.5.20/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
echo "tomcat日志清理完成..."
#clean dobbo logs

echo "开始清理dubbo日志..."
find /app/msc/1-service-base/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
find /app/msc/2-service-b2b/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
find /app/msc/4-service-client/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
find /app/msc/6-service-count/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
find /app/msc/9-service-supervise/logs/ -mtime +14 -name "*log*" -exec rm -rf {} \;
echo "dubbo日志清理完成..."
#clean zookeeper logs
find /app/zookeeper-3.4.8/data/version-2/ -mtime +14 -name "snapshot*" -exec rm -rf {} \;
find /app/zookeeper-3.4.8/logs/version-2/ -mtime +14 -name "*log*" -exec rm -rf {} \;



