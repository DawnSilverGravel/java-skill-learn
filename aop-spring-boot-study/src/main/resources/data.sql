delete
from t_user
where id > 0;
insert into t_user
values (1, 'Silver', '123456'),
       (2, 'Gravel', '123456'),
       (3, 'Dawn', '123456');


delete
from t_role
where id > 0;

insert into t_role
values (1, 'Admin'),
       (2, 'User'),
       (3, 'VIP');

delete
from t_user_role
where id > 0;

insert into t_user_role
values (1, 1, 1),
       (2, 1, 2),
       (3, 2, 3),
       (4, 2, 1),
       (5, 3, 2);
