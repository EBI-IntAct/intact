#!/bin/sh
#
# Purpose:
# Download controlled vocabularies.
# 
# Usage:
# downLoadCVs.sh targetDirectory
#


# write simple lists which are not part of PSI
for cv in CvAliasType CvComponentRole CvDatabase CvTopic CvXrefQualifier 
do
   scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv - $1/$cv.def 
   perl -w scripts/dag2html.pl -targetDir $1 -stems $cv
done

# write DAG CVs which are not part of PSI
for cv in CvInteractionType
do scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv - $1/$cv.def $1/$cv.dag 
   perl -w scripts/dag2html.pl -targetDir $1 -stems $cv
done

# write CVs which are part of PSI
for cv in CvIdentification CvInteraction
do scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv psi-mi $1/$cv.def $1/$cv.dag 
   perl -w scripts/dag2html.pl -targetDir $1 -stems $cv
done

# end

