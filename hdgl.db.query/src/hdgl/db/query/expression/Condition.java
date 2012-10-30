package hdgl.db.query.expression;

import hdgl.db.query.visitor.Visitor;

public class Condition extends Expression {
	
	String label;
	String op;
	String value;
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getOp() {
		return op;
	}
	public void setOp(String op) {
		this.op = op;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "["+label+op+value+"]";
	}
	@Override
	public <TR, TA> TR accept(Visitor<TR, TA> visitor, TA... arguments) {
		return visitor.visitCondition(this, arguments);
	}
	
	
}
