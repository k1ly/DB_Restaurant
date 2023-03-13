use restaurant_db;

-- drop procedure find_status_by_id;
delimiter &&
create procedure find_status_by_id (in p_id int)
begin
	select * from statuses where id = p_id;
end &&

-- -------------------------------------------

-- drop procedure find_status_by_name;
delimiter &&
create procedure find_status_by_name (in p_name nvarchar(30))
begin
	select * from statuses where name = p_name;
end &&