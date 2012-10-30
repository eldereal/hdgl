package hdgl.db.query.expression;

public abstract class Entity extends Expression {

	protected Order order;
	protected Condition[] conditions;
	protected String type;
	
	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Condition[] getConditions() {
		if(conditions!=null){
			return conditions.clone();
		}else{
			return null;
		}
	}

	public void setConditions(Condition[] conditions) {
		this.conditions = conditions!=null?conditions.clone():null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
