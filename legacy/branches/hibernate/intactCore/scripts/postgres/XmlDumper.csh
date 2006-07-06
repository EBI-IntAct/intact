#!/usr/local/bin/tcsh 


setenv CLASSPATH /ebi/sp/pro3/shared/java-lib/default/unpack:.:./classes:./lib/castor-0.9.3.9-xml.jar:./lib/castor-0.9.3.9.jar:./lib/jdbc-se2.0.jar:./lib/pgjdbc2.jar:./lib/jta1.0.1.jar:./config:./lib/jakarta-ojb-0.9.2.jar:./lib/log4j-1.2.5.jar:./lib/commons-collections-2.0.jar:./lib/commons-lang-0.1-dev.jar:./lib/jdori.jar:./lib/jmxri.jar:./lib/jta-spec1_0_1.jar:./lib/optional.jar:./lib/p6spy.jar

java  -Xms500m -Xmx500m  -Dconfig=./config/Properties.pro  uk.ac.ebi.intact.application.synchron.XmlDumper $1
