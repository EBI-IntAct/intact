/*
  Copyright (c) 2002 The European Bioinformatics Institute, and others.  
  All rights reserved. Please see the file LICENSE 
  in the root directory of this distribution.
*/
/*************************************************************

  Package:    IntAct core

  Purpose:    Drop all Postgres components for IntAct

  $Date$
  $Locker$


  *************************************************************/

 drop table int2exp ;
 drop table int2annot ;
 drop table exp2annot ;
 drop table cvobject2annot ;
 drop table biosource2annot ;
 drop table cv2cv ;
 drop table controlledvocab ;
 drop table biosource ;
 drop table interactor ;
 drop table annotation ;
 drop table experiment ;
 drop table xref ;
 drop table intactnode ;