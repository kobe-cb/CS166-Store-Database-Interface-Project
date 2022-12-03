-- create indexes on equality, avoid indexes on columns that are updated frequently
drop index if exists name_index;
drop index if exists storeID_Product_index;
drop index if exists customerID_Orders_index;
drop index if exists storeID_Store_index;
drop index if exists managerID_productupdates_index;
drop index if exists warehouseID_warehouse_index;

CREATE INDEX name_index
ON Users 
using btree (name);

CREATE INDEX storeID_Product_index
ON Product 
using btree (storeID);

CREATE INDEX customerID_Orders_index
ON Orders 
using btree (customerID);

CREATE INDEX storeID_Store_index
ON Store 
using btree (storeID);

CREATE INDEX managerID_productupdates_index
ON productupdates 
using btree (managerID);

CREATE INDEX warehouseID_warehouse_index
ON warehouse 
using btree (warehouseID);

