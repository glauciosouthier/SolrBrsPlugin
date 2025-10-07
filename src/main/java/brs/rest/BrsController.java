package brs.rest;

import java.time.Instant;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.logging.log4j.util.Strings;

import brs.components.UtilParse;

@Path("/brs")
public class BrsController {
	private static final String DEFAULT_OPERATOR = "e";
	private static final String QUERY_FIELDS = "decisao,ementa,inteiro_teor";

	/**
	 * ex: http://localhost:8081/brs/toLucene?q=((habeas adj3 corpus) nao caixa)&qf=ementa
	 * 
	 * @param query
	 * @param queryFields
	 * @param defaultOperator
	 * @return
	 * @throws JsonProcessingException 
	 */

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/toLucene")
	public String toLucene(@QueryParam("q") String query, @QueryParam("qf") String queryFields,
			@QueryParam("q.op") String defaultOperator) throws JsonProcessingException {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("q.op", Strings.isBlank(defaultOperator) ? DEFAULT_OPERATOR : defaultOperator);
		params.put("sm", "xxxsentencexxx");
		params.put("pm", "xxxparagraphxxx");
		params.put("qf", Strings.isBlank(queryFields) ? QUERY_FIELDS : queryFields);

		String luceneText= UtilParse.parseLucene(query, params);
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode wrapper = mapper.createObjectNode();
		wrapper.put("status", "success");
		wrapper.put("result", luceneText);
		wrapper.put("timestamp", Instant.now().toString());

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/toJson")
    public String toJson(@QueryParam("q") String query) throws JsonProcessingException {
	    String jsonText= UtilParse.parseJson(query);
	      ObjectMapper mapper = new ObjectMapper();
	        ObjectNode wrapper = mapper.createObjectNode();
	        wrapper.put("status", "success");
	        wrapper.put("result", jsonText);
	        wrapper.put("timestamp", Instant.now().toString());
	        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wrapper);

	}

}
