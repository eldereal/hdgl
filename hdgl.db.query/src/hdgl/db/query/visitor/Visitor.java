package hdgl.db.query.visitor;

import hdgl.db.query.expression.*;

public interface Visitor<TR, TA> {
	
	public TR visitQuery(Query query, TA... arguments);
	
	public TR visitVertex(Vertex vertex, TA... arguments);
	
	public TR visitEdge(Edge edge, TA... arguments);
	
	public TR visitAsteriskQuantifier(AsteriskQuantifier quantifier, TA... arguments);
	
	public TR visitQuestionQuantifier(QuestionQuantifier quantifier, TA... arguments);
	
	public TR visitPlusQuantifier(PlusQuantifier quantifier, TA... arguments);
	
	public TR visitConcat(Concat concat, TA... arguments);
	
	public TR visitCondition(Condition cond, TA... arguments);
	
	public TR visitOrder(Order order, TA... arguments);
	
	public TR visitParallel(Parallel parallel, TA... arguments);
}
