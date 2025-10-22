
create database if not exists badminton_booking character set utf8mb4 collate utf8mb4_unicode_ci;
use badminton_booking;

create table if not exists users (
	id bigint auto_increment primary key,
    email varchar(255) not null unique,
    password varchar(255) not null,
    full_name varchar (255) not null,
    phone_bumber varchar(20) not null unique,
    role enum('ROLE_USER', 'ROLE_ADMIN') not null default 'ROLE_USER',
    careted_at timestamp default current_timestamp
) engine = InnoDB;

drop table users;

create table if not exists courts (
	id bigint auto_increment primary key,
    name varchar(100) not null,
    description TEXT,
    base_price_per_hour decimal(10,2) not null,
    status enum('ACTIVE', 'UNDER_MAINTENANCE') not null default 'ACTIVE'
) engine = InnoDB;

create table if not exists pricing_rules (
	id bigint auto_increment primary key,
    rule_name varchar(255) not null,
    start_time time not null,
    end_time time not null,
    price_multiplier decimal(4, 2) not null,
    is_active boolean default TRUE
) engine = InnoDB;

create table if not exists bookings (
    id bigint auto_increment primary key,
    
    court_id bigint not null,
    
    user_id bigint null, 
    
    guest_name varchar(255),
    guest_phone varchar(20),
    guest_email varchar(255),
    
    start_time datetime not null,           
    end_time datetime not null,             

    base_price decimal(10, 2) not null,     
    price_multiplier decimal(4, 2) not null default 1.0, 
    total_price decimal(10, 2) not null,  
    
    status enum('PENDING', 'CONFIRMED', 'CANCELLED') not null default 'PENDING',
    created_at timestamp default current_timestamp,
    
    foreign key (court_id) references courts(id),
    foreign key (user_id) references users(id) on delete set null,
    
    index idx_start_time (start_time),
    index idx_court_id_start_time (court_id, start_time)
) engine=InnoDB;

create table if not exists payments(
	id bigint auto_increment primary key,
    booking_id bigint not null unique,
    
    payment_method varchar(50),
    amount decimal(10,2) not null,
    status enum('PENDING', 'SUSCCESS', 'FAILED'),
    transaction_code varchar(255), -- mã giao dịch 
	created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp on update current_timestamp,
    foreign key (booking_id) references bookings(id) on delete cascade
) engine = InnoDB;

-- tạo bảng 10 sân cầu
insert into courts (name, description, base_price_per_hour) values
	('Sân 1', 'Sân thảm tiêu chuẩn, ánh sáng tốt', 100000.00),
	('Sân 2', 'Sân thảm tiêu chuẩn, ánh sáng tốt', 100000.00),
    ('Sân 3', 'Sân thảm tiêu chuẩn, ánh sáng tốt', 100000.00),
    ('Sân 4', 'Sân thảm tiêu chuẩn, ánh sáng tốt', 100000.00),
    ('Sân 5', 'Sân thảm tiêu chuẩn, ánh sáng tốt', 100000.00),
	('Sân 6', 'Sân thảm, gần cửa', 100000.00),
    ('Sân 7', 'Sân thảm, gần cửa', 100000.00),
    ('Sân 8', 'Sân thảm, khu vực VIP', 120000.00),
    ('Sân 9', 'Sân thảm, gần cửa', 100000.00),
    ('Sân 10', 'Sân tập', 80000.00);
    
-- tạo bảng quy tắc (giờ cao điểm tăng 20%)    
insert into pricing_rules(rule_name, start_time, end_time, price_multiplier) values
	('Giờ cao điểm tối', '18:00:00', '22:00:00', 1.20);

-- tạo 1 tài khoản admin và 1 tài khoản user
insert into users (email, password, full_name, phone_number, role) values
	('admin@gmail.com','admin123', 'Quản Trị Viên', '0393565226', 'ROLE_ADMIN'),
    ('khachhang@gmail.com', 'khachhang', 'Dương Văn Dương', '0812084492', 'ROLE_USER');

show tables ;
select *from pricing_rules;
