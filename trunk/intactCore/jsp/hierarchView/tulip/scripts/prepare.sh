#!/bin/sh

# clean
rm -rf wsdd
rm -rf uk


# Your service
echo "compile your service..."
export ROOT=../../../..
javac ${ROOT}/src/uk/ac/ebi/intact/application/hierarchView/business/tulip/webService/*.java \
      -d ${ROOT}/classes

export SERVICE_URL=http://arafel:8080/axis/services/tulip
echo ""
echo "     Service will be available there : ${SERVICE_URL}"
echo ""


# WSDL file
echo "generate WSDL file..."
java -cp ${ROOT}/classes:${CLASSPATH} org.apache.axis.wsdl.Java2WSDL \
     -o tulip.wsdl -l"${SERVICE_URL}" -n urn:tulip \
     -p"uk.ac.ebi.intact.application.hierarchView.business.tulip.webService" \
     urn:uk.ac.ebi.intact.application.hierarchView.business.tulip.webService \
     uk.ac.ebi.intact.application.hierarchView.business.tulip.webService.TulipAccess

export SESSION_TYPE=Session
echo ""
echo "     Session scope = ${SESSION_TYPE}"
echo ""


# Server side
echo "generate server side files..."
java org.apache.axis.wsdl.WSDL2Java -o . -d ${SESSION_TYPE} -s -p uk.ac.ebi.intact.application.hierarchView.business.tulip.webService.generated tulip.wsdl


echo "get the generated wsdd (deploy & undeploy) file..."
mkdir wsdd
cp uk/ac/ebi/intact/application/hierarchView/business/tulip/webService/generated/*.wsdd wsdd/
echo "Remove generated server source files"
rm -rf uk/ac/ebi/intact/application/hierarchView/business/tulip/webService


# Client side
echo "generate client side files..."
java org.apache.axis.wsdl.WSDL2Java -o . -d ${SESSION_TYPE} -p uk.ac.ebi.intact.application.hierarchView.business.tulip.client.generated tulip.wsdl


# Copy in the src directory generated client source files
echo "Copy in the src directory generated client source files"
cp -r uk ${ROOT}/src


# create the jar file
echo "create the service jar file"
jar cvf tulipService.jar -C ${ROOT}/classes uk/ac/ebi/intact/application/hierarchView/business/tulip/webService
 

# copy the service on the tomcat server (Axis)
echo "copy the service on the tomcat server (Axis)"
cp tulipService.jar ${CATALINA_HOME}/webapps/axis/WEB-INF/lib
sleep 1



echo ""
echo ""
echo "Modify your WSDD file (wsdd/deploy.wsdd) to fit with your implementation "
echo "Then run startService.sh"


echo ""
echo "Now you can compile intactCore project sources"
echo ""

# clean the generated source files
rm -rf uk
