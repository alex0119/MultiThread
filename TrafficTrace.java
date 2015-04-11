
public class TrafficTrace {
	
	private String commandStr;
	
	public TrafficTrace(String dns, String datetime){
		commandStr = "traceroute " + dns + " >traceroute_" + datetime + ".log";
	}
	
	public void getRouter() throws Exception{ 		
		Process process = Runtime.getRuntime().exec(commandStr);
		process.waitFor();
		process.destroy();
	}

}
