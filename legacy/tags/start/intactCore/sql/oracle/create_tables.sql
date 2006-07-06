SET DOC OFF
/*************************************************************

  Package:    IntAct

  Purpose:    Create Oracle components for IntAct

  Usage:      sqlplus username/password @create_tables.sql


  $Date$

  $Locker$

  Requirements:

  *************************************************************/

/* Note:
   All storage clauses are to be multiplied by 1000 (K -> M) for the production version.
*/

-- Tables
PROMPT ... Institution
CREATE TABLE Institution
(
    shortLabel		VARCHAR2 (10),
    owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Institution$owner
						REFERENCES Institution(ac),
	fullName		VARCHAR2(50),
	postalAddress		VARCHAR2(2000),
	url			VARCHAR2(255),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Institution
						PRIMARY KEY USING INDEX,
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100 objects * 2500 Byte/Row. */
STORAGE (INITIAL 250K NEXT 250K) PARALLEL;
/* Don't increase this calculation from K to M! */

CREATE INDEX i_Institution$shortLabel on Institution(shortLabel);

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
PROMPT ... ControlledVocab
CREATE TABLE ControlledVocab
(
	objClass		VARCHAR2(255)	,
	shortLabel		VARCHAR2(10),
	fullName		VARCHAR2(70),
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_ControlledVocab$owner
						REFERENCES Institution(ac),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_ControlledVocab
						PRIMARY KEY USING INDEX,
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 10000 objects * 250 Byte/Row. */
STORAGE (INITIAL 3K NEXT 3K) PARALLEL;

CREATE INDEX i_ControlledVocab$shortLabel on ControlledVocab(shortLabel);

CREATE UNIQUE INDEX i_ControlledVocab$objClass
    ON ControlledVocab(objClass,shortLabel);

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


PROMPT creating table ...
PROMPT ... BioSource
CREATE TABLE BioSource
(
	taxId			NUMBER(10)
						CONSTRAINT uq_BioSource$taxId
						UNIQUE USING INDEX,
	scientificName		VARCHAR2(255),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_BioSource
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_BioSource$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 10000 objects * 380 Byte/Row. */
STORAGE (INITIAL 4K NEXT 4K) PARALLEL;

CREATE INDEX i_BioSource$scientificName on BioSource(scientificName);

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
PROMPT ... PolymerSeq
CREATE TABLE PolymerSeq
(
	polymerSeq		CLOB,
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_PolymerSeq
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_PolymerSeq$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 100 Byte/Row.
*/
STORAGE (INITIAL 10K NEXT 10K) PARALLEL
/* LOB storage calculation: 100000 objects * 400 Byte per sequence. */
LOB (polymerSeq) STORE AS (STORAGE (INITIAL 40K NEXT 40K));

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
PROMPT ... Interactor
CREATE TABLE Interactor
(
        /* Colums belonging to Interaction */
        kD                      NUMBER(10,8),
 	/* Colums belonging to Protein */
        crc64		        VARCHAR2(16),
	polymerSeq_ac		VARCHAR2(30)    CONSTRAINT fk_Interactor$polymerSeq
						REFERENCES PolymerSeq(ac),
        formOf                  VARCHAR2(30)    CONSTRAINT fk_Interactor$formOf
						REFERENCES Interactor(ac),
        proteinForm_ac          VARCHAR2(30)    CONSTRAINT fk_Interactor$proteinForm_ac
						REFERENCES ControlledVocab(ac),
	/* Colums belonging to Interactor */
	objClass		VARCHAR2(255)	,
	bioSource_ac		VARCHAR2(30)    CONSTRAINT fk_Interactor$bioSource
						REFERENCES BioSource(ac),
	interactionType_ac	VARCHAR2(30)	CONSTRAINT fk_Interactor$interactionType
						REFERENCES ControlledVocab(ac),
	/* Colums belonging to AnnotatedObject */
	shortLabel		VARCHAR2(10),
	fullName		VARCHAR2(50),
	/* Colums belonging to BasicObject */
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Interactor
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Interactor$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 200 Byte/Row.
*/
STORAGE (INITIAL 20K NEXT 20K) PARALLEL;

CREATE index i_Interactor$crc64 on Interactor(crc64);
CREATE index i_Interactor$bioSource_ac on Interactor(bioSource_ac);
CREATE index i_Interactor$shortLabel on Interactor(shortLabel);
CREATE index i_Interactor$fullName on Interactor(fullName);
CREATE index i_Interactor$formOf on Interactor(formOf);

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


PROMPT ... Component
CREATE TABLE Component
(
	stoichiometry		NUMBER(4,1),
	interactor_ac		VARCHAR2(30)
						CONSTRAINT fk_Component$interactor
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	interaction_ac		VARCHAR2(30)
						CONSTRAINT fk_Component$interaction
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	role			VARCHAR2(30)
						CONSTRAINT fk_Component$role
						REFERENCES ControlledVocab(ac),
        expressedIn_ac          VARCHAR2(30)    CONSTRAINT fk_Component$expressedIn
						REFERENCES BioSource(ac),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Component
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Component$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL ,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 200 Byte/Row.
*/
STORAGE (INITIAL 20K NEXT 20K) PARALLEL;

CREATE index i_Component$interaction_ac on Component(interaction_ac);
CREATE index i_Component$interactor_ac on Component(interactor_ac);

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


PROMPT ... Annotation
CREATE TABLE Annotation
(
	description		VARCHAR2(4000)	,
	topic_ac		VARCHAR2(30)
						CONSTRAINT fk_Annotation$topic
						REFERENCES ControlledVocab(ac),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Annotatation
						PRIMARY KEY
						USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Annotation$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 10000 objects * 4100 Byte/Row. */
STORAGE (INITIAL 40K NEXT 40K) PARALLEL;

CREATE index i_Annotation$topic on Annotation(topic_ac);

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


PROMPT ... Experiment
CREATE TABLE Experiment
(
	bioSource_ac		VARCHAR2(30)    CONSTRAINT fk_Experiment$bioSource
						REFERENCES BioSource(ac),
	detectMethod_ac		VARCHAR2(30)	CONSTRAINT fk_Experiment$detectMethod
						REFERENCES ControlledVocab(ac),
	identMethod_ac		VARCHAR2(30)	CONSTRAINT fk_Experiment$identMethod
						REFERENCES ControlledVocab(ac),
	relatedExperiment_ac    VARCHAR2(30)    CONSTRAINT fk_Experiment$relatedExp
						REFERENCES Experiment(ac),
	shortLabel		VARCHAR2(10),
	fullName		VARCHAR2(50),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Experiment
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Experiment$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 10000 objects * 200 Byte/Row. */
STORAGE (INITIAL 2K NEXT 2K) PARALLEL;

CREATE INDEX i_Experiment$shortLabel on Experiment(shortLabel);

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


PROMPT creating table ...
PROMPT ... Xref
CREATE TABLE Xref
(
	primaryId		VARCHAR2(30),
	secondaryId		VARCHAR2(30),
	dbRelease		VARCHAR2(10),
	qualifier_ac		VARCHAR2(30)	CONSTRAINT fk_Xref$qualifier
						REFERENCES ControlledVocab(ac),
	database_ac		VARCHAR2(30)
						CONSTRAINT fk_Xref$database
						REFERENCES ControlledVocab(ac),
	parent_ac		VARCHAR2(30),
	ac			VARCHAR2(30)	NOT NULL
						CONSTRAINT pk_Xref
						PRIMARY KEY USING INDEX,
	owner_ac		VARCHAR2(30)
						CONSTRAINT fk_Xref$owner
						REFERENCES Institution(ac),
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 100 Byte/Row. */
STORAGE (INITIAL 10K NEXT 10K) PARALLEL;

CREATE index i_Xref$parent_ac on Xref(parent_ac);

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


PROMPT ... Int2Exp
CREATE TABLE Int2Exp
(
	interaction_ac		VARCHAR2(30)
						CONSTRAINT fk_Int2Exp$interaction
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	experiment_ac		VARCHAR2(30)
						CONSTRAINT fk_Int2Exp$experiment
						REFERENCES Experiment(ac)
						ON DELETE CASCADE,
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 200 Byte/Row.
*/
STORAGE (INITIAL 20K NEXT 20K) PARALLEL;

CREATE index i_Int2Exp$int on Int2Exp(interaction_ac);
CREATE index i_Int2Exp$exp on Int2Exp(experiment_ac);

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


PROMPT ... Obj2Annot
CREATE TABLE Obj2Annot
(
	interactor_ac		VARCHAR2(30)	CONSTRAINT fk_Obj2Annot$interactor
						REFERENCES Interactor(ac)
						ON DELETE CASCADE,
	experiment_ac		VARCHAR2(30)	CONSTRAINT fk_Obj2Annot$experiment
						REFERENCES Experiment(ac)
						ON DELETE CASCADE,
	cvobject_ac		VARCHAR2(30)	CONSTRAINT fk_Obj2Annot$cvobject
						REFERENCES ControlledVocab(ac)
						ON DELETE CASCADE,
	biosource_ac		VARCHAR2(30)	CONSTRAINT fk_Obj2Annot$biosource
						REFERENCES Biosource(ac)
						ON DELETE CASCADE,
	annotation_ac		VARCHAR2(30)	NOT NULL
						CONSTRAINT fk_Obj2Annot$annotation
						REFERENCES Annotation(ac)
						ON DELETE CASCADE,
	created			DATE		DEFAULT  sysdate NOT NULL,
	updated			DATE		DEFAULT  sysdate NOT NULL,
	timestamp		DATE		DEFAULT  sysdate NOT NULL,
	userstamp		VARCHAR2(30)	DEFAULT	 USER	 NOT NULL  ,
	deprecated		SMALLINT	DEFAULT  0	 NOT NULL
)
/* Storage calculation: 100000 objects * 200 Byte/Row.
*/
STORAGE (INITIAL 20K NEXT 20K) PARALLEL;

CREATE index i_Obj2Annot$interactor on Obj2Annot(interactor_ac);
CREATE index i_Obj2Annot$experiment on Obj2Annot(experiment_ac);
CREATE index i_Obj2Annot$cvobject on Obj2Annot(cvobject_ac);
CREATE index i_Obj2Annot$annot on Obj2Annot(annotation_ac);

COMMENT ON TABLE Obj2Annot IS
'Obj2Annot. Link table from Annotation to subclasses of AnnotatedObject.';
COMMENT on COLUMN Obj2Annot.interactor_ac IS
'Refers to an Interactor to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.experiment_ac IS
'Refers to an Experiment to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.cvobject_ac IS
'Refers to a ControlledVocab object to which the Annotation is linked.';
COMMENT on COLUMN Obj2Annot.biosource_ac IS
'Refers to a BioSource object to which the Annotation is linked.';
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


/* The IntactNode table is the table which will be used for
storing informations about servers. */
PROMPT ... IntactNode
CREATE TABLE IntactNode
(
	owner_ac		    VARCHAR2(30)   	CONSTRAINT fk_IntactNode$owner
                            REFERENCES Institution(ac),
	ac			        VARCHAR2(30)	NOT NULL
                            CONSTRAINT pk_IntactNode
                            PRIMARY KEY USING INDEX,
	ftpAddress		    VARCHAR2(255),
    ftpLogin		    VARCHAR2(255),
	ftpPassword		    VARCHAR2(255),
    ftpDirectory	    VARCHAR2(255),
	lastCheckId		    NUMBER(5) 	DEFAULT  0 		NOT NULL,
	lastProvidedId		NUMBER(5)	DEFAULT  0 		NOT NULL,
	lastProvidedDate	Date		DEFAULT	 '01-JAN-1970' 	NOT NULL,
	rejected		    NUMBER(1)	DEFAULT  0 		NOT NULL,
	created			    DATE		DEFAULT  sysdate 	NOT NULL,
	updated			    DATE		DEFAULT  sysdate 	NOT NULL,
	timestamp		    DATE		DEFAULT  sysdate 	NOT NULL,
	userstamp		    VARCHAR2(30)	DEFAULT	 USER	 	NOT NULL,
	deprecated		    NUMBER(1)	DEFAULT  0	 	NOT NULL,
	ownerPrefix		    VARCHAR2(30)	DEFAULT  USER		NOT NULL
)
/* Storage calculation: 10000 objects * 250 Byte/Row. */
STORAGE (INITIAL 3K NEXT 3K) PARALLEL;




-- Sequences
PROMPT creating sequence ...
PROMPT ... Intact_ac
CREATE SEQUENCE Intact_ac start with 10;

exit;


