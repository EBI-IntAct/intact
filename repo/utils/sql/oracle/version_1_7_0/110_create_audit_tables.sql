
PROMPT Creating table "IA_IMEX_IMPORT_audit"
create table IA_IMEX_IMPORT_audit
	(
		 EVENT                              CHAR(1) NOT NULL
		,ID                                 VARCHAR2(50) NOT NULL
		,ORIGINAL_FILENAME                  VARCHAR2(200) NOT NULL
		,STATUS                             VARCHAR2(10) NOT NULL
		,PMID                               VARCHAR2(50) NOT NULL
		,MESSAGE                            VARCHAR2(4000) NULL
		,PROVIDER_AC                        VARCHAR2(30) NULL
		,CREATED                            DATE NOT NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
	)
tablespace &&intactIndexTablespace
/
