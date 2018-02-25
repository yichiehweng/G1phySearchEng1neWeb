package sg.edu.nus.iss.sa45.Angularproject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.annotation.Resource;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.ws.rs.core.MediaType;

@WebServlet(urlPatterns = "/display/*")
public class displayServlet extends HttpServlet{
    @Resource(lookup = "jdbc/giphy")
    private DataSource ds;
    private static final String SQL = "select * from searchHistory where userEmail=?";
     @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
        
        String userEmail =req.getPathInfo().substring(1);
        
        JsonArrayBuilder custBuilder = Json.createArrayBuilder();
         try (Connection conn=ds.getConnection()) {
             //Statement stmt = conn.createStatement();
             //ResultSet rs = stmt.executeQuery("select * from searchHistory where userEmail=?");
             PreparedStatement ps = conn.prepareStatement(SQL);
             ps.setString(1, userEmail);
             ResultSet rs = ps.executeQuery();
             while (rs.next()) {
				JsonObjectBuilder elem = Json.createObjectBuilder();
				elem.add("Id", rs.getInt("id"))
                                    .add("searchTerm", rs.getString("searchTerm"))
                                    .add("searchTime", rs.getDate("searchTime").toString());
				custBuilder.add(elem.build());
			}
			rs.close();
			conn.close();             
         } catch (SQLException e) {
            log(e.getMessage());
        }
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType(MediaType.APPLICATION_JSON);
		JsonArray customers = custBuilder.build();
		try (PrintWriter pw = resp.getWriter()) {
			pw.println(customers.toString());
		}
    }
    
    
    
}
