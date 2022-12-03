/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import java.text.*;
import java.util.Date;


/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Retail {

   // * Current User ID *
   static private String retail_user_id;

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Retail shop
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Retail(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Retail

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Retail.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Retail esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Retail object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Retail (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("========= WELCOME! =========");
            System.out.println("---------------------------");
            System.out.println("      Sign up / Log in     ");
            System.out.println("---------------------------");
            System.out.println("1. Sign up / Create User");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              System.out.println("\nSuccessfully logged in!\n");
              boolean usermenu = true;
              String customerPrint = "1. View Stores within 30 miles\n2. View Product List\n3. Place a Order\n4. View 5 recent orders";
              String managerPrint = "1. Update Product\n2. View 5 recent Product Updates Info\n3. View 5 Popular Items\n4. View 5 Popular Customers\n5. Place Product Supply Request to Warehouse";
              String adminPrint = "1. View user information\n2. Update user information\n3. View product information\n4. Update product information";
              String query = "SELECT type from users where userid = '" + retail_user_id + "';";
              List<List<String>> res = esql.executeQueryAndReturnResult(query);
              String uType = res.get(0).get(0).replaceAll("\\s+", "");
              while(usermenu) {
                System.out.println("---------------------------");
                System.out.println("MAIN MENU of " + uType + " (" + retail_user_id + ")");
                System.out.println("---------------------------\n");
                if (uType.equals("customer")) {
                  System.out.println(customerPrint);
                }
                else if (uType.equals("manager")) {
                  System.out.println(managerPrint);
                }
                else if (uType.equals("admin")) {
                  System.out.println(adminPrint);
                }
                /*
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");

                // admin
                System.out.println("10. View user information");
                System.out.println("11. Update user information");
                System.out.println("12. View product information");
                System.out.println("13. Update product information");
                System.out.println(".........................");
                */
                System.out.println("0. Log out\n");
                if (uType.equals("customer")) {
                     switch (readChoice()){
                        case 1: viewStores(esql); break; // customer
                        case 2: viewProducts(esql); break; // customer
                        case 3: placeOrder(esql); break; // customer
                        case 4: viewRecentOrders(esql); break; // customer

                        case 0: usermenu = false; break;
                        default : System.out.println("Unrecognized choice!"); break;
                  }
                }
                else if (uType.equals("manager")) { 
                  switch (readChoice()){
                     case 1: updateProduct(esql); break; // manager
                     case 2: viewRecentUpdates(esql); break; // manager
                     case 3: viewPopularProducts(esql); break; // manager
                     case 4: viewPopularCustomers(esql); break; // manager
                     case 5: placeProductSupplyRequests(esql); break; // manager

                     case 0: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
                }
                else if (uType.equals("admin")) {
                  switch (readChoice()){
                     case 1: adminViewUsers(esql); break; // admin
                     case 2: adminUpdateUsers(esql); break; // admin
                     case 3: adminViewProduct(esql); break; // admin
                     case 4: adminUpdateProduct(esql); break; // admin

                     case 0: usermenu = false; break;
                     default : System.out.println("Unrecognized choice!"); break;
                  }
                }
                /*
                switch (readChoice()){
                   case 1: viewStores(esql); break; // customer
                   case 2: viewProducts(esql); break; // customer
                   case 3: placeOrder(esql); break; // customer
                   case 4: viewRecentOrders(esql); break; // customer
                   case 5: updateProduct(esql); break; // manager
                   case 6: viewRecentUpdates(esql); break; // manager
                   case 7: viewPopularProducts(esql); break; // manager
                   case 8: viewPopularCustomers(esql); break; // manager
                   case 9: placeProductSupplyRequests(esql); break; // manager
                   case 10: adminViewUsers(esql); break; // admin
                   case 11: adminUpdateUsers(esql); break; // admin
                   case 12: adminViewProduct(esql); break; // admin
                   case 13: adminUpdateProduct(esql); break; // admin

                   case 0: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
                */
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();

         String type = "";
         String code = "";
         System.out.println("Please choose the type of user this will be. \n1. Customer\n2. Manager (Requires code)\n3. Admin (Requires code)");
         String choice = in.readLine();
         if (choice.equals("1")) {
            type = "customer";
         }  
         else if (choice.equals("2")) {
            System.out.println("You have chosen manager, please enter the special code to create new managers.");
            code = in.readLine();
            if (code.equals("1234")) {
               type = "manager";
            } else {
               System.out.println("Invalid code, returning to main menu.");
               return;
            }
         }  
         else if (choice.equals("3")) {
            System.out.println("You have chosen admin, please enter the special code to create new admins.");
            code = in.readLine();
            if (code.equals("5678")) {
               type = "admin";
            } else {
               System.out.println("Invalid code, returning to main menu.");
               return;
            }
         }
         else {
            System.out.println("Unrecognized Type, returning to main menu.");
            return;
         }

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Retail esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
         query = "SELECT userid from users where name = '" + name + "' and password = '" + password + "';";
         List<List<String>> curr = esql.executeQueryAndReturnResult(query);
         String userID = curr.get(0).get(0);
         retail_user_id = userID;
         
		   return name;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here
   // * helpers *
   public static boolean isManager(Retail esql) {
      try {
         // * uses userID to check *
         String query = "SELECT type from users where userid = '" + retail_user_id + "';";
         List<List<String>> currUser = esql.executeQueryAndReturnResult(query);
         String userType = currUser.get(0).get(0).replaceAll("\\s+", "");
         if (!userType.equals("manager")) { // Can't use == since that checks if they're same object (reference equality) vs value equality
            System.out.println("ERR: not manager");
            return false;
         }
         return true;
      }catch(Exception e){
         System.err.println(e.getMessage());
       }
       return false;
   }

   public static boolean isManagerOwnerStore(Retail esql, String storeID) {
      try {
         String query = "SELECT * from store where storeid = '" + storeID + "' AND managerid = '" + retail_user_id + "';";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1){
            System.out.println("You do not have managerial access to this store or this store does not exist.");
            return false;
         }
         return true;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
      return false;
   }

   public static boolean isProductAtStore(Retail esql, String productName, String storeID) {
      try {
         String query = "SELECT * from product where productname = '" + productName + "' AND storeid = '" + storeID + "';";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1) {
            System.out.println("ERR: This product does not exist at this store.");
            return false;
         }
         return true;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }
      return false;
   }

   public static boolean isAdmin(Retail esql) {
      try {
         // * uses userID to check *
         String query = "SELECT type from users where userid = '" + retail_user_id + "';";
         List<List<String>> currUser = esql.executeQueryAndReturnResult(query);
         String userType = currUser.get(0).get(0).replaceAll("\\s+", "");
         if (!userType.equals("admin")) { // Can't use == since that checks if they're same object (reference equality) vs value equality
            System.out.println("ERR: not admin");
            return false;
         }
         return true;
      }catch(Exception e){
         System.err.println(e.getMessage());
       }
       return false;
   }
   
   // *----* 
   // * admin *
   public static void adminViewUsers(Retail esql) {
      try {      
         // * (fxn) Check if user is an admin *
         if (!isAdmin(esql)) {
            return;
         }

         System.out.println("Please enter the userid of the user you wish to view:");
         String userID = in.readLine();

         String query = "SELECT * from users where userid = '" + userID + "';";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1) {
            System.out.println("User not found, returning to main menu...");
            return;
         }
         List<List<String>> result = esql.executeQueryAndReturnResult(query);
         String reUserID = result.get(0).get(0);
         String reUserName = result.get(0).get(1).replaceAll("\\s+", ""); ;
         String reUserPassword = result.get(0).get(2);
         String reUserLat = result.get(0).get(3);
         String reUserLong = result.get(0).get(4);
         String reUserType = result.get(0).get(5);

         System.out.println("User: " + reUserName + " (" + reUserID + ") | " + reUserPassword + "\nCoordiates: " + reUserLat + ", " + reUserLong + "\nType: " + reUserType);
         return;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   public static void adminUpdateUsers(Retail esql) {
      try {
      // * (fxn) Check if user is an admin *
      if (!isAdmin(esql)) {
         return;
      }

      System.out.println("Please enter the userid of the user you wish to update:");
      String userID = in.readLine();

      String query = "SELECT * from users where userid = '" + userID + "';";
      int rowCount = esql.executeQuery(query);
      if (rowCount < 1) {
         System.out.println("User not found, returning to main menu...");
         return;
      }
      List<List<String>> result = esql.executeQueryAndReturnResult(query);
      String reUserID = result.get(0).get(0);
      String reUserName = result.get(0).get(1).replaceAll("\\s+", ""); ;
      String reUserPassword = result.get(0).get(2);
      String reUserLat = result.get(0).get(3);
      String reUserLong = result.get(0).get(4);
      String reUserType = result.get(0).get(5);

      String choice = "";
      String update = "";
      System.out.println("User: " + reUserName + " (" + reUserID + ") | " + reUserPassword);
      System.out.println("Would you like to update the user's username?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new user's username: ");
         update = in.readLine();
         query = "UPDATE users SET name = '" + update + "' WHERE userid = '" + reUserID + "';";
         esql.executeUpdate(query); 
      }

      System.out.println("Would you like to update the user's password?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new user's password: ");
         update = in.readLine();
         query = "UPDATE users SET password = '" + update + "' WHERE userid = '" + reUserID + "';";
         esql.executeUpdate(query); 
      }

      System.out.println("Coordiates: " + reUserLat + ", " + reUserLong);
      System.out.println("Would you like to update the user's latitude?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new user's latitude: (00.000000)");
         update = in.readLine();
         query = "UPDATE users SET latitude = '" + update + "' WHERE userid = '" + reUserID + "';";
         esql.executeUpdate(query); 
      }

      System.out.println("Would you like to update the user's longitude?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new user's longitude: (00.000000)");
         update = in.readLine();
         query = "UPDATE users SET longitude = '" + update + "' WHERE userid = '" + reUserID + "';";
         esql.executeUpdate(query); 
      }

      System.out.println("Type: " + reUserType);
      System.out.println("Would you like to update the user's type?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please choose the user's type: \n1. Customer\n2.Manager\n3.Admin");
         choice = in.readLine();
         if (choice.equals("1")) {
            update = "customer";
         }
         else if (choice.equals("2")) {
            update = "manager";
         }
         else if (choice.equals("3")) {
            update = "admin";
         }
         else {
            System.out.println("Option unrecognized, changes unsaved.");
            update = reUserType;
         }
         query = "UPDATE users SET type = '" + update + "' WHERE userid = '" + reUserID + "';";
         esql.executeUpdate(query); 
      }

      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   public static void adminViewProduct(Retail esql) {
      try {
      // * (fxn) Check if user is an admin *
      if (!isAdmin(esql)) {
         return;
      }
      System.out.println("Please enter the productname of the product you wish to view:");
      String productName = in.readLine();
      System.out.println("Please enter the storeid of the product you wish to view:");
      String storeID = in.readLine();
      if (!isProductAtStore(esql, productName, storeID)) {
         System.out.println("Invalid parameters, returning to main menu...");
         return;
      }
      
      String query = "SELECT * from product where productname = '" + productName + "' AND storeid = '" + storeID + "';";
      int rowCount = esql.executeQuery(query);
      if (rowCount < 1) {
         System.out.println("Product not found, returning to main menu...");
         return;
      }
      List<List<String>> result = esql.executeQueryAndReturnResult(query);
      String reStoreID = result.get(0).get(0);
      String reProductName = result.get(0).get(1).replaceAll("\\s+", ""); ;
      String reNumUnits = result.get(0).get(2);
      String rePricePerUnit = result.get(0).get(3);

      System.out.println("Store " + reStoreID + " has " + reNumUnits + " units of " + reProductName + " at $" + rePricePerUnit + " each.");
      return;


      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   public static void adminUpdateProduct(Retail esql) {
      try {
      // * (fxn) Check if user is an admin *
      if (!isAdmin(esql)) {
         return;
      }
      System.out.println("Please enter the productname of the product you wish to update:");
      String productName = in.readLine();
      System.out.println("Please enter the storeid of the product you wish to update:");
      String storeID = in.readLine();
      if (!isProductAtStore(esql, productName, storeID)) {
         System.out.println("Invalid parameters, returning to main menu...");
         return;
      }
      
      String query = "SELECT * from product where productname = '" + productName + "' AND storeid = '" + storeID + "';";
      int rowCount = esql.executeQuery(query);
      if (rowCount < 1) {
         System.out.println("Product not found, returning to main menu...");
         return;
      }
      List<List<String>> result = esql.executeQueryAndReturnResult(query);
      String reStoreID = result.get(0).get(0);
      String reProductName = result.get(0).get(1).replaceAll("\\s+", ""); ;
      String reNumUnits = result.get(0).get(2);
      String rePricePerUnit = result.get(0).get(3);

      System.out.println("Store " + reStoreID + " has " + reNumUnits + " units of " + reProductName + " at $" + rePricePerUnit + " each.");

      String choice = "";
      String update = "";
      System.out.println("Would you like to update the number of units for the product?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new number of units: ");
         update = in.readLine();
         query = "UPDATE product SET numberofunits = '" + update + "' where productname = '" + productName + "' AND storeid = '" + storeID + "';";
         esql.executeUpdate(query); 
      }

      System.out.println("Would you like to update the price per unit for the product?");
      System.out.println("1. Yes\n2. No");
      choice = in.readLine();
      if (choice.equals("1")) {
         System.out.println("Please enter the new price per unit: ");
         update = in.readLine();
         query = "UPDATE product SET priceperunit = '" + update + "' where productname = '" + productName + "' AND storeid = '" + storeID + "';";
         esql.executeUpdate(query); 
      }
      System.out.println("Updates completed succesfully! Returning to main menu.");
      return;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   // *----* 
   // * Regular *
   public static void viewStores(Retail esql) {}
   public static void viewProducts(Retail esql) {}
   public static void placeOrder(Retail esql) {
      try {
         // keep track of userid when logged in
         String userID = retail_user_id;
         String query = "";
         List<List<String>> latList;
         List<List<String>> longList;

         /*
         query = "SELECT latitude from users where userid = ";
         query += "'" + userID + "';";
         //double latUser = esql.executeQuery(query);
         //double latUser = esql.executeQueryAndPrintResult(query);
         List<List<String>> latList = esql.executeQueryAndReturnResult(query);
         String latUserS = latList.get(0).get(0);
         double latUser = Double.parseDouble(latUserS);

         System.out.println("Answer: " + latUser);
         //System.out.println("Type: " + latUser.getClass().getSimpleName());

         System.out.println("Outside: " + latList.get(0));
         */

         System.out.println("Please enter storeID: ");
         String storeID = in.readLine();
         //String storeID = "2";
         System.out.println("Please enter productName: ");
         String productName = in.readLine();
         //String productName = "7up";
         System.out.println("Please enter numberofUnits: ");
         String numberofUnits = in.readLine();
         //String numberofUnits = "10";

         query = "SELECT * from product where storeid = '" + storeID + "' and productname = '" + productName + "';";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1) {
            System.out.println("ERR: The product does not exist at this store or the store does not exist. We apologize for the inconvenience.");
            return;
         }

         query = "SELECT latitude from users where userid = ";
         query += "'" + userID + "';";
         latList = esql.executeQueryAndReturnResult(query);
         String latUserS = latList.get(0).get(0);
         double latUser = Double.parseDouble(latUserS);
         
         query = "SELECT longitude from users where userid = ";
         query += "'" + userID + "';";
         longList = esql.executeQueryAndReturnResult(query);
         String longUserS = longList.get(0).get(0);
         double longUser = Double.parseDouble(longUserS);

         query = "SELECT latitude from store where storeid = ";
         query += "'" + storeID + "';";
         latList = esql.executeQueryAndReturnResult(query);
         String latStoreS = latList.get(0).get(0);
         double latStore = Double.parseDouble(latStoreS);

         query = "SELECT longitude from store where storeid = ";
         query += "'" + storeID + "';";
         longList = esql.executeQueryAndReturnResult(query);
         String longStoreS = longList.get(0).get(0);
         double longStore = Double.parseDouble(longStoreS);

         //System.out.println("User: " + latUser + ", " + longUser);
         //System.out.println("Store: " + latStore + ", " + longStore);

         double miles = calculateDistance(latUser, longUser, latStore, longStore);
         //System.out.println("Distance: " + miles);
         miles = 0;
         if (miles > 30) {
            System.out.println("ERR: nah bruh ur out of range");
            return;
         }
         
         //orders is table of orders going through
         //product has current inventory of products
         //**1. pull numberof units from product (productname, storeid) and check if its enough
         //**2. insert into order (...)
         //**3. update product set numberofunits='61' where productname='7up' and storeid='2';
         //**4. select priceperunit from product where ....
         //**5. select name from store where storeid = ...
         //**5. print(That will cost you priceperunit * numberofUnits dollars for numberofUnits productname at storename (storeid))
         //0000
         //**Check if store has product


         /*
         query = "delete from orders where ordernumber=502;";
         esql.executeUpdate(query);
         query = "INSERT INTO orders (ordernumber, customerid, storeid, productname, unitsordered, ordertime) VALUES ('502', '22', '3', 'Brisk', '22', '2017-09-10 23:23:11');";
         esql.executeUpdate(query);
         */


         // * Creates date format for sql insert *
         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Date date = new Date();
         String currDate = dateFormat.format(date);

         // * Grab latest ordernumber from orders *
         List<List<String>> currOrders;
         query = "select ordernumber from orders order by ordernumber desc limit 5;";
         currOrders = esql.executeQueryAndReturnResult(query);
         String newestOrder = currOrders.get(0).get(0);
         int newOrder = Integer.parseInt(newestOrder);
         newOrder += 1;
         newestOrder = String.valueOf(newOrder);

         // * Grab current numberofunits from product where storeid and productname *
         List<List<String>> currProduct;
         query = "select numberofunits, priceperunit from product where productname = '" + productName + "' and storeid = '" + storeID + "';";
         currProduct = esql.executeQueryAndReturnResult(query);
         String unitInventory = currProduct.get(0).get(0);
         
         // * Check if current product inventory has enough for user's number of units *
         if (Integer.parseInt(unitInventory) < Integer.parseInt(numberofUnits)) {
            System.out.println("ERR: You have requested more than what we have at this store, " + unitInventory + " < " + numberofUnits);
            return;
         }
         

         // * Insert into Orders the user's order information *
         //query = String.format("INSERT INTO orders (ordernumber, customerid, storeid, productname, unitsordered, ordertime) VALUES ('%s','%s', %s, %s,'%s', '%s');", newestOrder, userID, storeID, productName, numberofUnits, currDate);
         query = "INSERT INTO orders (ordernumber, customerid, storeid, productname, unitsordered, ordertime) VALUES ('" + newestOrder + "', '" + userID + "', '" + storeID + "', '" + productName + "', '" + numberofUnits + "', '" + currDate + "');";
         esql.executeUpdate(query);

         // * Update product inventory to reflect new order *
         int totalUnits = Integer.parseInt(unitInventory) - Integer.parseInt(numberofUnits);
         String totalUnitsS = String.valueOf(totalUnits); 
         query = "UPDATE product SET numberofunits = '" + totalUnitsS + "' where storeid = '" + storeID + "' and productname = '" + productName + "';";
         esql.executeUpdate(query);

         // * Print information of recent order; Product Price and Store Name
         List<List<String>> currStore;
         query = "select name from store where storeid = '" + storeID + "';";
         currStore = esql.executeQueryAndReturnResult(query);
         String storeName = currStore.get(0).get(0).replaceAll("\\s+", ""); //removes all whitespace and non-visible characters (e.g. tab \t) https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java

         String priceperunit = currProduct.get(0).get(1);
         int totalCost = Integer.parseInt(priceperunit) * Integer.parseInt(numberofUnits);
         String totalCostS = String.valueOf(totalCost);
         System.out.println("You have successfully placed an order at " + storeName + "(" + storeID + ") for " + numberofUnits + " units of " + productName + " each costing $" + priceperunit + ", totaling $" + totalCostS + ". Thank you!");


         // select * from store where false;
         // select * from store limit 1;


         return;
      }
      catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }
   public static void viewRecentOrders(Retail esql) {
      //3
      // SELECT o.storeid, s.name, o.productname, o.unitsordered, o.ordertime FROM orders o INNER JOIN store s ON (o.storeid = s.storeid) order by ordertime desc limit 5;
      try {
         String query = "SELECT o.storeid, s.name, o.productname, o.unitsordered, o.ordertime FROM orders o INNER JOIN store s ON (o.storeid = s.storeid) WHERE o.customerid = '" + retail_user_id + "' order by ordertime desc limit 5;";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1) {
            System.out.println("ERR: You don't have any recent orders");
            return;
         }
         esql.executeQueryAndPrintResult(query);

         return;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   public static void updateProduct(Retail esql) {
      try {
         String query = "";
      //2 Managers can view last 5 recent updates of his/her store(s)

      // * (fxn) Check if user is a manager *
      if (!isManager(esql)) {
         return;
      }

      // * (fxn) Ask for storeID and check if managerID matches it *
      System.out.println("Please enter the store's ID: ");
      String storeID = in.readLine();
      if (!isManagerOwnerStore(esql, storeID)) {
         return;
      }

      // * (fxn) Ask for product name and check if it exists at that store *
      System.out.println("Please enter the product's name: ");
      String productName = in.readLine();
      if (!isProductAtStore(esql, productName, storeID)) {
         return;
      }

      // * Showcase product's current [numberofunits] and [priceperunit] and ask *
      query = "SELECT numberofunits, priceperunit from product where productname = '" + productName + "' AND storeid = '" + storeID + "';";
      List<List<String>> currProduct = esql.executeQueryAndReturnResult(query);
      String numUnits = currProduct.get(0).get(0);
      String priceUnits = currProduct.get(0).get(1);
      System.out.println("\n" + productName + " currently has " + numUnits + " priced at " + priceUnits + " each. Would you like to update this?\n 1. Update number of units.\n 2. Update price per unit.\n 3. Update both.\n 4. Return to menu.");
      String userChoice = in.readLine();
      if (userChoice.equals("1")) {
         System.out.println("Please enter the new number of units.");
         numUnits = in.readLine();
      }
      else if (userChoice.equals("2")) {
         System.out.println("Please enter the new price per unit.");
         priceUnits = in.readLine();
      }
      else if (userChoice.equals("3")) {
         System.out.println("Please enter the new number of units.");
         numUnits = in.readLine();
         System.out.println("Please enter the new price per unit.");
         priceUnits = in.readLine();
      }
      else if (userChoice.equals("4")) {
         return;
      }
      else {
         System.out.println("unrecognized option, returning to menu ...");
         return;
      }

      // * Update the product table *
      query = "UPDATE product SET numberofunits = '" + numUnits + "', priceperunit = '" + priceUnits + "' WHERE storeid = '" + storeID + "' AND productname = '" + productName + "';";
      esql.executeUpdate(query);
      System.out.println("You have successfully updated the product.");

      // * Update the productupdates table *
      // -- * Grab current updatenumber to use later (currUpdateNumber) *
      query = "SELECT * from productUpdates order by updatenumber desc limit 1";
      List<List<String>> currUpdateTable = esql.executeQueryAndReturnResult(query);
      String currUpdateNumber = currUpdateTable.get(0).get(0);
      int currNum = Integer.parseInt(currUpdateNumber);
      currNum += 1;
      currUpdateNumber = String.valueOf(currNum);

      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date date = new Date();
      String currDate = dateFormat.format(date);
      query = "INSERT INTO productUpdates (updatenumber, managerid, storeid, productname, updatedon) VALUES ('" + currUpdateNumber + "', '" + retail_user_id + "', '" + storeID + "', '" + productName + "', '" + currDate + "');";
      esql.executeUpdate(query);


      // **Ask for storeID,
      // **Check if storeID matches managerID
      // **Ask for productname
      // **Showcase current numberofunits and priceperunit; ask if they would like to make an update | else "this productname doesn't exist at this store" == doublequery
      // **Ask for update to numberofunits?
      // **Ask for update to priceperunit?
      // **update query
      // **insert productUpdates
      //
      // stores have one manager; managers have multiple stores
      return;
      }catch(Exception e){
         System.err.println(e.getMessage());
      }

   }
   public static void viewRecentUpdates(Retail esql) {
      try {
      //2 Managers can view last 5 recent updates of his/her store(s)
      String userID = "10";
      String query = "SELECT type from users where userid = '" + userID + "';";
      List<List<String>> currUser = esql.executeQueryAndReturnResult(query);
      String userType = currUser.get(0).get(0).replaceAll("\\s+", "");
      if (!userType.equals("manager")) { // Can't use == since that checks if they're same object (reference equality) vs value equality
         System.out.println("ERR: not manager");
         return;
      }

      query = "SELECT * from productUpdates where managerid = '" + userID + "' order by updatedon desc limit 5";
      esql.executeQueryAndPrintResult(query);
      return;
      }
      catch(Exception e){
         System.err.println(e.getMessage());
      }
   }
   public static void viewPopularProducts(Retail esql) {}
   public static void viewPopularCustomers(Retail esql) {}
   public static void placeProductSupplyRequests(Retail esql) {
      try {
         String query = "";
         // * (fxn) Check if user is a manager *
         if (!isManager(esql)) {
            return;
         }

         // * (fxn) Ask for storeID and check if managerID matches it *
         System.out.println("Please enter the store's ID: ");
         String storeID = in.readLine();
         if (!isManagerOwnerStore(esql, storeID)) {
            return;
         }

         // * (fxn) Ask for product name and check if it exists at that store *
         System.out.println("Please enter the product's name: ");
         String productName = in.readLine();
         if (!isProductAtStore(esql, productName, storeID)) {
            return;
         }

         // * Print information of recent order; Product Price and Store Name
         List<List<String>> currStore;
         query = "select name from store where storeid = '" + storeID + "';";
         currStore = esql.executeQueryAndReturnResult(query);
         String storeName = currStore.get(0).get(0).replaceAll("\\s+", "");

         // * Showcase product's current [numberofunits] and [priceperunit] and ask *
         query = "SELECT numberofunits from product where productname = '" + productName + "' AND storeid = '" + storeID + "';";
         List<List<String>> currProduct = esql.executeQueryAndReturnResult(query);
         String numUnits = currProduct.get(0).get(0);
         String newUnits = "";
         System.out.println(storeName + " (" + storeID + ") has " + numUnits + " units of " + productName + ". " + "Would you like to update this?\n 1. Update number of units.\n 2. Return to menu.");
         String userChoice = in.readLine();
         if (userChoice.equals("1")) {
            System.out.println("Please enter the requested amount of units.");
            newUnits = in.readLine();
         }
         else if (userChoice.equals("2")) {
            return;
         }
         else {
            System.out.println("unrecognized option, returning to menu ...");
            return;
         }

         // * grab warehouse ID
         System.out.println("Please enter the warehouse ID you would like to request supplies from:");
         String warehouseID = in.readLine();
         query = "SELECT * from warehouse where warehouseid = '" + warehouseID + "';";
         int rowCount = esql.executeQuery(query);
         if (rowCount < 1) {
            System.out.println("Warehouse not found from warehouse ID. Returning to main menu...");
            return;
         }
         
         // * Grab latest request number *
         query = "SELECT requestnumber from productsupplyrequests order by requestnumber desc limit 1";
         List<List<String>> warehouseRequest = esql.executeQueryAndReturnResult(query);
         String currRequestNumber = warehouseRequest.get(0).get(0);
         int requestNumber = Integer.parseInt(currRequestNumber);
         requestNumber += 1;
         currRequestNumber = String.valueOf(requestNumber);

         // * make insertion into productsupplyrequest *
         query = "INSERT INTO productsupplyrequests VALUES ('" + currRequestNumber + "', '" + retail_user_id + "', '" + warehouseID + "', '" + storeID + "', '" + productName + "', '" + numUnits + "');";
         esql.executeUpdate(query);
         System.out.println("Product supply request has been successfully placed at " + warehouseID);

         // * Update the product table *
         numUnits = String.valueOf(Integer.parseInt(numUnits) + Integer.parseInt(newUnits));
         query = "UPDATE product SET numberofunits = '" + numUnits + "' WHERE storeid = '" + storeID + "' AND productname = '" + productName + "';";
         esql.executeUpdate(query);

         // * Update the productupdates table *
         // -- * Grab current updatenumber to use later (currUpdateNumber) *
         query = "SELECT * from productUpdates order by updatenumber desc limit 1";
         List<List<String>> currUpdateTable = esql.executeQueryAndReturnResult(query);
         String currUpdateNumber = currUpdateTable.get(0).get(0);
         int currNum = Integer.parseInt(currUpdateNumber);
         currNum += 1;
         currUpdateNumber = String.valueOf(currNum);

         DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
         Date date = new Date();
         String currDate = dateFormat.format(date);
         query = "INSERT INTO productUpdates (updatenumber, managerid, storeid, productname, updatedon) VALUES ('" + currUpdateNumber + "', '" + retail_user_id + "', '" + storeID + "', '" + productName + "', '" + currDate + "');";
         esql.executeUpdate(query);


         System.out.println(storeName + " (" + storeID + ")" + " now has " + numUnits + " units of " + productName + ".");

         return;


      }catch(Exception e){
         System.err.println(e.getMessage());
      }


   }

}//end Retail

