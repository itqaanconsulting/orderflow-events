create table order_processing_outbox (
    message_id uuid primary key,
    order_id uuid not null references customer_orders(id),
    status varchar(20) not null,
    attempts integer not null,
    last_error varchar(1000),
    created_at timestamp with time zone not null,
    published_at timestamp with time zone
);

create index idx_order_processing_outbox_status_created_at
    on order_processing_outbox(status, created_at);
