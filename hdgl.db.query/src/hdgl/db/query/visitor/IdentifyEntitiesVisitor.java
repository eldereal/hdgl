package hdgl.db.query.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hdgl.db.query.expression.AsteriskQuantifier;
import hdgl.db.query.expression.Concat;
import hdgl.db.query.expression.Condition;
import hdgl.db.query.expression.Edge;
import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Order;
import hdgl.db.query.expression.Parallel;
import hdgl.db.query.expression.PlusQuantifier;
import hdgl.db.query.expression.Query;
import hdgl.db.query.expression.QuestionQuantifier;
import hdgl.db.query.expression.Vertex;

public class IdentifyEntitiesVisitor implements Visitor<Void, Void> {

	ArrayList<Entity> ids = new ArrayList<Entity>();
	
	public Map<Integer, Entity> getIdMap(){
		Map<Integer, Entity> res = new HashMap<Integer, Entity>();
		for (int i = 0; i < ids.size(); i++) {
			res.put(i, ids.get(i));
		}
		return res;
	}
	
	public Map<Entity, Integer> getEntityMap(){
		Map<Entity, Integer> res = new HashMap<Entity, Integer>();
		for (int i = 0; i < ids.size(); i++) {
			res.put(ids.get(i), i);
		}
		return res;
	}
	
	@Override
	public Void visitQuery(Query query, Void... arguments) {
		query.getExpression().accept(this);
		return null;
	}

	@Override
	public Void visitVertex(Vertex vertex, Void... arguments) {
		ids.add(vertex);
		return null;
	}

	@Override
	public Void visitEdge(Edge edge, Void... arguments) {
		ids.add(edge);
		return null;
	}

	@Override
	public Void visitAsteriskQuantifier(AsteriskQuantifier quantifier,
			Void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public Void visitQuestionQuantifier(QuestionQuantifier quantifier,
			Void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public Void visitPlusQuantifier(PlusQuantifier quantifier,
			Void... arguments) {
		quantifier.getQuantified().accept(this);
		return null;
	}

	@Override
	public Void visitConcat(Concat concat, Void... arguments) {
		concat.getFirst().accept(this);
		concat.getSecond().accept(this);
		return null;
	}

	@Override
	public Void visitCondition(Condition cond, Void... arguments) {
		return null;
	}

	@Override
	public Void visitOrder(Order order, Void... arguments) {
		return null;
	}

	@Override
	public Void visitParallel(Parallel parallel, Void... arguments) {
		parallel.getFirst().accept(this);
		parallel.getSecond().accept(this);
		return null;
	}

	
	
}
