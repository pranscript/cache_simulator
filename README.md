

# Cache Simulator


[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/pranscript)  [![made-with-java](https://img.shields.io/badge/Made%20with-java-blue)]()

****

### Cache and memory hierarchy simulator using L1 or both L1 and L2 cache.

****

### How to run (make is needed)

1. make // this will compile all the .java files to .class files

2. sim_cache <BLOCKSIZE> <L1_SIZE> <L1_ASSOC> <L2_SIZE> <L2_ASSOC> <REPLACEMENT_POLICY> <INCLUSION_PROPERTY> <trace_file>

3. Example --> a) sim_cache 16 1024 2 0 0 0 0 gcc_trace.txt			// only L1 with LRU and non-inclusive
			
		   --> b) sim_cache 16 1024 2 8192 4 0 1 gcc_trace.txt		// L1 and L2 with LRU and inclusive	 

4. make clean // this will remove all the .class files

****

[![HitCount](http://hits.dwyl.com/pranscript/cache_simulator.svg)](http://hits.dwyl.com/pranscript/cache_simulator)