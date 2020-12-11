create table customers (
    customerID int not null generated always as identity constraint customer_pk primary key,
    fullname varchar(50) not null,
    salestax double not null,
    status varchar(20) not null,
    address varchar(70) not null,
    phone varchar(30) not null
);

create table employees (
    employeeID int not null generated always as identity constraint employee_pk primary key,
    fullname varchar(50) not null,
    commissionrate double not null,
    phone varchar(30) not null
);

create table warehouses (
    warehousename varchar(50) not null constraint warehouse_pk primary key,
    address varchar(70) not null,
    phone varchar(30) not null
);

create table products (
    productID int not null generated always as identity constraint product_pk primary key,
    productname varchar(100) not null,
    sellprice double not null,
    buyprice double not null,
    stock int not null,
    warehousename varchar(50) not null,
    constraint product_fk foreign key(warehousename)
    references warehouses(warehousename)
);

create table invoices (
    invoiceid int not null generated always as identity constraint invoice_pk primary key,
    total double not null,
    status varchar(20),
    remainingbalance double not null,
    customerid int not null,
    employeeid int not null,
    deliveryfee double not null,
    taxamount double not null,
    commissionamount double not null,
    thirtydaycount int not null,
    isdeliverable boolean not null,
    openeddate date not null,
    constraint invoice_fk1 foreign key(customerid)
    references customers (customerid),
    constraint invoice_fk2 foreign key (employeeid)
    references employees (employeeid)
);


create table orderdetails (
    invoiceid int not null,
    productid int not null,
    quantityordered int not null,
    constraint orderdetails_fk1 foreign key (invoiceid)
    references invoices (invoiceid),
    constraint orderdetails_fk2 foreign key (productid)
    references products (productid),
    constraint orderdetails_pk primary key (invoiceid, productid)
);

create table users (
    username varchar(50) not null constraint users_pk primary key,
    password varchar(50) not null
);