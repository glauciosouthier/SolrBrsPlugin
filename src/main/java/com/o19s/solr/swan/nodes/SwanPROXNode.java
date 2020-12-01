package com.o19s.solr.swan.nodes;

import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class SwanPROXNode extends SwanProxNode {

	  public SwanPROXNode(SwanNode left, SwanNode right, int proximity) {
	    super(left, right, proximity);
	  }

	  //this is a copy constructor
	  public SwanPROXNode(SwanPROXNode originalNode) {
		this(originalNode._left, originalNode._right, originalNode._proximity);
	  }
	  
	  public SpanQuery getSpanQuery(SwanNode left, SwanNode right, String field) {
	    return new SpanNearQuery(
	        new SpanQuery[] { left.getSpanQuery(field), right.getSpanQuery(field) },
	        _proximity-1,
	        false
	    );
	  }

	  @Override
	  public String toString() {
	    return "PROX("+ _left +","+ _right +","+ _proximity +")";
	  }

	}

