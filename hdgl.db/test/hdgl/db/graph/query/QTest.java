package hdgl.db.graph.query;

import static org.junit.Assert.*;
import hdgl.db.graph.query.Q;
import hdgl.db.graph.query.Q.FilterQ;

import org.junit.Test;

public class QTest {

	@Test
	public void test() {
		Q q = Q.and( Q.filter("a","=","1"),Q.or(Q.filter("b", ">", 3), Q.filter("name", "=", "bob")));
		Q q2 = Q.and( Q.filter("a","=","1"),Q.or(Q.filter("b", ">", 3), Q.filter("name", "=", "bob")));
		assertEquals("equal test failed", q, q2);
		System.out.println(q);
		System.out.println(q.cnf());
		System.out.println(q.dnf());
		Q q3=Q.or(
			Q.and(Q.filter("a", ">", 0), Q.filter("a", "<=", 10))
			,
			Q.and(Q.or(Q.filter("name", "=", "elm"), Q.filter("name", "=", "eldereal")),
				  Q.not(Q.filter("id", "=", 10))
					)
				);
		System.out.println(q3);
		System.out.println(q3.cnf());
		System.out.println(q3.dnf());
	}

}
