All scripts must be executed from ROOT of Intact

#ant compile-core in ROOT
#ant compile in APPS/goDensity

#scripts/postgres/testfill.sh user db small

#import of go in intact
scripts/goDensity.sh InsertGoFromFlatfile data/component.ontology
scripts/goDensity.sh InsertGoFromFlatfile data/process.ontology
scripts/goDensity.sh InsertGoFromFlatfile data/function.ontology

#create tables for goDensity

#update proteins
scripts/javaRun.sh UpdateProteins file:data/yeast.sp 4932

#insert complex
scripts/javaRun.sh InsertComplexes data/ho_gavin_large.dat 4932

#import of go graph to goDensity tables
scripts/goDensity.sh SetupGoDensityTables

#generate binary interaction data
scripts/goDensity.sh CalcBinaryInteractions true true false

#precalculate densities of go-go-pairs
scripts/goDensity.sh PrecalcGoGoDensities data/goSlim.ontology