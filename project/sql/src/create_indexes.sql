drop index if exists name_index;
drop index if exists userid_index;
drop index if exists storeID_Product_index;
drop index if exists customerID_Orders_index;
drop index if exists managerID_Store_index;
drop index if exists productName_Product_index;

CREATE INDEX name_index
ON Users 
using btree (name);

CREATE INDEX userid_index
ON Users
using btree (userid);

CREATE INDEX storeID_Product_index
ON Product
using btree (storeID);

CREATE INDEX customerID_Orders_index
ON Orders 
using btree (customerID);

CREATE INDEX managerID_Store_index
ON Store 
using btree (managerID);

CREATE INDEX productName_Product_index
ON Product
using btree (productname);


