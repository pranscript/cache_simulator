
import java.io.FileNotFoundException;
import java.io.IOException;

public class sim_cache {
	
	public static void main(String args[]) throws FileNotFoundException, IOException{
	CacheSimulator c = new CacheSimulator();
	sim_cache obj = new sim_cache();
	obj.func(c,args);
	}
	
	public void func(CacheSimulator c, String args[]) throws FileNotFoundException, IOException{
		c.main(args);
	}
		
}