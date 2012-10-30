package hdgl.db.query.parser;

import static org.junit.Assert.*;
import hdgl.db.query.convert.QueryCompletion;
import hdgl.db.query.convert.QueryToStateMachine;
import hdgl.db.query.expression.Expression;


import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenRewriteStream;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class ParserTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	QueryParser parser(String query){
		QueryLexer lexer=new QueryLexer(new ANTLRStringStream(query));
		QueryParser parser = new QueryParser(new TokenRewriteStream(lexer));
		return parser;
	}
	
	@Test
	public void test() throws RecognitionException {
		assertEquals(parser(".[desc:label<=val]*").expression().toString(), parser(".[DESC:label][label<=val]*").expression().toString());
		assertEquals(parser(".-.[<:price]").expression().toString(), parser("((. -) .[ASC:price])").expression().toString());
		assertEquals(parser(".[id=1]|-[price<10](.)*").expression().toString(), parser("(.[id=1]|-[price<10] .*)").expression().toString());
		assertEquals(parser(".[desc:label<=val]*").expression().clone().toString(), parser(".[DESC:label][label<=val]*").expression().toString());
		assertEquals(parser(".-.[<:price]").expression().clone().toString(), parser("((. -) .[ASC:price])").expression().toString());
		assertEquals(parser(".[id=1]|-[price<10](.)*").expression().clone().toString(), parser("(.[id=1]|-[price<10] .*)").expression().toString());
		
		assertEquals("(.[id=1]|((. -[price<10]) .) (- .)*)", QueryCompletion.complete(parser(".[id=1]|-[price<10](.)*").expression()).toString());
		assertEquals("((. -[price<10])|(.[id=1] -) ((. -)* .))", QueryCompletion.complete(parser("-[price<10]|.[id=1](.)*").expression()).toString());
		
		Expression q = QueryCompletion.complete(parser(".[id=1]|-[price<10](.)*").expression());
		QueryToStateMachine.convert(q);
	}

}
