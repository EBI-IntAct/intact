#!/usr/local/bin/perl -w

################################################################################
# Description: This program tests the process time needed for updating and 
#              deleting data with or without the auditing. 
# 
#              It has a 10 circle loop, the 1st circle Without auditing, 
#              the 2nd circle With auditing, 3rd Without auditing, ...
#              There is a pause (sleep) of 1.5 h between each circle.
#
# How to use: perl benchMark.pl -login <username/password>
#                               -db <db name>
#                               -path <the sql scripts path where drop_tables.sql ... are stored>
#                               -file <the path and filename of the ho/gavin data>
#
# Algorithm:
#              Foreach circle do below: {
#                 drop_tables.sql ;                
#                 create_tables.sql ;
#                 create_dummy.sql ;
#                 insertComplexData.pl (500 complexes) ;
#
#                 if (With Auditing) {
#                   makeAuditTables.pl ; #it generates 2 script below
#                   dropAuditTables.sql ;
#                   makeAuditTables.sql ;
#                 }
#                
#                 connect to DB ;
#                 getStartTime ; #pseudo
#                 updateXref() ;       (2 columns of 1533 rows)
#                 updateInteractor() ; (3 columns of 2033 rows)
#                 updateComponent() ;  (1 columns of 4322 rows)
#                 deleteXref() ;       (1533 rows)
#                 deleteComponent() ;  (4322 rows)
#                 getEndTime ; #pseudo
#
#                 TimeUsed = EndTime - StartTime ;
#                 print 'TimeUsed' to a text file ;
#              }  
# See the result: from benchMark.txt at the same path of this file.
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
use vars qw( $sLOGIN $sDB $sPath $sFile $dbh ) ;
use Getopt::Long ;
use Time::Local ;

GetOptions ( "login=s"  => \$sLOGIN,
             "db=s"     => \$sDB,
             "path=s"   => \$sPath, 
             "file=s"   => \$sFile
           ) ; 

die "\nUSAGE: $0 -login <username/password> -db <DB name> -path <path of sql scripts> -file <path and file_name of mass_spec_nature.txt> \n" 
unless ( $sLOGIN && $sDB && $sPath && $sFile );
 
my $sLogOn = my $sLogin = $sLOGIN . "\@" . $sDB ;
$sLogin =~ s/\$/\\\$/o ;
$sLOGIN =~ s/\$/\\\$/o ;

my $nComplexSize = 500 ;

open (OUT, ">>benchMark.txt~") || die "$!" ;

print OUT "The benchmarking tests the processing time needed without and with Auditing process:\n" ;
print OUT "Two events are audited: first Update several columns in the following tables and \nthen 'Delete' these rows or null these cells if 'delete' is not allowed:\n\n" ;
print OUT "Xref", "\n" ;
print OUT "Interactor", "\n" ;
print OUT "Component", "\n\n" ;
print OUT "Total $nComplexSize complexes", "\n\n" ;

#a test of 10 circles, each has update and delete
for (my $nCircle = 0; $nCircle < 10; $nCircle++ ) {

  #########has to be connected every time, becasue of 'idle time limit'
  $dbh  = DBI -> connect ('dbi:Oracle:', $sLogOn, '', { RaiseError=>1, 
         PrintError=>1, AutoCommit=>1 } ) || die "Cannot connect to $sDB, $DBI::errstr" ; 
  #########

  toOriginalDB() ;

  if ($nCircle == 0) {
    print OUT "The number of rows to be audited:\n\n" ;
    print OUT "Update:\n" ;
    print OUT totalRowNumXref(), " rows in the Xref table", "\n" ;
    print OUT totalRowNumInteractor(), " rows in the Interactor table", "\n" ;
    print OUT totalRowNumComponent(), " rows in the Component table", "\n\n" ;

    print OUT "then Delete:\n" ;
    print OUT "All rows updated above in the Xref table", "\n" ;
    print OUT "All rows updated above in the Component table", "\n\n" ;
  }

  print OUT "Started at: " , scalar gmtime(), "\n" ;
  
  startAudit() if ($nCircle % 2 == 1) ;

  my $nStart = time() ;

  updateXref() ;
  updateInteractor() ;
  updateComponent() ;
  
  deleteXref() ;
  deleteComponent() ;

  my $nEnd = time() ; 

  print OUT "With auditing: ", $nEnd - $nStart, " seconds\n\n\n" if ($nCircle % 2 == 1) ;
  print OUT "Without auditing: ", $nEnd - $nStart, " seconds\n\n\n" if ($nCircle % 2 == 0) ;

  $dbh -> disconnect ;
  sleep(5400) ; 
  # pause for 1.5 h between each circle, if intervel too short, 
  #'Delete' causes 'System busy' error 
} #for (my $nCircle) 

close(OUT) ;

########################## 
sub updateXref {
  my $sSTM = "update XRef set secondaryID = 'hello12345', DBrelease = 
                'hello000' 
                where secondaryID is null and DBrelease is null" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

sub updateInteractor {
  my $sSTM = "update Interactor set KD = 12.34567890, CRC64 = 'CRC64 is here',
                fullname = 'protein and DNA RNA Carbohydrates' 
                where KD is null and CRC64 is null and 
                FormOf is null and fullname is null" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

sub updateComponent {
  my $sSTM = "update Component set stoichiometry = 123.4
                where Stoichiometry is null" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

sub deleteXref {
  my $sSTM = "delete from XRef where secondaryID = 'hello12345' and DBrelease = 
                'hello000'" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

sub deleteComponent {
  my $sSTM = "delete from Component where stoichiometry = 123.4" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

#cannot detele rows from this table, because of they are referenced by other tables. 
sub deleteInteractor { #UPDATE really
  my $sSTM = "update Interactor set CRC64 = '' where CRC64 = 'CRC64 is here'" ;
  my $sUpDate = $dbh -> do($sSTM) || die "$dbh->errstr" ;
}

sub totalRowNumXref {
  my $sSTM = "select count(*) from Xref where secondaryID 
              is null and DBrelease is null" ;
  my $sSel = $dbh -> prepare($sSTM) ;
  $sSel -> execute() || die "$dbh->errstr" ;
  return my $nNum = $sSel -> fetchrow_array ;
}

sub totalRowNumInteractor {
  my $sSTM = "select count(*) from Interactor where KD is null 
              and CRC64 is null and 
              FormOf is null and fullname is null" ;
  my $sSel = $dbh -> prepare($sSTM) ;
  $sSel -> execute() || die "$dbh->errstr" ;
  return my $nNum = $sSel -> fetchrow_array ;
}

sub totalRowNumComponent {
  my $sSTM = "select count(*) from Component where ExpressedIN_ac is 
              null and stoichiometry is null" ;
  my $sSel = $dbh -> prepare($sSTM) ;
  $sSel -> execute() || die "$dbh->errstr" ;
  return my $nNum = $sSel -> fetchrow_array ;
}

#it drops and creates all tables in IntactCore, then inserts dummy data and life data to some tables.
sub toOriginalDB {
  system("sqlplus $sLogin \@$sPath/drop_tables.sql" ) ;
  system("sqlplus $sLogin \@$sPath/create_tables.sql" ) ;
  system("sqlplus $sLogin \@$sPath/create_dummy.sql" ) ;
  system("perl insertComplexData.pl -login $sLOGIN -db $sDB -file $sFile -acPrefix EBI -loadRecords $nComplexSize") ;
}

sub startAudit {
  system("perl makeAuditTables.pl -login $sLOGIN -db $sDB -dest $sPath/makeAuditTables.sql") ;
  system("sqlplus $sLogin \@$sPath/dropAuditTables.sql" ) ;
  system("sqlplus $sLogin \@$sPath/makeAuditTables.sql" ) ;
}
