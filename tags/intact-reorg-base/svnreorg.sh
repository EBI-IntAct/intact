#!/bin/bash

SOURCE_BASE=~/projects/intact-clean
DEST_BASE=~/projects/intact-reorg

INTACT_CORE=N
APP_COMMONS=N
SANITY_CHECKER=N
SEARCH_ENGINE=N
SEARCH_APP=N
EDITOR_APP=Y

INTACT_PKG=uk/ac/ebi/intact

#############################################################################################
#                        INTACT CORE                                                        #
#############################################################################################

if [ "$INTACT_CORE" = "Y" ]; then

echo INTACT CORE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG
IC_DEST_SRC=$DEST_BASE/intact-core/trunk/src/main/java/$INTACT_PKG

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/annotation $IC_DEST_SRC/annotation
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

#############################################################################################
#                        APP COMMONS                                                        #
#############################################################################################

if [ "$APP_COMMONS" = "Y" ]; then

echo APP COMMONS
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/commons
IC_DEST_SRC=$DEST_BASE/app-commons/trunk/src/main/java/$INTACT_PKG/application

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC $IC_DEST_SRC

fi

#############################################################################################
#                        SANITY CHECKER                                                     #
#############################################################################################

if [ "$SANITY_CHECKER" = "Y" ]; then

# SANITY CHECKER
# -----------
echo SANITY_CHECKER
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/util
IC_DEST_SRC=$DEST_BASE/sanity-checker/trunk/src/main/java/$INTACT_PKG/util

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/sanityChecker $IC_DEST_SRC
svn cp $IC_SOURCE_SRC/rangeChecker $IC_DEST_SRC
svn cp $IC_SOURCE_SRC/correctionAssigner $IC_DEST_SRC

fi

#############################################################################################
#                        SEARCH ENGINE                                                      #
#############################################################################################

if [ "$SEARCH_ENGINE" = "Y" ]; then

echo SEARCH ENGINE
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/search3
IC_DEST_SRC=$DEST_BASE/search/search-engine/trunk/src/main/java/$INTACT_PKG/application/search3

#svn revert $IC_DEST_SRC
rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC/searchEngine $IC_DEST_SRC/searchEngine
svn cp $IC_SOURCE_SRC/util $IC_DEST_SRC/util

fi

#############################################################################################
#                        SEARCH APP                                                         #
#############################################################################################

if [ "$SEARCH_APP" = "Y" ]; then

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

rm -rf $IC_DEST_SRC/config
svn update $IC_DEST_SRC
#svn revert $IC_DEST_SRC/hibernate.cfg.xml
#svn cp $SOURCE_BASE/src/hibernate.cfg.xml $IC_DEST_SRC
svn cp $SOURCE_BASE/application/search3/WEB-INF/config $IC_DEST_SRC
svn cp $SOURCE_BASE/config/Institution.properties $IC_DEST_SRC/config

fi

#############################################################################################
#                        EDITOR APP                                                         #
#############################################################################################

if [ "$EDITOR_APP" = "Y" ]; then

echo EDITOR WEB APP
echo " " Java files
IC_SOURCE_SRC=$SOURCE_BASE/src/$INTACT_PKG/application/editor
IC_DEST_SRC=$DEST_BASE/editor/editor-app/trunk/src/main/java/$INTACT_PKG/application

rm -rf $IC_DEST_SRC/*
svn update $IC_DEST_SRC/
svn cp $IC_SOURCE_SRC $IC_DEST_SRC

echo " " Webapp files
IC_SOURCE_SRC=$SOURCE_BASE/application/editor
IC_DEST_SRC=$DEST_BASE/editor/editor-app/trunk/src/main

rm -rf $IC_DEST_SRC/editor
rm -rf $IC_DEST_SRC/webapp
svn update $IC_DEST_SRC/webapp
svn cp $IC_SOURCE_SRC $IC_DEST_SRC
mv $IC_DEST_SRC/editor $IC_DEST_SRC/webapp

for i in `find $SOURCE_BASE/application/tld/*.tld`; do
  svn cp $i $IC_DEST_SRC/webapp/WEB-INF/tld
done
for i in `find $SOURCE_BASE/application/layouts/*.jsp`; do
  svn cp $i $IC_DEST_SRC/webapp/layouts
done
for h in `find $SOURCE_BASE/application/images/*.gif $SOURCE_BASE/application/images/*.jpg $SOURCE_BASE/application/images/*.png`; do
  svn cp $h $IC_DEST_SRC/webapp/images
done

echo " " Resource files

IC_DEST_SRC=$DEST_BASE/editor/editor-app/trunk/src/main/resources

rm -rf $IC_DEST_SRC/config
svn update $IC_DEST_SRC
#svn revert $IC_DEST_SRC/hibernate.cfg.xml
#svn cp $SOURCE_BASE/src/hibernate.cfg.xml $IC_DEST_SRC
svn cp $SOURCE_BASE/config $IC_DEST_SRC
svn cp $SOURCE_BASE/config/Institution.properties $IC_DEST_SRC/config

for i in `find $IC_SOURCE_SRC/WEB-INF/config/*.properties`; do
  svn cp $i $IC_DEST_SRC/uk/ac/ebi/intact/application/editor
done

rm -rf $IC_DEST_SRC/OJB.properties
svn rm $IC_DEST_SRC/OJB.properties
svn cp $SOURCE_BASE/template/repository_database.template_mavenreorg $IC_DEST_SRC/config/repository_database.xml
svn cp $SOURCE_BASE/config/OJB.properties $IC_DEST_SRC/

fi
