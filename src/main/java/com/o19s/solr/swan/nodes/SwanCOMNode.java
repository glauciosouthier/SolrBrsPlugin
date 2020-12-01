package com.o19s.solr.swan.nodes;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

import com.o19s.solr.swan.query.SpanWithinQuery;

public class SwanCOMNode extends SwanProxNode {

	  // TODO: Remove n from operator if not required
	  public SwanCOMNode(SwanNode left, SwanNode right, int proximity) {
	    super(left, right, proximity);
	  }
	  
	  //this is a copy constructor
	  public SwanCOMNode(SwanCOMNode originalNode){
		  this(originalNode._left, originalNode._right, originalNode._proximity);
	  }

	  public SpanQuery getSpanQuery(SwanNode left, SwanNode right, String field) {
	    SpanQuery terms = new SpanNearQuery(
	        new SpanQuery[] { left.getSpanQuery(field), right.getSpanQuery(field) },
	        Integer.MAX_VALUE,
	        false
	    );
	    SpanQuery sentence_boundary = new SpanTermQuery(new Term(field, getSentenceMarker()));
	    return new SpanWithinQuery(terms,sentence_boundary, _proximity);
	  }

	  @Override
	  public String toString() {
	    return "COM("+ _left +","+ _right + "," + _proximity + ")";
	  }

	}

