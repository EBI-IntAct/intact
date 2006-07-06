#!/bin/sh

# deploy the service (with the modified WSDD file)
echo "deploy the service (with the WSDD file you had modified)"
java org.apache.axis.client.AdminClient wsdd/deploy.wsdd

echo "If an error occur, check : "
echo " (1) if Tomcat is properly running (with service available in Axis)"
echo " (2) if Serveur (binary file from Bordeaux) is running"



echo ""
echo "server side ready."
echo ""

echo "" 
echo "You can test your service with a WEB browser here :"
echo "http://arafel:8080/axis/services/tulip?wsdl"
echo "You'll read the WSDL contract"
echo "" 
