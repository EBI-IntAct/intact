CREATE OR REPLACE TRIGGER trgFeature_propagate
	AFTER INSERT OR UPDATE OR DELETE
	ON IA_FEATURE
	FOR EACH ROW /*, make statement otherwise in case of cascade delete of component, ORA-04091 */
BEGIN
	IF INSERTING THEN
		UPDATE IA_COMPONENT
		SET UPDATED = SYSDATE
		WHERE AC= :NEW.component_ac;
	ELSIF UPDATING THEN
		UPDATE IA_COMPONENT
		SET UPDATED = SYSDATE
		WHERE AC= :OLD.component_ac
		OR 	  AC =:NEW.component_ac;

	ELSIF DELETING THEN
	   BEGIN
			UPDATE IA_COMPONENT
			SET UPDATED = SYSDATE
			WHERE AC= :OLD.component_ac;
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
END;
/
