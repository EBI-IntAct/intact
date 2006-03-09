#!/sw/arch/bin/perl -w
# *************************************************************
#
# Purpose: Generate PSI MI XML files from prepared input file
#
# Usage:
# psiXmlDownload.pl -filePrefix prefix < sections_file > result_file
# *************************************************************

# ** Initialisation
use vars qw($opt_filePrefix);
use strict;
use Getopt::Long;

# Constant declarations

# Variable declarations


# Preparation
&GetOptions("filePrefix=s");

# Main
while (<>){
  chomp;
  my ($filename, $searchPattern) = split;
  print STDERR "Processing $_\n";

  my $result = `scripts/psiRun.sh FileGenerator $searchPattern $opt_filePrefix$filename`; 
  print $_, ": ", $?, "\n";
}



