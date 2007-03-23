PROMPT "Disabling update date propagation triggers..."

ALTER TRIGGER trgAlias_propagate         DISABLE;
ALTER TRIGGER trgAnnotation_propagate    DISABLE;
ALTER TRIGGER trgExp2Annot_propagate     DISABLE;
ALTER TRIGGER trgFeature_propagate       DISABLE;
ALTER TRIGGER trgFeature2annot_propagate DISABLE;
ALTER TRIGGER trgInt2Annot_propagate     DISABLE;
ALTER TRIGGER trgINT2EXP_propagate       DISABLE;
ALTER TRIGGER trgInteractor_propagate    DISABLE;
ALTER TRIGGER trgRange_propagate         DISABLE;
ALTER TRIGGER trgXref_propagate          DISABLE;

