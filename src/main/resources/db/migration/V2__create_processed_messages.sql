create table processed_messages (
    message_id uuid primary key,
    order_id uuid not null references customer_orders(id),
    processed_at timestamp with time zone not null
);

create index idx_processed_messages_order_id on processed_messages(order_id);
