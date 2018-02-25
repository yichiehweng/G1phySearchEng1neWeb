
package sg.edu.nus.iss.sa45.Angularproject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

@WebServlet(urlPatterns = "/search/*")
public class SearchServlet extends HttpServlet{
    
    @Resource(lookup = "jdbc/giphy")
    private DataSource ds;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
            String theString=req.getPathInfo();
            log(theString);
            String userEmail =req.getPathInfo().substring(1,theString.indexOf("&"));
            String searchTerm =req.getPathInfo().substring(theString.indexOf("&")+1);
            
            java.util.Date theDate = new java.util.Date();
            Date sqlDate = new Date(theDate.getTime());
            
            JsonArrayBuilder custBuilder = Json.createArrayBuilder();
            try (Connection conn=ds.getConnection()) {
                String insertQuery = "INSERT into searchHistory (id,searchTerm,searchTime,userEmail) values(?,?,?,?)";	       
                PreparedStatement ps = conn.prepareStatement( insertQuery );
                ps.setInt(1,getNewId());
                ps.setString(2, searchTerm);
                ps.setDate(3,sqlDate);
                ps.setString(4, userEmail);
                ps.executeUpdate();
                ps.close();
                conn.close();            
            } catch (Exception e) {
              log(e.getMessage());
            }
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(MediaType.APPLICATION_JSON);
        }
    private int getNewId() throws SQLException{        
        int newid=0;   
        try (Connection conn=ds.getConnection()) {
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM searchHistory order by id desc limit 1");
             while (rs.next()) {
             newid=rs.getInt("id")+1;
            }
    return newid;
   }
  }
}
