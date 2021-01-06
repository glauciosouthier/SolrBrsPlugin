package brs.rest;

import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.util.Strings;

import brs.components.UtilParse;

@Path("/brs")
public class BrsController {
	private static final String DEFAULT_OPERATOR = "ou";
	private static final String QUERY_FIELDS = "decisao,ementa,inteiro_teor";


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/toLucene")
	public String toLucene(@QueryParam("q")String query,@QueryParam("qf" )String queryFields, @QueryParam("q.op")String defaultOperator) {
		HashMap<String,String> params= new HashMap<String,String>();
		params.put("q.op", Strings.isBlank(defaultOperator)? DEFAULT_OPERATOR:defaultOperator);
		params.put("sm","xxxsentencexxx");
		params.put("pm","xxxparagraphxxx");
		params.put("qf",Strings.isBlank(queryFields)?QUERY_FIELDS:queryFields);

		return UtilParse.parseLucene(query,params);
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/toJson")
	public String toJson(@QueryParam("q")String query) {
		return UtilParse.parseJson(query);
	}
	
	
	
	
	

	
}
