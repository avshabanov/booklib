-- Fixture Data
-- NOTE: There should be no zero values assigned to 'id' fields.

INSERT INTO int_type (id, name) VALUES (10, 'BOOK');
INSERT INTO int_type (id, name) VALUES (11, 'PERSON');

INSERT INTO ext_group (id, name) VALUES (20, 'eBay');
INSERT INTO ext_group (id, name) VALUES (21, 'AmazonKindleStore');
INSERT INTO ext_group (id, name) VALUES (22, 'AmazonPaperback');
INSERT INTO ext_group (id, name) VALUES (24, 'GoogleBooks');

-- book#3 - see book-fixture.sql
INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (3, 10, 20, '301575949364'); -- http://www.ebay.com/itm/301575949364
INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (3, 10, 21, 'B000OCXILW'); -- http://www.amazon.com/dp/B000OCXILW
INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (3, 10, 22, 'B0012GV992'); -- http://www.amazon.com/dp/B0012GV992
INSERT INTO ext_id (int_id, int_type_id, ext_group_id, ext_id) VALUES (3, 10, 24, 'OxM59ts4iloC'); -- https://books.google.com/books?id=OxM59ts4iloC

COMMIT;
