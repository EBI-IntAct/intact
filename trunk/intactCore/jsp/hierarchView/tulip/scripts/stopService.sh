#!/bin/sh

# undeploy the service (with WSDD file)
echo "undeploy the service (with the WSDD file)"
java org.apache.axis.client.AdminClient wsdd/undeploy.wsdd

echo ""
echo "server side stopped."
echo ""
