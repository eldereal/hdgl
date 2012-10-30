package hdgl.db.query.visitor;

import hdgl.db.query.expression.AsteriskQuantifier;
import hdgl.db.query.expression.Concat;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.expression.Order;
import hdgl.db.query.expression.Parallel;
import hdgl.db.query.expression.PlusQuantifier;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.QuestionQuantifier;
import hdgl.db.query.expression.Vertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NullableVisitor implements Visitor<Boolean, Void> {

	Map<Entity, Integer> idsMap;
	Map<Expression, Boolean> nullable = new HashMap<Expression, Boolean>();
	
	public Map<Expression, Boolean> getNullableMap(){
		return nullable;
	}
	
	public NullableVisitor(Map<Entity, Integer> idsMap){
		this.idsMap = idsMap;
	}
	
	@Override
	public Boolean visitQuery(Query query, Void... arguments) {
		boolean n = query.getExpression().accept(this);
		nullable.put(query, n);
		return n;
	}

	@Override
	public Boolean visitVertex(Vertex vertex, Void... arguments) {
		nullable.put(vertex, false);
		return false;
	}

	@Override
	public Boolean visitEdge(Edge edge, Void... arguments) {
		nullable.put(edge, false);
		return false;
	}

	@Override
	public Boolean visitAsteriskQuantifier(AsteriskQuantifier quantifier,
			Void... arguments) {
		quantifier.getQuantified().accept(this);
		nullable.put(quantifier, true);
		return true;
	}

	@Override
	public Boolean visitQuestionQuantifier(QuestionQuantifier quantifier,
			Void... arguments) {
		quantifier.getQuantified().accept(this);
		nullable.put(quantifier, true);
		return true;
	}

	@Override
	public Boolean visitPlusQuantifier(PlusQuantifier quantifier,
			Void... arguments) {
		boolean n = quantifier.getQuantified().accept(this);
		nullable.put(quantifier, n);
		return n;
	}

	@Override
	public Boolean visitConcat(Concat concat, Void... arguments) {
		boolean n1=concat.getFirst().accept(this);
		boolean n2=concat.getSecond().accept(this);
		nullable.put(concat, n1 && n2);
		return n1 && n2;
	}

	@Override
	public Boolean visitCondition(Condition cond, Void... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitOrder(Order order, Void... arguments) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean visitParallel(Parallel parallel, Void... arguments) {
		boolean n1=parallel.getFirst().accept(this);
		boolean n2=parallel.getSecond().accept(this);
		nullable.put(parallel, n1 || n2);
		return n1 || n2;
	}

}
