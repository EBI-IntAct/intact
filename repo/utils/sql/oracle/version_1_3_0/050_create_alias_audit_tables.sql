--
-- Create %ALIAS_AUDIT tables
--
 
PROMPT Creating table "IA_BIOSOURCE_ALIAS_AUDIT"
create table IA_BIOSOURCE_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_COMPONENT_ALIAS_AUDIT"
create table IA_COMPONENT_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_CONTROLLEDVOCAB_ALIAS_AUDIT"
create table IA_CONTROLLEDVOCAB_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_EXPERIMENT_ALIAS_AUDIT"
create table IA_EXPERIMENT_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_FEATURE_ALIAS_AUDIT"
create table IA_FEATURE_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_INTERACTOR_ALIAS_AUDIT"
create table IA_INTERACTOR_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/
 
PROMPT Creating table "IA_PUBLICATION_ALIAS_AUDIT"
create table IA_PUBLICATION_ALIAS_AUDIT
	(
		 EVENT                              CHAR(1) NOT NULL 
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,ALIASTYPE_AC                       VARCHAR2(30) NULL
		,PARENT_AC                          VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,NAME                               VARCHAR2(30) NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace;
/

