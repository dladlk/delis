drop schema if exists delis;
create schema delis;
alter database delis charset=utf8 collate=utf8_bin; 
drop user if exists delis@localhost;
create user delis@localhost identified by 'delis';
grant all on delis.* to delis@localhost;
