package swarm.recommender;


import java.net.URISyntaxException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import swarm.core.domain.Breakpoint;
import swarm.recommender.Candidate;
import swarm.recommender.CandidateList;

public class Recommender {

	// TODO: Should return the list of entities with their confidence
	public void recommend() throws URISyntaxException, ClassNotFoundException, SQLException {

		//DB parameters
		String hostNamePort = "";
		String dbName = "";
		String user = "";
		String pass = "";
		
		// Connect to the DB
    	Connection conn = null;

        Class.forName("org.postgresql.Driver");
          
        String url = "jdbc:postgresql://" + hostNamePort + "/"+ dbName;
        conn = DriverManager.getConnection(url, user, pass);
        //  System.out.println("Connected!");
          
          Statement st = conn.createStatement();

          
          // The breakpoint #223 is used as the current breakpoint i.e., from which to recommend co-breakpoints          
          int breakpointID = 223;
          // Query the current task and the program element on which the breakpoint is toggled 
        //TODO: This must be done when the breakpoint is toggled i.e., BreakpointListener ... when adding breakpoint
          ResultSet rs = st.executeQuery("SELECT bp.id as bpID, t.id as typeID, t.name as name, tsk.id as taskID "
          		+ "FROM breakpoint as bp, type as t, session as s, task as tsk "
          		+ "WHERE bp.id = '" + breakpointID + "' and bp.type_id = t.id and t.session_id = s.id and s.task_id = tsk.id");
    
          int taskID = 0;
          int sourceTypeID = 0;
          String entityName = "";
          
          while ( rs.next() )
          {
        	  sourceTypeID = rs.getInt("typeID");
              entityName = rs.getString("name");
              taskID = rs.getInt("taskID");

          }     
 
      // Query all the tasks (maybe session?) that have breakpoints toggled on this type ID (entityName)
      // TODO: Exclude the current task if already created in the database
      ResultSet rsTask = st.executeQuery("SELECT distinct tsk.* FROM type as t, session as s, task as tsk "
          		+ "WHERE t.name = '"+ entityName + "' and t.session_id = s.id and s.task_id = tsk.id");


      List<Integer> tasks = tasksId(rsTask);
      
      
      List<Breakpoint> breakpointCandidates = new ArrayList<Breakpoint>();
      
      CandidateList candidates = new CandidateList();
      
      int support = 0;
      
      for (int tsk: tasks) {
    	  
    	// Query all the breakpoints for these tasks
          ResultSet rsCandidate = st.executeQuery("SELECT distinct bp.*, t.name FROM breakpoint as bp, type as t, session as s, task as tsk "
            		+ "WHERE tsk.id = '"+ tsk + "' and tsk.id <>'" + taskID + "'and tsk.id = s.task_id and s.id = t.session_id and t.id = bp.type_id");
          
          
          List<String> entities = new ArrayList<String>();
          
          entities = mapCandidate(rsCandidate);
       // TODO: we should remove the duplicates
          for (String aCandidate : entities) {    

        	  support++;
        	  candidates.add(aCandidate);
          }             
    	  
      }
      
      candidates.rank(support);
      
      for (Candidate aCandidate : candidates.getCandidates()) {   
    	  System.out.println(aCandidate.getName() + " : " + aCandidate.getConfidence());
      }  
      
      rs.close();
      st.close();


	}
	
	// Use this only to have task ID
	static List<Integer> tasksId(ResultSet rs) throws SQLException {
		List<Integer> tasks = new ArrayList<Integer>();
		
        while ( rs.next() )
        {
        	tasks.add(rs.getInt("id"));
        }
        
		return tasks;
	}
	
	static List<String> mapCandidate(ResultSet rs) throws SQLException {
		List<String> entities = new ArrayList<String>();
		
        while (rs.next() )
        {
        	entities.add(rs.getString("name"));
        }
        
		return entities;
	}
	
}

