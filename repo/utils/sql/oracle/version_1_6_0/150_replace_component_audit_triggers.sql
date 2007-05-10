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
		, experimentalrole_ac
		, biologicalrole_ac                                                                        
		, expressedin_ac                                                              
		, owner_ac                                                                    
		, stoichiometry                                                               
		, created_user   
		, identmethod_ac
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
		, :OLD.experimentalrole_ac
		, :OLD.biologicalrole_ac                                                                   
		, :OLD.expressedin_ac                                                         
		, :OLD.owner_ac                                                               
		, :OLD.stoichiometry                                                          
		, :OLD.created_user
		, :OLD.identmethod_ac 
		);                                                                            
END;                                                                            
/                                                                               
show error                                                                      

