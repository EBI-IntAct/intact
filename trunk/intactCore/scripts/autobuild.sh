#!/bin/sh
# *************************************************************
#
# Package:    IntAct tools
#
# Purpose:    Automated nightly build of IntAct
#
# $Source$
# $Date$
# $Author$
# $Locker$
#
# Requirements: 
# - Postgres 7.2 installed and on the search path.
# - a database intacttest is up and running under the current user
# - $1 exists and is writable
# - $1/config directory exists and has the correct config files
#   in it.  
#               
# *************************************************************

source /homes/hhe/.cshrc
    
# Clean up from previous run
cd $1
rm -rf intactCore

# Get sources from CVS
cvs -z8 -d:pserver:anonymous@cvs.intact.sourceforge.net:/cvsroot/intact co intactCore

# Configure
# Note: Correct configuration files need to be in the config/ directory.
cp config/* intactCore/config

# Compile
cd intactCore
ant compile
ant javadoc

# Fill the database
scripts/postgres/testfill.sh hhe hhe small

# Run unit tests
scripts/runHelperTests.sh IntactHelperTest

# 
