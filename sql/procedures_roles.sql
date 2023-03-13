use restaurant_db;

-- drop procedure find_role_by_id;
delimiter &&
create procedure find_role_by_id (in p_id int)
begin
	select * from roles where id = p_id;
end &&

-- drop procedure get_roles_sorted;
delimiter &&
create procedure get_roles_sorted (in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from roles order by ', p_sort, ' ', p_direction);
	prepare statement from @query;
	execute statement;
end &&

-- -------------------------------------------

-- drop procedure find_role_by_name;
delimiter &&
create procedure find_role_by_name (in p_name nvarchar(30))
begin
	select * from roles where name = p_name;
end &&