

--DROP TABLE INTREBARI;
--DROP TABLE RASPUNSURI;
--DROP TABLE UTILIZATOR;
--DROP TABLE TESTE;
--DROP FUNCTION GET_DOMAIN;
--DROP PROCEDURE CREATE_TEST;
--DROP PROCEDURE NEXT_QUESTION;
--DROP FUNCTION GET_QUESTION_ANSWERS;
--DROP FUNCTION TOTAL_SCORE;
--DROP PACKAGE BODY QUIZ;
--DROP PACKAGE QUIZ;

CREATE TABLE INTREBARI (
  DOMENIU VARCHAR2(50),
  ID VARCHAR2(8) NOT NULL PRIMARY KEY,
  TEXT_INTREBARE VARCHAR2(1000) NOT NULL
)
/
CREATE TABLE RASPUNSURI(
  Q_ID VARCHAR2(8),
  ID VARCHAR2(8) NOT NULL PRIMARY KEY,
  TEXT_RASPUNS VARCHAR2(1000) NOT NULL,
  CORECT VARCHAR2(1) 
)
/
CREATE TABLE UTILIZATOR (
    EMAIL VARCHAR2(50) NOT NULL PRIMARY KEY,
    HASHCODE NUMBER(32),
    PUNCTAJ NUMBER(3),
    DOMENII VARCHAR2(500)
)
/
CREATE TABLE TESTE (
    EMAIL VARCHAR2(50) NOT NULL,
    Q_NR NUMBER(2),
    Q_ID VARCHAR2(8),
    ANS1_ID VARCHAR2(8),
    ANS2_ID VARCHAR2(8),
    ANS3_ID VARCHAR2(8),
    ANS4_ID VARCHAR2(8),
    ANS5_ID VARCHAR2(8),
    ANS6_ID VARCHAR2(8),
    CORRECT_ANS_ID VARCHAR2(50),
    FINAL_ANS_ID VARCHAR2(50)
)
/
CREATE OR REPLACE TRIGGER on_delete_user
   AFTER DELETE ON UTILIZATOR FOR EACH ROW
DECLARE   
BEGIN  
  DELETE FROM teste WHERE email LIKE :OLD.email;
END;
/


CREATE OR REPLACE PACKAGE QUIZ AS 
   FUNCTION GET_DOMAIN( p_email VARCHAR2) RETURN VARCHAR2;
   PROCEDURE CREATE_TEST ( p_email IN VARCHAR2, p_question OUT VARCHAR2 );
   PROCEDURE NEXT_QUESTION ( p_email IN VARCHAR2, p_raspuns IN VARCHAR2, p_val_return OUT VARCHAR2);
   PROCEDURE TOTAL_SCORE (  p_email IN VARCHAR2, p_score OUT VARCHAR2);
   PROCEDURE GET_QUESTION_ANSWERS ( p_email IN VARCHAR2, p_q_id  IN VARCHAR2, p_text OUT VARCHAR2);
   PROCEDURE GET_SCORES(p_scores IN OUT VARCHAR2);
   PROCEDURE GET_RESULTS(p_email IN VARCHAR2, p_text OUT VARCHAR2);
END QUIZ; 
/



CREATE OR REPLACE PACKAGE BODY QUIZ AS  
    --FUNCTION TO RETURN A DOMAIN THAT THE USER HAS NOT RECEIVED YET
    FUNCTION GET_DOMAIN( p_email VARCHAR2) RETURN VARCHAR2 AS
        CURSOR USER_TEST IS SELECT  Q_ID FROM TESTE WHERE EMAIL LIKE p_email;
        v_random_dom VARCHAR2(50); --ID OF THE DOMAIN
        v_domeniu VARCHAR2(50);
        v_found BOOLEAN;
    BEGIN
          LOOP
            --GENERATE A RANDOM DOMAIN
             SELECT DOMENIU INTO v_random_dom FROM ( SELECT DISTINCT(DOMENIU) FROM INTREBARI ORDER BY DBMS_RANDOM.VALUE)
             WHERE rownum = 1;
             
             --CHECK IF THE DOMAIN IS ALREADY TAKEN
              v_found := FALSE;
              FOR v_line IN USER_TEST LOOP
                SELECT DOMENIU INTO  v_domeniu FROM INTREBARI WHERE ID LIKE v_line.Q_ID;
                IF ( v_domeniu LIKE v_random_dom ) THEN
                    v_found := TRUE; --DOMAIN IS TAKEN
                END IF;
                EXIT WHEN v_found = TRUE ;
              END LOOP;
              
              IF( v_found = FALSE ) THEN --WE FOUND AN UNUSED DOMAIN
                RETURN v_random_dom; 
              END IF;
          END LOOP;
    END GET_DOMAIN;  
    
    
    --PROCEDURE TO CREATE A TEST MADE OF 10 QUESTIONS FOR AN USER
    PROCEDURE CREATE_TEST ( p_email IN VARCHAR2, p_question OUT VARCHAR2 ) AS
        v_index NUMBER(2); --THE INDEX OF THE QUESTION
        v_domain VARCHAR2(50); --THE DOMAIN
        v_q_id VARCHAR2(8); --THE ID OF THE QUESTIONS
        v_ans1_id VARCHAR2(8); --THE IDs OF THE ANSWERS 
        v_ans2_id VARCHAR2(8);
        v_ans3_id VARCHAR2(8);
        v_ans4_id VARCHAR2(8);
        v_ans5_id VARCHAR2(8);
        v_ans6_id VARCHAR2(8);
        v_good_ans VARCHAR2(50):= ''; --THE IDs OF THE CORRECT ANSWERS
        v_correct VARCHAR2(1);
        v_domenii VARCHAR2(500);
        
    BEGIN
        
        FOR v_index IN 1..10 LOOP
            --GET A RANDOM DOMAIN
            v_domain := GET_DOMAIN( p_email);
            
            --GET THE ID OF A RANDOM QUESTION FROM THE DOMAIN
            SELECT ID INTO  v_q_id FROM ( SELECT ID FROM INTREBARI WHERE DOMENIU LIKE v_domain ORDER BY DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            --RETURN THE FIRST QUESTION CHOSEN
            IF( v_index = 1 ) THEN
                p_question := v_q_id;
            END IF;
            
            --GET 6 RANDOM ANSWERS OF WHICH AT LEAST 1 IS CORRECT AND SAVE THE CORRECT ONES
            --ANSWER 1 (IS CORRECT)
            SELECT ID INTO  v_ans1_id FROM 
            (SELECT ID FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND CORECT = 1 ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
             v_good_ans := TO_CHAR(v_ans1_id);
            
            --ANSWER 2
            SELECT ID,CORECT INTO  v_ans2_id, v_correct FROM 
            (SELECT * FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND ID NOT IN(v_ans1_id) ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            IF( v_correct = 1 ) THEN 
                v_good_ans := CONCAT( v_good_ans, (  ',' || TO_CHAR(v_ans2_id)));
            END IF;
            
            --ANSWER 3
            SELECT ID,CORECT INTO  v_ans3_id, v_correct FROM 
            (SELECT * FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND ID NOT IN(v_ans1_id,v_ans2_id) ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            IF( v_correct = 1 ) THEN 
                v_good_ans := CONCAT( v_good_ans, ( ',' || TO_CHAR(v_ans3_id)));
            END IF;
            
            --ANSWER 4
            SELECT ID,CORECT INTO  v_ans4_id, v_correct FROM 
            (SELECT * FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND ID NOT IN(v_ans1_id,v_ans2_id,v_ans3_id) ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            IF( v_correct = 1 ) THEN 
                v_good_ans := CONCAT( v_good_ans, ( ',' || TO_CHAR(v_ans4_id)));
            END IF;
            
            --ANSWER 5
            SELECT ID,CORECT INTO  v_ans5_id, v_correct FROM 
            (SELECT * FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND ID NOT IN(v_ans1_id,v_ans2_id,v_ans3_id,v_ans4_id ) ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            IF( v_correct = 1 ) THEN 
                v_good_ans := CONCAT( v_good_ans, ( ',' || TO_CHAR(v_ans5_id)));
            END IF;
            
            --ANSWER 6
            SELECT ID,CORECT INTO  v_ans6_id, v_correct FROM 
            (SELECT * FROM RASPUNSURI WHERE  Q_ID LIKE v_q_id AND ID NOT IN(v_ans1_id,v_ans2_id,v_ans3_id,v_ans4_id,v_ans5_id) ORDER BY  DBMS_RANDOM.VALUE)
            WHERE rownum = 1;
            
            IF( v_correct = 1 ) THEN 
                v_good_ans := CONCAT( v_good_ans, (',' || TO_CHAR(v_ans6_id)));
            END IF;
            
            --INSERT THE INFORMATION INTO THE TABLE
            INSERT INTO TESTE VALUES( p_email, v_index, v_q_id, v_ans1_id, v_ans2_id, v_ans3_id, v_ans4_id, v_ans5_id, v_ans6_id, v_good_ans, NULL);
            --SAVE THE DOMAIN INTO A CSV
            IF( v_index = 1 ) THEN
                SELECT DOMENIU INTO v_domenii FROM INTREBARI WHERE ID LIKE v_q_id;
            ELSE
                SELECT DOMENIU INTO v_domain FROM INTREBARI WHERE ID LIKE v_q_id;
                v_domenii := v_domenii || ',' || v_domain;
            END IF;
        END LOOP;
         --INSERT INTO UTILIZATOR THE DOMAINS AS CSV
         UPDATE UTILIZATOR SET DOMENII = v_domenii WHERE EMAIL LIKE p_email;
    END CREATE_TEST;
    

    PROCEDURE NEXT_QUESTION ( p_email IN VARCHAR2, p_raspuns IN VARCHAR2, p_val_return OUT VARCHAR2)AS
        v_ans_id VARCHAR2(50); --THE IDs OF THE ANSWERS
        v_q_id VARCHAR2(8); --THE ID OF THE QUESTION
        v_q_nr NUMBER(2); --THE NUMBER OF THE QUESTION
        v_exists NUMBER(2);
        invalid_question_id EXCEPTION;
        PRAGMA EXCEPTION_INIT(invalid_question_id, -20001);
        invalid_answer_id EXCEPTION;
        PRAGMA EXCEPTION_INIT(invalid_answer_id, -20002);
        already_completed EXCEPTION;
        PRAGMA EXCEPTION_INIT(already_completed, -20003);
        v_counter INTEGER;

    BEGIN
        --FIRST VERIFY IF THERE IS A TEST FOR THIS USER || CHECK IF p_raspuns IS NULL
         SELECT COUNT(*) INTO v_exists FROM  TESTE WHERE EMAIL LIKE p_email;
         IF( v_exists = 0) THEN --WE HAVE TO CREATE A NEW TEST, THAT WILL RETURN THE QUESTION ID
             CREATE_TEST( p_email, v_q_id );
             p_val_return  := v_q_id ;
--         ELSIF( v_ans_id IS NOT NULL) THEN
--            raise_application_error (-20003,'[Error]Testul poate fi rezolvat o singura data. ');   
         ELSE
             --THE FUNCTION NEXT_QUESTION WILL RECEIVE NULL FOR PARAMETER p_raspuns  ONLY IF THE USER DOESN'T HAVE A TEST CREATED
             
             --GET THE QUESTION ID
             v_q_id :=  SUBSTR(  p_raspuns, 0 , INSTR( p_raspuns,',')-1);
            --IF THE QUESTION IS NULL, RETURNAM  ULTIMA INTREBARE LA CARE NU S-A RASPUNS
            IF p_raspuns IS NULL THEN
                SELECT Q_ID INTO p_val_return FROM (SELECT Q_ID FROM TESTE WHERE EMAIL LIKE p_email AND FINAL_ANS_ID IS NULL ORDER BY Q_NR)
                WHERE ROWNUM =1;
            ELSE
             
                 --GET ANSWERS IDs
                 v_ans_id := SUBSTR(  p_raspuns, (LENGTH(v_q_id) + 2) );
                 
                 --VALIDATE IDs
                 SELECT COUNT(*) INTO v_counter FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE v_q_id;
                 IF v_counter = 0 THEN
                    raise_application_error (-20001,'[Error]Intrebarea cu id-ul ' || v_q_id || ' nu este valid.');
                 ELSE
                    FOR v_verify_ans IN ( SELECT REGEXP_SUBSTR( v_ans_id, '[^,]+', 1, level) AS id FROM DUAL 
                            CONNECT BY REGEXP_SUBSTR( v_ans_id, '[^,]+', 1, level) IS NOT NULL ) 
                    LOOP
                        SELECT COUNT(*) INTO v_counter FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE v_q_id AND 
                        (ans1_id LIKE v_verify_ans.id OR ans2_id LIKE v_verify_ans.id OR ans3_id LIKE v_verify_ans.id OR 
                        ans4_id LIKE v_verify_ans.id OR ans5_id LIKE v_verify_ans.id OR ans6_id LIKE v_verify_ans.id);
                        IF v_counter = 0 THEN
                          raise_application_error (-20002,'[Error]Raspunsul cu id-ul ' || v_verify_ans.id || ' nu este valid.');
                        END IF;
                    END LOOP;             
                END IF;
                
                --WE RETURN THE SAME QUESTION
                 IF(v_ans_id IS NULL) THEN
                    p_val_return := v_q_id;
                 ELSE
                     --ADD THE CSV ANSWER IN THE TABLE TESTE
                      UPDATE TESTE SET FINAL_ANS_ID = v_ans_id WHERE EMAIL LIKE p_email AND Q_ID LIKE v_q_id;
                      
                      SELECT Q_NR INTO v_q_nr FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE v_q_id;
                      
                      IF( v_q_nr = 10 )THEN   
                        --WE RETURN FINAL SCORE
                         GET_RESULTS (p_email, p_val_return);
            
                        
                      ELSE
                        --WE RETURN THE NEXT QUESTION
                        SELECT Q_ID INTO  p_val_return  FROM TESTE WHERE EMAIL LIKE p_email AND Q_NR = v_q_nr + 1 ;        
                      END IF;
                 END IF;
            END IF;
            
         END IF;  
    END NEXT_QUESTION;
    

    PROCEDURE TOTAL_SCORE (p_email IN VARCHAR2, p_score OUT VARCHAR2) AS
        v_sum NUMBER := 0; --TOTAL SCORE
        v_ans_points NUMBER; --THE NUMBER OF POINTS PER ANSWER
        v_nr_ans NUMBER; --THE NUMBER OF CORRECT ANSWERS GIVEN
        v_q_score NUMBER;
        v_found BOOLEAN;
        CURSOR QUESTION IS SELECT * FROM TESTE WHERE EMAIL LIKE p_email;
    BEGIN
        
        FOR v_line IN QUESTION LOOP 
            --GET THE NUMBER OF CORRECT ANSWERS BY PARSING THE CSV,  CONNECT BY- HIERARCHICAL QUERY OPERATOR
            SELECT COUNT(REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level)) INTO v_nr_ans FROM DUAL 
            CONNECT BY REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) IS NOT NULL;
            
            --THE NUMBER OF POINTS PER ANSWER
             v_ans_points := 10 / v_nr_ans; 
             
             --THE SCORE PER QUESTION
             v_q_score := 0; 
             --PARSE THE USER'S ANSWERS
            FOR v_final_ans IN ( SELECT REGEXP_SUBSTR( v_line.FINAL_ANS_ID, '[^,]+', 1, level) AS id FROM DUAL 
            CONNECT BY REGEXP_SUBSTR( v_line.FINAL_ANS_ID, '[^,]+', 1, level) IS NOT NULL ) 
            LOOP
            
                v_found := FALSE;
                --PARSE THE CORRECT ANSWERS AND SEARCH FOR THE USER ANSWER 
                FOR v_good_ans IN ( SELECT REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) AS id FROM DUAL 
                CONNECT BY REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) IS NOT NULL ) 
                LOOP
                    IF( v_final_ans.id LIKE v_good_ans.id ) THEN
                        v_q_score := v_q_score + v_ans_points;  --THE ANSWER WAS CORRECT, WE ADD  TO THE QUESTION SCORE
                        v_found := TRUE;
                    END IF;
                    EXIT WHEN v_found = TRUE;
                END LOOP;
                
                IF( v_found = FALSE) THEN
                   v_q_score := v_q_score - v_ans_points;  --THE ANSWER WAS INCORRECT
                END IF;
                IF ( v_q_score < 0 ) THEN
                    v_q_score := 0;
                END IF;
                
            END LOOP;
           
            --IF TE SCORE HAS PERIOD OR SOMETHING
            IF( v_nr_ans = 3 OR v_nr_ans = 6 ) THEN
                v_q_score := ROUND(v_q_score);
            END IF;
            
            v_sum := v_sum + v_q_score; --ADD TO FINAL SCORE
            
            --DBMS_OUTPUT.PUT_LINE(v_line.Q_NR || ' SCOR ANS ' || v_ans_points|| ' SCOR Q ' || v_q_score);
        END LOOP;
        p_score := TO_CHAR(v_sum);
    END TOTAL_SCORE;


    PROCEDURE GET_QUESTION_ANSWERS ( p_email IN VARCHAR2, p_q_id  IN VARCHAR2 ,p_text OUT VARCHAR2 ) AS
        v_intrebare VARCHAR2(100);
        v_nr VARCHAR2(5);
        v_domeniu VARCHAR2(50);
        v_raspuns VARCHAR2(100);
        v_id_raspuns VARCHAR2(8);
    BEGIN
        p_text := '';
        p_text := '{"id_intrebare": "' || p_q_id ||'", '  ;
        --GET THE NUMBER OF THE QUESTION
        SELECT Q_NR INTO v_nr FROM TESTE WHERE Q_ID LIKE p_q_id AND email LIKE p_email;
        p_text := p_text || '"numar_intrebare": "Intrebarea numarul ' || v_nr ||'",'  ;
        --GET THE DOMAIN
        SELECT DOMENIU INTO v_domeniu FROM INTREBARI WHERE ID LIKE p_q_id;
        p_text := p_text || '"domeniu": "' || v_domeniu||'",'  ;
        --GET THE QUESTION
        SELECT TEXT_INTREBARE INTO v_intrebare FROM INTREBARI WHERE ID LIKE p_q_id;
        p_text := p_text || '"intrebare": "' || v_intrebare || '",' ;
        --GET THE ANSWERS AND CONCATENATE TO THE TEXT WITH '#' DELIMITER
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS1_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS1_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_1": "' || v_id_raspuns || '", "raspuns_1": "' || v_raspuns || '",';
        
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS2_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS2_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_2": "' || v_id_raspuns || '", "raspuns_2": "' || v_raspuns || '",' ;
        
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS3_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS3_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_3": "' || v_id_raspuns || '", "raspuns_3": "' || v_raspuns || '",'  ;
        
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS4_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS4_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_4": "' || v_id_raspuns || '", "raspuns_4": "' || v_raspuns || '",'  ;
        
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS5_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS5_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_5": "' || v_id_raspuns || '", "raspuns_5": "' || v_raspuns || '",'  ;
        
        SELECT TEXT_RASPUNS INTO v_raspuns FROM RASPUNSURI R JOIN TESTE  T ON R.ID=T.ANS6_ID WHERE T.EMAIL LIKE p_email AND T.Q_ID LIKE p_q_id;
        SELECT ANS6_ID INTO v_id_raspuns FROM TESTE WHERE EMAIL LIKE p_email AND Q_ID LIKE p_q_id;
        p_text := p_text || '"id_rasp_6": "' || v_id_raspuns || '", "raspuns_6": "' || v_raspuns || '"}' ;
        
    END GET_QUESTION_ANSWERS;
    
    PROCEDURE GET_SCORES(p_scores IN OUT VARCHAR2)
        AS
        v_users INTEGER;
        v_counter INTEGER;
        BEGIN
            p_scores := '{';
            SELECT COUNT(*)INTO v_users FROM UTILIZATOR WHERE PUNCTAJ IS NOT NULL;
            v_counter := 0;
            FOR v_user IN  (SELECT * FROM UTILIZATOR WHERE PUNCTAJ IS NOT NULL ORDER BY PUNCTAJ DESC) LOOP     
            v_counter := v_counter + 1;
            IF v_counter = v_users THEN
                p_scores := p_scores || '"player_'|| v_counter || '": ' || '{"email": "' || v_user.email || '", "punctaj": "' || v_user.punctaj || '"}}';
            ELSE
                p_scores := p_scores ||'"player_'|| v_counter || '": ' || '{"email": "' || v_user.email || '", "punctaj": "' || v_user.punctaj || '"},';  
            END IF;
        END LOOP;  
        IF p_scores = '{' THEN
                p_scores := '{}';
            END IF;
    END GET_SCORES;
    
    PROCEDURE GET_RESULTS(p_email IN VARCHAR2, p_text OUT VARCHAR2) AS
        v_ans_points NUMBER; --THE NUMBER OF POINTS PER ANSWER
        v_q_points NUMBER;
        v_nr_ans NUMBER; --THE NUMBER OF CORRECT ANSWERS GIVEN
        v_found BOOLEAN;
        v_q_text VARCHAR2(100);
        v_ans_text VARCHAR2(100);
        v_counter_resp NUMBER;
        CURSOR QUESTION IS SELECT * FROM TESTE WHERE EMAIL LIKE p_email;
        BEGIN
            p_text := '{';
            FOR v_line IN QUESTION LOOP 
                SELECT TEXT_INTREBARE INTO v_q_text FROM INTREBARI WHERE id LIKE v_line.q_id;
                v_q_points := 0;
                p_text := p_text || '"intrebare_' || v_line.q_nr || '": {"intrebare": "' || v_q_text ||'",';
                --GET THE NUMBER OF CORRECT ANSWERS BY PARSING THE CSV,  CONNECT BY- HIERARCHICAL QUERY OPERATOR
                SELECT COUNT(REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level)) INTO v_nr_ans FROM DUAL 
                CONNECT BY REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) IS NOT NULL;
                
                v_counter_resp := 1;
                FOR v_good_ans IN ( SELECT REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) AS id FROM DUAL 
                    CONNECT BY REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) IS NOT NULL ) 
                    LOOP
                        SELECT TEXT_RASPUNS INTO v_ans_text FROM RASPUNSURI WHERE id LIKE v_good_ans.id;
                        p_text := p_text || '"raspuns_ok_'|| v_counter_resp ||'": "' || v_ans_text || '",';
                        v_counter_resp := v_counter_resp + 1;
                    END LOOP;
                
                --THE NUMBER OF POINTS PER ANSWER
                 v_ans_points := 10 / v_nr_ans; 
                 
                 --PARSE THE USER'S ANSWERS
                v_counter_resp := 1;
                FOR v_final_ans IN ( SELECT REGEXP_SUBSTR( v_line.FINAL_ANS_ID, '[^,]+', 1, level) AS id FROM DUAL 
                CONNECT BY REGEXP_SUBSTR( v_line.FINAL_ANS_ID, '[^,]+', 1, level) IS NOT NULL ) 
                LOOP      
                    v_found := FALSE;
                    --PARSE THE CORRECT ANSWERS AND SEARCH FOR THE USER ANSWER 
                    FOR v_good_ans IN ( SELECT REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) AS id FROM DUAL 
                    CONNECT BY REGEXP_SUBSTR( v_line.CORRECT_ANS_ID, '[^,]+', 1, level) IS NOT NULL ) 
                    LOOP
                        IF( v_final_ans.id LIKE v_good_ans.id ) THEN
                            v_found := TRUE;
                            v_q_points := v_q_points + v_ans_points;
                        END IF;
                        EXIT WHEN v_found = TRUE;
                    END LOOP;   
                    IF( v_found = FALSE) THEN
                       v_q_points := v_q_points - v_ans_points;
                       IF v_q_points < 0 THEN
                            v_q_points := 0;
                       END IF;
                    END IF;
                    SELECT TEXT_RASPUNS INTO v_ans_text FROM RASPUNSURI WHERE id LIKE v_final_ans.id;
                    p_text := p_text || '"raspuns_'|| v_counter_resp ||'": "' || v_ans_text || '", ';
                    v_counter_resp := v_counter_resp + 1;
                END LOOP;
                p_text := p_text || '"punctaj": "'|| ROUND(v_q_points,2) ||'"},';
            END LOOP;
            TOTAL_SCORE(p_email, v_ans_text);
            UPDATE UTILIZATOR SET PUNCTAJ = v_ans_text WHERE EMAIL LIKE p_email;
            p_text := p_text || '"total-score": "' || v_ans_text || '"}';
    END GET_RESULTS;
    
END QUIZ;
/



set serveroutput on;
declare
    text varchar2(5000);
begin

     QUIZ.GET_SCORES(text);
     --QUIZ.NEXT_QUESTION('ok@yahoo.com', '',text);
     --QUIZ.GET_RESULTS('anastasia@gmail.com', text);
     DBMS_OUTPUT.PUT_LINE(text);
end;

delete from utilizator where email like 'alt@gmail.com';
delete from teste where email like 'alt@gmail.com';
commit;

