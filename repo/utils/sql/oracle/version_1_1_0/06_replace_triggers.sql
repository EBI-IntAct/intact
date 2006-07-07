SET serveroutput ON                                                             
spool cr_audit_triggers.SQL                                                     

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_XREF                                       
CREATE OR REPLACE TRIGGER trgAud_ia_xref                                        
	BEFORE UPDATE OR DELETE                                                        
	ON IA_XREF                                                                     
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_XREF_AUDIT                                                      
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, qualifier_ac                                                                
		, database_ac                                                                 
		, parent_ac                                                                   
		, owner_ac                                                                    
		, primaryid                                                                   
		, secondaryid                                                                 
		, dbrelease
		, created_user                                                                   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.qualifier_ac                                                           
		, :OLD.database_ac                                                            
		, :OLD.parent_ac                                                              
		, :OLD.owner_ac                                                               
		, :OLD.primaryid                                                              
		, :OLD.secondaryid                                                            
		, :OLD.dbrelease  
		, :OLD.created_user                                                            
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_SEQUENCE_CHUNK                             
CREATE OR REPLACE TRIGGER trgAud_ia_sequence_chunk                              
	BEFORE UPDATE OR DELETE                                                        
	ON IA_SEQUENCE_CHUNK                                                           
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER; 
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_SEQUENCE_CHUNK_AUDIT                                            
		( event                                                                       
		, ac                                                                          
		, userstamp                                                                   
		, parent_ac                                                                   
		, sequence_chunk                                                              
		, sequence_index   
		, created                                                                     
		, updated                                                            
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.userstamp                                                              
		, :OLD.parent_ac                                                              
		, :OLD.sequence_chunk                                                         
		, :OLD.sequence_index  
		, :OLD.created                                                                
		, :OLD.updated                                                         
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_INTERACTOR                                 
CREATE OR REPLACE TRIGGER trgAud_ia_interactor                                  
	BEFORE UPDATE OR DELETE                                                        
	ON IA_INTERACTOR                                                               
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_INTERACTOR_AUDIT                                                
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, kd                                                                          
		, crc64                                                                       
		, formof                                                                      
		, proteinform_ac                                                              
		, objclass                                                                    
		, biosource_ac                                                                
		, interactiontype_ac                                                          
		, shortlabel                                                                  
		, fullname                                                                    
		, owner_ac                                                                    
		, interactortype_ac                                                           
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.kd                                                                     
		, :OLD.crc64                                                                  
		, :OLD.formof                                                                 
		, :OLD.proteinform_ac                                                         
		, :OLD.objclass                                                               
		, :OLD.biosource_ac                                                           
		, :OLD.interactiontype_ac                                                     
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.owner_ac                                                               
		, :OLD.interactortype_ac                                                      
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_INTACTNODE                                 
CREATE OR REPLACE TRIGGER trgAud_ia_intactnode                                  
	BEFORE UPDATE OR DELETE                                                        
	ON IA_INTACTNODE                                                               
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_INTACTNODE_AUDIT                                                
		( event                                                                       
		, ac                                                                          
		, lastcheckid                                                                 
		, lastprovidedid                                                              
		, lastprovideddate                                                            
		, rejected                                                                    
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, deprecated                                                                  
		, ownerprefix                                                                 
		, owner_ac                                                                    
		, ftpaddress                                                                  
		, ftplogin                                                                    
		, ftppassword                                                                 
		, ftpdirectory                                                                
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.lastcheckid                                                            
		, :OLD.lastprovidedid                                                         
		, :OLD.lastprovideddate                                                       
		, :OLD.rejected                                                               
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.deprecated                                                             
		, :OLD.ownerprefix                                                            
		, :OLD.owner_ac                                                               
		, :OLD.ftpaddress                                                             
		, :OLD.ftplogin                                                               
		, :OLD.ftppassword                                                            
		, :OLD.ftpdirectory                                                           
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_INT2EXP                                    
CREATE OR REPLACE TRIGGER trgAud_ia_int2exp                                     
	BEFORE UPDATE OR DELETE                                                        
	ON IA_INT2EXP                                                                  
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_INT2EXP_AUDIT                                                   
		( event                                                                       
		, interaction_ac                                                              
		, experiment_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.interaction_ac                                                         
		, :OLD.experiment_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_INT2ANNOT                                  
CREATE OR REPLACE TRIGGER trgAud_ia_int2annot                                   
	BEFORE UPDATE OR DELETE                                                        
	ON IA_INT2ANNOT                                                                
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_INT2ANNOT_AUDIT                                                 
		( event                                                                       
		, interactor_ac                                                               
		, annotation_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.interactor_ac                                                          
		, :OLD.annotation_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_INSTITUTION                                
CREATE OR REPLACE TRIGGER trgAud_ia_institution                                 
	BEFORE UPDATE OR DELETE                                                        
	ON IA_INSTITUTION                                                              
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_INSTITUTION_AUDIT                                               
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, owner_ac                                                                    
		, shortlabel                                                                  
		, fullname                                                                    
		, postaladdress                                                               
		, url                                                                         
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.owner_ac                                                               
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.postaladdress                                                          
		, :OLD.url                                                                    
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_EXPERIMENT                                 
CREATE OR REPLACE TRIGGER trgAud_ia_experiment                                  
	BEFORE UPDATE OR DELETE                                                        
	ON IA_EXPERIMENT                                                               
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_EXPERIMENT_AUDIT                                                
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, biosource_ac                                                                
		, detectmethod_ac                                                             
		, identmethod_ac                                                              
		, relatedexperiment_ac                                                        
		, owner_ac                                                                    
		, shortlabel                                                                  
		, fullname                                                                    
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.biosource_ac                                                           
		, :OLD.detectmethod_ac                                                        
		, :OLD.identmethod_ac                                                         
		, :OLD.relatedexperiment_ac                                                   
		, :OLD.owner_ac                                                               
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_EXP2ANNOT                                  
CREATE OR REPLACE TRIGGER trgAud_ia_exp2annot                                   
	BEFORE UPDATE OR DELETE                                                        
	ON IA_EXP2ANNOT                                                                
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_EXP2ANNOT_AUDIT                                                 
		( event                                                                       
		, experiment_ac                                                               
		, annotation_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.experiment_ac                                                          
		, :OLD.annotation_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_CVOBJECT2ANNOT                             
CREATE OR REPLACE TRIGGER trgAud_ia_cvobject2annot                              
	BEFORE UPDATE OR DELETE                                                        
	ON IA_CVOBJECT2ANNOT                                                           
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_CVOBJECT2ANNOT_AUDIT                                            
		( event                                                                       
		, cvobject_ac                                                                 
		, annotation_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.cvobject_ac                                                            
		, :OLD.annotation_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_CV2CV                                      
CREATE OR REPLACE TRIGGER trgAud_ia_cv2cv                                       
	BEFORE UPDATE OR DELETE                                                        
	ON IA_CV2CV                                                                    
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_CV2CV_AUDIT                                                     
		( event                                                                       
		, parent_ac                                                                   
		, child_ac                                                                    
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.parent_ac                                                              
		, :OLD.child_ac                                                               
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_FEATURE2ANNOT                              
CREATE OR REPLACE TRIGGER trgAud_ia_feature2annot                               
	BEFORE UPDATE OR DELETE                                                        
	ON IA_FEATURE2ANNOT                                                            
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_FEATURE2ANNOT_AUDIT                                             
		( event                                                                       
		, feature_ac                                                                  
		, annotation_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.feature_ac                                                             
		, :OLD.annotation_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_CONTROLLEDVOCAB                            
CREATE OR REPLACE TRIGGER trgAud_ia_controlledvocab                             
	BEFORE UPDATE OR DELETE                                                        
	ON IA_CONTROLLEDVOCAB                                                          
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_CONTROLLEDVOCAB_AUDIT                                           
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, owner_ac                                                                    
		, objclass                                                                    
		, shortlabel                                                                  
		, fullname                                                                    
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.owner_ac                                                               
		, :OLD.objclass                                                               
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_COMPONENT                                  
CREATE OR REPLACE TRIGGER trgAud_ia_component                                   
	BEFORE UPDATE OR DELETE                                                        
	ON IA_COMPONENT                                                                
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_COMPONENT_AUDIT                                                 
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, interactor_ac                                                               
		, interaction_ac                                                              
		, ROLE                                                                        
		, expressedin_ac                                                              
		, owner_ac                                                                    
		, stoichiometry                                                               
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.interactor_ac                                                          
		, :OLD.interaction_ac                                                         
		, :OLD.ROLE                                                                   
		, :OLD.expressedin_ac                                                         
		, :OLD.owner_ac                                                               
		, :OLD.stoichiometry                                                          
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_BIOSOURCE2ANNOT                            
CREATE OR REPLACE TRIGGER trgAud_ia_biosource2annot                             
	BEFORE UPDATE OR DELETE                                                        
	ON IA_BIOSOURCE2ANNOT                                                          
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.userstamp := USER;                                                       
		:NEW.updated := SYSDATE;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_BIOSOURCE2ANNOT_AUDIT                                           
		( event                                                                       
		, biosource_ac                                                                
		, annotation_ac                                                               
		, deprecated                                                                  
		, created                                                                     
		, userstamp                                                                   
		, updated                                                                     
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.biosource_ac                                                           
		, :OLD.annotation_ac                                                          
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.userstamp                                                              
		, :OLD.updated                                                                
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_RANGE                                      
CREATE OR REPLACE TRIGGER trgAud_ia_range                                       
	BEFORE UPDATE OR DELETE                                                        
	ON IA_RANGE                                                                    
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_RANGE_AUDIT                                                     
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, undetermined                                                                
		, LINK                                                                        
		, feature_ac                                                                  
		, owner_ac                                                                    
		, fromintervalstart                                                           
		, fromintervalend                                                             
		, fromfuzzytype_ac                                                            
		, tointervalstart                                                             
		, tointervalend                                                               
		, tofuzzytype_ac                                                              
		, SEQUENCE                                                                    
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.undetermined                                                           
		, :OLD.LINK                                                                   
		, :OLD.feature_ac                                                             
		, :OLD.owner_ac                                                               
		, :OLD.fromintervalstart                                                      
		, :OLD.fromintervalend                                                        
		, :OLD.fromfuzzytype_ac                                                       
		, :OLD.tointervalstart                                                        
		, :OLD.tointervalend                                                          
		, :OLD.tofuzzytype_ac                                                         
		, :OLD.SEQUENCE                                                               
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_FEATURE                                    
CREATE OR REPLACE TRIGGER trgAud_ia_feature                                     
	BEFORE UPDATE OR DELETE                                                        
	ON IA_FEATURE                                                                  
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_FEATURE_AUDIT                                                   
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, component_ac                                                                
		, identification_ac                                                           
		, featuretype_ac                                                              
		, linkedfeature_ac                                                            
		, shortlabel                                                                  
		, fullname                                                                    
		, owner_ac                                                                    
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.component_ac                                                           
		, :OLD.identification_ac                                                      
		, :OLD.featuretype_ac                                                         
		, :OLD.linkedfeature_ac                                                       
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.owner_ac                                                               
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_BIOSOURCE                                  
CREATE OR REPLACE TRIGGER trgAud_ia_biosource                                   
	BEFORE UPDATE OR DELETE                                                        
	ON IA_BIOSOURCE                                                                
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_BIOSOURCE_AUDIT                                                 
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, taxid                                                                       
		, owner_ac                                                                    
		, shortlabel                                                                  
		, fullname                                                                    
		, tissue_ac                                                                   
		, celltype_ac                                                                 
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.taxid                                                                  
		, :OLD.owner_ac                                                               
		, :OLD.shortlabel                                                             
		, :OLD.fullname                                                               
		, :OLD.tissue_ac                                                              
		, :OLD.celltype_ac                                                            
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_ANNOTATION                                 
CREATE OR REPLACE TRIGGER trgAud_ia_annotation                                  
	BEFORE UPDATE OR DELETE                                                        
	ON IA_ANNOTATION                                                               
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_ANNOTATION_AUDIT                                                
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, topic_ac                                                                    
		, owner_ac                                                                    
		, description                                                                 
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.topic_ac                                                               
		, :OLD.owner_ac                                                               
		, :OLD.description                                                            
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_PUBMED                                     
CREATE OR REPLACE TRIGGER trgAud_ia_pubmed                                      
	BEFORE UPDATE OR DELETE                                                        
	ON IA_PUBMED                                                                   
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_PUBMED_AUDIT                                                    
		( event                                                                       
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, primaryid                                                                   
		, status                                                                      
		, description                                                                 
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.primaryid                                                              
		, :OLD.status                                                                 
		, :OLD.description                                                            
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               

                                                                               
PROMPT Creating AUDIT TRIGGER FOR IA_ALIAS                                      
CREATE OR REPLACE TRIGGER trgAud_ia_alias                                       
	BEFORE UPDATE OR DELETE                                                        
	ON IA_ALIAS                                                                    
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated := SYSDATE;                                                       
		:NEW.userstamp := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_ALIAS_AUDIT                                                     
		( event                                                                       
		, ac                                                                          
		, deprecated                                                                  
		, created                                                                     
		, updated                                                                     
		, userstamp                                                                   
		, aliastype_ac                                                                
		, parent_ac                                                                   
		, owner_ac                                                                    
		, name                                                                        
		, created_user   
		)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.ac                                                                     
		, :OLD.deprecated                                                             
		, :OLD.created                                                                
		, :OLD.updated                                                                
		, :OLD.userstamp                                                              
		, :OLD.aliastype_ac                                                           
		, :OLD.parent_ac                                                              
		, :OLD.owner_ac                                                               
		, :OLD.name                                                                   
		, :OLD.created_user 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

                                                                               
CREATE OR REPLACE TRIGGER trgAud_ia_db_info                                       
	BEFORE UPDATE OR DELETE                                                        
	ON IA_DB_INFO                                                                    
	FOR EACH ROW                                                                   
DECLARE                                                                         
	l_event CHAR(1);                                                               
BEGIN                                                                           
	IF DELETING THEN                                                               
		l_event := 'D';                                                               
	ELSIF UPDATING THEN                                                            
		l_event := 'U';                                                               
		:NEW.updated_date := SYSDATE;                                                       
		:NEW.updated_user := USER;                                                       
	END IF ;                                                                       
	                                                                               
	                                                                               
	INSERT INTO IA_DB_INFO_AUDIT                                                     
	(
	event
	,dbi_key
	,value
	,created_date
	,created_user
	,updated_date
	,updated_user
	)                                                                             
	VALUES                                                                         
		( l_event                                                                     
		, :OLD.dbi_key
		, :OLD.value
		, :OLD.created_date
		, :OLD.created_user
		, :OLD.updated_date
		, :OLD.updated_user
		);                                                                            
END;                                                                            
/                                                                               
show error  
                                                                               
spool OFF                                                                      
