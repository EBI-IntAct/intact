# this file has to be sourced to be properly used

echo "Set some environment variables in csh ..."

setenv CATALINA_HOME [YOUR_CATALINA_HOME]
echo "CATALINA_HOME=$CATALINA_HOME"
echo ""

setenv JAVA_HOME [YOUR_JAVA_HOME]
echo "JAVA_HOME=$JAVA_HOME"
echo "" 

setenv CLASSPATH .
echo "CLASSPATH=$CLASSPATH"
echo "" 

# Only for hierarchView
setenv TLPDIR /sw/arch/pkg/tulip-1.2.1
echo "TLPDIR=$TLPDIR"
echo "" 

setenv LD_LIBRARY_PATH ${LD_LIBRARY_PATH}:TLPDIR/lib
echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
echo "" 
