package com.o19s.solr.swan;

import java.util.HashMap;

import org.apache.solr.SolrTestCaseJ4;

import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.util.TestHarness;
import org.junit.BeforeClass;
import org.junit.Test;

public class BrsTest extends SolrTestCaseJ4 {
	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml");
	}

	@Test
	public void testRangeQuery1() {
		 test("((mobile adj (unit$1 or terminal)) or (cellular adj (telephone or phone))) with convave with shap$1", "spanWithin(spanNear([spanWithin(spanNear([spanOr([spanNear([x:mobile, spanOr([SpanMultiTermQueryWrapper(x:/unit.{0,1}/), x:terminal])], 0, true), spanNear([x:cellular, spanOr([x:telephone, x:phone])], 0, true)]), x:convave], 2147483647, false), 1 ,x:xxxsentencexxx), SpanMultiTermQueryWrapper(x:/shap.{0,1}/)], 2147483647, false), 1 ,x:xxxsentencexxx) spanWithin(spanNear([spanWithin(spanNear([spanOr([spanNear([y:mobile, spanOr([SpanMultiTermQueryWrapper(y:/unit.{0,1}/), y:terminal])], 0, true), spanNear([y:cellular, spanOr([y:telephone, y:phone])], 0, true)]), y:convave], 2147483647, false), 1 ,y:xxxsentencexxx), SpanMultiTermQueryWrapper(y:/shap.{0,1}/)], 2147483647, false), 1 ,y:xxxsentencexxx)");
	  }

	private void test(String in, String out) {
		assertQ("", lquery(in), "//lst[@name='debug']/str[@name='parsedquery_toString' and text()='" + out + "']");
	}
	private LocalSolrQueryRequest lquery(String q) {
	    HashMap<String, String> args = new HashMap<String, String>();
	    args.put("fl", "*");
	    args.put("qf", "x, y");
	    args.put("indent", "true");
	  //  args.put("sm", "xxxsentencexxx");
	   // args.put("pm", "xxxparagraphxxx");
	    args.put("debugQuery", "true");
//	    args.put("q.op", "OR");
	    TestHarness.LocalRequestFactory sumLRF = h.getRequestFactory("swan", 0,
	        200, args);
	    return sumLRF.makeRequest(q);
	  }
}
