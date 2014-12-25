#!/bin/sh

SERVICE_NAME=booklib
PATH_TO_JAR=/usr/local/booklib/booklib-website.jar
CONFIG_PATH=file:/usr/local/booklib/booklib.properties
LOG_PATH=/usr/local/booklib/log
PID_PATH_NAME=/tmp/booklib-pid

start_server ()
{
    echo "Starting $SERVICE_NAME ..."
    if [ ! -f $PID_PATH_NAME ]; then
        nohup java -jar $PATH_TO_JAR --config $CONFIG_PATH 2>> $LOG_PATH.err >> $LOG_PATH.out &
                    echo $! > $PID_PATH_NAME
        echo "$SERVICE_NAME started ..."
    else
        echo "$SERVICE_NAME is already running ..."
    fi
}

stop_server ()
{
    if [ -f $PID_PATH_NAME ]; then
        PID=$(cat $PID_PATH_NAME);
        echo "$SERVICE_NAME stoping ..."
        kill $PID;
        echo "$SERVICE_NAME stopped ..."
        rm $PID_PATH_NAME
    else
        echo "$SERVICE_NAME is not running ..."
    fi
}

case $1 in
    start)
        start_server
    ;;
    stop)
        stop_server
    ;;
    restart)
        stop_server
        start_server
    ;;
esac