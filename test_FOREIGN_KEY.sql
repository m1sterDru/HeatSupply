select * from public.user;
select * from meter;
select * from users_web;
select * from meter_user;

insert into public.user (id,login) values (1, 'u-1');
insert into public.meter (id,serialnumber) values (1, 'sn-1');
insert into public.users_web (id,login) values (1, 'uw-1');
insert into public.meter_user (iduser,idmeter) values (1, 1);

insert into public.user (id,login) values (2, 'u-2');
insert into public.meter (id,serialnumber) values (2, 'sn-2');
insert into public.users_web (id,login) values (2, 'uw-2');
insert into public.meter_user (iduser,idmeter) values (2, 2);

delete from meter_user where iduser = 2 and idmeter = 2;
delete from users_web where id = 2;