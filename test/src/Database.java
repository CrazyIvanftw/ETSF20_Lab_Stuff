import java.sql.*;

/*
 * Class for managing the database.
 */
public class Database {
	
	// If you have the mysql server on your own computer use "localhost" as server address.
	private static String databaseServerAddress = "localhost";
	//private static String databaseServerPassword = "akcDDei620";
	private static String databaseUser = "Greg";             // database login user
	private static String databasePassword = "";          // database login password
	private static String database = "etsf20";             // the database to use, i.e. default schema
	Connection conn = null;
	
	public Database() {
		try{
			Class.forName("com.mysql.jdbc.Driver");  //Necessary on Windows computers and mac
			conn = DriverManager.getConnection("jdbc:mysql://" + databaseServerAddress + "/" + 
                    database, databaseUser, databasePassword);
			
			// Display the contents of the database in the console. 
			// This should be removed in the final version
			Statement stmt = conn.createStatement();		    
		    ResultSet rs = stmt.executeQuery("select * from responses"); //Respondents
		    while (rs.next( )) {
		    	String name = rs.getString("name"); 
		    	System.out.println(name);
		    	}

		    stmt.close();
			
		} catch (SQLException e) {
			printSqlError(e);
		} catch (ClassNotFoundException e) {    
            e.printStackTrace();
		}
	}
	
	public boolean addName(String name) {
		boolean resultOK = false;
		PreparedStatement ps = null;
		try{
			String responces = "insert into responses values(?, null, null, null, null)";
			//String sql = "insert into Respondents (name) values(?)";
			ps = conn.prepareStatement(responces);
			ps.setString(1, name);
		    ps.executeUpdate(); 
		    resultOK = true;
		    ps.close();
		} catch (SQLException e) {
		    resultOK = false;  // one reason may be that the name is already in the database
		    if(e.getErrorCode()==1062 && e.getSQLState().equals("23000")){ 
		    	// duplicate key error
		    	System.out.println(name + " already exists in the database");
		    } else {
				printSqlError(e);
		    }
		} finally {
			if(ps != null){
				try {
					ps.close();
				} catch (SQLException e) {
					printSqlError(e);
				}
			}
		}
		return resultOK;
	}
	
	/**
	 * Need to set new variables in the database (responses)
	 * TODO
	 * @param e
	 */
	public boolean addResponse(String name, int first, int second, int third, int fourth){
		boolean update = false;
		String responsesString = 
		"update responses "
		+ "set firstAnswer = ?, secondAnswer = ?, thirdAnswer = ?, fourthAnswer = ? "
		+ "where userName = ?";
		try {
			PreparedStatement responses = conn.prepareStatement(responsesString);
			responses.setString(1 , "" + first);
			responses.setString(2 , "" + second);
			responses.setString(3 , "" + third);
			responses.setString(4 , "" + fourth);
			responses.setString(5 , name);
			int n = responses.executeUpdate();
			if(n == 1){
				update = true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return update;
	}
	
	private void printSqlError(SQLException e){
	    System.out.println("SQLException: " + e.getMessage());
	    System.out.println("SQLState: " + e.getSQLState());
	    System.out.println("VendorError: " + e.getErrorCode());
		//e.printStackTrace(); //TODO
	}

}
