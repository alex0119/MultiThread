import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CaseRun {
	
	private Properties properties = new Properties();
	private String propertiesfileName = "./config.properties";
	private String resultfilenameprefix = "./result";
	private FileReader propertiesfileReader;
	private FileWriter resultWriter;
	public int count;
	public String serverDNS;
	public ConcurrentHashMap<String,String> concurrentHM;
	
	public CaseRun(){
		try {
			propertiesfileReader = new FileReader(propertiesfileName);
			properties.load(propertiesfileReader);
			count = getThreadCountFromProperties();
			serverDNS = getServerDNS();
			concurrentHM = new ConcurrentHashMap<String,String>(count,(float)0.75,count);
			resultWriter = new FileWriter(resultfilenameprefix + "(" + getDateTime() + ").csv");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getDateTime() {
	        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd HH_mm_ss");
	        Date date = new Date();
	        return dateFormat.format(date);
	}
	
	private int getThreadCountFromProperties() {
        return Integer.parseInt(properties.getProperty("thread_count"));		
	}
	
	private String getServerDNS(){
		return properties.getProperty("server");
	}
	
	public String[] getSortedKeyArray(ConcurrentHashMap<String,String> chm){
		String[] sortedKeyArray = new String[chm.size()];
		Object[] keyArray = chm.keySet().toArray();
		Arrays.sort(keyArray);
		for(int i=0; i<sortedKeyArray.length; i++){
			sortedKeyArray[i] = keyArray[i].toString();
		}
		return sortedKeyArray;
	}
	
	public String getMapValue(ConcurrentHashMap<String,String> chm, String key){        
		return chm.get(key).toString();
	}
	
	public void saveResult(String key, String value) throws IOException{
		resultWriter.write(key + "," + value + "\n");
	}
	
	public void closeFile(){
		try {
			resultWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CaseRun run = new CaseRun();
		TrafficTrace tt= new TrafficTrace(run.serverDNS,run.getDateTime());
		DoCalculate calculate;
		int thread_count = run.count;
		Thread[] threadPool = new Thread[thread_count];
		boolean flag = false;
		try {
			tt.getRouter();
			for (int i=1; i<=thread_count; i++){
				threadPool[i-1] = new Thread(new MultiThread(i, run.concurrentHM));
				threadPool[i-1].start();
			}
			while(!flag){
				for (int k=0; k<thread_count; k++){
					if(!threadPool[k].isAlive()){
						flag = true;
					}else{
						flag = false;
						Thread.sleep(500);
						break;
					}
				}
			}
			String[] keyArray = run.getSortedKeyArray(run.concurrentHM);
			for (int j=0; j<keyArray.length; j++){
				run.saveResult(keyArray[j], run.getMapValue(run.concurrentHM,keyArray[j]));
			}
			calculate = new DoCalculate(run.concurrentHM);
			run.saveResult("Total Execution Time", String.valueOf(calculate.getTotalResponseTime()));
			run.saveResult("Average Response Time", String.valueOf(calculate.getAvgResponseTime()));
			run.saveResult("90% Response Time", String.valueOf(calculate.get90pResponseTime()));
			run.saveResult("90% Average Response Time", String.valueOf(calculate.get90pAvgResponseTime()));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {			
			run.closeFile();
			System.out.println("Performance Test Completed!");
		}
	}
}
