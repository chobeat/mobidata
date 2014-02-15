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
		
		String query="SELECT id,path_name[array_upper(path_name,1)] as end, path_name[array_lower(path_name,1)] as start, ST_Length(ST_GeometryFromText(shape)) as length, \"POIs\".\"routePopularity2\"(ST_Distance(ST_SetSRID(ST_MakePoint("+lat+","+lng+"),4326),ST_SetSRID(ST_GeometryFromText(shape),4326)),1,1)" + 
				" AS popularity " + 
				"FROM \"POIs\".\"routes2\" as r where ST_Length(shape)>0 order by popularity desc limit 5";
		System.out.println(query);
		return QueryToJSONResponse(query);
	}
	@POST
	@Path("routeinfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response routeToPoints( MultivaluedMap<String, String> params){
		String id=params.getFirst("id");
		
		
		String query="select shape::geometry,path_name, path, points from \"POIs\".routes2 where id="+id+";";
				
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

