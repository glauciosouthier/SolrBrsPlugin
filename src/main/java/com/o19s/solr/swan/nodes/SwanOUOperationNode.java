package com.o19s.solr.swan.nodes;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class SwanOUOperationNode extends SwanOperatorNode {

	  public SwanOUOperationNode(SwanNode left, SwanNode right) {
	    super(left, right);
	  }

	  //this is a copy constructor
	  public SwanOUOperationNode(SwanOUOperationNode originalNode) {
	    super(originalNode.getNodes().toArray(new SwanNode[originalNode.getNodes().size()]));
	  }

	  @Override
	  public SpanQuery getSpanQuery(String field) {
	    SpanQuery [] nodes = new SpanQuery[_nodes.size()];
	    for(int i = 0; i < _nodes.size(); i++) {
	      nodes[i] = _nodes.get(i).getSpanQuery(field);
	    }
	    return new SpanOrQuery(nodes);
	  }

	  @Override
	  protected String getOperation() {
	    return "OU";
	  }

	  @Override
	  public BooleanClause.Occur getClause() {
	    return BooleanClause.Occur.SHOULD;
	  }
	}

