package brs.parser.nodes;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;

import com.o19s.solr.swan.nodes.SwanNode;
import com.o19s.solr.swan.nodes.SwanOperatorNode;

public class SwanXOUOperationNode extends SwanOperatorNode {

	  public SwanXOUOperationNode(SwanNode left, SwanNode right) {
	    _nodes.add(left);
	    _nodes.add(right);
	  }

	  //this is a copy constructor
	  public SwanXOUOperationNode(SwanXOUOperationNode originalNode){
	    this(originalNode.getNodes().get(0), originalNode.getNodes().get(1));
	  }

	  public void add(SwanNode node) {
	    _nodes.add(node);
	  }

	  @Override
	  protected String getOperation() {
	    return "XOU";
	  }

	  @Override
	  protected BooleanClause.Occur getClause() {
	    return BooleanClause.Occur.SHOULD;
	  }

	  @Override
	  public Query getQuery(String field) {
	    return getSpanQuery(field);
	  }

	  @Override
	  public Query getQuery(String[] fields) {
	    BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

	    List<SwanNode> inc;
	    for (int x = 0; x < _nodes.size(); x++) {
	      inc = new ArrayList<SwanNode>();
	      inc.addAll(_nodes);
	      inc.remove(x);

	      BooleanQuery.Builder innerBuilder = new BooleanQuery.Builder();
	      for (SwanNode n : inc) {
	        innerBuilder.add(n.getQuery(), BooleanClause.Occur.MUST_NOT);
	      }
	      innerBuilder.add(_nodes.get(x).getQuery(), BooleanClause.Occur.MUST);
	      queryBuilder.add(innerBuilder.build(), BooleanClause.Occur.SHOULD);
	    }

	    return queryBuilder.build();
	  }

	  @Override
	  public SpanQuery getSpanQuery(String field) {
	    //SpanOrQuery query = new SpanOrQuery();
	    List<SpanNotQuery> notQueries = new ArrayList<>();

	    List<SwanNode> inc;
	    for (int x = 0; x < _nodes.size(); x++) {
	      inc = new ArrayList<>();
	      inc.addAll(_nodes);
	      inc.remove(x);

	      //SpanOrQuery or = new SpanOrQuery();
	      List<SpanQuery> orQueries = new ArrayList<>();
	      for (SwanNode n : inc) {
	        orQueries.add(n.getSpanQuery(field));
	      }
	      SpanOrQuery or = new SpanOrQuery(orQueries.toArray(new SpanQuery[0]));
	      if (or.getClauses().length > 0) {
	        SpanNotQuery not = new SpanNotQuery(_nodes.get(x).getSpanQuery(field), or);
	        notQueries.add(not);
	      }
	    }

	    return new SpanOrQuery(notQueries.toArray(new SpanNotQuery[0]));
	  }

	  @Override
	  public String toString() {
	    return "XOU(" + StringUtils.join(_nodes, ",") + ")";
	  }
	}

