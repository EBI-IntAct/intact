# Database schema 2.4.0 #

In order to make the representation of the database simpler we have chosen to split the diagram into many of smaller size. So first of all, we will see the major object types and their respective tables allowing to store their Annotations, Xrefs and Aliases.

Then we will see how these object types relate to each other.

Here is what we cover in this version:
  * [ControlledVocabulary](DatabaseSchemaV240#Controlled_Vocabulary.md)
  * [Publication](DatabaseSchemaV240#Publication.md)
  * [Experiment](DatabaseSchemaV240#Experiment.md)
  * [Generic Interactor](DatabaseSchemaV240#Generic_Interactor.md)
  * [Component](DatabaseSchemaV240#Component.md)
  * [BioSource](DatabaseSchemaV240#BioSource.md)
  * [Feature](DatabaseSchemaV240#Feature.md)
  * [Full data model](DatabaseSchemaV240#Full_data_model.md)
  * [User](DatabaseSchemaV240#User.md)
  * [Statistics Tables](DatabaseSchemaV240#Statistics_Tables.md)

#### Controlled Vocabulary ####

The column objclass (used by hibernate) allow to discriminate the CV type of a specific instance.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/ControlledVocab.png' />


#### Publication ####

This provide for representing a scientific publication to which we can attach experiments.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Publication.png' />


#### Experiment ####

This is the description of an experimental setting. One can define various detection methods (interactor, interaction and feature) through controlled vocabularies.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Experiment.png' />


#### Generic Interactor ####

Interactor is a generic type that have concrete implementation such as Interaction, Protein, NucleicAcid, Polymer... The column objclass (used by hibernate) and the interactorType\_ac (controlled vocabulary) allow to discriminate the interactor type of a specific instance.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Interactor.png' />


#### Component ####

This is the object that allows to specify which iteractor interacts in an interaction. The interactor can be annotated to specify experimental condition such as the role (bait, prey...), the organism the interactor was expressed in, binding domains...

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Component.png' />


#### BioSource ####

This is the instance of an organism. Optional cell type, tissue, cell compartment can be specified through controlled vocabulary.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/BioSource.png' />


#### Feature ####

This is the definition of an experimental feature such as a tag or a binding region.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Feature.png' />


#### Full data model ####

Here we show the main tables the IntAct data model is relying on. Note that all Xref, Annotation and Alias tables haven't been repeated for clarity purpose.

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Overview.png' />


#### User ####

This is the definition of the user management done within the IntAct database. Users can have 0..n roles and 0..n preferences (handy to store settings).

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/User.png' />

#### Statistics Tables ####

These tables are mostly used for production purposes (eg. webapp StatisticsView).

<img width='800' src='http://intact.googlecode.com/svn/wiki/images/database/2.4.0/Statistics.png' />