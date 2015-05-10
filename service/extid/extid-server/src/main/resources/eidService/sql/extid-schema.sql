--
-- Tables
--

-- Type of internal item
CREATE TABLE int_type (
  id                INTEGER,
  name              VARCHAR(256) NOT NULL,
  CONSTRAINT pk_int_type PRIMARY KEY (id),
  CONSTRAINT uq_int_type_name UNIQUE (name)
);

CREATE TABLE ext_group (
  id                INTEGER,
  name              VARCHAR(256) NOT NULL,
  CONSTRAINT pk_ext_group PRIMARY KEY (id),
  CONSTRAINT uq_ext_group_name UNIQUE (name)
);

CREATE TABLE ext_id (
  int_id            INTEGER,
  int_type_id       INTEGER NOT NULL,
  ext_group_id      INTEGER NOT NULL,
  ext_id            VARCHAR(256) NOT NULL,
  CONSTRAINT pk_ext_id PRIMARY KEY (int_id, int_type_id, ext_group_id),
  CONSTRAINT fk_ext_id_int_type FOREIGN KEY (int_type_id) REFERENCES int_type(id),
  CONSTRAINT fk_ext_id_ext_group FOREIGN KEY (ext_group_id) REFERENCES ext_group(id)
);

--
-- Sequences
--

CREATE SEQUENCE seq_type        START WITH 1000000;
CREATE SEQUENCE seq_group       START WITH 2000000;

--
-- Indexes
--

CREATE UNIQUE INDEX idx_ext_id ON ext_id(ext_group_id, ext_id);
