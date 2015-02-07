#!/bin/sh
################################################################

# Exit on error
set -e

#
# Properties
#

SERVICE_NAME=booklib
BASE_DIR=/usr/local/booklib


JAR_PATH=$BASE_DIR/app.jar
CONFIG_PATH=file:$BASE_DIR/var/app.properties
PID_PATH=$BASE_DIR/var/process-pid

LOGDIR_PATH=$BASE_DIR/var/log
JUL_LOG_FILE=$BASE_DIR/var/log/jul.log

JMX_PORT=7299
HEAP_DUMP_PATH=$BASE_DIR/var/heapdump

# No: 0 or Yes: 1
JVM_DEBUG_ENABLED=0
# No: 'n' or Yes: 'y'
JVM_DEBUG_SUSPEND=n
JVM_DEBUG_PORT=7301

# TODO: consider the fact that PermGen doesn't exist in GC introduced in Java8
GC_PERM_SIZE=64m
GC_MAX_HEAP_SIZE=256m
GC_START_HEAP_SIZE=64m



#
# Preparations
#

# Prepare JVM command line

JVM_PROPS="-server"

# Logging Configuration
JVM_PROPS="$JVM_PROPS -Dapp.logback.logBaseName=$LOGDIR_PATH/booklib -Dapp.logback.rootLogId=ROLLING_FILE"

# Internal Java Logging
JVM_PROPS="$JVM_PROPS -Djava.util.logging.config.file=$JUL_LOG_FILE"

# Inet Settings
JVM_PROPS="$JVM_PROPS -Dsun.net.inetaddr.ttl=60 -Dnetworkaddress.cache.ttl=60 -Dsun.net.inetaddr.negative.ttl=10 -Djava.net.preferIPv4Stack=true"

# JMX Settigns
JVM_PROPS="$JVM_PROPS -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=$JMX_PORT -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"

# OOM handling
JVM_PROPS="$JVM_PROPS -XX:OnOutOfMemoryError="/bin/kill -9 %p" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$HEAP_DUMP_PATH"

# GC Heap Settings
JVM_PROPS="$JVM_PROPS -XX:+UseCompressedOops -XX:MaxPermSize=$GC_PERM_SIZE -Xmx$GC_MAX_HEAP_SIZE -Xms$GC_START_HEAP_SIZE"

# Debug Settings
if [[ $JVM_DEBUG_ENABLED == 1 ]]; then
  # TODO: consider adding -verbose:gc and -XX:+PrintGCDetails
  JVM_PROPS="$JVM_PROPS -agentlib:jdwp=transport=dt_socket,server=y,suspend=$JVM_DEBUG_SUSPEND,address=$JVM_DEBUG_PORT"
fi

echo "Using JVM settings: $JVM_PROPS"


# Create internal directory structure

if [ ! -d $LOGDIR_PATH ]; then
    # For efficiency log dir can point to /var/log or to the other, custom FS partition for the logs only
    mkdir -p $LOGDIR_PATH
fi

# Check for existence of important files
if [ ! -f $CONFIG_PATH ]; then
    echo "WARNING: Config file doesn't exist at $CONFIG_PATH"
fi

if [ ! -f $JAR_PATH ]; then
    echo "ERROR: No JAR to start, make sure you have appropriate file at $JAR_PATH"
    exit -1
fi

#
# Server Start/Stop Functions
#

start_server ()
{
    echo "Starting $SERVICE_NAME ..."
    if [ ! -f $PID_PATH ]; then
        # Entry Point
        # Uses custom configuration as well as "live" log settings
        nohup java $JVM_PROPS -jar $JAR_PATH --config $CONFIG_PATH &
        echo $! > PID_PATH
        echo "$SERVICE_NAME started ..."
    else
        echo "$SERVICE_NAME is already running ..."
    fi
}

stop_server ()
{
    if [ -f $PID_PATH ]; then
        PID=$(cat $PID_PATH);
        echo "$SERVICE_NAME stoping ..."
        kill $PID;
        echo "$SERVICE_NAME stopped ..."
        rm $PID_PATH
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