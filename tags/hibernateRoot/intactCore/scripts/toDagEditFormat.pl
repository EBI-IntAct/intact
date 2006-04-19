#!/sw/arch/bin/perl -w
# *************************************************************
#
# Purpose: Reformat separate GO formatted files released by IntAct into a single file
#          compatible to DAGedit
#

# Usage: toDagEditFormat.pl -sourceDagFile file [-sourceDagFile file] 
#                           -sourceDefFile file [-sourceDefFile file]
#                           -targetDagFile string 
#                           -targetDefFile string
#
# WARNING: This script assumes that concatenated def/dag files are in 'new' dag
# format (i.e., goid: is replaced with id:). This is OK as files are downloaded
# in 'new' format by default.
# - Sugath 04/02/2005.

use Getopt::Long;
use Data::Dumper;
use vars qw(@opt_sourceDagFile @opt_sourceDefFile $opt_targetDagFile $opt_targetDefFile);

&GetOptions("sourceDagFile=s@",
	    "sourceDefFile=s@",
            "targetDagFile=s",
            "targetDefFile=s"
	    );

# Valid GO line qualifiers 
my @goQualifiers = ("term:", "goid:", "definition:", "definition_reference:");
my $pseudoAC = 0;
my %pseudoAcAssignments = ();

# Maps: goid -> dag file
my %goidToDagFile = ();
# Maps: goid -> dag line
my %goidToDagLine = ();

# Forward declaration to avoid called too early to check prototype error.
sub leftTrim($);

# Create the target dag file
open (TARGETDAG, ">$opt_targetDagFile") || die "Could not open target dag file $opt_targetDagFile\n";

# Add this tag for the Dag editor to load this file properly.
print TARGETDAG "!type: \@is_a\@ is_a is a\n";
print TARGETDAG "\$intactCVs ; PSEUDOAC:$pseudoAC\n";
$pseudoAC++;

for (my $i=0; $i <= $#opt_sourceDagFile; $i++) {
    open (SOURCEDAG, $opt_sourceDagFile[$i]) || die "Could not open source dag file $opt_sourceDagFile[$i]\n";
    print STDERR "Processing $opt_sourceDagFile[$i]\n";

    while (<SOURCEDAG>){
        # Replace $ in each file with a 'is_a' relation.
        s/^\$/\@is_a\@/;

        if (/MI\:/){
            # Extract the goid from the current line.
            (my $goid) = /(MI\:\d+)/;

            # Have we processed this goid before?
            if ($goidToDagLine{$goid}) {
               # Extract the existing and new term for matching.
               (my $existingTerm) = $goidToDagLine{$goid} =~ /\@is_a\@([^:;\\]+)/;
               (my $newTerm) = /\@is_a\@([^:;\\]+)/;

               # Compare existing and new terms, flag it as a warning if they differ. Also
               # note that the line is written using the EXISTING dag line.
               if ($existingTerm ne $newTerm) {
                  print "$goid found in $goidToDagFile{$goid}. Ignoring the entry from file: $opt_sourceDagFile[$i]\n";

                  # Need to calculate the current spaces before printing.
                  (my $spaceSize) = /(^\s+)/;
                  print TARGETDAG " " . (" " x length $spaceSize) . $goidToDagLine{$goid};
               }
               else {
                  # Terms do match, print it as it is.
                  print TARGETDAG " " . $_;
               }
            }
            else {
               # Read a new go term. Save the source and the line in maps.
               $goidToDagFile{$goid} = $opt_sourceDagFile[$i];
               # Trim left spaces before storing it in the map.
               $goidToDagLine{$goid} = leftTrim($_);

               # No processing for the output
               print TARGETDAG " " . $_;
            }
        } else {
            chomp;
            print TARGETDAG " " . $_ . " \; PSEUDOAC\:$pseudoAC" . "\n";

            # Parse out the GO term
            (my $goTerm) = /[\%\$](.*)\s?\;?/;
            $pseudoAcAssignments{"term: " . $goTerm . "\n"} = "PSEUDOAC\:$pseudoAC";

            $pseudoAC++;
        }
    } 
}
close TARGETDAG;

# Create target def file
open (TARGETDEF, ">$opt_targetDefFile") || die "Could not open target def file $opt_targetDefFile\n";


for (my $i=0; $i <= $#opt_sourceDefFile; $i++) {
    open (SOURCEDEF, $opt_sourceDefFile[$i]) || die "Could not open source def file $opt_sourceDefFile[$i]\n";
    print STDERR "Processing $opt_sourceDefFile[$i]\n";
    
    my %entry = ();

    while (<SOURCEDEF>){
        # Read one entry into memory
        # ignore comments
        if (/^\!/){
            next;
        }

        # read content line
        unless (/^\s*\n*$/){
            my ($key) = /(\w+\:)/;
            push @{$entry{$key}}, $_;
            next;
        }

        # A blank line has been found, write modified entry

        # write term
        print TARGETDEF @{$entry{"term:"}}[0];

        # write goid
        if (! defined $entry{"id:"}){
            if (defined $pseudoAcAssignments{@{$entry{"term:"}}[0]}){
                $entry{"id:"}[0] = "id: " . $pseudoAcAssignments{@{$entry{"term:"}}[0]} . "\n";
            } else {
                $entry{"id:"}[0] = "id: PSEUDOAC:" . $pseudoAC . "\n";
                $pseudoAC++;
            }
        }
        print TARGETDEF $entry{"id:"}[0];
        delete $entry{"term:"};
        delete $entry{"id:"};

        #write definition
        if (defined $entry{"definition:"}){
            print TARGETDEF $entry{"definition:"}[0];
        }
        delete $entry{"definition:"};

        #write definition_reference
        if (defined $entry{"definition_reference:"}){
            foreach my $ref (@{$entry{"definition_reference:"}}) {
                print TARGETDEF $ref;
            }
        } else {
            print TARGETDEF  "definition_reference: PMID:-\n";
        }
        delete $entry{"definition_reference:"};


        # Print the non-GO lines as comments
        my $comment = "";
        foreach my $key (keys %entry){
            foreach my $keyterms (@{$entry{$key}}) {
                chomp $keyterms;
                if ($comment eq "") {
                    $comment .= $keyterms;
                }
                else {
                    $comment .= "  ||  ". $keyterms;
                }
            }
        }
        if ($comment){
            print TARGETDEF "comment: " . $comment . "\n";
        }

        print TARGETDEF "\n";

        # Reinitialise entry
        %entry = ();
    }
}
close TARGETDEF;

# -----------------------------------------------------------------------------

# Remove whitespace from the start a string
sub leftTrim($) {
	my $string = shift;
	$string =~ s/^\s+//;
	return $string;
}