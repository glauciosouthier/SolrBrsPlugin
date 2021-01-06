package brs.components;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.lucene.analysis.util.FilesystemResourceLoader;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.core.SolrConfig;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.IndexSchemaFactory;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.support.ParsingResult;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.o19s.solr.swan.JsonStubSwanSearcher;
import com.o19s.solr.swan.LuceneSwanSearcher;
import com.o19s.solr.swan.SwanParser;
import com.o19s.solr.swan.nodes.SwanNode;

public class UtilParse {
	private static final String FIELD_ALIASES_FILE_NAME = "fieldAliases.txt";
	private static final String SCHEMA_FILE_NAME = "managed-schema";
	private static final String CONFIG_FILE_NAME = "solrconfig.xml";
	private static final String CORE_PATH = "solr/juris/conf/";
	
	public static String parseLucene(String query, HashMap<String, String> params) {
		IndexSchema schema = null;
		try {
			SolrConfig solrConfig = new SolrConfig(Paths.get(CORE_PATH), CONFIG_FILE_NAME);
			schema = IndexSchemaFactory.buildIndexSchema(SCHEMA_FILE_NAME, solrConfig);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ListMultimap<String, String> fieldAliases = LinkedListMultimap.create();
		try {
			fieldAliases = UtilParse.loadMap(Paths.get(CORE_PATH), FIELD_ALIASES_FILE_NAME);
			// fieldAliases.asMap().entrySet().stream().parallel().forEach(e -> {
			// System.out.println( e.getKey() + e.getValue());
			// });
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return parseLucene(new MapSolrParams(params), schema, fieldAliases, query);
	}

	public static ListMultimap<String, String> loadMap(Path basePath, String fileName) throws Exception {
		ListMultimap<String, String> resourceMap = ArrayListMultimap.create();
		if (fileName != null) {
			ResourceLoader loader = new FilesystemResourceLoader(basePath);
			InputStream is = loader.openResource(fileName);
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				String[] keysVal = strLine.split("=>");
				if (keysVal.length != 2) {
					throw new IOException("keysVal.length != 2 after split");
				}
				String[] keys = keysVal[0].trim().split("\\s*,\\s*");
				String val = keysVal[1].trim().toLowerCase();
				if (!val.matches("[a-zA-Z0-9_-]+")) {
					throw new IOException("value doesn't match regex [a-zA-Z0-9_]+");
				}
				for (String k : keys) {
					if (!k.matches("[a-zA-Z0-9_-]+")) {
						throw new IOException("key doesn't match regex [a-zA-Z0-9_]+");
					}
					resourceMap.put(k.trim().toLowerCase(), val);
				}
			}
			in.close();
		}
		return resourceMap;
	}

	public static String parseLucene(SolrParams params, IndexSchema schema, ListMultimap<String, String> fieldAliases,
			String query) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		@SuppressWarnings("unchecked")
		SwanParser<SwanNode> parser = Parboiled.createParser(SwanParser.class);
		parser.setSearcher(new LuceneSwanSearcher(params, fieldAliases));
		ParsingResult<?> result = new RecoveringParseRunner<String>(parser.Query()).run(query);
		if (result.hasErrors()) {
			try {
				String err = printParseErrors(result);
				System.out.println(err);
				out.write(err.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SwanNode node = (SwanNode) result.parseTreeRoot.getValue();
			node.setSchema(schema);
			// node.setParser(parser);
			if (node.getQuery().toString().equals(""))
				throw new IllegalArgumentException("Invalid Query");
			try {
				out.write(node.getQuery().toString().getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return new String(out.toByteArray());
	}

	public static String parseJson(String query) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		@SuppressWarnings("unchecked")
		SwanParser<String> parser = Parboiled.createParser(SwanParser.class);
		parser.setSearcher(new JsonStubSwanSearcher());
		ParsingResult<?> result = new RecoveringParseRunner<String>(parser.Query()).run(query);
		if (result.hasErrors()) {
			try {
				out.write(printParseErrors(result).getBytes());
			} catch (IOException e) {
			}
		} else {
			try {
				out.write(result.parseTreeRoot.getValue().toString().getBytes());
			} catch (IOException e) {
			}
		}
		return new String(out.toByteArray());
	}
}
