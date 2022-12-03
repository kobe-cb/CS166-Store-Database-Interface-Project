COPY Users
FROM '/extra/kbrag003/project_phase_3/project/data/users.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101;

COPY Store
FROM '/extra/kbrag003/project_phase_3/project/data/stores.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Product
FROM '/extra/kbrag003/project_phase_3/project/data/products.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Warehouse
FROM '/extra/kbrag003/project_phase_3/project/data/warehouse.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Orders
FROM '/extra/kbrag003/project_phase_3/project/data/orders.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE orders_orderNumber_seq RESTART 501;


COPY ProductSupplyRequests
FROM '/extra/kbrag003/project_phase_3/project/data/productSupplyRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productsupplyrequests_requestNumber_seq RESTART 11;

COPY ProductUpdates
FROM '/extra/kbrag003/project_phase_3/project/data/productUpdates.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productupdates_updateNumber_seq RESTART 51;
