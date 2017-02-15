

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class Survey.
 */
@WebServlet("/Survey")
public class Survey extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Define states 
	private static final int NEED_NAME = 0;
	private static final int NEED_PROJECT_DATA = 2;
	private static final int NEED_PROJECT_INFO = 1;
	
	private FormGenerator formGenerator = new FormGenerator(); 
	
	static private Database db = new Database();
	
		
    /**
     * Default constructor. 
     */
    public Survey() {
    }
    
    /*
     * Checks first if name includes characters (i.e. is longer than zero characters) and then 
     * if so if the name is possible to add to 
     * the database. It is not possible to add an already existing name to the database. 
     */
    boolean nameOk(String name){
    	boolean result = !name.equals("");
    	if (result)
    		result = db.addName(name);
    	return result;
    }
    
    /*
     * Checks if a value entered as answer is OK. Answers should be between 1 and 10.
     */
    boolean valueOk(int value){
    	return value > 0 && value <11;
    }
        
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Get the session
		HttpSession session = request.getSession(true);
		
		int state = 0;
		String name;
		boolean sessionShouldBeEnded = false;
		
		// Decide which state the session is in
		if (session.isNew())
			state = NEED_NAME;
		else {
			state = (Integer) session.getAttribute("state");
		}
		
		// Get a writer, which will be used to write the next page for the user 
		PrintWriter out = response.getWriter();
		
		// Start the page, print the HTML header and start the body part of the page
		out.println("<html>");
        out.println("<head><title> FANTASTIC WEB APPLICATION </title></head>");
        out.println("<body>");
        
        switch (state){
        case NEED_NAME: // First state: get user name
        	name = request.getParameter("user"); // get the string that the user entered in the form
        	if (name != null) {
        		if (nameOk(name)) {
        			session.setAttribute("name", name);  // save the name in the session
        			state = NEED_PROJECT_INFO;
        			out.println(formGenerator.randomQuestionRequestForm());
        			//out.println(formGenerator.projectDataRequestForm());
        		}
        		else {
        			//XXX here is the place for db.getUserValues(String name)
        			out.println("Previous responses found: ");
        			out.println(formGenerator.nameRequestForm()); 
        			out.println(db.getUserValues(name));
        		}
        	}else{ // name was null, probably because no form has[n't] been filled out yet. Display form.
        		out.println(formGenerator.nameRequestForm());
        	}
        	break;
        	
        case NEED_PROJECT_INFO:
        	name = (String) session.getAttribute("name");
        	String question1 = request.getParameter("q1");
        	String question2 = request.getParameter("q2");
        	String question3 = request.getParameter("q3");
        	if (question1!=null || question2!=null || question3!=null){ //not null
        	question1 = question1.trim();
        	question2 = question2.trim();
        	question3 = question3.trim();
        	if (question1=="" || question2=="" || question3==""){ //if empty
        		out.println(formGenerator.randomQuestionRequestForm());
        		out.println("Try Again");
        	}else{//if not empty
        		db.projectSet(name, question1, question2, question3);
        		out.println(formGenerator.projectDataRequestForm());
        		state = NEED_PROJECT_DATA;
        	}
        	}else{//if null
        		out.println(formGenerator.randomQuestionRequestForm());
        		out.println("Try Again");
        	}
        	break;
        	
        	
        case NEED_PROJECT_DATA:
        	int s11 = 0, s12 = 0, s13 = 0, s14 = 0;
        	name = (String) session.getAttribute("name");
        	String s11String = request.getParameter("s11");
        	String s14String = request.getParameter("s14");
        	String s12String = request.getParameter("s12");
        	String s13String = request.getParameter("s13");
        	if (s11String==null) 
        		out.println(formGenerator.projectDataRequestForm()); // first time
        	else {
        		boolean valuesOk = true;
            	try {
            		s11 = Integer.parseInt(s11String);
                	s12 = Integer.parseInt(s12String);
                	s13 = Integer.parseInt(s13String);
                	s14 = Integer.parseInt(s14String);
            	} catch (NumberFormatException e) {
            		valuesOk = false;
            	}
            	valuesOk = valuesOk && valueOk(s11) && valueOk(s12) && valueOk(s13) && valueOk(s14);
            	
            	// display the next page
            	if (valuesOk){
            		// TODO get the responses from here
            		db.addResponse(name, s11 , s12 , s13 , s14);
        			int sum = s11 + s12 + s13 + s14;
        			// This is only to show that it is possible to do something with the values. 
        			// It is of course meaningless to calculate the sum. 
            		out.println("<p> Hello " + name);
                	out.println("<p> The sum of the values you entered is " + sum);
                	sessionShouldBeEnded = true;
            	} else { 
            		out.println("The values you entered were not OK");
            		out.println(formGenerator.projectDataRequestForm());          
            	}
        	}
        	break;	        	
        }
        
        // Save the state in the session until next time doGet is requested
        session.setAttribute("state", state);
       
        // Print the end of the HTML-page
        out.println("</body></html>");
        
        if (sessionShouldBeEnded)
        	session.invalidate();        		    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
