#!/sw/arch/bin/perl -w
# *************************************************************
#
# Package:    IntAct utilities
#
# Purpose:    Convert xls file for controlled vocabulary
#             into GO format.
#
# Usage:      progname.pl -tree treefile
#                         -dag dagfile 
#                         -def deffile < file.tab
#
#             file.tab is a tab-delimited file exported from Exel.
#             treefile is an auxiliary hierarchy file
#             dagfile is the file in which the raw DAG 
#                     will be stored.
#             deffile is the file in which the definitions 
#                     will be stored.
#             
# 
# $Source$
# $Date$
# $Author$
# $Locker$
#
# *************************************************************
    
# Imports
use vars qw($opt_dag $opt_def $opt_tree);
use strict;
use Getopt::Long;
use Data::Dumper;

# * Initialisation
GetOptions("dag=s", "def=s", "tree=s");

my @termDescription;
my $termCount = 1;
my %termLevel;
my (%termId, %casedTermId, %aliases);
open (TREE, "$opt_tree") || die "Could not open $opt_tree";
open (DAG, ">$opt_dag") || die "Could not open $opt_dag";
open (DEF, ">$opt_def") || die "Could not open $opt_def";


# * Load hierarchy file to get aliases

while (<TREE>) {
  chomp;
  my (@levels) = split /\t/;
  my $level = 0;
  my $term;
  while (@levels) {
    $term = shift @levels;
    $term =~ s/\A\s*//;
    $term =~ s/\s*\Z//;
    if ($term) {
      while (@levels) {
	my $alias = shift @levels;
	$alias =~ s/\A\s*//;
	$alias =~ s/\s*\Z//;
	if ($alias) {
	  push @{$aliases{uc $term}}, $alias;
	}
      }
    } 
  }
}

# ignore first line
my $dummy = <>;

# * Main
while (<>) {

  if ($_ =~ /\A\s*\n\Z/) {
    next;
  }

  my $special = chr(92);
  # cleanup special characters
  # TODO
  $_ =~ tr/\222\226/\'\-/;
  
  # Parse descriptions
  @termDescription = split "\t";
  
  # Drop leading and trailing whitespace
  $termDescription[0] =~ s/\A\s*//;
  $termDescription[0] =~ s/\s*\Z//;
  # Output descriptions in GO format
  print DEF "term: ", $termDescription[0], "\n";
  $termId{uc $termDescription[0]} = sprintf("IX:%04d", $termCount);
  $casedTermId{uc $termDescription[0]} = $termDescription[0];

  printf DEF "goid: IX:%04d\n", $termCount;
  
  # strip \"
  for (my $i = 1; $i < $#termDescription; $i++){
    $termDescription[$i] =~ s/\A\"// if defined  $termDescription[$i];
    $termDescription[$i] =~ s/\"\Z// if defined  $termDescription[$i];
  }
    print DEF "definition: ", $termDescription[1], "\n";
      
  # PMIDs
  # convert 'PMID:' to 'and'
  $termDescription[7] =~ s/PMID\:/and/g;
  my (@pmids) = split /\s*and\s*/, $termDescription[7];
  foreach my $pmid (@pmids) {
    # Drop leading and trailing whitespace
    $pmid =~ s/\A\s*//;
    $pmid =~ s/\s*\Z//;
	
    if ($pmid =~ /\A\d+\Z/) {
      print DEF "definition_reference: PMID:", $pmid, "\n";
    } elsif ($pmid eq "") {
      print DEF "definition_reference: IX:curators\n";
    } else {
      print DEF "definition_reference: ERROR: [$pmid]\n";
    }
  }

  # print comments
  my $comment = "comment: ";
  if ($termDescription[2]){
    $comment .= "role: " . $termDescription[2];
  }

  # print interactionTypes
  if ($termDescription[3]){
    $comment .= " interactionType: " . $termDescription[3];
  }

  # print enzymatic activity
  if ($termDescription[4]){
    $comment .= " enzymatic: " . $termDescription[4];
  }

  # print kinetic measurement
  if ($termDescription[5]){
    $comment .= " kinetic: " . $termDescription[5];
  }

  # print subcellular localisation
  if ($termDescription[6]){
    $comment .= " subcellular: " . $termDescription[6];
  }

  print DEF $comment, "\n";


  $termCount++;

  print DEF "\n";
    
}

# Determine DAG level
my $dagLevel = 0;
if (defined $termLevel{$termDescription[0]}) {
  $dagLevel = $termLevel{$termDescription[0]}
} else {
  $dagLevel = 1;
}

# * reload hierarchy file 

# Print ontology file header
print DAG "\$Interaction Methods ; IX:0000\n";

open (TREE, "$opt_tree") || die "Could not open $opt_tree";
while (<TREE>) {
  chomp;
  my (@levels) = split /\t/;
  my $level = 0;
  my $term;
  while (@levels) {
    $term = shift @levels;
    if ($term) {
      $term =~ s/\A\s*//;
      $term =~ s/\s*\Z//;
      last;
    } else {
      $level++;
    }
  }
  if ($term!~ /\S+/) {
    next;
  }
  
  # write to DAG file
  my $indentation = "";
  for (my $i = 0; $i<$level; $i++) {
    $indentation .= ' ';
  }
  $indentation .= "\%";
  if (defined $termId{uc $term}) {
    printf DAG "%s%s \; %s", $indentation, 
      $term, $termId{uc $term};
    delete $termId{uc $term};

    # print aliases
    if (defined $aliases{uc $term}) {
      foreach my $alias (@{$aliases{uc $term}}) {
	print DAG " ; synonym: ", $alias;
      }
    }  
    
    print DAG "\n";
    
  } else {
    printf "%s%s \; %s\n", $indentation, 
      $term;
  }
}

# write remaining terms to DAG file
foreach my $key (keys %termId) {
  print "\t", $casedTermId{$key}, "\n";
}
