--
-- Queries
--

SELECT bm.id,bm.title,l.code AS book_lang,bo.code AS origin_code FROM book_meta AS bm INNER JOIN lang_code AS l ON l.id=bm.lang_id INNER JOIN book_origin AS bo ON bm.origin_id=bo.id WHERE bm.id=140508;

-- Books by author
SELECT bm.id, bm.title FROM book_meta AS bm INNER JOIN book_author AS ba ON ba.book_id=bm.id WHERE ba.author_id=1;

SELECT bm.ser,bm.ser_num,bm.title,bm.ext,bo.code FROM book_meta AS bm INNER JOIN book_author AS ba ON bm.id=ba.book_id INNER JOIN book_origin AS bo ON bm.origin_id=bo.id WHERE ba.author_id=1 ORDER BY bm.origin_id;

-- Select all sci-fi books
SELECT bm.title FROM book_meta AS bm INNER JOIN book_genre AS bg ON bm.id=bg.book_id INNER JOIN genre AS g ON bg.genre_id=g.id WHERE g.code='sf' LIMIT 10;

-- Short stories
SELECT a.f_name, bm.title FROM author AS a INNER JOIN book_author AS ba ON a.id=ba.author_id INNER JOIN book_meta AS bm ON bm.id=ba.book_id WHERE bm.f_size<10000 LIMIT 10;

-- Book with more than one author
SELECT DISTINCT bm.id, bm.title FROM book_meta AS bm INNER JOIN book_author AS ba ON bm.id=ba.book_id WHERE (SELECT COUNT(0) FROM book_author AS ba2 WHERE ba2.book_id=bm.id)>1;

-- Find all book authors
SELECT a.f_name, bm.title FROM author AS a INNER JOIN book_author AS ba ON a.id=ba.author_id INNER JOIN book_meta AS bm ON bm.id=ba.book_id WHERE bm.id=30200;

-- Find all book genres
SELECT g.code, bm.title FROM genre AS g INNER JOIN book_genre AS bg ON g.id=bg.genre_id INNER JOIN book_meta AS bm ON bm.id=bg.book_id WHERE bm.id=30200;

-- Find count of books of the particular genre
SELECT COUNT(0) FROM book_meta AS bm INNER JOIN book_genre AS bg ON bm.id=bg.book_id INNER JOIN genre AS g ON bg.genre_id=g.id WHERE g.code='sf';

-- Find count of books by particular genres
SELECT g2.code, (SELECT COUNT(0) FROM book_meta AS bm INNER JOIN book_genre AS bg ON bm.id=bg.book_id WHERE bg.genre_id=g2.id) FROM genre AS g2;