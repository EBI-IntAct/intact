#!/usr/local/bin/perl -w

#####################################################################################
# Description: This program, from command line writes two sql scripts. One to be 
#              named by the user, will create row-level audit table(s) and trigger(s) 
#              if executed at SQLPlus.
#
#              Another scrip will drop all existing audit tables. User should run this 
#              script before running the first. The name of the script is defaulted as:
#              dropAuditTables.sql and in the same directory as the first script.
#
# Usage:       auditTable.pl -login <username/password> 
#                            -db <DB_name> 
#                            -dest <destination of the script, e.g. path/filename >
#                            -tablename <table1> [-tablename <table2> ...]
#                            -storagesizeFactor <real number -- 1.5 means the audit- 
#                             table's initial size is 1.5 times of that of master table.>
#              (the '-tablename' is optional, if null, All master tables in the 
#                  specified db will be audited.
#              (the '-storagesizeFactor' is optional, if null set default value 1)
#
# Note:        1) It's OK for: -tablename <'table1 table2 table3...'> in quotes.
#              2) storagesizeFactor decides the INITIAL size of audit-table, not the 
#              NEXT increment size which is defaulted to the same size as the 
#              INITIAL.
#
# Algorithm:   If command line arguments false, prompt USAGE: ...
#              Find out the data definition of the master table (column_name, 
#                    data_type, data_length, nullable).
#              Find out initial storage space size and increment size for the master 
#                     table.
#              Create audit table according to the data definition of master table 
#                     and storage size.
#              Create the trigger.
#
# Requirement:     
#
# $Source$
# $Date$
# $Author$
# $Locker$
#
#####################################################################################


use strict ;
use DBI ;
use DBD::Oracle ;
use vars qw( $sLogin $sDB @aTabNames $sDestination $nSizeFactor ) ;
use Getopt::Long ;

GetOptions ( "login=s"             => \$sLogin,
             "db=s"                => \$sDB,
             "tablename:s@"        => \@aTabNames,
	     "dest=s"              => \$sDestination,
             "storagesizeFactor:f" => \$nSizeFactor #optional REAL number arg, if null
           ) ;                                      #set to default 1

$nSizeFactor = 1 if ! $nSizeFactor ;
@aTabNames = split ( /\s+/, $aTabNames[0] ) if ( scalar @aTabNames == 1 ) ;  

die "\nUSAGE: $0 -login <username/password> -db <DB name> -dest <destination of output> [-tablename <'table1 table2 ...'>] [-storagesizeFactor <rel>]\n" unless ( $sLogin && $sDB && $sDestination );

die "Negative value means a decrease of initial storage size, impossible!
unless you re-create the master table." unless $nSizeFactor >= 0 ;

my ($sSchema) = $sLogin =~ /(.*?)\// ;
$sLogin = $sLogin . "\@" . $sDB ;

my $dbh  = DBI -> connect ('dbi:Oracle:', $sLogin, '', { RaiseError=>1, 
PrintError=>1, AutoCommit=>0 } ) || die "Cannot connect to $sDB, $DBI::errstr" ; 

my @aAllTablesInDB = getAllTableNames() ; 
my @aAuditTabs ;

#if no table name was given all table names will be taken from the DB.
my @aAuditTabsAll   = grep /_audit$/i, @aAllTablesInDB ;
my @aMasterTabNames = grep ! /_audit$/i, @aAllTablesInDB if scalar@aTabNames == 0 ;
@aMasterTabNames = @aTabNames if scalar@aTabNames > 0 ;

my @aAllTablesInfo ;

my ($sPath) = $sDestination =~ /(.*)\// ;

foreach my $sTableName ( @aMasterTabNames ) { 

  $sTableName = uc $sTableName ; 
  my %hHASH_t ;

  #find out the table definition of the master tables
  #we can also use built-in returns from DBI, which also give 'mutants' results, e.g.
  #VARCHAR2 returned as VARCHAR, NUMBER returned as DECIMAL, etc.

  my $STM = qq{select Column_name, Data_type, Data_length, Nullable from 
               All_TAB_COLUMNS
               where table_name = ? and owner = user order by Column_id};
  my $sTabQ = $dbh -> prepare ( $STM ) ;
  $sTabQ -> execute( $sTableName ) || die "$dbh->errstr" ;

  my @aColDetails ;  
  while (my ($sCol, $sType, $sLength, $sNullable)  = $sTabQ -> fetchrow_array ) {

    my %hHASH_c ;
    $hHASH_c{ 'column'}   = $sCol ;  

    if ( $sType eq "DATE" ||      #these data types don't have length spec
         $sType eq "DECIMAL" ||   #we cannot have "Created DATE(7)".
         $sType eq "FLOAT" || 
         $sType eq "DOUBLE" || 
         $sType eq "SMALLINT" ||  #this one returned as NUMBER(38) 
         $sType eq "INTEGER" ||   #and number(n) returned as NUMBER(22)
         $sType =~ /LOB/ ||
         $sType eq "BFILE" ||
         $sType eq "REF" ||
         $sType eq "TABLE" ||
         $sType =~ /RAW/ ||
         $sType eq "UNSIGNED" ||
         $sType =~ /LONG/         # e.g. LONG ROW/LONG/LONG RAW...      
       ) {
      $hHASH_c{ 'type' } = $sType ;
    } 
    else { 
      $hHASH_c{ 'type' } = $sType ."\(". $sLength ."\)" ; 
    }
 
    $hHASH_c{ 'nullable'} = "" if  $sNullable eq "Y";
    $hHASH_c{ 'nullable'} = "NOT NULL" if  $sNullable eq "N";
    push ( @aColDetails, \%hHASH_c ) ;
   
  } #eventually only 'column', 'type', and 'nullable' 3 hash keys are to be used. 

  $sTabQ -> finish() ;

  ######### find out table storage size and increment size.
  my $sSizeQ = $dbh -> prepare ("select Initial_extent, Next_extent from All_Tables
                                where table_name = ? and owner = user") ; 
  $sSizeQ -> execute ( $sTableName ) || die "$dbh->errstr" ;

  my ($nStartSize, $nIncrement)  = $sSizeQ -> fetchrow_array ; 

  $sSizeQ -> finish();
  
  my $sBadTable = uc $sSchema .".". $sTableName ;
  die "\nSorry, the table $sBadTable doesn't exist." unless ( $nStartSize ) ;

  $nStartSize  = $nStartSize / 1024 ;
  $nIncrement  = $nIncrement / 1024 ;
  my $sStorage = qq{STORAGE (INITIAL $nStartSize NEXT $nIncrement) PARALLEL}; 

  $hHASH_t{ 'auditTABname'} = $sTableName . "_Audit" ;
  $hHASH_t{ 'tablebody'}    = \@aColDetails ;
  $hHASH_t{ 'M_initSize'}   = $nStartSize ;  
  $hHASH_t{ 'nextSize'}     = $nIncrement ; #not used at the moment
  $hHASH_t{ 'num_of_col'}   = scalar @aColDetails ;

  push ( @aAllTablesInfo, \%hHASH_t ) ; # now info for all tables is in @aAllTablesInfo 
                                    # it's a 3D reference.  

  foreach my $sTable (@aAuditTabsAll) {
    push (@aAuditTabs, $sTable) if ($hHASH_t{ 'auditTABname'} =~ /$sTable/i ) ;  
  } 
}

###### write to the .sql file ############################################

#my $sFileName = "auditTable.sql" ;
open (OUT, ">$sDestination") || die "Cannot write $sDestination";

print OUT headerCreate( @aAuditTabs ) ;

foreach ( @aAllTablesInfo ) {
  print OUT "\n\nPROMPT creating table ", $_ ->{'auditTABname'}, " ...\n" ;
  print OUT "Create table ", $_ ->{'auditTABname'}, " \n(\n" ;

  my $sPseudoVAL ; #for trigger

  for (my $i = 0; $i < $_ ->{'num_of_col'}; $i++ ) {
    my $sEnd = "\n)" ;
    $sEnd = "\," if $i < ($_ ->{'num_of_col'} - 1 ) ;
    
    print OUT sprintf ("%s %-25s %-20s %-9s", "     ", 
                       $_ ->{'tablebody'} ->[$i] -> {'column'},
                       $_ ->{'tablebody'} ->[$i] -> {'type'}, 
                       $_ ->{'tablebody'} ->[$i] -> {'nullable'}, 
                      ), "$sEnd", "\n" ;

    $sPseudoVAL .= ":old." . $_ -> {'tablebody'} -> [$i] -> {'column'} . ", " ; 
    # above line for trigger, nothing to do with the audit-table
    # if not because of the $sPseudoVAL, this sec can be in a sub. 
  } ;

  my $nAuditTabSize = $_ ->{'M_initSize'} * $nSizeFactor ;
  my $nNextSize     = $nAuditTabSize ;

  print OUT "\/* Storage calculation: 
     INITIAL size = master-table-initial-storage-size * storagesizeFactor
     NEXT size = INITIAL size *\/\n" ;
  print OUT "STORAGE \(INITIAL ${nAuditTabSize}K NEXT ${nNextSize}K\) PARALLEL\;\n\n\n" ;

  $sPseudoVAL =~ s/,\s?$// ;
  print OUT createTrigger ( $_ ->{'auditTABname'}, $sPseudoVAL ) ;

  print OUT "\n------------------------------------------------------------\n\n" ; 
}

print OUT "\n\nexit\;" ;

close ( OUT ) ;

#### make a script which drop all audit_tables #######
open (DROP, ">$sPath/dropAuditTables.sql") || die "Cannot write $sDestination";
print DROP getDropHeader() ;

foreach (@aAuditTabs) {
  print DROP "\nPrompt Dropping autit table $_ ...\n" ;
  print DROP "Drop table $_ ;\n\n" ;
}

print DROP "exit;" ;

$dbh -> disconnect if $dbh ;



### functions / bulky blocks etc ###########################################

sub headerCreate {

my @aTableNames = @_ ;
my $sTabList ;
$sSchema = uc $sSchema .".". uc $sDB ;

foreach ( @aTableNames ) {  
  $sTabList .= "\t\t" . $_ . "\n" ;  
}

my $sHeader = qq{
SET DOC OFF
/*****************************************************************************
Description: This script creates row-level audit tables and relevant triggers
             for database $sSchema
             
Usage:       sqlplus username/password \@$sDestination (filename only, not dir)

Note:        The audit-table name and trigger name are identical.  

Requiment    The following audit-table(s) may already exist, drop them first.
             (they can be dropped by running dropAuditTables.sql)
 
$sTabList
*****************************************************************************/
} ;

return $sHeader ;

}

 
## the header for the script dropping the audit tables
sub getDropHeader {

my $sDrop = qq{
SET DOC OFF
/*****************************************************************************
Description: This program works together with script:\n 
             $sDestination\n 
             i.e. drop the existing audit tables before creating new ones.
             
Warning:     THINK AGAIN! the data in the audit tables are important!
Usage:       sqlplus username/password \@dropAuditTables
*****************************************************************************/
} ;

return $sDrop ;
}

sub createTrigger {

my $sTrigName  = $_[0] ;
my $ColVals    = $_[1] ;  
my ( $sTableName ) = $sTrigName =~ /(.*)_/ ;   

my $sScript = 
"PROMPT creating trigger $sTrigName ...
CREATE OR REPLACE trigger $sTrigName
AFTER delete or update
ON $sTableName
FOR EACH ROW
	BEGIN
	        INSERT INTO $sTrigName
	        VALUES ( $ColVals );
	END;
/";

return $sScript ;

}

sub getAllTableNames {

  my $sSel = $dbh -> prepare("select table_name from user_tables") ;
  $sSel -> execute() || die "$dbh->errstr" ;
  
  my @aTableNames ;
  while (my $sName = $sSel->fetchrow_array) {
    push (@aTableNames, $sName) ; 
  }
  return @aTableNames ;
}
