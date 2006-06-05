#!/bin/sh
#
echo "Usage: createTableOracle.sh user database"


psql -U $1 -d $2 -f sql/cv2tree/createTableOracle.sql

if [ $? != 0 ]
then
    echo "usage: createTableOracle.sh user database "
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi
