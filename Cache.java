import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class Cache {

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
    
    int readHitsL2 =0;
    int readMissesL2 =0;
    int writeHitsL2 =0;
    int writeMissesL2 =0;
    int writeBackL2 =0;
    
    
    
    StringBuilder[][] cacheMemory;
    String [][] addressesInMemory;
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
    
	Cache(int blockSize, int cacheSize, int cacheAsso, int replacementPolicy, int inclusionProperty) {
		
		this.blockSize = blockSize;
        this.cacheSize = cacheSize;
        this.cacheAsso = cacheAsso;
        this.replacementPolicy = replacementPolicy;
        this.inclusionProperty = inclusionProperty;
        this.sets = (this.cacheSize/(this.blockSize*this.cacheAsso));
        cacheMemory = new StringBuilder[sets][cacheAsso];
        this.addressesInMemory = new String[sets][cacheAsso];
        
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
        // converts hexidecimal address to decimal
        long decimalAddress = (Long.parseLong(hexAddress, 16));
        String binary = Long.toBinaryString(decimalAddress);
        StringBuilder binaryBuilder = new StringBuilder(binary);
        binaryBuilder.reverse();
        while(binaryBuilder.length()<31) {
        	binaryBuilder.append("0");
        }
        binaryBuilder.reverse();
        int blockNumber = getIndex(binaryBuilder);
        String tag = generateTag(binaryBuilder);
        StringBuilder tagreal = new StringBuilder(tag);
        
	        if(opCode.equals("r")) {
	        	readInTag(tagreal,blockNumber,opCode);
	        }
	        else if(opCode.equals("w")){
	        	storeTag(tagreal,blockNumber, opCode);
	        }
    }
	
	
	private void storeTag(StringBuilder tag, int blockNumber, String opCode)
    {   
    	boolean flag= false;
		for(int i=0;i<this.cacheAsso;i++) {
	        	if(cacheMemory[blockNumber][i].compareTo(tag)==0) {
	        			writeHits++;
	        			counter[blockNumber][i]=1;
	        			l1state[blockNumber][i] = 'D';
	        			l1LRU[blockNumber][i]=sequenceNumber;
	        			sequenceNumber++;
	        			flag=true;
	        			break;
	        	//}
     
			} 	
        }
		
		if(flag==false) {
			if(opCode.equals("w")) {
				writeMisses++;
			}
			for(int i=0;i<this.cacheAsso;i++) {
				if(cacheMemory[blockNumber][i].length()==1) {
					cacheMemory[blockNumber][i] = new StringBuilder(tag);
					l1LRU[blockNumber][i] = sequenceNumber;
					l1state[blockNumber][i] = 'D';
					if(opCode.equals("r")) {
						l1state[blockNumber][i] = 'V';
					}
					l1FIFO[blockNumber][i] = sequenceNumber;
					sequenceNumber++;
					counter[blockNumber][i] = 1;
					flag=true;
					break;
				}
			}
		}
		
		if(flag==false) {
			replacementPolicy(tag, blockNumber, this.replacementPolicy, this.inclusionProperty, opCode);
		}
    }
	
	private void replacementPolicy(StringBuilder tag, int blockNumber, int replacementPolicy, int inclusionProperty, String opCode)
    {   
		if(replacementPolicy ==0) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<this.cacheAsso;i++) {
				if(l1LRU[blockNumber][i]<min) {
					min = l1LRU[blockNumber][i];
					index = i;
				}
			}
			
			StringBuilder temp = cacheMemory[blockNumber][index];
			if(l1state[blockNumber][index]=='D') {
				writeBack++;
			}
			cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			counter[blockNumber][index] = 0;
			l1state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				l1state[blockNumber][index] = 'V';
			}
			l1LRU[blockNumber][index] = sequenceNumber;
			sequenceNumber++;
		}
		else if(replacementPolicy ==1 || replacementPolicy ==2) {
			int min=Integer.MAX_VALUE;
			int index = 0;
			for(int i=0;i<this.cacheAsso;i++) {
				if(l1FIFO[blockNumber][i]<min) {
					min = l1FIFO[blockNumber][i];
					index = i;
				}
			}
			
			StringBuilder temp = cacheMemory[blockNumber][index];
			if(l1state[blockNumber][index]=='D') {
				writeBack++;
			}
			cacheMemory[blockNumber][index] =  new StringBuilder(tag);
			counter[blockNumber][index] = 0;
			l1state[blockNumber][index] = 'D';
			if(opCode.equals("r")) {
				l1state[blockNumber][index] = 'V';
			}
			l1FIFO[blockNumber][index] = sequenceNumber;
			sequenceNumber++;
		}
		
    }
	
	private int getIndex(StringBuilder decimalAddress)
    {
		int offset = (int) (Math.log(blockSize) / Math.log(2) + 1e-10);
		int noOfIndexBits = (int) (Math.log(sets) / Math.log(2) + 1e-10);
		String blockNumber = decimalAddress.substring(32-offset-noOfIndexBits-1,32-offset-1);
        return Integer.parseInt(blockNumber,2);
    }
	
	private String generateTag(StringBuilder decimalAddress)
    {
		
		
		int offset = (int) (Math.log(blockSize) / Math.log(2) + 1e-10);
		int noOfIndexBits = (int) (Math.log(sets) / Math.log(2) + 1e-10);
		String tag = decimalAddress.substring(0, 32-offset-noOfIndexBits-1);
        return tag;
    }
	
	private void readInTag(StringBuilder tag, int blockNumber, String opCode)
    {   
		boolean flag=false;
		for(int i=0;i<this.cacheAsso;i++) {
        		if(cacheMemory[blockNumber][i].compareTo(tag)==0) {
        			readHits++;
        			l1LRU[blockNumber][i] = sequenceNumber;
        			sequenceNumber++;
        			flag=true;
        			break;
        		}
        }
		if(flag==false) {
			readMisses++;
			storeTag(tag,blockNumber, opCode);
		}
    }
}
