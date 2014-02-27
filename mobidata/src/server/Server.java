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
import org.postgresql.util.PSQLException;

import com.sun.org.glassfish.gmbal.ParameterNames;

@Path("/")
public class Server {
	
	final int PSQL_QUERY=1;
	final int PSQL_UPDATE=2;

	private Response QueryToJSONResponse(String query,int type){
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
				ResultSet rs;
				if(type==PSQL_QUERY) 
				{rs = p. executeQuery();
				return Response.ok(convertToJSON(rs)).build() ;
				
				}
				else if (type==PSQL_UPDATE)
					p.executeUpdate();
				
				return Response.ok().build();
		         
			
		} catch (SQLException e) {
			e.printStackTrace();
			return Response.ok("Fail").build();
 
		} catch (Exception e) {
			e.printStackTrace();
			return Response.ok("Fail").build();
		}
		
		
}
	@GET
	@Path("route/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response routeById(@PathParam("id") final String routeID){
		System.out.println(routeID);
		String query="select * from (select unnest(path) as point from \"POIs\".routes2 where id="+routeID+")" +
				"as route join \"POIs\".\"POIsManhattan\" on point=\"4sqExtended\"" +
				";";

		return QueryToJSONResponse(query,PSQL_QUERY);
	}	
	@GET
	@Path("categories")
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCategories(){
		String query="SELECT \"nomeMC\" from \"POIs\".\"MacroCategorie\"";
		
		return QueryToJSONResponse(query,PSQL_QUERY);
	}	

	@POST
	@Path("addinterest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addInterest( MultivaluedMap<String, String> params){
		String uid=params.getFirst("uid");
		String interest=params.getFirst("interest");
		String query="INSERT INTO \"Users\".interests(\n" + 
				"            userid, interest,valid,time)"+
				"    VALUES ('"+uid+"',	'"+interest+"',true,now())";
		QueryToJSONResponse(query,PSQL_UPDATE);

		query="select unnest(interests) as interest from \"Users\".get_interests('"+uid+"') as interests";
		

		return QueryToJSONResponse(query,PSQL_QUERY);
	}
	

	@GET
	@Path("getinterests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getInterests(@QueryParam(value = "uid") final String uid){
		
		String query="select unnest(interests) as interest from \"Users\".get_interests('"+uid+"') as interests";
		System.out.println(query);
		return QueryToJSONResponse(query,PSQL_QUERY);
	}
	
	@POST
	@Path("removeinterest")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeInterest( MultivaluedMap<String, String> params){
		String uid=params.getFirst("uid");
		String interest=params.getFirst("interest");
		String query="INSERT INTO \"Users\".interests(\n" + 
						"            userid, interest,valid,time)\n" + 
						"    VALUES ('"+uid+"','"+interest+"',false,now())";
		QueryToJSONResponse(query,PSQL_UPDATE);
		
		query="select unnest(interests) as interest from \"Users\".get_interests('"+uid+"') as interests";

		return QueryToJSONResponse(query,PSQL_QUERY);
	}
	
	
	@POST
	@Path("closeRoutes")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response listCloseRoutes( MultivaluedMap<String, String> params){
		String lat=params.getFirst("lat");
		String lng=params.getFirst("lng");
		String userid=params.getFirst("userid");
		String poiNR=params.getFirst("poiNR");
		String range=params.getFirst("range");
		String query="SELECT id,path_name[array_upper(path_name,1)] as end, path_name[array_lower(path_name,1)] as start, popularity, ST_Length(ST_Transform(ST_SetSRID(ST_GeometryFromText(shape),4326),26915)) as length " + 
				" " + 
				"FROM \"POIs\".\"routes2\" as r" +
				" where ST_Length(ST_Transform(ST_SetSRID(ST_GeometryFromText(shape),4326),26915))>0" +
				" and array_length(path,1)="+poiNR+
				"and (CASE WHEN start_time::time > localtime THEN start_time::time - localtime ELSE localtime - start_time::time END)<'1 hour'"+
				" and ST_Contains(ST_Buffer(ST_Transform(ST_GeomFromText('POINT("+lng+" "+lat+")',4326),26915),"+range+"),ST_Transform(ST_SetSRID(ST_GeometryFromText(shape),4326),26915)) ";
				query+=" and category IN (select unnest(interests) from \"Users\".get_interests('"+userid+"') as interests)"
				;
						
				
		
		query+="order by popularity desc "
		+"limit 5;";
		System.out.println(query);
		return QueryToJSONResponse(query,PSQL_QUERY);
	}
	@POST
	@Path("routeinfo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response routeToPoints( MultivaluedMap<String, String> params){
		String id=params.getFirst("id");
		
		
		String query="select * from (select unnest(path) as point from \"POIs\".routes2 where id="+id+")" +
				"as route join \"POIs\".\"POIsManhattan\" on point=\"4sqExtended\"" +
				";";

		return QueryToJSONResponse(query,PSQL_QUERY);
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

