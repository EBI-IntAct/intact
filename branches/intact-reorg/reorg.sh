#!/bin/bash

SOURCE_BASE=~/projects/intact
DEST_BASE=~/projects/intact-reorg

#When using subversion the value of this variable has to be the client path ('svn')
SVN=

# Set when not using SVN
if [ "$SVN" = "" ]; then
  MKDIR="mkdir -p"
  CP="cp -r"
  RM="rm -rf"
  MV="mv"
else
  COMMIT_MSG=-m "Done automatically by the reorganization script"
  MKDIR=$SVN mkdir $COMMIT_MSG
  CP=$SVN cp $COMMIT_MSG
  RM=$SVN rm $COMMIT_MSG
  MV=$SVN mv $COMMIT_MSG
fi

INTACT_PKG=uk/ac/ebi/intact


# INTACT CORE
# -----------
echo INTACT CORE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG
IC_DEST_SRC=$DEST_BASE/intact-core/trunk/src/main/java/$INTACT_PKG

$RM $IC_DEST_SRC
$MKDIR $IC_DEST_SRC
$CP $IC_SOURCE_SRC/business $IC_DEST_SRC/business
$CP $IC_SOURCE_SRC/model $IC_DEST_SRC/model
$CP $IC_SOURCE_SRC/persistence $IC_DEST_SRC/persistence
$CP $IC_SOURCE_SRC/simpleGraph $IC_DEST_SRC/simpleGraph
$CP $IC_SOURCE_SRC/util $IC_DEST_SRC/util
$CP $IC_SOURCE_SRC/core $IC_DEST_SRC/core

# removes the sanity checker classes from core
echo   removing sanityChecker packages
$RM $IC_DEST_SRC/util/sanityChecker
$RM $IC_DEST_SRC/util/rangeChecker
$RM $IC_DEST_SRC/util/correctionAssigner
$RM $IC_DEST_SRC/util/test

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC -name ".svn" | xargs rm -rf
fi

# APP COMMONS
# -----------
echo APP COMMONS
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/commons
IC_DEST_SRC=$DEST_BASE/app-commons/trunk/src/main/java/$INTACT_PKG/application/commons

$RM $IC_DEST_SRC
$CP $IC_SOURCE_SRC $IC_DEST_SRC

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC -name ".svn" | xargs rm -rf
fi

# SEARCH ENGINE
# -----------
echo SEARCH ENGINE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-engine/trunk/src/main/java/$INTACT_PKG/application/search3

$RM $IC_DEST_SRC
$MKDIR $IC_DEST_SRC
$CP $IC_SOURCE_SRC/searchEngine $IC_DEST_SRC/searchEngine
$CP $IC_SOURCE_SRC/util $IC_DEST_SRC/util

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC -name ".svn" | xargs rm -rf
fi

# SEARCH WEB APP
# -----------
echo SEARCH WEB APP
echo " " Java files
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-app/trunk/src/main/java/$INTACT_PKG/application/search3

$RM $IC_DEST_SRC
$MKDIR $IC_DEST_SRC
$CP $IC_SOURCE_SRC/advancedSearch $IC_DEST_SRC/advancedSearch
$CP $IC_SOURCE_SRC/business $IC_DEST_SRC/business
$CP $IC_SOURCE_SRC/servlet $IC_DEST_SRC/servlet
$CP $IC_SOURCE_SRC/struts $IC_DEST_SRC/struts

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC -name ".svn" | xargs rm -rf
fi

echo " " Webapp files
IC_SOURCE_SRC=$SOURCE_BASE/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-app/trunk/src/main

if [ "$SVN" = "" ]; then
  $RM $IC_DEST_SRC/webapp
fi

$CP $IC_SOURCE_SRC $IC_DEST_SRC
$MV $IC_DEST_SRC/search3 $IC_DEST_SRC/webapp

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC/webapp -name ".svn" | xargs rm -rf
fi

$CP $SOURCE_BASE/application/tld/* $IC_DEST_SRC/webapp/WEB-INF/tld
$CP $SOURCE_BASE/application/layouts/* $IC_DEST_SRC/webapp/layouts
$CP $IC_SOURCE_SRC/layouts/styles/* $IC_DEST_SRC/webapp/layouts/styles
$CP $SOURCE_BASE/application/images/* $IC_DEST_SRC/webapp/images
$CP $SOURCE_BASE/src/hibernate.cfg.xml $IC_DEST_SRC/resources/

$MKDIR $IC_DEST_SRC/resources/config
$CP $SOURCE_BASE/application/search3/WEB-INF/config/* $IC_DEST_SRC/resources/config
$CP $SOURCE_BASE/config/Institution.properties $IC_DEST_SRC/resources/config

if [ "$SVN" = "" ]; then
  echo " " Removing .svn
  find $IC_DEST_SRC/webapp -name ".svn" | xargs rm -rf
fi


