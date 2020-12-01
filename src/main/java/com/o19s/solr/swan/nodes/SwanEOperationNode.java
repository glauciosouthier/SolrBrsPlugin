package com.o19s.solr.swan.nodes;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class SwanEOperationNode extends SwanOperatorNode {

	  public SwanEOperationNode(SwanNode left, SwanNode right) {
	    super(left, right);
	  }
	  
	  //this is a copy constructor
	  public SwanEOperationNode(SwanEOperationNode originalNode){
		  super(originalNode.getNodes().toArray(new SwanNode[originalNode.getNodes().size()]));
	  }

	  @Override
	  public SpanQuery getSpanQuery(String field) {
	    return new SpanNearQuery(getQueries(field), Integer.MAX_VALUE, false);
	  }

	  @Override
	  public BooleanClause.Occur getClause() {
	    return BooleanClause.Occur.MUST;
	  }

	  @Override
	  protected String getOperation() {
	    return "E";
	  }
	}

