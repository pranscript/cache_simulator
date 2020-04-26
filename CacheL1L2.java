import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class CacheL1L2 {


	final int blockSize;
    final int cacheSize;
    final int cacheAsso;
    final int replacementPolicy;
    final int inclusionProperty;
    final int sets;
    int readHits =0;
    int readMisses =0;
    int writeHits =0;
    int writeMisses =0;
    int writeBack =0;
    
    int readsL2 =0;
    int readHitsL2 =0;
    int readMissesL2 =0;
    int writeHitsL2 =0;
    int writeMissesL2 =0;
    int writeBackL2 =0;
    
    
    StringBuilder[][] cacheMemory;
    StringBuilder [] addresses;
    int [][] counter;
    int [][] l1LRU;
    int [][] l1FIFO;
    int [][] l1optimal;
    char [][] l1state;
    
    int [][] l2LRU;
    int [][] l2FIFO;
    int [][] l2optimal;
    char [][] l2state;
    
    int sequenceNumber = 0;
    int sequenceNumberL2 = 0;
    
	CacheL1L2(int blockSize, int cacheSize, int cacheAsso, int replacementPolicy, int inclusionProperty) {
		
		this.blockSize = blockSize;
        this.cacheSize = cacheSize;
        this.cacheAsso = cacheAsso;
        this.replacementPolicy = replacementPolicy;
        this.inclusionProperty = inclusionProperty;
        this.sets = (this.cacheSize/(this.blockSize*this.cacheAsso));
        cacheMemory = new StringBuilder[sets][cacheAsso];
        this.addresses = new StringBuilder[sets];
        
        this.counter = new int[sets][cacheAsso];
        this.l1LRU = new int[sets][cacheAsso];
        this.l1FIFO = new int[sets][cacheAsso];
        this.l1optimal = new int[sets][cacheAsso];
        this.l1state = new char[sets][cacheAsso];
        
        
        this.l2LRU = new int[sets][cacheAsso];
        this.l2FIFO = new int[sets][cacheAsso];
        this.l1optimal = new int[sets][cacheAsso];
        this.l2state = new char[sets][cacheAsso];
        
        for(int i = 0; i < sets	; i++)
        {
        	for(int j = 0; j < cacheAsso; j++)
            {
        		this.cacheMemory[i][j] = new StringBuilder("1");
        		this.addresses[i] = new StringBuilder("1");
        		this.l1LRU[i][j] = 0;
        		this.l1FIFO[i][j] = 0;
        		this.l1state[i][j] = 'I';
        		
        		this.l2LRU[i][j] = 0;
        		this.l2FIFO[i][j] = 0;
        		this.l2state[i][j] = 'I';
            }
        }
	}
	
	
	public void inCache(String hexAddress, String opCode)
    {	
        long decimalAddress = (Long.parseLong(hexAddress, 16));
        String binary = Long.toBinaryString(decimalAddress);
        StringBuilder binaryBuilder = new StringBuilder(binary);
        binaryBuilder.reverse();
        while(binaryBuilder.length()<31) {
        	binaryBuilder.append("0");
        }
        binaryBuilder.reverse();
        int blockNumber = getIndex(binaryBuilder);
        storeDecAddress(blockNumber, binaryBuilder);
        String tag = generateTag(binaryBuilder);
        StringBuilder tagreal = new StringBuilder(tag);

        	if(opCode.equals("r")) {
	        	readInTagL1(tagreal,blockNumber,opCode);
	        }
	        else if(opCode.equals("w")){
	        	storeTagL1(tagreal,blockNumber, opCode);
	        }
          
    }
    
	private int getIndex(StringBuilder decimalAddress)
    {
		int offset=0;
		int noOfIndexBits = 0;
		offset = (int) (Math.log(MemoryManager.l1Cache_2way.blockSize) / Math.log(2) + 1e-10);
		noOfIndexBits = (int) (Math.log(MemoryManager.l1Cache_2way.sets) / Math.log(2) + 1e-10);
		String blockNumber = decimalAddress.substring(32-offset-noOfIndexBits-1,32-offset-1);
        return Integer.parseInt(blockNumber,2);
    }
	
	private String generateTag(StringBuilder decimalAddress)
    {
		int offset=0;
		int noOfIndexBits = 0;
		offset = (int) (Math.log(MemoryManager.l1Cache_2way.blockSize) / Math.log(2) + 1e-10);
		noOfIndexBits = (int) (Math.log(MemoryManager.l1Cache_2way.sets) / Math.log(2) + 1e-10);
		String tag = decimalAddress.substring(0, 32-offset-noOfIndexBits-1);
        return tag;
    }
	
	
	
	
	private void readInTagL1(StringBuilder tag, int blockNumber, String opCode)
    {   
		boolean flagl1=false;
		boolean flagl2=false;
		for(int i=0;i<MemoryManager.l1Cache_2way.cacheAsso;i++) {
        		if(MemoryManager.l1Cache_2way.cacheMemory[blockNumber][i].compareTo(tag)==0) {
        			MemoryManager.l1Cache_2way.l1LRU[blockNumber][i] = MemoryManager.l1Cache_2way.sequenceNumber;
        			MemoryManager.l1Cache_2way.sequenceNumber++;
        			flagl1=true;
        			break;
        		}
        }
		if(flagl1==false) {
			
			MemoryManager.l1Cache_2way.readMisses++;
			int blockNumberL2 = getIndexL2(addresses[blockNumber]);
			String tagL2 = generateTagL2(addresses[blockNumber]);
			StringBuilder tagrealL2 = new StringBuilder(tagL2);
			for(int i=0;i<MemoryManager.l2Cache_2way.cacheAsso;i++) {
	        		if(MemoryManager.l2Cache_2way.cacheMemory[blockNumberL2][i].compareTo(tagrealL2)==0) {
	        			MemoryManager.l2Cache_2way.l2LRU[blockNumberL2][i] = MemoryManager.l2Cache_2way.sequenceNumberL2;
	        			MemoryManager.l2Cache_2way.sequenceNumberL2++;
	        			flagl2=true;
	        			break;
	        		} 
        	}
			if(flagl2==false) {
				MemoryManager.l2Cache_2way.readMissesL2++;
			}
		}
		
		if(flagl1==false && flagl2==false) {
			storeTagL1(tag,blockNumber, opCode);
			int blockNumberL2 = getIndexL2(addresses[blockNumber]);
			String tagL2 = generateTagL2(addresses[blockNumber]);
			StringBuilder tagrealL2 = new StringBuilder(tagL2);
			storeTagOnlyL2(tagrealL2,blockNumberL2, opCode);
			
		}
    }
	
	
	private void storeTagL1(StringBuilder tag, int blockNumber, String opCode)
    {   
    	boolean flag= false;
		for(int i=0;i<MemoryManager.l1Cache_2way.cacheAsso;i++) {
	        	if(MemoryManager.l1Cache_2way.cacheMemory[blockNumber][i].compareTo(tag)==0) {
	        		MemoryManager.l1Cache_2way.writeHits++;
	        			MemoryManager.l1Cache_2way.l1state[blockNumber][i] = 'D';
	        			MemoryManager.l1Cache_2way.l1LRU[blockNumber][i]=MemoryManager.l1Cache_2way.sequenceNumber;
	        			MemoryManager.l1Cache_2way.sequenceNumber++;
	        			flag=true;
	        			break;
			} 	
        }
		
		if(flag==false) {
			if(opCode.equals("w")) {
				MemoryManager.l1Cache_2way.writeMisses++;
			}
			for(int i=0;i<MemoryManager.l1Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l1Cache_2way.cacheMemory[blockNumber][i].length()==1) {
					MemoryManager.l1Cache_2way.cacheMemory[blockNumber][i] = new StringBuilder(tag);
					MemoryManager.l1Cache_2way.l1LRU[blockNumber][i] = MemoryManager.l1Cache_2way.sequenceNumber;
					MemoryManager.l1Cache_2way.l1state[blockNumber][i] = 'D';
					if(opCode.equals("r")) {
						MemoryManager.l1Cache_2way.l1state[blockNumber][i] = 'V';
					}
					MemoryManager.l1Cache_2way.l1FIFO[blockNumber][i] = MemoryManager.l1Cache_2way.sequenceNumber;
					MemoryManager.l1Cache_2way.sequenceNumber++;
					flag=true;
					break;
				}
			}
		}
		
		if(flag==false) {
			MemoryManager.l1Cache_2way.writeBack++;
			replacementPolicyL1(tag, blockNumber, this.replacementPolicy, this.inclusionProperty, opCode);
		}
    }
	
	private void replacementPolicyL1(StringBuilder tag, int blockNumber, int replacementPolicy, int inclusionProperty, String opCode)
    {   
		if(replacementPolicy ==0) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<MemoryManager.l1Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l1Cache_2way.l1LRU[blockNumber][i]<min) {
					min = MemoryManager.l1Cache_2way.l1LRU[blockNumber][i];
					index = i;
				}
			}
			MemoryManager.l1Cache_2way.cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			MemoryManager.l1Cache_2way.l1state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				MemoryManager.l1Cache_2way.l1state[blockNumber][index] = 'V';
			}
			MemoryManager.l1Cache_2way.l1LRU[blockNumber][index] = MemoryManager.l1Cache_2way.sequenceNumber;
			MemoryManager.l1Cache_2way.sequenceNumber++;
			int blockNumberL2 = getIndexL2(addresses[blockNumber]);
			String tagL2 = generateTagL2(addresses[blockNumber]);
			StringBuilder tagrealL2 = new StringBuilder(tagL2);
			storeTagOnlyL2(tagrealL2,blockNumberL2, opCode);
			
		}
		else if(replacementPolicy ==1 || replacementPolicy ==2) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<MemoryManager.l1Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l1Cache_2way.l1FIFO[blockNumber][i]<min) {
					min = MemoryManager.l1Cache_2way.l1FIFO[blockNumber][i];
					index = i;
				}
			}
			
			StringBuilder temp = MemoryManager.l1Cache_2way.cacheMemory[blockNumber][index];
			MemoryManager.l1Cache_2way.cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			MemoryManager.l1Cache_2way.l1state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				MemoryManager.l1Cache_2way.l1state[blockNumber][index] = 'V';
			}
			l1FIFO[blockNumber][index] = MemoryManager.l1Cache_2way.sequenceNumber;
			MemoryManager.l1Cache_2way.sequenceNumber++;
			int blockNumberL2 = getIndexL2(addresses[blockNumber]);
			String tagL2 = generateTagL2(addresses[blockNumber]);
			StringBuilder tagrealL2 = new StringBuilder(tagL2);
			storeTagOnlyL2(tagrealL2,blockNumberL2, opCode);
		}
		
    }
	
	
	private void storeTagOnlyL2(StringBuilder tag, int blockNumber, String opCode)
    {   
    	boolean flag= false;
		for(int i=0;i<MemoryManager.l2Cache_2way.cacheAsso;i++) {
	        	if(MemoryManager.l2Cache_2way.cacheMemory[blockNumber][i].compareTo(tag)==0) {
	        		MemoryManager.l2Cache_2way.writeHitsL2++;
	        			MemoryManager.l2Cache_2way.l2state[blockNumber][i] = 'D';
	        			MemoryManager.l2Cache_2way.l2LRU[blockNumber][i]=MemoryManager.l2Cache_2way.sequenceNumberL2;
	        			MemoryManager.l2Cache_2way.sequenceNumberL2++;
	        			flag=true;
	        			break;
			} 	
        }
		
		if(flag==false) {
			if(opCode.equals("w")) {
				MemoryManager.l2Cache_2way.writeMissesL2++;
			}
			for(int i=0;i<MemoryManager.l2Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l2Cache_2way.cacheMemory[blockNumber][i].length()==1) {
					MemoryManager.l2Cache_2way.cacheMemory[blockNumber][i] = new StringBuilder(tag);
					MemoryManager.l2Cache_2way.l2LRU[blockNumber][i] = MemoryManager.l2Cache_2way.sequenceNumberL2;
					MemoryManager.l2Cache_2way.l2state[blockNumber][i] = 'D';
					if(opCode.equals("r")) {
						MemoryManager.l2Cache_2way.l2state[blockNumber][i] = 'V';
					}
					MemoryManager.l2Cache_2way.l2FIFO[blockNumber][i] = MemoryManager.l2Cache_2way.sequenceNumberL2;
					MemoryManager.l2Cache_2way.sequenceNumberL2++;
					flag=true;
					break;
				}
			}
		}
		
		if(flag==false) {
			replacementPolicyL2(tag, blockNumber, this.replacementPolicy, this.inclusionProperty, opCode);
		}
    }
	
	private void replacementPolicyL2(StringBuilder tag, int blockNumber, int replacementPolicy, int inclusionProperty, String opCode)
    {   
		if(replacementPolicy ==0) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<MemoryManager.l2Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l2Cache_2way.l2LRU[blockNumber][i]<min) {
					min = MemoryManager.l2Cache_2way.l2LRU[blockNumber][i];
					index = i;
				}
			}
			MemoryManager.l2Cache_2way.cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			MemoryManager.l2Cache_2way.l2state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				MemoryManager.l2Cache_2way.l2state[blockNumber][index] = 'V';
			}
			MemoryManager.l2Cache_2way.l2LRU[blockNumber][index] = MemoryManager.l2Cache_2way.sequenceNumberL2;
			MemoryManager.l2Cache_2way.sequenceNumberL2++;
			
		}
		else if(replacementPolicy ==1 || replacementPolicy ==2) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<MemoryManager.l2Cache_2way.cacheAsso;i++) {
				if(MemoryManager.l2Cache_2way.l2FIFO[blockNumber][i]<min) {
					min = MemoryManager.l2Cache_2way.l2FIFO[blockNumber][i];
					index = i;
				}
			}
			
			StringBuilder temp = MemoryManager.l1Cache_2way.cacheMemory[blockNumber][index];
			MemoryManager.l2Cache_2way.cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			MemoryManager.l2Cache_2way.l2state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				MemoryManager.l2Cache_2way.l2state[blockNumber][index] = 'V';
			}
			MemoryManager.l2Cache_2way.l2FIFO[blockNumber][index] = MemoryManager.l2Cache_2way.sequenceNumberL2;
			MemoryManager.l2Cache_2way.sequenceNumberL2++;
		}
		
    }
	
	private void storeDecAddress(int blockNumber, StringBuilder binaryAddress)
    {
        addresses[blockNumber] = new StringBuilder(binaryAddress);
    }
    
	
	private int getIndexL2(StringBuilder binaryAddress)
    {
		int offset=0;
		int noOfIndexBits = 0;
		offset = (int) (Math.log(MemoryManager.l2Cache_2way.blockSize) / Math.log(2) + 1e-10);
		noOfIndexBits = (int) (Math.log(MemoryManager.l2Cache_2way.sets) / Math.log(2) + 1e-10);
		String blockNumberL2 = binaryAddress.substring(32-offset-noOfIndexBits-1,32-offset-1);
        return Integer.parseInt(blockNumberL2,2);
    }
	
	
	
	private String generateTagL2(StringBuilder binaryAddress)
    {
		int offset=0;
		int noOfIndexBits = 0;
		offset = (int) (Math.log(MemoryManager.l2Cache_2way.blockSize) / Math.log(2) + 1e-10);
		noOfIndexBits = (int) (Math.log(MemoryManager.l2Cache_2way.sets) / Math.log(2) + 1e-10);
		String tagL2 = binaryAddress.substring(0, 32-offset-noOfIndexBits-1);
        return tagL2;
    }
 
}
