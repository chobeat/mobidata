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
	@Path("close")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listRoutes( MultivaluedMap<String, String> params){
		String lat=params.getFirst("lat");
		String lng=params.getFirst("lng");
		/*return QueryToJSONResponse("\n" + 
						"WITH close_POI AS\n" + 
						"	(WITH candidate_set AS ( SELECT nome, location,\n" + 
						"	ST_Distance(ST_MakePoint(longitude,latitude),\n" + 
						"	ST_MakePoint(-73.99,40.74))*100000 AS cart_distance\n" + 
						"	FROM \"POIs\".\"POIsManhattan\"\n" + 
						"	ORDER BY cart_distance limit 1) \n" + 
						"	SELECT\n" + 
						"	nome,ST_Distance(P.location,\n" + 
						"	ST_GeographyFromText( 'POINT(-73.99 40.74)')) AS\n" + 
						"	geo_distance\n" + 
						"	FROM candidate_set as P\n" + 
						"	ORDER BY geo_distance \n" + 
						"	limit 1	\n" + 
						") SELECT ST_AsText(ST_MakeLine(location::geometry))\n" + 
						"from \"POIs\".\"Checkins4sqManhattan\" as c NATURAL INNER JOIN close_POI as p \n" + 
						"GROUP BY location\n" + 
						"\n" + 
						"limit 100;");*/
		String query="SELECT * FROM (SELECT *," + 
				"	ST_Distance(ST_MakePoint(latitude,longitude)," + 
				"	ST_MakePoint("+lat+","+lng+")) AS cart_distance" + 
				"	FROM \"POIs\".\"POIsManhattan\"" + 
				"	ORDER BY cart_distance limit 35) AS T1 ORDER BY nome";;
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

