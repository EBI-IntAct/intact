#!/bin/sh
#
# Purpose:
# Download controlled vocabularies.
# 
# Usage:
# downLoadCVs.sh targetDirectory
#

if [ $# -ne 1 ]; then
   echo ""
   echo "ERROR: wrong number of parameters."
   echo "usage: downLoadCVs.sh <output directory>"
   echo ""
   exit 1
fi


OUTPUT_DIR=$1

# check that the directory exits, if not create it.
if [ -d $OUTPUT_DIR ]
then
        echo "Found directory $OUTPUT_DIR"
else
        echo "Directory $OUTPUT_DIR doesn't exist!"
        echo "Creating it..."
        mkdir $OUTPUT_DIR
        echo "done."
fi


# write simple lists which are not DAGs
for cv in CvTopic \
          CvAliasType \
          CvComponentRole \
          CvDatabase \
          CvXrefQualifier \
          CvFuzzyType
do
   scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv psi-mi $1/$cv.def
   perl -w scripts/dag2html.pl -targetDir $1 -stems $cv
done

# write CVs which are DAGs
for cv in CvIdentification \
          CvInteraction \
          CvInteractionType \
          CvInteractorType \
          CvFeatureType \
          CvFeatureIdentification
do
   scripts/javaRun.sh GoTools download uk.ac.ebi.intact.model.$cv psi-mi $1/$cv.def $1/$cv.dag
   perl -w scripts/dag2html.pl -targetDir $1 -stems $cv
done

# create a single DAG-Edit file for all hierarchical CVS for curation support
echo Creating DAG-Edit formatted file
perl -w scripts/toDagEditFormat.pl -sourceDagFile $1/CvInteractionType.dag \
                                   -sourceDagFile $1/CvInteractorType.dag \
                                   -sourceDagFile $1/CvIdentification.dag \
                                   -sourceDagFile $1/CvInteraction.dag \
                                   -sourceDagFile $1/CvFeatureType.dag \
                                   -sourceDagFile $1/CvFeatureIdentification.dag \
                                   -sourceDefFile $1/CvInteractionType.def \
                                   -sourceDefFile $1/CvInteractorType.def \
                                   -sourceDefFile $1/CvIdentification.def \
                                   -sourceDefFile $1/CvInteraction.def \
                                   -sourceDefFile $1/CvFeatureType.def \
                                   -sourceDefFile $1/CvFeatureIdentification.def \
                                   -targetDagFile $1/allCVsInDAGeditFormat.dag \
                                   -targetDefFile $1/allCVsInDAGeditFormat.def

# end

