#!/usr/bin/perl -w
# SOAP::Lite version 0.67
# Please note: IntAct webservices uses document/literal binding

#use SOAP::Lite + trace => qw(debug);
use SOAP::Lite;

# Setup service
#my $WSDL = 'http://localhost:9090/intact/ws-1.6.0-beta-2/binarysearch?wsdl';
my $WSDL = 'http://www.ebi.ac.uk/intact/binary-search-ws/binarysearch?wsdl';
my $nameSpace = 'http://ebi.ac.uk/intact/binarysearch/wsclient/generated';

my $soap = SOAP::Lite
-> uri($nameSpace)
-> proxy($WSDL);

my $result = $soap -> getVersion();

# if no error
unless ($result->fault) {

    my $version = $result->valueof('//getVersionResponse/return');

   print 'Service version: ' . $version;
   print "\n";

} else {
   # some error handling
  print join ', ',
    $result->faultcode,
    $result->faultstring,
    $result->faultdetail;
}
