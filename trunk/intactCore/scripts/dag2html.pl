#!/sw/arch/bin/perl -w
# *************************************************************
#
# Purpose: HTMLise GO def and dag files.
# 

# Usage: dag2html.pl -stems filename_stems_list
# 


use Getopt::Long;
use vars qw(@opt_stems);

&GetOptions("stems=s@"
	   );

for (my $i=0; $i <= $#opt_stems; $i++) {

  # prepare file names  
  $stemname=$opt_stems[$i];
  print "Processing $stemname\n";
  $dagfile=$stemname . ".dag";
  $deffile=$stemname . ".def";
  $htmldagfile=$stemname . ".dag.html";
  $htmldeffile=$stemname . ".def.html";

  # add NAMEs to the definition file
  # do some crude HTML formatting
  open(IN, $deffile);
  open(OUT, ">$htmldeffile");

  print OUT "<html><table>\n";

  while(<IN>){
    s/MI\:\d+/\<a name\=\"$&\"\>$&/;
    s/[^\:]+\:/<tr><td>$&<td>/;
    s/^\s*$/<tr><td colspan=2><hr><tr>/;
    print OUT $_;
  }

  print OUT "</table></html>";  
  close OUT;
  close IN;

  # exit if there is no DAG file
  if (! (-e $dagfile)) {
    next;
  }

  # add HREFs to the dag file
  open(IN, $dagfile);
  open(OUT, ">$htmldagfile");

  print OUT "<html><pre>\n";

  while(<IN>){
    s/MI\:\d+/\<a href\=\"$htmldeffile\#$&\"\>$&\<\/a\>/;
    print OUT $_;
  }

  print OUT "</pre></html>";  
  close OUT;
  close IN;

};  

