
import java.sql.*;
import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DatabaseList
 */
@WebServlet("/DatabaseList")
public class DatabaseList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Database db = new Database();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DatabaseList() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		PrintWriter out = response.getWriter();
		try{
			ResultSet rs = db.getDatabase();
			while(rs.next()){
				String name = rs.getString(1);
				Integer a1 = rs.getInt(2);
				Integer a2 = rs.getInt(3);
				Integer a3 = rs.getInt(4);
				Integer a4 = rs.getInt(5);
				String pName = rs.getString(6);
				String pRole = rs.getString(7);
				String pGoal = rs.getString(8);
				out.println(name + "   " + a1 + "   " + a2 + "   " + a3 + "   " + a4 + "   " + pName + "   " + pRole + "   " + pGoal);
			}
		}catch (SQLException e){
			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
