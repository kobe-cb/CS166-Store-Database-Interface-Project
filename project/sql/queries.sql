-- viewStores

SELECT latitude, longitude FROM Users WHERE 'Abdullah' = name;

-- viewProducts

SELECT * FROM Product WHERE storeID = 18;

-- viewPopularProducts

SELECT productName, SUM(unitsordered) FROM Orders 
WHERE storeID in (
    SELECT storeID FROM Store WHERE 10 = managerID
    ) 
    GROUP BY productName 
    ORDER BY SUM(unitsordered) 
    DESC LIMIT 5;

-- viewPopularCustomers

SELECT name, SUM(unitsordered) FROM Orders, Users 
WHERE storeID in (
    SELECT storeID FROM Store WHERE 10 = managerID
    ) 
    AND customerID = userID 
    GROUP BY name 
    ORDER BY SUM(unitsordered) 
    DESC LIMIT 5;

-- viewOrders

SELECT * FROM Orders 
WHERE storeID in (
    SELECT storeID FROM Store WHERE 10 = managerID
    );