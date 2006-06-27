#!/bin/bash --verbose

SOURCE_BASE=~/projects/intact
DEST_BASE=~/projects/intact-reorg

INTACT_CORE=N
APP_COMMONS=N
SEARCH_ENGINE=N
SEARCH_APP=Y

INTACT_PKG=uk/ac/ebi/intact

if [ "$INTACT_CORE" = "Y" ]; then

# INTACT CORE
# -----------
echo INTACT CORE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG
IC_DEST_SRC=$DEST_BASE/intact-core/trunk/src/main/java/$INTACT_PKG

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/business $IC_DEST_SRC/business
svn cp $IC_SOURCE_SRC/model $IC_DEST_SRC/model
svn cp $IC_SOURCE_SRC/persistence $IC_DEST_SRC/persistence
svn cp $IC_SOURCE_SRC/simpleGraph $IC_DEST_SRC/simpleGraph
svn cp $IC_SOURCE_SRC/util $IC_DEST_SRC/util
svn cp $IC_SOURCE_SRC/core $IC_DEST_SRC/core

# removes the sanity checker classes from core
echo   removing sanityChecker packages
svn rm $IC_DEST_SRC/util/sanityChecker
svn rm $IC_DEST_SRC/util/rangeChecker
svn rm $IC_DEST_SRC/util/correctionAssigner
svn rm $IC_DEST_SRC/util/test
svn rm $IC_DEST_SRC/business/test

fi

if [ "$APP_COMMONS" = "Y" ]; then

# APP COMMONS
# -----------
echo APP COMMONS
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/commons
IC_DEST_SRC=$DEST_BASE/app-commons/trunk/src/main/java/$INTACT_PKG/application

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC $IC_DEST_SRC

fi

if [ "$SEARCH_ENGINE" = "Y" ]; then

# SEARCH ENGINE
# -----------
echo SEARCH ENGINE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-engine/trunk/src/main/java/$INTACT_PKG/application/search3

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/searchEngine $IC_DEST_SRC/searchEngine
svn cp $IC_SOURCE_SRC/util $IC_DEST_SRC/util

fi

if [ "$SEARCH_APP" = "Y" ]; then

# SEARCH WEB APP
# -----------
echo SEARCH WEB APP
echo " " Java files
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-app/trunk/src/main/java/$INTACT_PKG/application/search3

rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/advancedSearch $IC_DEST_SRC/advancedSearch
svn cp $IC_SOURCE_SRC/business $IC_DEST_SRC/business
svn cp $IC_SOURCE_SRC/servlet $IC_DEST_SRC/servlet
svn cp $IC_SOURCE_SRC/struts $IC_DEST_SRC/struts

echo " " Webapp files
IC_SOURCE_SRC=$SOURCE_BASE/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-app/trunk/src/main

rm -rf $IC_DEST_SRC/search3
rm -rf $IC_DEST_SRC/webapp
svn update $IC_DEST_SRC/webapp
svn cp $IC_SOURCE_SRC $IC_DEST_SRC
mv $IC_DEST_SRC/search3 $IC_DEST_SRC/webapp

for i in `find $SOURCE_BASE/application/tld/*.tld`; do
  svn cp $i $IC_DEST_SRC/webapp/WEB-INF/tld
done
#for i in `find $SOURCE_BASE/application/layouts/*.jsp`; do
#  svn cp $i $IC_DEST_SRC/webapp/WEB-INF/layouts
#done
for h in `find $SOURCE_BASE/application/images/*.gif $SOURCE_BASE/application/images/*.jpg $SOURCE_BASE/application/images/*.png`; do
  svn cp $h $IC_DEST_SRC/webapp/images
done

echo " " Resource files

IC_DEST_SRC=$DEST_BASE/search/search-app/trunk/src/main/resources

rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC
#svn cp $SOURCE_BASE/src/hibernate.cfg.xml $IC_DEST_SRC
svn cp $SOURCE_BASE/application/search3/WEB-INF/config $IC_DEST_SRC
svn cp $SOURCE_BASE/config/Institution.properties $IC_DEST_SRC/config

fi

