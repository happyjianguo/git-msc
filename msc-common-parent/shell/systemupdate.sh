#!/bin/sh
tfile=`date +%Y%m%d%H%M%S`
path="srcbackup/$tfile"
index=0
for service_dir in `ls src/msc`  
    do 
	index=$[$index + 1]
        if [ "`ls src/msc/$service_dir`" != "" ] ; then  
            echo "开始启动msc/$service_dir/下的升级"
            if [ ! -d "$path" ] ; then
                mkdir -p $path
            fi
            if [ "`netstat -tln | grep 2088$index`" != "" ] ; then
	        msc/$service_dir/msc-service.sh stop
            fi
            tar -czvf srcbackup/$tfile/$service_dir.tar.gz msc/$service_dir/lib msc/$service_dir/msc-service-${service_dir##*-}.jar
            if [[ $1 == "-d" ]] ; then
                echo "删除原程序文件"
                rm -rf msc/$service_dir/lib
            else
                echo "覆盖原程序文件"
            fi
            mv src/msc/$service_dir/* msc/$service_dir/
	    msc/$service_dir/msc-service.sh start
            if [[ $index -eq 1 ]] ; then
                sleep 10
            else
                sleep 1
            fi
        fi  
    done  

if [ "`ls src/webapps`" != "" ] ; then
    echo "开始启动tomcat下的升级"
    if [ ! -d "$path" ] ; then
        mkdir -p $path
    fi

    if [ "`netstat -tln | grep 8080`" != "" ] ; then
        tomcat/bin/catalina.sh stop
    fi
    tar -czvf srcbackup/$tfile/webapps.tar.gz tomcat/webapps/ --exclude *.war

    if [[ $1 == "-d" ]] ; then
        echo "删除原程序文件"
        rm -rf tomcat/webapps/*
    else
        echo "覆盖原程序文件"
    fi
    mv src/webapps/* tomcat/webapps/
    tomcat/bin/catalina.sh start
fi
