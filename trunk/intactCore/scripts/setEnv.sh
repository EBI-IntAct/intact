# this file has to be sourced to be properly used

echo "Set some environment variables under bash ..."

export CATALINA_HOME=[YOUR_CATALINA_HOME]
echo "CATALINA_HOME=$CATALINA_HOME"
echo ""

export JAVA_HOME=[YOUR_JAVA_HOME]
echo "JAVA_HOME=$JAVA_HOME"
echo "" 

export CLASSPATH=.
echo "CLASSPATH=$CLASSPATH"
echo "" 

# Only for hierarchView
export TLPDIR=/sw/arch/pkg/tulip-1.2.1
echo "TLPDIR=$TLPDIR"
echo "" 

export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:TLPDIR/lib
echo "LD_LIBRARY_PATH=$LD_LIBRARY_PATH"
echo "" 
