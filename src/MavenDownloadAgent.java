

public class MavenDownloadAgent {

	/**
	 * @param target
	 * @param repo
	 * @param group
	 * @param artifact
	 * @param version
	 */
	public static boolean downloadJar(String storage,String repo,
			String group,String artifact,String version){
		
		/*
		 * 5.0.10.FL.20111102/    
		 */
		
		return false;
	}
	
	public static void main(String[] args) {
		
		boolean success= downloadJar(
			"test/",
			"http://mavensync.zkoss.org/zk/ee-eval/",
			"org.zkoss.theme",
			"breeze",
			"5.0.9.FL.20111017"
		);
		
		//http://mavensync.zkoss.org/zk/ee-eval/org/zkoss/theme/breeze/
		
	}
}
