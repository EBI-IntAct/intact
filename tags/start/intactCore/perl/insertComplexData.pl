#!/usr/local/bin/perl -w


###############################################################################
# Description: This program populates 2 SQL tables INTERACTOR and COMPONENT.
#              All data are compiled from 2 papers:
#              PMID: 11805837; PMID: 11805826.
#
# Usage: insertComplexData.pl -login <username/password>
#                             -file </path/filename>
#                             -db <DB_name>
#                             -acPrefix <string>
#                             [ -loadRecords <int> ] # Number of interactions
#                                                    # to load
#                             [ -count <int> ]       # Give progress report
#                                                    # every <int> records
#
#         The -file is the textfile from which data will be parsed and loaded
#             to Oracle db.
#
#         The -acPrefix is the string prefixed to the sequence number
#         to form an AC in a format of
#         ABC-12345, the 'ABC' is the -acPrefix parameter.
#
# Algorithm: #Check comand line parameters, if false warn the user.
#            #If the ac Prefix is not in the Institution table (shortlabel),
#             warn the user.
#            #Load required controlled vocabularies
#            #Process each line of the textfile by:
#                #get Interaction complex number.
#                #get all interactor names.
#                #if the interaction_number not already in
#                   #then insert to interactor table,
#                   #else do next line of textfile.
#                #for each molecule, if not in interactor table,
#                   #insert into Interactor table.
#                   #and insert into Component table.
#                else insert into Component table.
#
# $Source$
# $Version: $
# $Date$
# $Author$
# $Locker$
#
###############################################################################

use strict ;
use SWISS::CRC64 ;
use DBI ;
use DBD::Oracle ;
use vars qw( $sLogin $sFile $sDB $sPrefix $iLoadRecords $iCount) ;
use Getopt::Long ;

$/ = "\n" ;

#### command line input for login and path_file arguments
GetOptions ( "login=s"          => \$sLogin,
             "file=s"           => \$sFile,
             "db=s"             => \$sDB,
             "acPrefix=s"       => \$sPrefix,
	     "loadRecords=i"    => \$iLoadRecords,
	     "count=i"          => \$iCount,

           );

##### message to user if wrong parameters
die "\nUSAGE: perl $0 -login <username/password> -file </path/filename>
-db <DB_name> -acPrefix <Letter(s)> [ -loadRecords <int> ] [ -count <int> ] \n\n"
unless ( $sLogin && $sFile && $sDB && $sPrefix ) ;

my $sLonginDB = $sLogin . "\@" . $sDB ;

my $dbh  = DBI -> connect ('dbi:Oracle:', $sLonginDB, '', { RaiseError=>1,
PrintError=>1, AutoCommit=>0 } ) || die "Cannot connect to $sDB,$DBI::errstr";

#check validity of the prefix #############
$sPrefix  = uc $sPrefix ; # all to upper case

# * Prepare statements

# Prepare statement for insertion of Int2Exp rows
my $int2ExpSth = $dbh->prepare("insert into Int2Exp(interaction_ac,
                                                    experiment_ac)
                                values(?,?)")
                 || die $dbh->errstr();

# Prepare insertion of Interactions
my $interaction_sth = $dbh->prepare("INSERT INTO Interactor
                                            ( AC, shortLabel, owner_ac, objclass )
                                     VALUES (concat('$sPrefix-', Intact_ac.nextval),
                                             ?,?,
                                             'uk.ac.ebi.intact.model.Interaction')"
				    );

# Prepare statement for insertion of Proteins
my $sIns_i = $dbh -> prepare ( "INSERT INTO Interactor
                                ( AC, shortLabel, owner_ac, objclass )
                                VALUES
                                ( concat('$sPrefix-', Intact_ac.nextval),
                                  ?,?,
                                  'uk.ac.ebi.intact.model.Protein')"
                              );

# Prepare statement for insertion of Xrefs
my $xref_stm = $dbh->prepare ("insert into Xref
                               (ac, parent_ac,
                                primaryid,
                                database_ac,
                                owner_ac) values
                               (concat('$sPrefix-', Intact_ac.nextval),?,?,?,?)");

# * Load controlled vocabularies

# CvComponentRole
my $sthCv = $dbh-> prepare("select shortLabel, ac from controlledVocab
                              where ObjClass='uk.ac.ebi.intact.model.CvComponentRole'") ;
$sthCv -> execute() ;

my %hRole ;
while (my ($sVocab, $sAC) = $sthCv -> fetchrow_array) {
    $hRole{ $sVocab } = $sAC ;
}

# CvDatabase
$sthCv = $dbh-> prepare("select shortLabel, ac from controlledVocab
                              where ObjClass='uk.ac.ebi.intact.model.CvDatabase'") ;
$sthCv -> execute() ;

my %hDatabase ;
while (my ($sVocab, $sAC) = $sthCv -> fetchrow_array) {
    $hDatabase{ $sVocab } = $sAC ;
}

# CvExperiment
$sthCv
    = $dbh->prepare("select shortLabel, ac from EXPERIMENT")
    || die "Error retrieving Experiment ACs: \n"
    . $dbh->errstr();

$sthCv ->execute() || die $dbh->errstr();

my %hExperiment;
while (my ($sShortLabel, $sAC) = $sthCv -> fetchrow_array) {
    $hExperiment{ $sShortLabel } = $sAC ;
}

# CvInstitution
$sthCv = $dbh-> prepare ("select shortLabel, AC from Institution");

$sthCv -> execute() || die "$dbh->errstr" ;

my %hInst;
while ( my ($sLabel, $sAC)  = $sthCv ->fetchrow_array) {
    $hInst{ $sLabel } = $sAC ;
}

# Check if the selected Institution exists
if ( ! exists $hInst{$sPrefix} ) {
    print "\nThe -acPrefix value $sPrefix is invalid:\n";
    listPrefix();
    exit ;
}

#### if user requirement was right ########
open (IN, "$sFile") || die "Cannot open $sFile, $!";

# If no limit for the number of records to load is given, load only 10
if (! $iLoadRecords) {
    $iLoadRecords = 10;
}

my $count = 0;

LINE:
my $line;
while (<IN>) {

    # Progress report
    if (($iCount) && ($count++ % $iCount == 0)) {
      print STDERR "Entry $count\n";
    }

    # End if maximum number of lines processed
    if ($count >= $iLoadRecords){
	last;
    }

    # Line format:
    # interactionNumber baitAc preyAcList experimentLabel
    my ($sInt_Name, @aMolecule) = split;
    my ($experimentLabel) = pop @aMolecule;

    my $sLast_Int_AC ;

    # Check experiment
    my $experimentAc;
    unless ( $experimentAc = $hExperiment{$experimentLabel}){
	die "Experiment label $experimentLabel undefined.";
    }

    # Insert interaction
    $sLast_Int_AC = insertInteraction($sInt_Name,
				      $experimentAc,
				      $sPrefix);

    # Insert molecules
    if ( @aMolecule ) {

	my $nCountMol ;
	MOL:
	    foreach my $sMol (@aMolecule) {
		$nCountMol++ ;
		my $sRole = $hRole{'bait'} if $nCountMol == 1 ;
		$sRole = $hRole{'prey'} if $nCountMol > 1 ;
		my $sLast_Mol_AC = is_molecule_in($sMol) ;

		if (! $sLast_Mol_AC ) {
		  $sLast_Mol_AC = insertMolecule($sMol, $sPrefix);

		  insert_into_Component($sLast_Int_AC,$sLast_Mol_AC,$sRole,$hInst{$sPrefix}) ;
		}
		else {
		  insert_into_Component($sLast_Int_AC,$sLast_Mol_AC,$sRole,$hInst{$sPrefix}) ;
		  next MOL ;
		}
	    }
	}

    else {
	next LINE ;
    }
$count++ ;
} #while (<IN>)


$dbh -> disconnect if $dbh ;

close (IN);


####### functions #############
sub is_intact_in {
    my $sShortLabel = shift ;
    my $sSel = $dbh -> prepare("select count(shortLabel) from interactor where
                                shortLabel = ?") ;
    $sSel -> execute($sShortLabel) ;
    my $nAns = $sSel -> fetchrow_array ;
    return $nAns ;
}

sub is_molecule_in {
    my $sMolName = shift ;
    my $sSel = $dbh -> prepare("select AC from interactor where shortLabel = ? ") ;
    $sSel -> execute($sMolName) ;
    my $sAns = $sSel -> fetchrow_array ;
    return $sAns ;
}

sub getSeq {

  my $nAC_sql = qq{select Intact_ac.currval from DUAL};
  my $nAC = $dbh -> prepare ( $nAC_sql ) || die "$dbh->errstr";
     $nAC ->execute() || die "Cannot get AC from Interaction table, $dbh->errstr";
  my $nAC_S = $nAC -> fetchrow_array ;
  return $nAC_S ;
}

sub insert_into_Component {

  my ( $sLast_Int_ac, $sLast_Mol_ac, $sRole, $sOwner_ac ) = @_ ;

  my $sStm_C = qq{insert into component (AC, interaction_ac, interactor_ac, role, owner_ac)
                 values ( concat('$sPrefix-', Intact_ac.nextval), ?,?,?,? )};
  my $sIns_c = $dbh -> prepare ( $sStm_C ) || die "$dbh->errstr" ;
  $sIns_c ->execute( $sLast_Int_ac, $sLast_Mol_ac, $sRole, $sOwner_ac ) || die "$dbh->errstr";
  $sIns_c = $dbh -> commit() ;
}

sub listPrefix {
    print "Please use one of following:\n" ;
    foreach my $sSLabel (keys %hInst ){
	print $sSLabel, "\n" ;
    }
    print "\n" ;
}

# Insert a new molecule, return it's AC.
sub insertMolecule{
  my ($name, $institutionPrefix) = @_;

  $sIns_i -> execute( $name, $hInst{$institutionPrefix}) || die "$dbh->errstr" ;
  my $moleculeAc = $sPrefix ."-". getSeq();

  # Attach xref to molecule
  if ($name =~ /[OPQ]\d+/){
      # SPTR AC
      $xref_stm -> execute($moleculeAc, $name,
			   $hDatabase{'SPTR'}, $hInst{$institutionPrefix})
	  || die "$dbh->errstr";
  }

  return $moleculeAc;
}

# Insert a new Interaction, return it's AC
sub insertInteraction{
    my ($interactionName, $experimentAc, $institutionPrefix) = @_;

    # Insert Interaction
    $interaction_sth -> execute($interactionName,
				$hInst{$institutionPrefix})
	|| die $dbh->errst();

    my $lastAc = $sPrefix ."-". getSeq();

    # insert Int2Exp row
    $int2ExpSth->execute($lastAc,
			 $experimentAc)
	|| die $dbh->errst();

    return $lastAc;
}
