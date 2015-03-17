IntAct is a Molecular Interaction framework including a relational database system that we are going to describe further here. A slightly outdated but very useful explanation of the schema can be found [here](DatabaseSchema.md)


# Database schema versions #

Here we are going to describe the different versions of the relational database and there major features/update.


#### Version 1.7.0 ####

  * created a table to keep track of IMEx imports: IA\_IMEX\_IMPORT
    * update includes the creation of a dedicated sequence, audit table, trigger, appropriate grants and public synonyms.

#### Version 1.6.3 ####

  * Add missing columns in the ia\_component\_audit table: shorltabel and fullname
    * Rebuild the corresponding audit trigger
  * add new tables required by new component features:
    * ia\_component2exp\_preps   : indirection table that allows a component to have 0..n experimental preparations
    * ia\_component2part\_detect : indirection table that allows a component to have 0..n participant detection methods
  * add new tables required by the institution becoming an annotated object
    * ia\_institution\_xref      : xrefs of institution
    * ia\_institution\_alias     : aliases of institution
    * ia\_institution2annot     : annotations of institution
  * update ia\_institution so it fits the annotated object requirements


#### Version 1.6.0 ####

Changes in this version:

  * Add on Component the possibility to define participant detection method.
  * Add BiologicalRole on Component
  * Add Experimental role on Component
  * Script that splits the current role into Biological and Experimental
> > Curators are to define such algorithm


#### Version 1.3.0 ####

You can have a closer look at the database schema [here](DatabaseSchema.md).

Changes in this version:

  * Component becomes an Annotated object (so we now can add Xref, Alias, Amnotation).
  * Create alias audit table (incl. public synonyms, grants) & audit triggers
  * Create xref audit table (incl. public synonyms, grants) & audit triggers
  * We need different Alias tables, one for each parent type, so hibernate handles well the database mapping and constraints are enabled for the parent\_ac field.


#### version 1.2.3 ####

Changes in this version:

  * IA\_DB\_INFO table needs public synonyms in order to be accessible which any user.


#### version 1.2.2 ####

Changes in this version:

  * Added 6 new tables, with the same structure than IA\_Xref: IA\_BioSourceXref, IA\_ExperimentXref, IA\_ControlledVocabXref, IA\_FeatureXref, IA\_InteractorXref, IA\_PublicationXref
  * Created synonims for those tables
  * Granted the correct permissions
  * Creates a procedure to copy existing data from IA\_Xref table to the splitted tables
  * Script to run the procedure
  * Triggers to maintain the coexistence with the old IA\_Xref table while everything is migrated


#### version 1.2.0 ####

Changes in this version:

  * add new tables: ia\_publication                - Representation of a publication
  * add new tables: ia\_publication\_audit          - Corresponding audit table
  * add new tables: ia\_pub2annot                  - Indirection table that allow a publication to have annotations
  * add new tables: ia\_pub2annot\_audit            - Corresponding audit table
  * add new tables: ia\_key\_assigner\_request       - Log table for Key Assigner request
  * add new tables: ia\_key\_assigner\_request\_audit - Corresponding audit table


#### version 1.1.7 ####

Changes in this version:

  * Added missing 20 indexes on tables.


#### version 1.1.6 ####

Changes in this version:

  * New view: IA\_DELETED\_ACS - list of AC's of Experiments, Interactions and Interactor which have been deleted.


#### version 1.1.5 ####

Changes in this version:

  * To prevent problem of mutating tables in case of delete of Interactor (via cascade delete fires triggers Component)
> > there is added package Pck\_Mutating\_Tables\_Component, triggers (TRG\_COMPONENT\_AR, TRG\_COMPONENT\_AS en TRG\_COMPONENT\_BS)
> > and procedure Process\_Array\_Interactor.
  * Renamed package Pck\_Mutating\_Tables into Pck\_Mutating\_Tables\_Interactor
  * Renamed Procedure process\_array into process\_array\_interactor
  * Renamed TRG\_BS\_INTERACTOR into  TRG\_INTERACTOR\_BS
  * Renamed TRG\_AR\_INTERACTOR into  TRG\_INTERACTOR\_AR
  * Renamed TRG\_AS\_INTERACTOR into  TRG\_INTERACTOR\_AS
  * because of the renamed package, the code of the triggers on Interactor was also adjusted
  * Add additional comments ,  removed remaining dbms\_output statements and convert tabs in spaces.


#### version 1.1.4 ####

Changes in this version:

  * In audit-tables, the fields  UPDATED and USERSTAMP are now getting SYSDATE and USER as values in stead of the

> OLD.UPDATED and OLD.USERSTAMP. Otherwise one is not able to trace deletes.


#### version 1.1.3 ####

Changes in this version:

  * Adding view IA\_EXP\_PUBMED\_NOT\_FULLY\_ACCEPT for all the experiments of pubmedids of which not all experiments are accepted.


#### version 1.1.2 ####

Changes in this version:

  * propagation of updated user and timestamp towards the corresponding experiment, this is done through the use of database triggers and stored procedure to work arond mutating table problems.


#### version 1.1.0 ####

Changes in this version:

  * Adding column created\_user to all tables which has an audit-table. The audit-table get the column as well. `Reason`: info about original creator is now lost. For example curators don't want this information to get lost, solely because someone put an "accepted" label on it.
  * Also added a new table IA\_DB\_INFO for meta-data about the database-schema.
> > Inserted the key:value pair: schema\_version:1.1.0.
  * Update new column: CREATED\_USER as the value of userstamp in the same record is used if there are no records for this record in the audittable. Also, the value of userstamp in the earliest record in the audittable is used in case there is one or more records in the audittable. For most tables the earliest record is determined looking at the field updated. This is because mostly this field in the audittable was set to sysdate by the triggers, or the field was copied from the field updated from the main-table, which had been updated in a previous change.
> > The earliest record is determined based on the field timestamp in case of the following tables: IA\_BIOSOURCE, IA\_FEATURE, IA\_FEATURE2ANNOT and IA\_RANGE. In these cases the field updated cannot be used because the trigger given in the audit-table the field 'updated'  the old value of the same field in the main-table, in stead of sysdate.
> > Furthermore in the main-table the field 'updated' was not updated in previous changes. However in the main-table the field 'timestamp' was updated and with the next update op this record, this value was copied as old-value into into the field timestamp of the audittable. Therefore in the case of this three tables, the field 'timestamp' contains the history information.
  * For the tables IA\_BIOSOURCE, IA\_FEATURE, IA\_FEATURE2ANNOT and IA\_RANGE , the field 'updated' gets now also sysdate in case of update.
  * In the audittables, the field 'updated' now gets always old values in stead of sysdate (a few tables like ia\_biosource already got old-value).
  * Added filling the field created\_user.
  * Dropped column TIMESTAMP in main and audittables



#### Version 1.0 ####


> You can have a closer look at the database schema [here](https://intact.googlecode.com/svn/wiki/images/database/1.0/v1.0.png).


