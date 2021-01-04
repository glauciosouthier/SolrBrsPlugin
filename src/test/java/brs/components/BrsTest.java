package brs.components;

import java.util.HashMap;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.request.LocalSolrQueryRequest;
import org.apache.solr.util.TestHarness;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.errors.ErrorUtils;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

import com.o19s.solr.swan.StringStubSwanSearcher;
import com.o19s.solr.swan.SwanParser;

public class BrsTest extends SolrTestCaseJ4 {
	@BeforeClass
	public static void beforeClass() throws Exception {
		initCore("solrconfig.xml", "schema.xml");
	}

	

	@Test
	public void testRangeQuery1() {
		String input = "((mobile ADJ (unit$1 OU terminal)) OU (cellular ADJ (telephone OU phone))) COM convave COM shap$1";
		//hardTest(input) ;
		//test(input,0);
		test(input, "spanWithin(spanNear([spanWithin(spanNear([spanOr([spanNear([x:mobile, spanOr([SpanMultiTermQueryWrapper(x:/unit.{0,1}/), x:terminal])], 0, true), spanNear([x:cellular, spanOr([x:telephone, x:phone])], 0, true)]), x:convave], 2147483647, false), 1 ,x:xxxsentencexxx), SpanMultiTermQueryWrapper(x:/shap.{0,1}/)], 2147483647, false), 1 ,x:xxxsentencexxx) spanWithin(spanNear([spanWithin(spanNear([spanOr([spanNear([y:mobile, spanOr([SpanMultiTermQueryWrapper(y:/unit.{0,1}/), y:terminal])], 0, true), spanNear([y:cellular, spanOr([y:telephone, y:phone])], 0, true)]), y:convave], 2147483647, false), 1 ,y:xxxsentencexxx), SpanMultiTermQueryWrapper(y:/shap.{0,1}/)], 2147483647, false), 1 ,y:xxxsentencexxx)");

	}
	@Test
	public void testFourXOrAdjOrTerms8() {
	    test("(big XOU data) ADJ (rocks OU you).y.", "(-(spanNear([y:data, y:rocks], 0, true) spanNear([y:data, y:you], 0, true)) +(spanNear([y:big, y:rocks], 0, true) spanNear([y:big, y:you], 0, true))) (-(spanNear([y:big, y:rocks], 0, true) spanNear([y:big, y:you], 0, true)) +(spanNear([y:data, y:rocks], 0, true) spanNear([y:data, y:you], 0, true)))");
	}
	@Test
	public void testThreeAndAdjTerms14() {
		    test("(big E data) ADJ rocks", "+(spanNear([x:big, x:rocks], 0, true) spanNear([y:big, y:rocks], 0, true)) +(spanNear([x:data, x:rocks], 0, true) spanNear([y:data, y:rocks], 0, true))");
	}
	@Test
	public void testThreeWithOrTerms13() {
	    test("big COM (data OU rocks.x.)", "spanWithin(spanNear([x:big, x:data], 2147483647, false), 1 ,x:xxxsentencexxx) spanWithin(spanNear([y:big, y:data], 2147483647, false), 1 ,y:xxxsentencexxx) spanWithin(spanNear([x:big, x:rocks], 2147483647, false), 1 ,x:xxxsentencexxx)");
	}
	@Test
	public void testThreeSameOrTerms6() {
	    test("(big MESMO data.y.) OU rocks.x.", "spanWithin(spanNear([y:big, y:data], 2147483647, false), 1 ,y:xxxparagraphxxx) x:rocks");
	}

	@Test
	public void testThreeNearOrTerms13() {
		test("big PROX (data OU rocks.x.)",
				"spanNear([x:big, x:data], 0, false) spanNear([y:big, y:data], 0, false) spanNear([x:big, x:rocks], 0, false)");
	}

	@Test
	public void testThreeNearOrTerms14() {
		test("(big PROX data) OU rocks",
				"(spanNear([x:big, x:data], 0, false) spanNear([y:big, y:data], 0, false)) (x:rocks y:rocks)");
	}
	
	@Test
	public void testThreeWithXOrTerms11() {
		test("(big COM data XOU rocks).x.",
				"spanOr([spanNot(spanWithin(spanNear([x:big, x:data], 2147483647, false), 1 ,x:xxxsentencexxx), spanOr([x:rocks]), 0, 0), spanNot(x:rocks, spanOr([spanWithin(spanNear([x:big, x:data], 2147483647, false), 1 ,x:xxxsentencexxx)]), 0, 0)])");
	}

	@Test
	public void testThreeWithXOrTerms12() {
		test("big COM (data XOU rocks).x.",
				"(-spanWithin(spanNear([x:big, x:rocks], 2147483647, false), 1 ,x:xxxsentencexxx) +spanWithin(spanNear([x:big, x:data], 2147483647, false), 1 ,x:xxxsentencexxx)) (-spanWithin(spanNear([x:big, x:data], 2147483647, false), 1 ,x:xxxsentencexxx) +spanWithin(spanNear([x:big, x:rocks], 2147483647, false), 1 ,x:xxxsentencexxx))");
	}

	@Test
	public void testTwoNotTerms1() {
		test("big NAO data", "+(x:big y:big) -(x:data y:data)");
	}

	@Test
	public void testTwoNearNTerms5() {
		test("big.x. PROX12 data", "spanNear([x:big, x:data], 11, false)");
	}

	@Test
	public void testTwoNearNTerms6() {
		test("big PROX9 data.y.", "spanNear([y:big, y:data], 8, false)");
	}

	@Test
	public void testTwoNearNTerms1() {
		test("big PROX1 data", "spanNear([x:big, x:data], 0, false) spanNear([y:big, y:data], 0, false)");
	}

	@Test
	public void testTwoAdjNTerms2() {
		test("(big ADJ42 data).x.", "spanNear([x:big, x:data], 41, true)");
		assertQ("", lquery("(big ADJ42 data).x."),
				"//lst[@name='debug']/str[@name='parsedquery' and text()='SpanNearQuery(spanNear([x:big, x:data], 41, true))']");
	}

	@Test
	public void testRangeQuery6() {
		test("@range > 1000", "range:{1000 TO *]");
	}

	@Test
	public void testRangeQuery7() {
		test("@range > 1000 < 2000", "range:{1000 TO 2000}");
	}

	@Test
	public void testClassificationRangePlusOr() {
		test("(123/456-789,234).range.", "range:[123/456 TO 123/789] range:123/234");
	}
	
	private void test(String in, String out) {
		assertQ("", lquery(in), "//lst[@name='debug']/str[@name='parsedquery_toString' and text()='" + out + "']");
	}
	
	 private void test(String q,int numFound) {
		    assertQ(req("qt","brs",
		      "debugQuery", "true",
		      "q",q,
		      "qf","x, y, z",
		      "indent","true",
		      "sm","xxxsentencexxx",
		      "pm","xxxparagraphxxx"),"//*[@numFound='"+ Integer.toString(numFound) +"']");
	 }
	 
	private LocalSolrQueryRequest lquery(String q) {
		HashMap<String, String> args = new HashMap<String, String>();
		args.put("fl", "*");
		args.put("qf", "x, y");
		args.put("indent", "true");
		args.put("sm", "xxxsentencexxx");
		args.put("pm", "xxxparagraphxxx");
		args.put("debugQuery", "true");
		// args.put("q.op", "adj");
		TestHarness.LocalRequestFactory sumLRF = h.getRequestFactory("brs", 0, 200, args);
		return sumLRF.makeRequest(q);
	}
	@Ignore
	private void hardTest(String input) {
		SwanParser<String> _parser = Parboiled.createParser(SwanParser.class);
		_parser.setSearcher(new StringStubSwanSearcher());

		// input = "apple E banana PROX3 cocon*";
		// input = "((mobile PROX (unit$1 OU terminal)) OU (cellular PROX (telephone OU
		// phone))) COM convave COM shap$1";
		ParsingResult<?> result = new RecoveringParseRunner<String>(_parser.Query()).run(input);

		if (result.hasErrors()) {
			System.out.println("\nParse Errors:\n" + ErrorUtils.printParseErrors(result));
		}

		Object value = result.parseTreeRoot.getValue();
		System.out.println(value);
		// System.out.println(ParseTreeUtils.printNodeTree(result));

	}
}
