--
-- Update existing Xref audit: IA_XREF
-- 

ALTER TABLE IA_XREF_AUDIT ADD(PARENTCLASS  VARCHAR2(255)  NULL);

--
-- Create %_XREF_AUDIT tables
--


PROMPT Creating table "IA_BIOSOURCE_XREF_AUDIT"
create table IA_BIOSOURCE_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_COMPONENT_XREF_AUDIT"
create table IA_COMPONENT_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_CONTROLLEDVOCAB_XREF_AUDIT"
create table IA_CONTROLLEDVOCAB_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_EXPERIMENT_XREF_AUDIT"
create table IA_EXPERIMENT_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_FEATURE_XREF_AUDIT"
create table IA_FEATURE_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_INTERACTOR_XREF_AUDIT"
create table IA_INTERACTOR_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_PUBLICATION_XREF_AUDIT"
create table IA_PUBLICATION_XREF_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,QUALIFIER_AC                       VARCHAR2(30) NULL
		,DATABASE_AC                        VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,PRIMARYID                          VARCHAR2(30) NULL
		,SECONDARYID                        VARCHAR2(30) NULL
		,DBRELEASE                          VARCHAR2(10) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/

