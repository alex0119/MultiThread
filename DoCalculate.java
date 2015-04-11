import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;


public class DoCalculate {
	
	private long[] sortedValueArray;
	
	public DoCalculate(ConcurrentHashMap<String,String> concurrentHashMap){
		Object[] valueArray = concurrentHashMap.values().toArray();
		Arrays.sort(valueArray);
		sortedValueArray = new long[concurrentHashMap.size()];
		for(int i=0; i<valueArray.length; i++){
			sortedValueArray[i] = Long.parseLong(valueArray[i].toString());
		}
	}
	
	public long getTotalResponseTime(){
		long sum = 0;
		for(int i=0; i<sortedValueArray.length; i++){
			sum = sortedValueArray[i] + sum;
		}
		return sum;		
	}
	
	public double getAvgResponseTime(){
		double avgTime = (double)getTotalResponseTime()/(double)sortedValueArray.length;
		return avgTime;
	}
	
	public long get90pResponseTime(){
		if(sortedValueArray.length == 1){
			return sortedValueArray[0];
		} else {
			int position = sortedValueArray.length*9/10;
			return sortedValueArray[position-1];
		}
	}
	
	public double get90pAvgResponseTime(){
		long sum = 0;
		if(sortedValueArray.length == 1){
			return sortedValueArray[0];
		} else {
			int position = sortedValueArray.length*9/10;
			for(int i=0; i<position; i++){
				sum = sortedValueArray[i] + sum;
			}
			return sum/(double)position;
		}
	}
}
