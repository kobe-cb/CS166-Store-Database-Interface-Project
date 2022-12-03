SELECT * FROM Users WHERE name = 'Audra';

SELECT * FROM Product WHERE storeID = 18;

SELECT o.storeid, s.name, o.productname, o.unitsordered, o.ordertime FROM orders o INNER JOIN store s ON (o.storeid = s.storeid) WHERE o.customerid = 5 order by ordertime desc limit 5;

SELECT * from store where storeid = 17 AND managerid = 20;

SELECT * from productUpdates where managerid = 10 order by updatedon desc limit 5;

SELECT * from warehouse where warehouseid = 3;