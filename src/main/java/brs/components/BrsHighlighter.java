package brs.components;

import org.apache.solr.core.SolrCore;

import com.o19s.solr.swan.highlight.SwanHighlighter;

public class BrsHighlighter extends SwanHighlighter {
	public BrsHighlighter(SolrCore solrCore) {
		super(solrCore);
	}
}
