#!/bin/sh

# Installs an intact home in the passed directory
#
# Usage $0 dir version
#
# $Id$
#
##################################################################

if [ $# -ne 2 ]; then
      echo ""
      echo "ERROR: wrong number of parameters ($#)."
      echo "usage: $0 <install-dir> <version>"
      echo ""
      exit 1
fi

INSTALL_DIR=$1
VERSION=$2

CURRENT_DIR=`pwd`

WORKING_DIR=/tmp/solr-home-wd

rm -rf $WORKING_DIR

mkdir -p $WORKING_DIR
cd $WORKING_DIR

JAR=intact-solr-home-$VERSION.jar
REPO_BASE=http://www.ebi.ac.uk/~maven/m2repo
REPO_SNAPSHOTS_BASE=http://www.ebi.ac.uk/~maven/m2repo_snapshots
ARTIFACT_PATH=/uk/ac/ebi/intact/dataexchange/psimi/intact-solr-home/$VERSION/$JAR

URL_STABLE=$REPO_BASE$ARTIFACT_PATH
URL_SNAPSHOT=$REPO_SNAPSHOTS_BASE$ARTIFACT_PATH

echo "Java Home: $JAVA_HOME"

echo "Getting file from server"
rm $JAR

if [[ `wget $URL_STABLE` ]]
then
  echo "Got stable file from server: $URL_STABLE"
else
  echo "Getting snapshot file from server: $URL_SNAPSHOT"
  rm $JAR
  `wget $URL_SNAPSHOT`
fi

rm home -rf

jar -xvf $JAR

cd $CURRENT_DIR

mv $WORKING_DIR/home $INSTALL_DIR

rm -rf $WORKING_DIR

exit 0