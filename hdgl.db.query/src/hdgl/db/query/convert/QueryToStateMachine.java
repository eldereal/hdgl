package hdgl.db.query.convert;

import java.util.Map;

import hdgl.db.query.expression.Entity;
import hdgl.db.query.expression.Expression;
import hdgl.db.query.visitor.IdentifyEntitiesVisitor;
import hdgl.db.query.visitor.NullableVisitor;

public class QueryToStateMachine {

	public static void convert(Expression query){
		IdentifyEntitiesVisitor visitor = new IdentifyEntitiesVisitor();
		query.accept(visitor);
		Map<Entity, Integer> rev = visitor.getEntityMap();
		for(Map.Entry<Integer, Entity> i:visitor.getIdMap().entrySet()){
			assert i.getKey().equals(rev.get(i.getValue()));
			System.out.println(i.getKey()+": "+i.getValue());
		}
		
		NullableVisitor visitor2 = new NullableVisitor(rev);
		query.accept(visitor2);
		for(Map.Entry<Expression, Boolean> i:visitor2.getNullableMap().entrySet()){
			System.out.println(i.getKey()+": "+i.getValue());
		}
	}
}
