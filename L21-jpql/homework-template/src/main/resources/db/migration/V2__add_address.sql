CREATE TABLE address
(
    id bigserial not null primary key,
    address_line varchar(150) NOT NULL
);

ALTER TABLE IF EXISTS client
    ADD COLUMN address_id bigint;
ALTER TABLE IF EXISTS public.client
    ADD CONSTRAINT fk_client_address FOREIGN KEY (address_id)
    REFERENCES address (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    NOT VALID;