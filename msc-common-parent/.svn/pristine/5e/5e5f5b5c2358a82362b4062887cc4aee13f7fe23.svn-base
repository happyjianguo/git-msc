#！/bin/bash
#delete expire log
#script name drop_log
#script default remove 7 day log and remove remove archived a year ago
#make date 2016/07/02


#tomcat日志备份
logResources=("localhost_access_log" "tomcat" "msc-web.log")
tomcatBasedir="tomcat"
for index in 0 1 2
do	
	logResource=${logResources[$index]}
	logs=`find $tomcatBasedir/logs/ -mtime +7 -name "$logResource.*"|awk -F '/' '{print $5}'`
	echologs=`echo $logs|wc -c`
	
	echo "$logResource logs start backup"
	
	if [ $echologs -gt 1 ] ; then                                                   #判断有没有压缩记录
		rj=`date +%Y%m%d`
		if [ -f runlogs/$logResource$rj.tar ] ; then                           #判断文件有没有存在
			echo "file already exists"
		else
			cd $tomcatBasedir/logs/
			tar -czvf runlogs/$logResource$rj.tar.gz  $logs >/dev/null         #这里指定压缩路径

			RESULT=$?
			if [ $RESULT -eq 0 ] ; then
				echo "$logResource log The backup successful" 
			else
				echo "$logResource log The backup failed" 
			fi
			if [ $RESULT -eq 0 ] ; then
				cd $tomcatBasedir/logs/                                             #切换要删除目录

				rm -f $logs                                                         #删除已经压缩日志
				RESULT=$?
				if [ $RESULT -eq 0 ] ; then
					echo "$logResource log The delete successful" 
				else
					echo "$logResource log The delete failed" 
				fi
			fi
		fi

	else
		echo "There is no backup"
	fi
done

#dubbo日志备份
projectNames=("1-service-base" "2-service-b2b" "4-service-client" "5-service-pe")
for index in 0 1 2 3
do	
	projectName=${projectNames[$index]}
	logs=`find msc/$projectName/logs/ -mtime +7 -name "msc-service.log.*"|awk -F '/' '{print $5}'`
	echologs=`echo $logs|wc -c`
	
	echo "$projectName logs start backup"
	
	if [ $echologs -gt 1 ] ; then                                                     #判断有没有压缩记录
		rj=`date +%Y%m%d`
		if [ -f runlogs/$projectName$rj.tar ] ; then                             #判断文件有没有存在
			echo "file already exists"
		else
			cd msc/$projectName/logs/
			tar -czvf runlogs/$projectName$rj.tar.gz  $logs >/dev/null           #这里指定压缩路径

			RESULT=$?
			if [ $RESULT -eq 0 ] ; then
				echo "$projectName log The backup successful" 
			else
				echo "$projectName log The backup failed" 
			fi
			if [ $RESULT -eq 0 ] ; then
				cd msc/$projectName/logs/                                        #切换要删除目录

				rm -f $logs                                                           #删除已经压缩日志
				RESULT=$?
				if [ $RESULT -eq 0 ] ; then
					echo "$projectName log The delete successful" 
				else
					echo "$projectName log The delete failed" 
				fi
			fi
		fi

	else
		echo "There is no backup"
	fi
done


#删除日志备份
RESULT=$?
if [ $RESULT -eq 0 ] ; then
    find runlogs/ -mtime +365 -name "*.tar" -exec rm -f   {} \;                 #修改要删除压缩备份路径默认365天
else
    echo "remove archived a year ago failed"
fi
