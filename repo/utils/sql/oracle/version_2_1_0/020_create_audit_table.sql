PROMPT Creating table "IA_COMPONENT_PARAMETER_audit"
create table IA_COMPONENT_PARAMETER_audit
	(
		 EVENT                              CHAR(1) NOT NULL
		,AC                                 VARCHAR2(30) NOT NULL
		,DEPRECATED                         NUMBER(1) NOT NULL
		,CREATED                            DATE NOT NULL
		,UPDATED                            DATE NOT NULL
		,USERSTAMP                          VARCHAR2(30) NOT NULL
		,COMPONENT_AC                       VARCHAR2(30) NULL
		,PARAMETERTYPE_AC                   VARCHAR2(30) NULL
		,PARAMETERUNIT_AC                   VARCHAR2(30) NULL
		,OWNER_AC                           VARCHAR2(30) NULL
		,EXPERIMENT_AC                      VARCHAR2(30) NULL
		,BASE                               INT NULL
		,EXPONENT                           INT NULL
		,UNCERTAINTY                        FLOAT NULL
		,FACTOR                             FLOAT NULL
		,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
TABLESPACE &&intactMainTablespace;


 
PROMPT Creating table "IA_INTERACTION_PARAMETER_audit"
create table IA_INTERACTION_PARAMETER_audit
	(
		  EVENT                              CHAR(1) NOT NULL
		 ,AC                                 VARCHAR2(30) NOT NULL
		 ,DEPRECATED                         NUMBER(1) NOT NULL
		 ,CREATED                            DATE NOT NULL
		 ,UPDATED                            DATE NOT NULL
		 ,USERSTAMP                          VARCHAR2(30) NOT NULL
		 ,INTERACTION_AC                     VARCHAR2(30) NULL
		 ,PARAMETERTYPE_AC                   VARCHAR2(30) NULL
		 ,PARAMETERUNIT_AC                   VARCHAR2(30) NULL
		 ,OWNER_AC                           VARCHAR2(30) NULL
		 ,EXPERIMENT_AC                      VARCHAR2(30) NULL
		 ,BASE                               INT NULL
		 ,EXPONENT                           INT NULL
		 ,UNCERTAINTY                        FLOAT NULL
		 ,FACTOR                             FLOAT NULL
		 ,CREATED_USER                       VARCHAR2(30) NOT NULL
	)
TABLESPACE &&intactMainTablespace;
