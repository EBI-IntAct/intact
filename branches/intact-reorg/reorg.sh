#!/bin/bash

SOURCE_BASE=~/projects/intact
DEST_BASE=~/projects/intact-reorg

SVN=

INTACT_PKG=/uk/ac/ebi/intact


# INTACT CORE
# -------------
IC_SOURCE_SRC=$SOURCE_BASE/intactCore/src/$INTACT_PKG
IC_DEST_SRC=$DEST_BASE/intact-core/trunk/src/$INTACT_PKG

$SVN mkdir -p $IC_DEST_SRC
$SVN cp $IC_SOURCE_SRC/business $IC_DEST_SRC/business
$SVN cp $IC_SOURCE_SRC/model $IC_DEST_SRC/model
$SVN cp $IC_SOURCE_SRC/persistence $IC_DEST_SRC/persistence
$SVN cp $IC_SOURCE_SRC/simpleGraph $IC_DEST_SRC/simpleGraph
$SVN cp $IC_SOURCE_SRC/util $IC_DEST_SRC/util

