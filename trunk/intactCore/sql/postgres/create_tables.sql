/*************************************************************

  Package:    IntAct

  Purpose:    Create Postgres components for IntAct

  $Date$

  $Locker$

  Requirements:

  *************************************************************/

-- Tables
CREATE TABLE Institution
(
	shortLabel		VARCHAR(10),
	fullName		VARCHAR(50),
	postalAddress	VARCHAR(2000),
	url		    	VARCHAR(255),
	ac			    VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Institution
						PRIMARY KEY   ,
	created			DATE		DEFAULT  now() NOT NULL,
	updated         DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL
);

CREATE INDEX i_InstitutionShortLabel on Institution(shortLabel);

COMMENT ON TABLE Institution IS
'Institution, e.g. a university, company, etc.';
COMMENT ON COLUMN Institution.shortLabel IS
'A short string identifying the object, not necessarily unique. Could be e.g. a gene name. ';
COMMENT on COLUMN Institution.fullName IS
'The full name of the object.';
COMMENT on COLUMN Institution.postalAddress IS
'The postal address. Contains line breaks for formatting.';
COMMENT on COLUMN Institution.url IS
'The URL of the entry page of the institution.';
COMMENT on COLUMN Institution.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Institution.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Institution.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Institution.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Institution.userstamp IS
'Database user who has performed the last update of the column.';

/* The ControlledVocab table is the master table which will be used by
all controlled vocabularies. */
CREATE TABLE ControlledVocab
(
	objClass		VARCHAR(255)	,
	shortLabel		VARCHAR(20),
	fullName		VARCHAR(70),
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_ControlledVocab_owner
						REFERENCES Institution(ac),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_ControlledVocab
						PRIMARY KEY   ,
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL
);

CREATE INDEX i_ControlledVocab_shortLabel on ControlledVocab(shortLabel);
CREATE UNIQUE INDEX i_ControlledVocab_objClass on ControlledVocab(objClass,shortLabel);

COMMENT ON TABLE ControlledVocab IS
'Master table for all controlled vocabularies.';
COMMENT ON COLUMN ControlledVocab.objClass IS
'The fully qualified classname of the object. This is needed for the OR mapping.';
COMMENT on COLUMN ControlledVocab.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN ControlledVocab.ac IS
'Unique, auto-generated accession number.';
COMMENT ON COLUMN ControlledVocab.shortLabel IS
'A short version of the term. Used e.g. in selection lists. ';
COMMENT on COLUMN ControlledVocab.fullName IS
'The full, descriptive term. ';
COMMENT on COLUMN ControlledVocab.created IS
'Date of the creation of the row.';
COMMENT on COLUMN ControlledVocab.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN ControlledVocab.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN ControlledVocab.userstamp IS
'Database user who has performed the last update of the column.';

CREATE TABLE BioSource
(
	taxId			VARCHAR(10)
						CONSTRAINT uq_BioSource_taxId
						UNIQUE   ,
	scientificName 		VARCHAR(255)	,
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_BioSource
						PRIMARY KEY   ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_BioSource_owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL
);

CREATE INDEX i_BioSource_scientificName on BioSource(scientificName);

COMMENT ON TABLE BioSource IS
'BioSource, normally some kind of organism. ';
COMMENT on COLUMN BioSource.taxId IS
'The NCBI tax ID.';
COMMENT on COLUMN BioSource.scientificName IS
'The full scientific name of the biological source organism.';
COMMENT on COLUMN BioSource.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN BioSource.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN BioSource.created IS
'Date of the creation of the row.';
COMMENT on COLUMN BioSource.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN BioSource.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN BioSource.userstamp IS
'Database user who has performed the last update of the column.';


/* The PolymerSequence has been factured out from the table Interactor
as Clobs often need special handling. The sequence is expected to be
rarely needed, but the Interactor table is a heavily-queried table.
*/
CREATE TABLE PolymerSeq
(
	polymerSeq		VARCHAR(4000),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_PolymerSeq
						PRIMARY KEY,
	owner_ac		VARCHAR(30)    	NOT NULL
						CONSTRAINT fk_PolymerSeq_owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

COMMENT ON TABLE PolymerSeq IS
'PolymerSeq stores all biological sequence objects.';
COMMENT ON COLUMN PolymerSeq.polymerSeq IS
'The polymer sequence, usually DNA or amino acid.';
COMMENT on COLUMN PolymerSeq.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN PolymerSeq.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN PolymerSeq.created IS
'Date of the creation of the row.';
COMMENT on COLUMN PolymerSeq.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN PolymerSeq.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN PolymerSeq.userstamp IS
'Database user who has performed the last update of the column.';


/* This is the key table. The class Interactor is the parent class of
a class hierarchy which comprises all molecular objects. All
subclasses are mapped to this table. It's likely to be the largest and
most queried, hence most performance-critical table.
*/
CREATE TABLE Interactor
(
        /* Colums belonging to Interaction */
        kD                    	NUMERIC(10,8),
 	/* Colums belonging to Protein */
        crc64		        VARCHAR(16),
	polymerSeq_ac		VARCHAR(30)    CONSTRAINT fk_Interactor_polymerSeq
						REFERENCES PolymerSeq(ac),
        formOf                 	VARCHAR(30)    CONSTRAINT fk_Interactor_formOf
						REFERENCES Interactor(ac),
        proteinForm_ac         	VARCHAR(30)    CONSTRAINT fk_Interactor_proteinForm_ac
						REFERENCES ControlledVocab(ac),
	/* Colums belonging to Interactor */
	objClass		VARCHAR(255)	,
	bioSource_ac		VARCHAR(30)    CONSTRAINT fk_Interactor_bioSource
						REFERENCES BioSource(ac),
	interactionType_ac	VARCHAR(30)	CONSTRAINT fk_Interactor_interactionType
						REFERENCES ControlledVocab(ac),
	/* Colums belonging to AnnotatedObject */
	shortLabel		VARCHAR(10),
	fullName		VARCHAR(50),
	/* Colums belonging to BasicObject */
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Interactor
						PRIMARY KEY   ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_Interactor_owner
						REFERENCES Institution(ac),
	created		 	DATE		DEFAULT  now() NOT NULL,
	updated		 	DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

CREATE index i_Interactor_crc64 on Interactor(crc64);
CREATE index i_Interactor_bioSource_ac on Interactor(bioSource_ac);
CREATE index i_Interactor_shortLabel on Interactor(shortLabel);
CREATE index i_Interactor_fullName on Interactor(fullName);
CREATE index i_Interactor_formOf on Interactor(formOf);

COMMENT ON TABLE Interactor IS
'Interactor. Key table. All subclasses of Interactor are mapped to it, too.';
COMMENT ON COLUMN Interactor.kD IS
'Dissociation constant of an Interaction';
COMMENT ON COLUMN Interactor.crc64 IS
'CRC64 checksum of the polymerSequence. Stored in hexadecimal, not integer format.';
COMMENT ON COLUMN Interactor.polymerSeq_ac IS
'Refers to the polymer sequence, usually DNA or amino acid.';
COMMENT ON COLUMN Interactor.formOf IS
'References another Protein which the current one is a form of. Example: A fragment.';
COMMENT ON COLUMN Interactor.objClass IS
'The fully qualified classname of the object. This is needed for the OR mapping.';
COMMENT ON COLUMN Interactor.bioSource_ac IS
'The biological system in which the Interactor is found.';
COMMENT ON COLUMN Interactor.interactionType_ac IS
'The kind of interaction, e.g. covalent binding.';
COMMENT ON COLUMN Interactor.shortLabel IS
'A short string identifying the object, not necessarily unique. Could be e.g. a gene name. Used e.g. in selection lists. ';
COMMENT on COLUMN Interactor.fullName IS
'The full name of the object. ';
COMMENT on COLUMN Interactor.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN Interactor.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Interactor.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Interactor.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Interactor.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Interactor.userstamp IS
'Database user who has performed the last update of the column.';


CREATE TABLE Component
	(
	stoichiometry		REAL,
	interactor_ac		VARCHAR(30)
						CONSTRAINT fk_Component_interactor
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	interaction_ac		VARCHAR(30)
						CONSTRAINT fk_Component_interaction
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	role			VARCHAR(30)
						CONSTRAINT fk_Component_role
						REFERENCES ControlledVocab(ac),
        expressedIn_ac        	VARCHAR(30)    CONSTRAINT fk_Component_expressedIn
						REFERENCES BioSource(ac),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Component
						PRIMARY KEY   ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_Component_owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

CREATE index i_Component_interaction_ac on Component(interaction_ac);
CREATE index i_Component_interactor_ac on Component(interactor_ac);

COMMENT ON TABLE Component IS
'Component. Link table from Interaction to Interactor. A Component is a particular instance of an Interactor, which participates in an Interaction.';
COMMENT on COLUMN Component.stoichiometry IS
'Relative quantity of the Component participating in the Interaction.';
COMMENT on COLUMN Component.interactor_ac IS
'Refers to the Interactor which is participating in the Interaction.';
COMMENT on COLUMN Component.interaction_ac IS
'Refers to the Interaction in which the Interactor is participating.';
COMMENT on COLUMN Component.role IS
'The role of the Interactor in the Interaction. This is usually characterised by the experimental method. Examples: bait, prey in Yeast 2-hybrid experiments.';
COMMENT on COLUMN Component.expressedIn_ac IS
'The biological system in which the protein has been expressed.';
COMMENT on COLUMN Component.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN Component.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Component.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Component.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Component.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Component.userstamp IS
'Database user who has performed the last update of the column.';

CREATE TABLE Annotation
(
	description		VARCHAR(4000),
	topic_ac		VARCHAR(30)
						CONSTRAINT fk_Annotation_topic
						REFERENCES ControlledVocab(ac),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Annotatation
						PRIMARY KEY
						  ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_Annotation_owner
						REFERENCES Institution(ac),
	created		DATE		DEFAULT  now() NOT NULL,
	updated		DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

CREATE index i_Annotation_topic on Annotation(topic_ac);

COMMENT ON TABLE Annotation IS
'Contains the main biological annotation of the object.';
COMMENT on COLUMN Annotation.description IS
'The free text description of the annotation item.';
COMMENT on COLUMN Annotation.topic_ac IS
'Refers to the topic of the annotation item.';
COMMENT on COLUMN Annotation.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN Annotation.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Annotation.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Annotation.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Annotation.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Annotation.userstamp IS
'Database user who has performed the last update of the column.';

CREATE TABLE Experiment
(
	bioSource_ac		VARCHAR(30)     CONSTRAINT fk_Experiment_bioSource
						REFERENCES BioSource(ac),
	detectMethod_ac		VARCHAR(30)	CONSTRAINT fk_Experiment_detectMethod
						REFERENCES ControlledVocab(ac),
	identMethod_ac		VARCHAR(30)	CONSTRAINT fk_Experiment_identMethod
						REFERENCES ControlledVocab(ac),
	relatedExperiment_ac  	VARCHAR(30)     CONSTRAINT fk_Experiment_relatedExp
						REFERENCES Experiment(ac),
	shortLabel		VARCHAR(10)	,
	fullName		VARCHAR(50),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Experiment
						PRIMARY KEY   ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_Experiment_owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

CREATE INDEX i_Experiment_shortLabel on Experiment(shortLabel);

COMMENT ON TABLE Experiment IS
'Describes the experiment which has yielded information about Interactions';
COMMENT ON COLUMN Experiment.bioSource_ac IS
'The biological system in which the experiment has been performed.';
COMMENT on COLUMN Experiment.identMethod_ac IS
'Refers to the method by which the Interactor has been detected as a participant in the Interaction.';
COMMENT ON COLUMN Experiment.detectMethod_ac IS
'Refers to the method by which the interactions have been detected in the experiment.';
COMMENT ON COLUMN Experiment.detectMethod_ac IS
'Refers to the method by which the interactions have been detected in the experiment.';
COMMENT ON COLUMN Experiment.relatedExperiment_ac IS
'An experiment which is related to the current experiment. This serves just as a pointer, all information on the type of relationship will be given in the annotation of the experiment.';
COMMENT on COLUMN Experiment.fullName IS
'The full name of the object. ';
COMMENT on COLUMN Experiment.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN Experiment.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Experiment.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Experiment.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Experiment.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Experiment.userstamp IS
'Database user who has performed the last update of the column.';


CREATE TABLE Xref
(
	primaryId		VARCHAR(30)	,
	secondaryId		VARCHAR(30),
	dbRelease		VARCHAR(10),
	qualifier_ac		VARCHAR(30)	CONSTRAINT fk_Xref_qualifier
						REFERENCES ControlledVocab(ac),
	database_ac		VARCHAR(30)
						CONSTRAINT fk_Xref_database
						REFERENCES ControlledVocab(ac),
	parent_ac		VARCHAR(30)	,
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_Xref
						PRIMARY KEY   ,
	owner_ac		VARCHAR(30)
						CONSTRAINT fk_Xref_owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT 0 NOT NULL

);

CREATE index i_Xref_parent_ac on Xref(parent_ac);

COMMENT ON TABLE Xref IS
'Represents a crossreference. Several objects may have crossreferences, e.g. Interactor, Experiment. Therefore the column parent_ac can unfortunately not have a foreign key constraint.';
COMMENT on COLUMN Xref.primaryId IS
'The primary id of the object referred to in the external database.';
COMMENT on COLUMN Xref.secondaryId IS
'The secondary id of the object referred to in the external database.';
COMMENT on COLUMN Xref.dbRelease IS
'Highest release number of the external database in which the xref was known to be correct.';
COMMENT on COLUMN Xref.qualifier_ac IS
'Refers to an object qualifying the relationship between the object to which this crossreference belongs and the external object referred to. Example: identity, generalisation.';
COMMENT on COLUMN Xref.database_ac IS
'Refers to the object describing the external database.';
COMMENT on COLUMN Xref.parent_ac IS
'Refers to the parent object this crossreference belongs to.';
COMMENT on COLUMN Xref.owner_ac IS
'Refers to the owner of this object. ';
COMMENT on COLUMN Xref.ac IS
'Unique, auto-generated accession number.';
COMMENT on COLUMN Xref.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Xref.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Xref.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Xref.userstamp IS
'Database user who has performed the last update of the column.';


CREATE TABLE Int2Exp
(
	interaction_ac		VARCHAR(30)	,
	experiment_ac		VARCHAR(30)	,
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL
);
CREATE index i_Int2Exp_int on Int2Exp(interaction_ac);
CREATE index i_Int2Exp_exp on Int2Exp(experiment_ac);

COMMENT ON TABLE Int2Exp IS
'Link table from Interaction to Experiment.';
COMMENT on COLUMN Int2Exp.interaction_ac IS
'Refers to an Interation derived from an Experiment.';
COMMENT on COLUMN Int2Exp.experiment_ac IS
'Refers to an Experiment.';
COMMENT on COLUMN Int2Exp.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Int2Exp.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Int2Exp.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Int2Exp.userstamp IS
'Database user who has performed the last update of the column.';


CREATE TABLE Obj2Annot
(
	interactor_ac		VARCHAR(30)	CONSTRAINT fk_Obj2Annot_interactor
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	experiment_ac		VARCHAR(30)	CONSTRAINT fk_Obj2Annot_experiment
						REFERENCES Experiment(ac)
						ON DELETE CASCADE,
	cvobject_ac		VARCHAR(30)	CONSTRAINT fk_Obj2Annot_cvobject
						REFERENCES ControlledVocab(ac)
						ON DELETE CASCADE,
    biosource_ac            VARCHAR(30)     CONSTRAINT fk_Obj2Annot_biosource
                                                REFERENCES Biosource(ac)
                                                ON DELETE CASCADE,
	annotation_ac		VARCHAR(30)
						CONSTRAINT fk_Obj2Annot_annotation
						REFERENCES Annotation(ac)
						ON DELETE CASCADE,
	created			DATE		DEFAULT  now() NOT NULL,
	updated			DATE		DEFAULT  now() NOT NULL,
	timestamp 		DATE		DEFAULT  now() NOT NULL,
	userstamp		VARCHAR(30)	DEFAULT	 USER	 NOT NULL
);

CREATE index i_Obj2Annot_interactor on Obj2Annot(interactor_ac);
CREATE index i_Obj2Annot_experiment on Obj2Annot(experiment_ac);
CREATE index i_Obj2Annot_cvobject on Obj2Annot(cvobject_ac);
CREATE index i_Obj2Annot_annot on Obj2Annot(annotation_ac);

COMMENT ON TABLE Obj2Annot IS
'Obj2Annot. Link table from Annotation to subclasses of AnnotatedObject.';
COMMENT on COLUMN Obj2Annot.interactor_ac IS
'Refers to an Interactor to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.experiment_ac IS
'Refers to an Experiment to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.cvobject_ac IS
'Refers to a ControlledVocab object to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.annotation_ac IS
'Refers to the annotation object linked to another object.';
COMMENT on COLUMN Obj2Annot.created IS
'Date of the creation of the row.';
COMMENT on COLUMN Obj2Annot.updated IS
'Date of the last update of the row.';
COMMENT on COLUMN Obj2Annot.timestamp IS
'Date of the last update of the column.';
COMMENT on COLUMN Obj2Annot.userstamp IS
'Database user who has performed the last update of the column.';

CREATE TABLE IntactNode
(
	owner_ac	VARCHAR(30)	CONSTRAINT fk_IntactNode_owner
					REFERENCES Institution(ac),
	ac			VARCHAR(30)	NOT NULL
						CONSTRAINT pk_IntactNode
						PRIMARY KEY   ,
	ftpAddress	VARCHAR(255),
	ftpLogin	VARCHAR(255),
	ftpPassword	VARCHAR(255),
	ftpDirectory	VARCHAR(255),
	lastCheckId	SMALLINT	DEFAULT 0	NOT NULL,
	lastProvidedId	SMALLINT	DEFAULT 0	NOT NULL,
	lastProvidedDate	DATE	DEFAULT '01-JAN-1970'	NOT NULL,
	rejected	NUMERIC(1)	DEFAULT 0	NOT NULL,
	created		DATE		DEFAULT now()	NOT NULL,
	updated		DATE		DEFAULT now()	NOT NULL,
	timestamp	DATE		DEFAULT now()	NOT NULL,
	userstamp	VARCHAR(30)	DEFAULT USER	NOT NULL,
	deprecated	SMALLINT	DEFAULT 0	NOT NULL,
	ownerPrefix	VARCHAR(30)	DEFAULT USER	NOT NULL
);

-- Sequences
CREATE SEQUENCE Intact_ac START 10;


