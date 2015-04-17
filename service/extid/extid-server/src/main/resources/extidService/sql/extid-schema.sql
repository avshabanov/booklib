--
-- Tables
--

CREATE TABLE ext_group (
  id                INTEGER,
  name              VARCHAR(256) NOT NULL,
  CONSTRAINT pk_ext_group PRIMARY KEY (id),
  CONSTRAINT uq_ext_group_name UNIQUE (name)
);

--
-- Sequences
--

CREATE SEQUENCE seq_group       START WITH 1500;

--
-- Indexes
--
