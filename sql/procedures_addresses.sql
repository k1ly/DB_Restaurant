use restaurant_db;

-- drop procedure find_address_by_id;
delimiter &&
create procedure find_address_by_id (in p_id int)
begin
	select * from addresses where id = p_id;
end &&

-- drop procedure add_address;
delimiter &&
create procedure add_address (in p_country nvarchar(30), in p_locality nvarchar(40), in p_street nvarchar(40), in p_house nvarchar(10), in p_apartment nvarchar(10),
	in p_user_id int)
begin
insert into addresses(`country`, `locality`, `street`, `house`, `apartment`, `user_id`)
	values (p_country, p_locality, p_street, p_house, p_apartment, p_user_id);
end &&

-- -------------------------------------------

-- drop procedure find_addresses_by_user_sorted;
delimiter &&
create procedure find_addresses_by_user_sorted (in p_user_id int, in p_sort nvarchar(20), in p_direction varchar(5))
begin
	set @query = concat('select * from addresses where user_id = ', p_user_id, ' order by ', p_sort, ' ', p_direction);
	prepare statement from @query;
	execute statement;
end &&