import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentHashMap;

class MultiThread implements Runnable {
	private int number;
	private DecimalFormat df = new DecimalFormat("00000");
	private ConcurrentHashMap<String,String> chm;
	
	public MultiThread (int count, ConcurrentHashMap<String,String> concurrentHM){
		number = count;
		chm = concurrentHM;
	}
	public void run() {
		long start_time = System.nanoTime();
		//affairs handling
		long end_time= System.nanoTime();
		long term = end_time - start_time;
		chm.put("Thread_" + df.format(number), Long.toString(term));
		System.out.println("Thread_" + df.format(number) + ": TimeResume = " + chm.get("Thread_" + df.format(number)).toString());
		return;
	}
}
