package hdgl.db.store;
import java.io.IOException;

public class query_exm {
	public static void main(String[] args) throws IOException 
	{
		GraphStore gs = new GraphStore();
		
		for (int i = 0; i < 5; i++)
		{
			System.out.println(gs.parseVertex(i).getString());
		}
		for (int i = 0; i < 9; i++)
		{
			System.out.println(gs.parseEdge(i).getString());
		}
		
	}
}
