create table band (
  id INTEGER AUTO_INCREMENT,
  name VARCHAR(256)
);

create table record (
  id INTEGER AUTO_INCREMENT,
  name VARCHAR(256),
  bandId INTEGER
);

insert into band(id, name) values (1, 'AC/DC');
insert into record(bandId, name) values(1, 'High Voltage');
insert into record(bandId, name) values(1, 'T.N.T.');