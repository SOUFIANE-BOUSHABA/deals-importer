CREATE TABLE deals (
  id BIGSERIAL PRIMARY KEY,
  deal_unique_id VARCHAR(128) NOT NULL,
  from_currency VARCHAR(3) NOT NULL,
  to_currency VARCHAR(3) NOT NULL,
  deal_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
  amount NUMERIC(19,4) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL
);

CREATE UNIQUE INDEX ux_deals_unique_id ON deals(deal_unique_id);

CREATE INDEX ix_deals_timestamp ON deals(deal_timestamp);