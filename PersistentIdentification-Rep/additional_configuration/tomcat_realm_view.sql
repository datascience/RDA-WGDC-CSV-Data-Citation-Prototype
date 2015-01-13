create view tomcat_groups_view as
select 
  tomcat_users.name as username,
  groups.name as groupname
from tomcat_users 
left join (
        select 
                tomcat_users_cr_groups.tomcat_users_id,cr_groups.name 
        from cr_groups 
        left join 
                tomcat_users_cr_groups 
                on tomcat_users_cr_groups.groups_id=cr_groups.id
) as groups on groups.tomcat_users_id=id;

create view tomcat_users_view as
select 
  name as username
from tomcat_users;