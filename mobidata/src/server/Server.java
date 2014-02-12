package server;

import java.io.IOException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.Driver;

import com.sun.org.glassfish.gmbal.ParameterNames;

@Path("/")
public class Server {
	
	

	private Response QueryToJSONResponse(String query){
		try {
			 
			Class.forName("org.postgresql.Driver");
 
		} catch (ClassNotFoundException e) {
 
			System.out.println("Where is your PostgreSQL JDBC Driver? "
					+ "Include in your library path!");
			e.printStackTrace();
			return Response.ok("Fail").build();
 
		}
		Connection connection=null;
		try {
			 
			connection = DriverManager.getConnection(
					"jdbc:postgresql://127.0.0.1:5432/mobidata?searchpath=POIs", "chobeat",
					"q1w2e3");
				PreparedStatement p = connection.prepareStatement(query);
				  ResultSet rs = p. executeQuery();
		        
		         	
		         return Response.ok(convertToJSON(rs)).build() ;
		
			
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.ok("Fail").build();
 
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok("Fail").build();
		}
		
}
	@POST
	@Path("closeRoutes")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCloseRoutes( MultivaluedMap<String, String> params){
		String lat=params.getFirst("lat");
		String lng=params.getFirst("lng");

		String query="SELECT distinct route, \"POIs\".\"routePopularity\"(r.start, r.end, r.route, ST_Distance(ST_SetSRID(ST_MakePoint("+lat+","+lng+"),4326),route))" + 
				" AS popularity " + 
				"FROM \"POIs\".\"routes\" as r order by popularity limit 5";
			return QueryToJSONResponse(query);
	}
	@POST
	@Path("routeinfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response routeToPoints( MultivaluedMap<String, String> params){
		String route=params.getFirst("route");
		System.out.println(route);
		
		String query="select distinct s.from, s.to,ST_AsText(route) as shape from (SELECT distinct route, userid,day FROM \"POIs\".\"routes\" where route='"+route+ 
				"') as r join \"POIs\".steps as s on s.userid=r.userid and date_trunc('day',s.arrival_time)=r.day";
				System.out.println(query);
			return QueryToJSONResponse(query);
	}
	public static String convertToJSON(ResultSet resultSet)
			throws Exception {
			JSONArray jsonArray = new JSONArray();
			while (resultSet.next()) {	
			int total_rows = resultSet.getMetaData().getColumnCount();
			JSONObject obj = new JSONObject();
			for (int i = 0; i < total_rows; i++) {
			obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
			.toLowerCase(), resultSet.getObject(i + 1));
			}
			jsonArray.put(obj);
			}
			return jsonArray.toString();
			}
}

