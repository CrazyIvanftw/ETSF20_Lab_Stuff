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
	
	/**
	 * The database constructor.
	 */
	public Database() {
		try{
			Class.forName("com.mysql.jdbc.Driver");  //Necessary on Windows computers and mac
			conn = DriverManager.getConnection("jdbc:mysql://" + databaseServerAddress + "/" + 
                    database, databaseUser, databasePassword);
			
			// Display the contents of the database in the console. 
			// This should be removed in the final version
//			Statement stmt = conn.createStatement();		    
//		    ResultSet rs = stmt.executeQuery("select * from responses"); //Respondents
//		    while (rs.next( )) {
//		    	String name = rs.getString("userName"); 
//		    	System.out.println(name);
//		    	}
//		    stmt.close();
		} catch (SQLException e) {
			printSqlError(e);
		} catch (ClassNotFoundException e) {    
            e.printStackTrace();
		}
	}
	
	/**
	 * Put a name into the database with null values for the responses.
	 * @param name
	 * @return true if name gets into database.
	 */
	public boolean addName(String name) {
		boolean resultOK = false;
		PreparedStatement ps = null;
		try{
			String responces = "insert into responses values(?, null, null, null, null)";
			ps = conn.prepareStatement(responces);
			ps.setString(1, name);
		    ps.executeUpdate(); 
		    resultOK = true;
		    ps.close();
		} catch (SQLException e) {
		    resultOK = false;  // one reason may be that the name is already in the database
		    if(e.getErrorCode()==1062 && e.getSQLState().equals("23000")){ 
		    	// duplicate key error
		    	// System.out.println(name + " already exists in the database");
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
	 * Insert the responses and given name into the database.
	 * @param name = userName
	 * @param first = firstAnswer
	 * @param second = secondAnswer
	 * @param third = thirdAnswer
	 * @param fourth = fourthAnswer
	 * @return true if the name and responses got into the database
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
			e.printStackTrace();
		}
		return update;
	}
	
	/**
	 * Some sort of error printing thing.
	 * @param e
	 */
	private void printSqlError(SQLException e){
	    System.out.println("SQLException: " + e.getMessage());
	    System.out.println("SQLState: " + e.getSQLState());
	    System.out.println("VendorError: " + e.getErrorCode());
		e.printStackTrace();
	}
	
	/**
	 * Get the previously stored values for the name
	 * @param name
	 * @return
	 */
	protected String getUserValues(String name){
		PreparedStatement getValuesStatement = null;
		String returnValueString = "I dunno";
		try {
			String getValuesString = "select * from responses where userName = ?";		
			getValuesStatement = conn.prepareStatement(getValuesString);			
			getValuesStatement.setString(1, name);			
			ResultSet values = getValuesStatement.executeQuery();
			if(values.first()){
				int v1 = values.getInt(2);
				int v2 = values.getInt(3);
				int v3 = values.getInt(4);
				int v4 = values.getInt(5);
				returnValueString = 
					"Op-perf: " + v1 
					+ " Tech-perf: " + v2 
					+ " Proj-sched: " + v3 
					+ " Budget: " + v4 ;
			}
			return returnValueString;
		}catch (SQLException e){
			return "Error: While handsome, Greg seems to have missed something.";
		}
	}

}
