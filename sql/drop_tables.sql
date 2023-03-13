use `restaurant_db`;

alter table `users` drop constraint `user_role_fk`; 
alter table `users` drop constraint `user_order_fk`; 
alter table `addresses` drop constraint `address_user_fk`; 
alter table `dishes` drop constraint `dish_category_fk`; 
alter table `orders` drop constraint `order_address_fk`; 
alter table `orders` drop constraint `order_status_fk`; 
alter table `orders` drop constraint `order_customer_fk`; 
alter table `orders` drop constraint `order_manager_fk`; 
alter table `order_items` drop constraint `order_item_dish_fk`; 
alter table `order_items` drop constraint `order_item_order_fk`; 
alter table `reviews` drop constraint `review_user_fk`; 

drop table `users`;
drop table `roles`;
drop table `addresses`;
drop table `dishes`;
drop table `categories`;
drop table `orders`;
drop table `statuses`;
drop table `order_items`;
drop table `reviews`;

drop view order_items_dishes_view;