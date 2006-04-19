CREATE OR REPLACE PACKAGE Pck_Mutating_Tables_Interactor IS
   TYPE row_rec IS RECORD
   ( r_id         VARCHAR2(30)
    ,s_info       VARCHAR2(25)
   )
   ;

  TYPE id_array_type IS TABLE OF
  row_rec
  INDEX BY BINARY_INTEGER;
  TYPE trg_stack_type IS TABLE OF
  BINARY_INTEGER
  INDEX BY BINARY_INTEGER;
  --
  -- initialize array's for saving id's.
  PROCEDURE init_array
  ;
  --
  -- clear array's used to save id's.
  PROCEDURE clear_array
  ;
  --
  -- save id of mutated row
  PROCEDURE add_id(
    p_id    IN VARCHAR2
   ,p_info  IN VARCHAR2 := NULL)
  ;
  --
  -- read id mutated row from row_id queue.
  PROCEDURE get_id(
     p_id IN OUT VARCHAR2
    ,p_info  IN OUT VARCHAR2 )
  ;

  FUNCTION Get_Trigger_Stack_Id RETURN NUMBER;

END Pck_Mutating_Tables_Interactor;
/
CREATE OR REPLACE PACKAGE BODY Pck_Mutating_Tables_Interactor IS
  id_array id_ARRAY_TYPE;
  trg_stack TRG_STACK_TYPE;
  trg_stack_ind BINARY_INTEGER DEFAULT 0;
  id_ind BINARY_INTEGER DEFAULT 0;

  /* initialize array's for saving id's. */
  PROCEDURE init_array
  IS
  BEGIN
    trg_stack_ind             := trg_stack_ind+1;
    trg_stack(trg_stack_ind)  := id_ind;
  END;

  /* clear array's used to save id's. */
  PROCEDURE clear_array
  IS
  BEGIN
    IF trg_stack_ind > 0
    THEN
      WHILE id_ind > trg_stack(trg_stack_ind) LOOP
        id_array(id_ind)  := NULL;
        id_ind            := id_ind-1;
      END LOOP;
      trg_stack(trg_stack_ind) := NULL;
      trg_stack_ind            := trg_stack_ind-1;
    ELSE
      RAISE_APPLICATION_ERROR
      (-20001,'Error "clear_array" circumventing Mutating Table: bad stack');
    END IF;
  END;


  /* save id of mutated row */
  PROCEDURE add_id(
    p_id IN VARCHAR2
   ,p_info  IN VARCHAR2 := NULL)
  IS
  BEGIN
    IF Trg_stack_ind > 0
    THEN
      id_ind                 := id_ind+1;
      id_array(id_ind).r_id  := p_id;
      id_array(id_ind).s_info:= p_info;
    ELSE
      RAISE_APPLICATION_ERROR
      (-20001,'Error "add_id" circumventing Mutating Table: bad stack');
    END IF;
  END;


  /* read id mutated row from row_id queue. */
  PROCEDURE get_id(
    p_id IN OUT VARCHAR2
   ,p_info  IN OUT VARCHAR2 )
  IS
    l_result NUMBER(20);
  BEGIN
    IF trg_stack_ind > 0
    THEN
      IF Id_ind > trg_stack(trg_stack_ind)
      THEN
        p_id             := id_array(id_ind).r_id;
        p_info           := id_array(id_ind).s_info ;
        id_array(id_ind) := NULL;
        id_ind           := id_ind-1;
      END IF;
    ELSE
      RAISE_APPLICATION_ERROR
      (-20001,'Error "get_id" circumventing Mutating Table: bad stack');
    END IF;
  END;

  FUNCTION Get_Trigger_Stack_Id RETURN NUMBER IS
  BEGIN
     RETURN trg_stack_ind;
  END;

END Pck_Mutating_Tables_Interactor;
/
