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


# Create the target dag file
open (TARGETDAG, ">$opt_targetDagFile") || die "Could not open target dag file $opt_targetDagFile\n";

print TARGETDAG "\$intactCVs ; PSEUDOAC:$pseudoAC\n";
$pseudoAC++;

for (my $i=0; $i <= $#opt_sourceDagFile; $i++) {
    open (SOURCEDAG, $opt_sourceDagFile[$i]) || die "Could not open source dag file $opt_sourceDagFile[$i]\n";
    print STDERR "Processing $opt_sourceDagFile[$i]\n";
    while (<SOURCEDAG>){
	s/^\$/\%/;
	if (/MI\:/){
	    print TARGETDAG " " . $_;
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
	if (! defined $entry{"goid:"}){
	    if (defined $pseudoAcAssignments{@{$entry{"term:"}}[0]}){
		$entry{"goid:"}[0] = "goid: " . $pseudoAcAssignments{@{$entry{"term:"}}[0]} . "\n";
	    } else {
		$entry{"goid:"}[0] = "goid: PSEUDOAC:" . $pseudoAC . "\n";
		$pseudoAC++;
	    }
	}
	print TARGETDEF $entry{"goid:"}[0];
	delete $entry{"term:"};
	delete $entry{"goid:"};

	#write definition
	if (defined $entry{"definition:"}){
	    print TARGETDEF $entry{"definition:"}[0];
	}
	delete $entry{"definition:"};
	
	#write definition_reference
	if (defined $entry{"definition_reference:"}){
	    foreach my $ref (@{$entry{"definition_reference:"}}){
		print TARGETDEF $ref;
	    }
	} else {
	    print TARGETDEF  "definition_reference: PMID:-\n";
	}
	delete $entry{"definition_reference:"};


	# Print the non-GO lines as comments
	my $comment = "";
	foreach my $key (keys %entry){
	    foreach my $keyterms (@{$entry{$key}}){
		chomp $keyterms;
		$comment .= $keyterms . "  ||  ";
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

