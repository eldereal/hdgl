package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Query extends Expression {

	Expression expression;
	
	public Query() {
		super();
	}
	
	public Query(Expression expression) {
		super();
		this.expression = expression;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	
	@Override
	public String toString() {
		return expression.toString();
	}

	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitQuery(this, arguments);
	}

}
