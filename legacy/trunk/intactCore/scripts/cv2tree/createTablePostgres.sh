#!/bin/sh
#
echo "Usage: createTablePostgres.sh user database"


psql -U $1 -d $2 -f sql/cv2tree/createTablePostgres.sql

if [ $? != 0 ]
then
    echo "usage: createTablePostgres.sh user database "
    exit 1 # if something went wrong in the previous command line, exit with an error code
fi
