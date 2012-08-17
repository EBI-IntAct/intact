MAVEN_OPTS="-Xms512m -Xmx2024m -XX:MaxPermSize=256m"

export MAVEN_OPTS


echo "MAVEN_OPTS=$MAVEN_OPTS"

echo "input file $1"
echo "ouput file $2"
echo "taxid $3"

mvn -U clean install -Pprotein-sequence-identification -Dinput.file=$1 -Douput.file=$2 -Dtaxid=$3 -Dmaven.repo.local=repository -Dmaven.test.skip -Ddb=oracle