drop schema if exists delis2;

create schema delis2;
alter database delis2 charset=utf8 collate=utf8_bin; 

grant all on delis2.* to delis@localhost;
