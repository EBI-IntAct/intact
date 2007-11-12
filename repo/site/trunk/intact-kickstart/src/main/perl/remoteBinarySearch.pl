#!/usr/bin/perl -w
#
# Copyright 2001-2007 The European Bioinformatics Institute.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
# SOAP::Lite version 0.67
#
# Please note: IntAct webservices uses document/literal binding

use SOAP::Lite;


# Query
my $myQuery = 'brca2';

# Setup service
my $WSDL = 'http://www.ebi.ac.uk/intact/binary-search-ws/binarysearch?wsdl';
my $nameSpace = 'http://ebi.ac.uk/intact/binarysearch/wsclient/generated';

my $soap = SOAP::Lite
-> uri($nameSpace)
-> proxy($WSDL);


# Setup method and parameters
    my $method = SOAP::Data->name('findBinaryInteractions')
                           ->attr({xmlns => $nameSpace});

    my @params = ( SOAP::Data->name(query => $myQuery));

    # Call method
    my $result = $soap->call($method => @params);


# if no error
unless ($result->fault) {

    # Retrieve for example all interactions in PSIMITAB format
    @stuff = $result->valueof('//interactionLines');
    print @stuff;

} else {
   # some error handling
  print join ', ',
    $result->faultcode,
    $result->faultstring,
    $result->faultdetail;
  print "\n";
}
