package hdgl.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract class Q {

	public static Q filter(String name, String op, Object value) {
		return new LabelFilterQ(name,op,value);
	}
	
	public static Q and(Q left,Q right) {
		return new AndQ(left, right);
	}
	
	public static Q or(Q left,Q right) {
		return new OrQ(left, right);
	}
	
	public static Q not(Q q) {
		return q.not();
	}
	
	static abstract class FilterQ extends Q{
		@Override
		CNFQ cnf() {
			return new CNFQ(new DisjunctionQ(this));
		}


		@Override
		DNFQ dnf() {
			return new DNFQ(new ConjunctionQ(this));
		}
	}
	
	static class TypeFilterQ extends FilterQ{
		
		final Class<?> type;
		boolean exclude;
		
		TypeFilterQ(Class<?> type, boolean exclude) {
			super();
			this.type = type;
			this.exclude = exclude;
		}
		
		@Override
		public String toString() {
			if(exclude){
				return ".!"+type;
			}else{
				return "."+type;
			}
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (exclude ? 1231 : 1237);
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof TypeFilterQ)) {
				return false;
			}
			TypeFilterQ other = (TypeFilterQ) obj;
			if (exclude != other.exclude) {
				return false;
			}
			if (type == null) {
				if (other.type != null) {
					return false;
				}
			} else if (!type.equals(other.type)) {
				return false;
			}
			return true;
		}

		@Override
		Q not() {
			return new TypeFilterQ(type, !exclude);
		}
		
	}
	
	static class LabelFilterQ extends FilterQ{
		final String name;
		final String op;
		final Object value;
				
		LabelFilterQ(String name, String op, Object value) {
			super();
			this.name = name;
			this.op = op;
			this.value = value;
		}
		
		@Override
		public String toString() {
			return "["+name+op+value+"]";
		}


		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			result = prime * result + ((op == null) ? 0 : op.hashCode());
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof LabelFilterQ)) {
				return false;
			}
			LabelFilterQ other = (LabelFilterQ) obj;
			if (name == null) {
				if (other.name != null) {
					return false;
				}
			} else if (!name.equals(other.name)) {
				return false;
			}
			if (op == null) {
				if (other.op != null) {
					return false;
				}
			} else if (!op.equals(other.op)) {
				return false;
			}
			if (value == null) {
				if (other.value != null) {
					return false;
				}
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}

		@Override
		Q not() {
			return new LabelFilterQ(name,Op.not(op),value);
		}

		

	}
	
	static class AndQ extends Q{
		final Q left;
		final Q right;
		
		AndQ(Q left, Q right) {
			super();
			this.left = left;
			this.right = right;
		}
		
		@Override
		public String toString() {
			return "("+ left +") and ("+right+")";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((left == null) ? 0 : left.hashCode());
			result = prime * result + ((right == null) ? 0 : right.hashCode());
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof AndQ)) {
				return false;
			}
			AndQ other = (AndQ) obj;
			if (left == null) {
				if (other.left != null) {
					return false;
				}
			} else if (!left.equals(other.left)) {
				return false;
			}
			if (right == null) {
				if (other.right != null) {
					return false;
				}
			} else if (!right.equals(other.right)) {
				return false;
			}
			return true;
		}



		@Override
		Q not() {
			return new OrQ(left.not(),right.not());
		}
		
		@Override
		CNFQ cnf() {
			CNFQ cl=left.cnf();
			CNFQ cr=right.cnf();
			Set<DisjunctionQ> list=new HashSet<DisjunctionQ>();
			for (DisjunctionQ disjunctionQ : cl.elements) {
				list.add(disjunctionQ);
			}
			for (DisjunctionQ disjunctionQ : cr.elements) {
				list.add(disjunctionQ);
			}
			return new CNFQ(list.toArray(new DisjunctionQ[0]));
		}
		
		@Override
		DNFQ dnf() {
			return cnf().dnf();
		}
		
	}
	
	static class OrQ extends Q{
		final Q left;
		final Q right;
		
		OrQ(Q left, Q right) {
			super();
			this.left = left;
			this.right = right;
		}
		
		@Override
		public String toString() {
			return "("+ left +") or ("+right+")";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((left == null) ? 0 : left.hashCode());
			result = prime * result + ((right == null) ? 0 : right.hashCode());
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof OrQ)) {
				return false;
			}
			OrQ other = (OrQ) obj;
			if (left == null) {
				if (other.left != null) {
					return false;
				}
			} else if (!left.equals(other.left)) {
				return false;
			}
			if (right == null) {
				if (other.right != null) {
					return false;
				}
			} else if (!right.equals(other.right)) {
				return false;
			}
			return true;
		}



		@Override
		Q not() {
			return new AndQ(left.not(),right.not());
		}
		
		
		@Override
		CNFQ cnf() {
			return dnf().cnf();
		}
		
		@Override
		DNFQ dnf() {
			DNFQ cl=left.dnf();
			DNFQ cr=right.dnf();
			Set<ConjunctionQ> list=new HashSet<ConjunctionQ>();
			for (ConjunctionQ disjunctionQ : cl.elements) {
				list.add(disjunctionQ);
			}
			for (ConjunctionQ disjunctionQ : cr.elements) {
				list.add(disjunctionQ);
			}
			return new DNFQ(list.toArray(new ConjunctionQ[0]));
		}
		
	}
	
	static class ConjunctionQ extends Q{
		final FilterQ[] elements;

		ConjunctionQ(FilterQ... elements) {
			super();
			this.elements = Arrays.copyOf(elements, elements.length);
		}
		
		@Override
		public String toString() {
			StringBuffer sb=new StringBuffer();
			for (FilterQ fQ : elements) {
				if(sb.length()>0){
					sb.append(" and ");
				}
				sb.append(fQ);
			}
			return sb.toString();
		}
		
		@Override
		Q not() {
			FilterQ[] ne=new FilterQ[elements.length];
			for(int i=0;i<elements.length;i++){
				ne[i]=(FilterQ)elements[i].not();
			}
			return new DisjunctionQ(ne);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof ConjunctionQ)) {
				return false;
			}
			ConjunctionQ other = (ConjunctionQ) obj;
			if (!Arrays.equals(elements, other.elements)) {
				return false;
			}
			return true;
		}
		@Override
		CNFQ cnf() {
			DisjunctionQ[] ds=new DisjunctionQ[elements.length];
			for(int i=0;i<ds.length;i++){
				ds[i] =new DisjunctionQ(elements[i]);
			}
			return new CNFQ(ds);
		}
		@Override
		DNFQ dnf() {
			return new DNFQ(this);
		}
		
	}
	
	static class DisjunctionQ extends Q{
		final FilterQ[] elements;
	
		DisjunctionQ(FilterQ... elements) {
			super();
			this.elements = Arrays.copyOf(elements, elements.length);
		}
		
		@Override
		public String toString() {
			StringBuffer sb=new StringBuffer();
			for (FilterQ fQ : elements) {
				if(sb.length()>0){
					sb.append(" or ");
				}
				sb.append(fQ);
			}
			return sb.toString();
		}
		
		@Override
		Q not() {
			FilterQ[] ne=new FilterQ[elements.length];
			for(int i=0;i<elements.length;i++){
				ne[i]=(FilterQ)elements[i].not();
			}
			return new ConjunctionQ(ne);
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof DisjunctionQ)) {
				return false;
			}
			DisjunctionQ other = (DisjunctionQ) obj;
			if (!Arrays.equals(elements, other.elements)) {
				return false;
			}
			return true;
		}
		@Override
		CNFQ cnf() {
			return new CNFQ(this);
		}
		@Override
		DNFQ dnf() {
			ConjunctionQ[] ds=new ConjunctionQ[elements.length];
			for(int i=0;i<ds.length;i++){
				ds[i] =new ConjunctionQ(elements[i]);
			}
			return new DNFQ(ds);
		}
		
	}
	
	
	static class CNFQ extends Q{
		final DisjunctionQ[] elements;
		
		CNFQ(DisjunctionQ... elements) {
			super();
			this.elements = Arrays.copyOf(elements, elements.length);
		}

		@Override
		public String toString() {
			StringBuffer sb=new StringBuffer();
			for (DisjunctionQ fQ : elements) {
				if(sb.length()>0){
					sb.append(" and ");
				}
				sb.append("(");
				sb.append(fQ);
				sb.append(")");
			}
			return sb.toString();
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof CNFQ)) {
				return false;
			}
			CNFQ other = (CNFQ) obj;
			if (!Arrays.equals(elements, other.elements)) {
				return false;
			}
			return true;
		}


		@Override
		Q not() {
			ConjunctionQ[] cs=new ConjunctionQ[elements.length];
			for(int i=0;i<elements.length;i++){
				cs[i]=(ConjunctionQ)elements[i].not();
			}
			return new DNFQ(cs);
		}

		@Override
		CNFQ cnf() {
			return this;
		}

		@Override
		DNFQ dnf() {
			Set<ConjunctionQ> cs = new HashSet<ConjunctionQ>();
			int[] lengths=new int[elements.length];
			int[] poses=new int[elements.length];
			for(int i=0;i<elements.length;i++){
				lengths[i]=elements[i].elements.length;
			}
			boolean finish = false;
			while(!finish){
				FilterQ[] qs=new FilterQ[elements.length];
				for(int i=0;i<elements.length;i++){
					qs[i]=elements[i].elements[poses[i]];
				}
				cs.add(new ConjunctionQ(qs));
				poses[0]++;
				for (int i = 0; i < elements.length; i++) {
					if(poses[i]>=lengths[i]){
						poses[i]-=lengths[i];
						if(i<elements.length-1){
							poses[i+1]++;
						}else{
							finish = true;
						}
					}
				}
			}
			return new DNFQ(cs.toArray(new ConjunctionQ[0]));
		}
		
	}
	
	static class DNFQ extends Q{
		final ConjunctionQ[] elements;
		
		DNFQ(ConjunctionQ... elements) {
			super();
			this.elements = Arrays.copyOf(elements, elements.length);
		}

		@Override
		public String toString() {
			StringBuffer sb=new StringBuffer();
			for (ConjunctionQ fQ : elements) {
				if(sb.length()>0){
					sb.append(" or ");
				}
				sb.append("(");
				sb.append(fQ);
				sb.append(")");
			}
			return sb.toString();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}



		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof DNFQ)) {
				return false;
			}
			DNFQ other = (DNFQ) obj;
			if (!Arrays.equals(elements, other.elements)) {
				return false;
			}
			return true;
		}



		@Override
		Q not() {
			DisjunctionQ[] cs=new DisjunctionQ[elements.length];
			for(int i=0;i<elements.length;i++){
				cs[i]=(DisjunctionQ)elements[i].not();
			}
			return new CNFQ(cs);
		}

		@Override
		CNFQ cnf() {
			Set<DisjunctionQ> cs = new HashSet<DisjunctionQ>();
			int[] lengths=new int[elements.length];
			int[] poses=new int[elements.length];
			for(int i=0;i<elements.length;i++){
				lengths[i]=elements[i].elements.length;
			}
			boolean finish = false;
			while(!finish){
				FilterQ[] qs=new FilterQ[elements.length];
				for(int i=0;i<elements.length;i++){
					qs[i]=elements[i].elements[poses[i]];
				}
				cs.add(new DisjunctionQ(qs));
				poses[0]++;
				for (int i = 0; i < elements.length; i++) {
					if(poses[i]>=lengths[i]){
						poses[i]-=lengths[i];
						if(i<elements.length-1){
							poses[i+1]++;
						}else{
							finish = true;
						}
					}
				}
			}
			return new CNFQ(cs.toArray(new DisjunctionQ[0]));
		}

		@Override
		DNFQ dnf() {
			return this;
		}
		
	}
	
	abstract Q not();
	
	abstract CNFQ cnf();
	
	abstract DNFQ dnf();
	
	@Override
	public abstract boolean equals(Object obj);
	
	@Override
	public abstract int hashCode();
}
