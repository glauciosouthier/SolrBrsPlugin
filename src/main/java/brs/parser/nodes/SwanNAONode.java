package brs.parser.nodes;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.solr.schema.IndexSchema;

import com.o19s.solr.swan.nodes.SwanNode;

public class SwanNAONode extends SwanNode {

	  SwanNode _left;
	  SwanNode _right;

	  public SwanNAONode(SwanNode left, SwanNode right) {
	    _left = left;
	    _right = right;
	  }

	  //this is a copy constructor
	  public SwanNAONode(SwanNAONode originalNode) {
		this(originalNode._left, originalNode._right);
	  }

	  @Override
	  public String toString() {
	    return "NAO("+ _left +","+ _right +")";
	  }

	  @Override
	  public Query getQuery(String field) {
	    BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
	    queryBuilder.add(_left.getQuery(field),BooleanClause.Occur.MUST);
	    queryBuilder.add(_right.getQuery(field),BooleanClause.Occur.MUST_NOT);
	    return queryBuilder.build();
	  }

	  @Override
	  public SpanQuery getSpanQuery(String field) {
	    return new SpanNotQuery(_left.getSpanQuery(field), _right.getSpanQuery(field));
	  }

	  @Override
	  public Query getQuery(String[] fields) {
	    BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

	    queryBuilder.add(_left.getQuery(),BooleanClause.Occur.MUST);
	    queryBuilder.add(_right.getQuery(),BooleanClause.Occur.MUST_NOT);

	    return queryBuilder.build();
	  }

	  @Override
	  public void setSchema(IndexSchema schema) {
	    _left.setSchema(schema);
	    _right.setSchema(schema);
	    super.setSchema(schema);
	  }
	}

