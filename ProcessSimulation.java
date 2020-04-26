
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ProcessSimulation {
	
	private final String traceFile;
	MemoryManager memo;

	ProcessSimulation(int blockSize,int l1Size,int l1Asso,int replacementPolicy,int inclusionProperty,String traceFile){
		
		
		this.traceFile = traceFile;
		this.memo = new MemoryManager(blockSize,l1Size,l1Asso,replacementPolicy, inclusionProperty);
	}
	
	
	ProcessSimulation(int blockSize,int l1Size,int l1Asso,int l2Size,int l2Asso,int replacementPolicy,int inclusionProperty,String traceFile){
		
		this.traceFile = traceFile;
		this.memo = new MemoryManager(blockSize,l1Size,l1Asso,l2Size,l2Asso,replacementPolicy, inclusionProperty);
		
	}
	
	public void simulateL1() throws FileNotFoundException
    {
		try {
			  
		      File myObj = new File(this.traceFile);
		      Scanner sc = new Scanner(myObj);
		      int count=0;
		      while(sc.hasNextLine())
		        {
		            String line = sc.nextLine();
		            String[] arrOfStr = line.split(" ");
		            String hexAddress = arrOfStr[1];
		            String opcode = arrOfStr[0];
		            this.memo.simulateL1(hexAddress, opcode);
		            count++;
		        }
				
			if(CacheSimulator.l2Size==0) {
		      System.out.println("===== Simulator configuration =====");
		      System.out.println("BLOCKSIZE:"+CacheSimulator.blockSize);
		      System.out.println("L1_SIZE:"+CacheSimulator.l1Size);
		      System.out.println("L1_ASSOC:"+CacheSimulator.l1Asso);
		      System.out.println("L2_SIZE:"+CacheSimulator.l2Size);
		      System.out.println("L2_ASSOC:"+CacheSimulator.l2Asso);
		      String replacementPolicy ="LRU";
		      if(CacheSimulator.replacementPolicy==1) replacementPolicy = "FIFO";
		      else if(CacheSimulator.replacementPolicy==2) replacementPolicy = "optimal";
		      System.out.println("REPLACEMENT POLICY:"+replacementPolicy);
		      String inclusionPolicy ="non-inclusive";
		      if(CacheSimulator.inclusionProperty==1) inclusionPolicy = "inclusive";
		      System.out.println("INCLUSION PROPERTY: "+inclusionPolicy);
		      System.out.println("trace_file:"+CacheSimulator.traceFile);
		      System.out.println("===== L1 contents =====");
		      for(int i = 0; i < MemoryManager.l1Cache.sets	; i++)
			        {	System.out.print("Set    "+i + ": ");
			        	for(int j = 0; j < MemoryManager.l1Cache.cacheAsso; j++)
			            {
			        		System.out.print(Integer.toString(Integer.parseInt(MemoryManager.l1Cache.cacheMemory[i][j].toString(),2),16));
			        		if(MemoryManager.l1Cache.l1state[i][j]=='D') System.out.print(MemoryManager.l1Cache.l1state[i][j]+"  ");
			            }
			        	System.out.println();
			        }
		      System.out.println("===== Simulation results (raw) =====");
		      System.out.println("a. number of L1 reads:  "+this.memo.l1reads);
		      System.out.println("b. number of L1 read misses:"+MemoryManager.l1Cache.readMisses);
		      System.out.println("c. number of L1 writes:   "+this.memo.l1writes);
		      System.out.println("d. number of L1 write misses:"+MemoryManager.l1Cache.writeMisses);
		      int l1reads = this.memo.l1reads;
		      int l1writes = this.memo.l1writes;
		      float totalReadsAndWrites = l1reads + l1writes;
		      String missrate = String.format("%.6f", ((MemoryManager.l1Cache.readMisses+MemoryManager.l1Cache.writeMisses)/totalReadsAndWrites));
		      System.out.println("e. L1 miss rate:  "+ missrate);
		      System.out.println("f. number of L1 writebacks: "+MemoryManager.l1Cache.writeBack);
		      System.out.println("g. number of L2 reads:"+0);
		      System.out.println("h. number of L2 read misses:"+0);
		      System.out.println("i. number of L2 writes:"+0);
		      System.out.println("j. number of L2 write misses:"+0);
		      int l2missrate = 0;
		      System.out.println("k. L2 miss rate:   "+l2missrate);
		      System.out.println("l. number of L2 writebacks: "+0);
		      int memoryTraffic = MemoryManager.l1Cache.readMisses + MemoryManager.l1Cache.writeMisses + MemoryManager.l1Cache.writeBack;
		      System.out.println("m. total memory traffic: "+memoryTraffic);
		}
		
		
		else {
		    	  System.out.println("===== Simulator configuration =====");
			      System.out.println("BLOCKSIZE:"+CacheSimulator.blockSize);
			      System.out.println("L1_SIZE:"+CacheSimulator.l1Size);
			      System.out.println("L1_ASSOC:"+CacheSimulator.l1Asso);
			      System.out.println("L2_SIZE:"+CacheSimulator.l2Size);
			      System.out.println("L2_ASSOC:"+CacheSimulator.l2Asso);
			      String replacementPolicy ="LRU";
			      if(CacheSimulator.replacementPolicy==1) replacementPolicy = "FIFO";
			      else if(CacheSimulator.replacementPolicy==2) replacementPolicy = "optimal";
			      System.out.println("REPLACEMENT POLICY:"+replacementPolicy);
			      String inclusionPolicy ="non-inclusive";
			      if(CacheSimulator.inclusionProperty==1) inclusionPolicy = "inclusive";
			      System.out.println("INCLUSION PROPERTY: "+inclusionPolicy);
			      System.out.println("trace_file:"+CacheSimulator.traceFile);
			      System.out.println("===== L1 contents =====");
			      for(int i = 0; i < MemoryManager.l1Cache_2way.sets	; i++)
				        {	System.out.print("Set    "+i + ": ");
				        	for(int j = 0; j < MemoryManager.l1Cache_2way.cacheAsso; j++)
				            {
				        		System.out.print(Integer.toString(Integer.parseInt(MemoryManager.l1Cache_2way.cacheMemory[i][j].toString(),2),16));
				        		if(MemoryManager.l1Cache_2way.l1state[i][j]=='D') System.out.print(MemoryManager.l1Cache_2way.l1state[i][j]+"  ");
				            }
				        	System.out.println();
				        }
			      if(CacheSimulator.l2Size!=0) {
			    	  for(int i = 0; i < MemoryManager.l2Cache_2way.sets	; i++)
					        {	System.out.print("Set - "+i + ": ");
					        	for(int j = 0; j < MemoryManager.l2Cache_2way.cacheAsso; j++)
					            {
					        		System.out.print(Integer.toString(Integer.parseInt(MemoryManager.l2Cache_2way.cacheMemory[i][j].toString(),2),16));
					        		if(MemoryManager.l2Cache_2way.l2state[i][j]=='D') System.out.print(MemoryManager.l2Cache_2way.l1state[i][j]+"  ");
					            }
					        	System.out.println();
					        }
			      }
			      
			      
			      System.out.println("===== Simulation results (raw) =====");
			      System.out.println("a. number of L1 reads:  "+this.memo.l1reads);
			      System.out.println("b. number of L1 read misses:"+MemoryManager.l1Cache_2way.readMisses);
			      System.out.println("c. number of L1 writes:   "+this.memo.l1writes);
			      System.out.println("d. number of L1 write misses:"+MemoryManager.l1Cache_2way.writeMisses);
			      int l1reads = this.memo.l1reads;
			      int l1writes = this.memo.l1writes;
			      float totalReadsAndWrites = l1reads + l1writes;
			      String missrate = String.format("%.6f", ((MemoryManager.l1Cache_2way.readMisses+MemoryManager.l1Cache_2way.writeMisses)/totalReadsAndWrites));
			      System.out.println("e. L1 miss rate:  "+ missrate);
			      System.out.println("f. number of L1 writebacks: "+MemoryManager.l1Cache_2way.writeBack);
			      System.out.println("g. number of L2 reads:"+MemoryManager.l2Cache_2way.readsL2);
			      System.out.println("h. number of L2 read misses:"+MemoryManager.l2Cache_2way.readMissesL2);
			      System.out.println("i. number of L2 writes:"+MemoryManager.l2Cache_2way.writeHitsL2);
			      System.out.println("j. number of L2 write misses:"+MemoryManager.l2Cache_2way.writeMissesL2);
			      int l2missrate = 0;
			      System.out.println("k. L2 miss rate:   "+missrate);
			      System.out.println("l. number of L2 writebacks: "+MemoryManager.l2Cache_2way.writeBackL2);
			      int memoryTraffic = MemoryManager.l2Cache_2way.readMisses + MemoryManager.l2Cache_2way.writeMisses + MemoryManager.l2Cache_2way.writeBack;
			      System.out.println("m. total memory traffic: "+memoryTraffic);
		      }
		      sc.close();
	    } catch (FileNotFoundException e) {
	      System.out.println("File not found or error occured");
	      e.printStackTrace();
	    }        
    }
}
