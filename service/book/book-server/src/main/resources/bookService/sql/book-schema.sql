
-- Author info
CREATE TABLE person (
  id        INTEGER PRIMARY KEY,
  f_name    VARCHAR(256) NOT NULL
);

-- Genre codes
CREATE TABLE genre (
  id        INTEGER PRIMARY KEY,
  code      CHAR(32) NOT NULL,
  CONSTRAINT uq_genre_code UNIQUE (code)
);

-- Language codes
CREATE TABLE lang_code (
  id        INTEGER PRIMARY KEY,
  code      CHAR(8) NOT NULL,
  CONSTRAINT uq_lang_code UNIQUE (code)
);

-- Book range (origin)
CREATE TABLE book_origin (
  id        INTEGER PRIMARY KEY,
  code      VARCHAR(256) NOT NULL,
  CONSTRAINT uq_book_origin UNIQUE (code)
);

-- External ID types
CREATE TABLE external_id_type (
  id        INTEGER PRIMARY KEY,
  code      VARCHAR(256) NOT NULL,
  CONSTRAINT uq_external_id_type_code UNIQUE (code)
);

-- Book series
CREATE TABLE series (
  id        INTEGER PRIMARY KEY,
  name      VARCHAR(1024) NOT NULL,
  CONSTRAINT uq_series_name UNIQUE (name)
);



-- Book meta information
CREATE TABLE book_meta (
  id        INTEGER PRIMARY KEY,
  title     VARCHAR(1024) NOT NULL,
  f_size    INTEGER NOT NULL,
  add_date  DATE,
  lang_id   INTEGER,
  origin_id INTEGER NOT NULL,
  CONSTRAINT fk_book_meta_lang FOREIGN KEY (lang_id) REFERENCES lang_code (id),
  CONSTRAINT fk_book_meta_origin FOREIGN KEY (origin_id) REFERENCES book_origin (id)
);

-- Book-to-series mapping
CREATE TABLE book_series (
  book_id   INTEGER NOT NULL,
  series_id INTEGER NOT NULL,
  pos       INTEGER NOT NULL,
  CONSTRAINT pk_book_series PRIMARY KEY (book_id, series_id),
  CONSTRAINT fk_book_series_book FOREIGN KEY (book_id) REFERENCES book_meta (id),
  CONSTRAINT fk_book_series_series FOREIGN KEY (series_id) REFERENCES series (id)
);

-- Book-to-person link
CREATE TABLE book_person (
  book_id   INTEGER NOT NULL,
  person_id INTEGER NOT NULL,
  -- role == 1 (AUTHOR), 2 (ILLUSTRATOR)
  role      INTEGER NOT NULL DEFAULT(1),
  CONSTRAINT pk_book_person PRIMARY KEY (book_id, person_id, role),
  CONSTRAINT fk_book_person_book FOREIGN KEY (book_id) REFERENCES book_meta(id),
  CONSTRAINT fk_book_person_person FOREIGN KEY (person_id) REFERENCES person(id)
);

-- Book-to-genre link
CREATE TABLE book_genre (
  book_id   INTEGER NOT NULL,
  genre_id  INTEGER NOT NULL,
  CONSTRAINT pk_book_genre PRIMARY KEY (book_id, genre_id),
  CONSTRAINT fk_book_genre_book FOREIGN KEY (book_id) REFERENCES book_meta(id),
  CONSTRAINT fk_book_genre_genre FOREIGN KEY (genre_id) REFERENCES genre(id)
);

-- Book-to-external-id link
CREATE TABLE book_external_id (
  book_id           INTEGER NOT NULL,
  external_id_type  INTEGER NOT NULL,
  external_id       VARCHAR(256) NOT NULL,
  CONSTRAINT pk_book_external_id PRIMARY KEY (book_id, external_id_type, external_id),
  CONSTRAINT fk_book_ext_id_book FOREIGN KEY (book_id) REFERENCES book_meta (id),
  CONSTRAINT fk_book_ext_id_type FOREIGN KEY (external_id_type) REFERENCES external_id_type (id)
);

--
-- Sequences
--

CREATE SEQUENCE seq_lang_code   START WITH 1000;
CREATE SEQUENCE seq_ext_id_type START WITH 2000;
CREATE SEQUENCE seq_origin      START WITH 3000;
CREATE SEQUENCE seq_genre       START WITH 4000;
CREATE SEQUENCE seq_series      START WITH 5000;
CREATE SEQUENCE seq_person      START WITH 25000;
CREATE SEQUENCE seq_book        START WITH 150000;

--
-- Indexes
--

CREATE UNIQUE INDEX idx_person_f_name ON person(f_name);
CREATE UNIQUE INDEX idx_book_external_id ON book_external_id(external_id_type, external_id);