
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class CacheSimulator {
	
	 static int blockSize = 0;
	 static int l1Size = 0;
	 static int l1Asso = 0;
	 static int l2Size = 0;
	 static int l2Asso = 0;
	 static int replacementPolicy = 0;
	 static int inclusionProperty = 0;
	 static String traceFile="";

	public void main(String[] args) throws FileNotFoundException, IOException{

		this.blockSize = Integer.parseInt(args[0]);
		this.l1Size = Integer.parseInt(args[1]);
		this.l1Asso = Integer.parseInt(args[2]);
		this.l2Size = Integer.parseInt(args[3]);
		this.l2Asso = Integer.parseInt(args[4]);
		this.replacementPolicy = Integer.parseInt(args[5]);
		this.inclusionProperty = Integer.parseInt(args[6]);
		this.traceFile = args[7];
		
		
		
		if(l2Size == 0) {
			
			ProcessSimulation sim = new ProcessSimulation(blockSize,l1Size,l1Asso,replacementPolicy,inclusionProperty,this.traceFile);
			sim.simulateL1();
		}
		else{
			ProcessSimulation sim = new ProcessSimulation(blockSize,l1Size,l1Asso,l2Size,l2Asso,replacementPolicy,inclusionProperty,traceFile);
			sim.simulateL1();
		}

	}

}
