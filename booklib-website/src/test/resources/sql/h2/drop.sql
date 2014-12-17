--
-- Targeted
--

DELETE FROM book_author WHERE book_id=(SELECT id FROM book_meta WHERE origin_id=(SELECT id FROM book_origin WHERE code='my_origin'));
DELETE FROM book_genre WHERE book_id=(SELECT id FROM book_meta WHERE origin_id=(SELECT id FROM book_origin WHERE code='my_origin'));
DELETE FROM book_meta WHERE origin_id=(SELECT id FROM book_origin WHERE code='my_origin');
DELETE FROM book_origin WHERE code='my_origin';


--
-- All
--

DELETE FROM book_author;
DELETE FROM book_genre;
DELETE FROM book_origin WHERE code != 'unknown';
DELETE FROM book_meta;

SELECT * FROM book_origin;
