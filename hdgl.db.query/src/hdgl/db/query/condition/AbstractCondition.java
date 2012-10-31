package hdgl.db.query.condition;

public abstract class AbstractCondition {

	public static enum ConditionRelationship{
		Require, Sufficient, Equivalent, NotRelevant
	}
	
	/**
	 * Test whether "other" is a required condition of this. 
	 * meaning that if this is satisfied, "other" is satisfied certainly. 
	 * @param other
	 * @return
	 */
	public abstract boolean require(AbstractCondition other);
	
	/**
	 * Test whether this condition is compatible with "other"
	 * "Compatible" means "Can satisfied at a same time"
	 * @param other
	 * @return
	 */
	public abstract boolean compatible(AbstractCondition other);
	
	public ConditionRelationship relationship(AbstractCondition other){
		if(require(other)){
			if(other.require(this)){
				return ConditionRelationship.Equivalent;
			}else{
				return ConditionRelationship.Require;
			}
		}else{
			if(other.require(this)){
				return ConditionRelationship.Sufficient;
			}else{
				return ConditionRelationship.NotRelevant;
			}
		}
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
}
