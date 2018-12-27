#!/bin/sh

## java env
export JAVA_HOME=/usr/java/jdk1.8.0_121
export JRE_HOME=$JAVA_HOME/jre

## service name

SERVICE_NAME=msc-service-supervise
JAR_NAME=$SERVICE_NAME.jar
PID=$SERVICE_NAME.pid


case "$1" in

    start)
        nohup $JRE_HOME/bin/java -Xms1024m -Xmx2048m -jar $JAR_NAME >/dev/null 2>&1 &
        echo $! > $PID
        echo "=== start $SERVICE_NAME"
        ;;

    stop)
        kill `cat $PID`
        rm -rf $PID
        echo "=== stop $SERVICE_NAME"

        sleep 5

        P_ID=`ps -ef | grep -w "$SERVICE_NAME" | grep -v "grep" | awk '{print $2}'`
        if [ "$P_ID" == "" ]; then
            echo "=== $SERVICE_NAME process not exists or stop success"
        else
            echo "=== $SERVICE_NAME process pid is:$P_ID"
            echo "=== begin kill $SERVICE_NAME process, pid is:$P_ID"
            kill -9 $P_ID
        fi
        ;;

    restart)
        $0 stop
        sleep 2
        $0 start
        echo "=== restart $SERVICE_NAME"
        ;;

    *)
        ## restart
        $0 stop
        sleep 2
        $0 start
        ;;

esac
exit 0

