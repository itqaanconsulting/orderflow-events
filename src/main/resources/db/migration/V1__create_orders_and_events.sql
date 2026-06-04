create table customer_orders (
    id uuid primary key,
    external_reference varchar(80) not null unique,
    customer_email varchar(160) not null,
    total_amount numeric(19, 2) not null,
    currency varchar(3) not null,
    status varchar(40) not null,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone not null
);

create table order_events (
    id uuid primary key,
    order_id uuid not null references customer_orders(id),
    type varchar(60) not null,
    message varchar(255) not null,
    created_at timestamp with time zone not null
);

create index idx_order_events_order_id_created_at on order_events(order_id, created_at);
