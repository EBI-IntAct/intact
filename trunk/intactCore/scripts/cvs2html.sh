#!/bin/sh -x
#
# Purpose:
# Quick and dirty script to generate HTML listings of controlled vocabularies.
# 

for cv in $* 
do
   scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv ../tmp/$cv.def ../tmp/$cv.dag 
   perl -w scripts/dag2html.pl -stems ../tmp/$cv
done

# end

