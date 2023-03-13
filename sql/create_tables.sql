use `restaurant_db`;

create table `users`
(
`id` int primary key auto_increment,
`login` varchar(20) not null unique,
`password` varchar(200) not null,
`name` nvarchar(40) not null,
`email` varchar(40) null,
`phone` varchar(15) null,
`blocked` boolean not null default false,
`role_id` int not null,
`order_id` int null
);

create table `roles`
(
`id` int primary key auto_increment,
`name` nvarchar(30) not null unique
);

create table `addresses`
(
`id` int primary key auto_increment,
`country` nvarchar(30) not null,
`locality` nvarchar(40) not null,
`street` nvarchar(40) null,
`house` nvarchar(10) not null,
`apartment` nvarchar(10) null,
`user_id` int not null
);

create table `dishes`
(
`id` int primary key auto_increment,
`name` nvarchar(40) not null,
`description` nvarchar(200) not null,
`image_url` varchar(100) null,
`weight` smallint not null default(0) check(`weight` >= 0),
`price` double(5,2) not null default(0) check(`price` >= 0),
`discount` tinyint not null default(0) check(`discount` >= 0 and `discount` <= 100),
`category_id` int not null
);

create table `categories`
(
`id` int primary key auto_increment,
`name` nvarchar(30) not null unique
);

create table `orders`
(
`id` int primary key auto_increment,
`price` double(10,2) null default(0) check(`price` >= 0),
`specified_date` datetime null,
`order_date` timestamp null,
`delivery_date` timestamp null,
`address_id` int null,
`status_id` int not null,
`customer_id` int not null,
`manager_id` int null
);

create table `statuses`
(
`id` int primary key auto_increment,
`name` nvarchar(30) not null unique
);

create table `order_items`
(
`id` int primary key auto_increment,
`quantity` int not null default(0) check(`quantity` >= 0),
`dish_id` int not null,
`order_id` int not null
);

create table `reviews`
(
`id` int primary key auto_increment,
`grade` tinyint null check(`grade` >= 1 and `grade` <= 5),
`comment` nvarchar(200) null,
`date` timestamp not null,
`user_id` int not null
);
    
-- -------------------------------------------

alter table `users` add
constraint `user_role_fk` foreign key (`role_id`) references `roles` (`id`);
alter table `users` add
constraint `user_order_fk` foreign key (`order_id`) references `orders` (`id`);

alter table `addresses` add
constraint `address_user_fk` foreign key (`user_id`) references `users` (`id`);

alter table `dishes` add
constraint `dish_category_fk` foreign key (`category_id`) references `categories` (`id`);

alter table `orders` add
constraint `order_address_fk` foreign key (`address_id`) references `addresses` (`id`);
alter table `orders` add
constraint `order_status_fk` foreign key (`status_id`) references `statuses` (`id`);
alter table `orders` add
constraint `order_customer_fk` foreign key (`customer_id`) references `users` (`id`);
alter table `orders` add
constraint `order_manager_fk` foreign key (`manager_id`) references `users` (`id`);

alter table `order_items` add
constraint `order_item_dish_fk` foreign key (`dish_id`) references `dishes` (`id`);
alter table `order_items` add
constraint `order_item_order_fk` foreign key (`order_id`) references `orders` (`id`);

alter table `reviews` add
constraint `review_user_fk` foreign key (`user_id`) references `users` (`id`) on delete cascade on update cascade;

create view order_items_dishes_view
	as select o.order_id, d.price, d.discount, o.quantity from order_items o join dishes d on o.dish_id = d.id;
