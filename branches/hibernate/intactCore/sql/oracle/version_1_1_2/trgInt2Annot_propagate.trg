CREATE OR REPLACE TRIGGER trgInt2Annot_propagate 	-- NOG INBOUWEN CHECKEN DAT HET GAAT OM INTERACTION, zal daarvoor naar de interactor TABLE moeten */
	AFTER INSERT OR UPDATE OR DELETE
	ON IA_INT2ANNOT
	FOR EACH ROW

DECLARE
	CURSOR c_obj (P_ac VARCHAR2) IS 
		SELECT objclass
		FROM IA_INTERACTOR  
		WHERE ac = p_ac;
		
	lv_object VARCHAR2(255);
	lv_exception VARCHAR(1) := 'N';

BEGIN

	BEGIN 
		IF INSERTING THEN
			OPEN C_OBJ(:NEW.interactor_ac);
			FETCH C_OBJ INTO lv_object;
			CLOSE C_OBJ; 
		ELSE 
			OPEN C_OBJ(:OLD.interactor_ac);
			FETCH C_OBJ INTO lv_object;
			CLOSE C_OBJ; 	
		END IF;
	EXCEPTION
	  WHEN OTHERS THEN
	  	   IF SQLCODE = -04091 /* because of cascade delete in IA_RANGE. Because 
				  	 	  				   .. parent RECORD IN IA_RANGE gets deleted no need TO UPDATE it */ 
			  THEN lv_exception := 'J';	/* no need for update interactor as it is already deleting */
			  ELSE
			  	 RAISE;
			  END IF;											   
	END;
		
	IF lv_exception ='N' 
	THEN
/*		IF UPPER(lv_object) = 'UK.AC.EBI.INTACT.MODEL.INTERACTIONIMPL'
		THEN */
			IF INSERTING THEN
				UPDATE IA_INTERACTOR
				SET UPDATED = SYSDATE
				WHERE AC= :NEW.interactor_ac;
			ELSIF UPDATING THEN
				UPDATE IA_INTERACTOR
				SET UPDATED = SYSDATE
				WHERE AC= :OLD.interactor_ac
				OR 	  AC =:NEW.interactor_ac;
			ELSIF DELETING THEN
			   BEGIN
					UPDATE IA_INTERACTOR
					SET UPDATED = SYSDATE
					WHERE AC= :OLD.interactor_ac;
				EXCEPTION
					WHEN OTHERS THEN
					  IF SQLCODE = -04091 /* because of cascade delete in IA_RANGE. Because 
					  	 	  				   .. parent RECORD IN IA_RANGE gets deleted no need TO UPDATE it */ 
					  THEN NULL;	
					  ELSE
					  	 RAISE;
					  END IF;	  	
				END;		   
		END IF ;
/*			ELSIF UPPER(lv_object) = 'UK.AC.EBI.INTACT.MODEL.PROTEINIMPL'
		THEN
			IF INSERTING THEN	
				UPDATE IA_COMPONENT
				SET UPDATED=SYSDATE
				WHERE INTERACTOR_AC = :OLD.INTERACTOR_AC;	
			ELSIF UPDATING THEN
				UPDATE IA_COMPONENT
				SET UPDATED = SYSDATE
				WHERE AC= :OLD.interactor_ac
				OR 	  AC =:NEW.interactor_ac;	
			ELSIF DELETING THEN 
				UPDATE IA_COMPONENT
				SET UPDATED=SYSDATE
				WHERE INTERACTOR_AC = :OLD.INTERACTOR_AC;
			END IF; 
		END IF;*/
	END IF;
END;
/
