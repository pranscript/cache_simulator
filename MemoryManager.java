
public class MemoryManager {

	static Cache l1Cache;
    
    static Cache l2Cache;
    
    static CacheL1L2 l1Cache_2way;
    
    static CacheL1L2 l2Cache_2way;
    int l1reads = 0;
    int l1writes = 0;
    
    int l2reads = 0;
    int l2writes = 0;
    
    int l1Hits = 0;
    int l1Misses = 0;
    
    int l2Hits = 0;
    int l2Misses = 0;
    
	
	MemoryManager(int blockSize,int l1Size,int l1Asso,int replacementPolicy,int inclusionProperty){
		
		l1Cache = new Cache(blockSize, l1Size, l1Asso, replacementPolicy, inclusionProperty);
		l2Cache = null;
	}
	
	MemoryManager(int blockSize,int l1Size,int l1Asso,int l2Size, int l2Asso, int replacementPolicy,int inclusionProperty){
		
		l1Cache_2way = new CacheL1L2(blockSize, l1Size, l1Asso, replacementPolicy, inclusionProperty);
		l2Cache_2way = new CacheL1L2(blockSize, l2Size, l2Asso, replacementPolicy, inclusionProperty);
	}
	
	
	
	
	public void simulateL1(String address, String opCode)
    {
		if(opCode.equals("r")) {
			l1reads = l1reads +1;
		}
		if(opCode.equals("w")) {
			l1writes = l1writes +1;
		}
		if(l1Cache_2way!=null) l1Cache_2way.inCache(address, opCode);
		else l1Cache.inCache(address, opCode);
        
        
    }
}
