package mainPackage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLAccess {
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private ResultSet resultSet = null;
  
  private String myUsername = "serveruser";
  private String myPassword = "serverpassword";
  private String myDatabase = "accountData";
  private String myTable = "LOGININFO";

  public MYSQL_ACCESS_TYPE readDataBase(MYSQL_ACCESS_TYPE t, String username, String password) throws SQLException {
    try {
      //load mysql driver
      try {
		Class.forName("com.mysql.jdbc.Driver");
	   } catch (ClassNotFoundException e) {
		e.printStackTrace();
	   }
      //connect to the database on localhost
      connect = DriverManager
          .getConnection("jdbc:mysql://localhost/" + myDatabase + "?"
              + "user=" + myUsername + "&password=" + myPassword);
      
      //if the access type to the database was a request to register, then
      //check if a username exists -- if it does, return a register username already exists error
      //if it doesnt, then register it into the database
      if(t==MYSQL_ACCESS_TYPE.REGISTER){
	      preparedStatement = connect
	    		  .prepareStatement("SELECT COUNT(1) from " + myTable + " where username = ?;");
	      preparedStatement.setString(1, username);
	      resultSet = preparedStatement.executeQuery();
	      resultSet.first();
	      //if the resulting count said that there are more than 0 users with that username
	      //then refuse registration with proper error type
	      if(resultSet.getInt(1)>0){
	    	  return MYSQL_ACCESS_TYPE.REGISTER_ERROR_USERNAME_ALREADY_EXISTS;
	      }else{
	    	  //otherwise, register the user into the database
	    	  //and recursively call readDataBase in order to log in that user.
	    	  preparedStatement = connect
	    			  .prepareStatement("insert into " + myTable + " values (default, ?, ?)");
	    	  preparedStatement.setString(1, username);
	    	  preparedStatement.setString(2, password);
	    	  preparedStatement.executeUpdate();
	    	  return MYSQL_ACCESS_TYPE.REGISTER_SUCCESS;
	      }
      }
      
      if(t==MYSQL_ACCESS_TYPE.LOGIN){
    	  preparedStatement = connect
    			  .prepareStatement("SELECT username,pass FROM " + myTable + " where username = ?");
    	  preparedStatement.setString(1, username);
    	  resultSet = preparedStatement.executeQuery();
    	  boolean isEmpty = !(resultSet.next());
    	  //if the query returned an empty login name, return an error back to the caller
    	  if(isEmpty){
    		  return MYSQL_ACCESS_TYPE.LOGIN_ERROR_USERNAME_NOT_FOUND;
    	  }
    	  //otherwise, check whether the password given matches the password stored in the database
    	  //if it does, then proceed with the login
    	  if(resultSet.getString(2).compareTo(password)==0){
    		  return MYSQL_ACCESS_TYPE.LOGIN_SUCCESS;
    	  }else{
    		  //if it doesnt, return incorrect password error
    		  return MYSQL_ACCESS_TYPE.LOGIN_ERROR_INCORRECT_PASSWORD;
    	  }
      }

      return MYSQL_ACCESS_TYPE.ERROR;
// 
      
    } catch (SQLException e) {
    	if(e.getErrorCode()==1062){
    		System.out.println("Duplicate entry detected. No changes were made.");
    	}
    	throw e;
    } finally {
      close();
    }

  }

  private void writeMetaData(ResultSet resultSet) throws SQLException {
    //   Now get some metadata from the database
    // Result set get the result of the SQL query
    
    System.out.println("The columns in the table are: ");
    
    System.out.println("Table: " + resultSet.getMetaData().getTableName(1));
    for  (int i = 1; i<= resultSet.getMetaData().getColumnCount(); i++){
      System.out.println("Column " +i  + " "+ resultSet.getMetaData().getColumnName(i));
    }
  }

  private void writeResultSet(ResultSet resultSet) throws SQLException {
    // ResultSet is initially before the first data set
    while (resultSet.next()) {
      // It is possible to get the columns via name
      // also possible to get the columns via the column number
      // which starts at 1
      // e.g. resultSet.getSTring(2);
      String username = resultSet.getString("username");
      String password = resultSet.getString("password");
      System.out.println("Username: " + username);
      System.out.println("Password: " + password);
    }
  }

  // You need to close the resultSet
  private void close() {
    try {
      if (resultSet != null) {
        resultSet.close();
      }

      if (statement != null) {
        statement.close();
      }

      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
  }

} 
