#!/usr/local/bin/perl -w


###############################################################################
# Description: This program generates a SQL script which populates 2 SQL tables. 
#              All data are true and from Luisa (compiled from 2 papers
#              PMID: 11805837; PMID: 11805826).
#
# Usage: fillLuisaData.pl -login <username/password> 
#                   - from </path/filename>
#                   -to </path/filename.sql>
#                   -db <DB_name> 
#                   -acPrefix <string>
#
#         The -file is the textfile from which data will be parsed and loaded 
#             to Oracle db, it's location is:
#         ~danwu/download/mass_spec_nature.txt
#
#         The -acPrefix is the string prefixed to the sequence number to form a 
#         AC (including AC, Interaction_ac, Owner_ac, etc.) in a format of 
#         ABC-12345, the 'ABC' is the -acPrefix parameter. 
#  
# Algorithm: #Check command line parameters, if false warn the user.
#              foreach line of the text file:
#            #get Interaction number, Molecule names, 
#            #if the hash key (using the interaction_number) not exists,
#               # make an insert statement (OUT file handle) (into Interactor table) 
#                 to store interaction_num.
#               #foreach of the molecules (interactors)
#                  #if the the hash key(using the molecular name) not exists
#                     #make an insert statement (into Interactor table) to store the molecular name.
#                     # and make an insert statement (into Component table)
#                  #if the molecule already in the Interactor table, 
#                     #make an insert statement into (Component table) directly.
#
# Note: The 'AC' are self generated without querying the for the 'sequence.currval',
#       because the AC values don't exist in the Interactor and Component tables.
#       But the last sequence value can be found in the Experiment table (see create_dummy.sql) 
#
# $Source$
# $Version: $
# $Date$
# $Author$
# $Locker$
#
###############################################################################

use strict ;
use DBI ;
use DBD::Oracle ;
use vars qw( $sLogin $sDB $sFromFile $sToFile $sPrefix %hAction %hActor ) ;
use Getopt::Long ;
use Cwd ;

my $sDir = cwd() ; 

$/ = "\n" ; 

#### command line input for login and path_file arguments
GetOptions ( "login=s"      => \$sLogin,
	     "db=s"         => \$sDB,
             "from=s"       => \$sFromFile,
             "to=s"         => \$sToFile,
             "acPrefix=s"   => \$sPrefix
           ); 

##### message to user if wrong parameters
die "\nUSAGE: perl $0 -login <username/password> -db <DB name> -from </path/filename> 
-to </path/filename.sql> -acPrefix <Letter(s)> \n\n" 
unless ( $sLogin && $sDB && $sFromFile && $sToFile && $sPrefix ) ;

my $sLonginDB = $sLogin . "\@" . $sDB ;

##### message to user if wrong parameters
my $dbh  = DBI -> connect ('dbi:Oracle:', $sLonginDB, '', { RaiseError=>1, 
PrintError=>1, AutoCommit=>0 } ) || die "Cannot connect to $sDB,$DBI::errstr"; 

#check validity of the prefix #############
$sPrefix  = uc $sPrefix ; # all to upper case

my $sPrefxCK = $dbh-> prepare ("select shortLabel, AC from Institution") ;  
$sPrefxCK -> execute() || die "$dbh->errstr" ;

my %hInst ;
while ( my ($sLabel, $sAC)  = $sPrefxCK ->fetchrow_array) {
    $hInst{ $sLabel } = $sAC ; 
}  

if ( ! exists $hInst{$sPrefix} ) {
    print "\nThe -acPrefix value $sPrefix is invalid:\n\n" ;
    listPrefix() ;
    exit ;
}

#get the AC of roles (bait, prey) in the ControlledVocab table.
my $sVocab = $dbh-> prepare("select shortLabel, ac from controlledVocab") ;
$sVocab -> execute() ;

my %hRole ;
while (my ($sVocab, $sAC) = $sVocab -> fetchrow_array) {
    $hRole{ $sVocab } = $sAC ;
} 


##the OUT file handle is 'append' not 'writeover', must rm old file. 
system("rm -f $sToFile") ; 

#### if user requirement was right ########
open (IN, "$sFromFile") || die "Cannot open $sFromFile, $!";
open(OUT, ">>$sToFile") || die "coundn't write to $sToFile. $!" ;

makeScriptHeader() ;
 

my $nSequence = getSeq() + 1 ;

while ( <IN> ) {
    my $sLine = $_ ;
    my ($sInt_Name) = $sLine =~ /^(\d+)\s+/ ; 
    my @aMolecule = $sLine =~ /([A-Za_z]\d{4,})/g ;
    @aMolecule = () if scalar@aMolecule < 2; 

    my ($sIntAC, $sLastActionAC, $sCompntAC, $sRole, $sLastActorAC) = qw(. . . . .) ;
    my $sInsert_i = "Insert into Interactor (shortLabel, AC, Owner_ac)" ;
    my $sInsert_c = "Insert into Component (interaction_ac, Interactor_ac, Role, AC, Owner_ac)" ;    

    #insert InteracTION
    if ( ! exists $hAction{ $sInt_Name } ) {
	$sIntAC    = $sPrefix ."-". $nSequence ;
	my $sValue_i  = "('$sInt_Name', '$sIntAC', '$hInst{ $sPrefix }')" ;
	print OUT $sInsert_i, "\n\t values", $sValue_i, ";\n";
	$sLastActionAC = $sIntAC ; 
	$hAction{ $sInt_Name } = $sInt_Name ;
	$nSequence++ ;
    }

    #insert InteracTOR
    my $nMolCount ;
    foreach my $sMol (@aMolecule) {
	$nMolCount++ ;
	$sRole = $hRole{ 'bait' } if $nMolCount == 1 ;
	$sRole = $hRole{ 'prey' } if $nMolCount > 1 ;
 
	if (! exists $hActor {$sMol} ) {

	    #insert molecule to INTERACTOR
	    $sIntAC    = $sPrefix ."-". $nSequence ;
	    my $sValue_i = "('$sMol', '$sIntAC', '$hInst{ $sPrefix }')" ;
	    print OUT $sInsert_i, "\n\t values", $sValue_i, ";\n";
	    $sLastActorAC = $sIntAC ; 
	    $hActor{ $sMol } = $sIntAC ;
	    $nSequence++ ;

	    #insert into COMPONENT
	    $sCompntAC = $sPrefix ."-". $nSequence ;
	    my $sValue_c  = "('$sLastActionAC', '$sLastActorAC', '$sRole', '$sCompntAC', '$hInst{ $sPrefix }')" ;
	    print OUT $sInsert_c, "\n\t values", $sValue_c, ";\n";
	    $nSequence++ ;
	}
	else { # molecule already in the Intercator table
            # insert into COMPONENT
	     $sCompntAC = $sPrefix ."-". $nSequence ;
	    my $sValue_c  = "('$sLastActionAC', '$hActor{$sMol}', '$sRole', '$sCompntAC', '$hInst{ $sPrefix }')" ;
	    #$sLastActorAC = $hActor{ $sMol } ;
	    print OUT $sInsert_c, "\n\t values", $sValue_c, ";\n";
	    $nSequence++ ;	    
	}
    }
} #while (<IN>)
print OUT "\n\ncommit;\n";

close (IN);
close (OUT) ;

####### functions #############

sub makeScriptHeader {

my $sHeader = qq{
SET DOC OFF
/*****************************************************************************
Description: This script will populate 2 sql tables INTERACTOR and COMPONENT 

Usage:       sqlplus username/password \@$sToFile (file only, not dir needed
             i.e. sqlplus username/password \@file.sql)

Requiment:   Before runing this program, you must run 
		 drop_tables.sql, 
		 create_tables.sql
             and create_dummy.sql

Program generating this file: $sDir/$0 
*****************************************************************************/
} ;

return print OUT $sHeader, "\n\n\n" ;

}


sub getSeq {

#  my $nAC_sql = qq{select Intact_ac.nextval from DUAL};
    my $nAC_sql = qq{select max(substr(ac, instr(ac, '-',1,1) + 1, 10)) from experiment};
    my $nAC = $dbh -> prepare ( $nAC_sql ) || die "$dbh->errstr"; 
    $nAC ->execute() || die "Cannot get Sequence from Interaction table, $dbh->errstr"; 
    my $nAC_S = $nAC -> fetchrow_array ;
    return $nAC_S ;  
}

sub listPrefix {
    print "Please use one of following:\n" ;
    foreach my $sSLabel (keys %hInst ){
	print $sSLabel, "\n" ;
    }
    print "\n" ;
}
