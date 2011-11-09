package org.zkoss.fiddle.maven;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;



public class MavenDownloadAgent {

	public static boolean isFileExist(String repo,
			String group,String artifact,String version) throws IOException{
		return isFileExist(repo, group, artifact, version, null);
	}
	
	public static boolean isFileExist(String repo,
			String group,String artifact,String version,Proxy proxy) throws IOException{
		
		String url = generateURL(repo,group,artifact,version);
		URL u = new URL(url);
		URLConnection conn = null;
		if(proxy != null ){
			conn = u.openConnection(proxy);
		}else{
			conn = u.openConnection();
		}
		
		try{
			InputStream is = conn.getInputStream();
			is.close();
		}catch(FileNotFoundException ex){
			return false;
		}
		return true;
	}
	
	public static boolean downloadJar(String storage,String repo,
			String group,String artifact,String version) throws IOException{
		return downloadJar(storage,repo,group,artifact,version,null);
	}
	
	/**
	 * @param target
	 * @param repo
	 * @param group
	 * @param artifact
	 * @param version
	 * @param proxy
	 * @throws IOException 
	 */
	public static boolean downloadJar(String storage,String repo,
			String group,String artifact,String version,Proxy proxy) throws IOException{
		
		String url = generateURL(repo,group,artifact,version);
		System.out.println("Downloading ["+url+"]...");
		URL u = new URL(url);
		
		URLConnection conn = null;
		if(proxy != null ){
			conn = u.openConnection(proxy);
		}else{
			conn = u.openConnection();
		}
		
		try{
			InputStream is = conn.getInputStream();
			save(storage+"/"+generateFileName(artifact, version),is);
			System.out.print(" Done.\n");
		}catch(FileNotFoundException ex){
			System.out.print(" Not found.\n");
//			ex.printStackTrace();
			return false;
		}
		/*
		 * 5.0.10.FL.20111102/    
		 */
		
		return true;
	}
	
	private static void save(String file,InputStream is) throws IOException{
		
		File f = new File(file);
		f.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(file);

		int read = is.read();
		while(read!=-1){
			fos.write(read);
			read = is.read();
		}

		is.close();
		fos.close();

	}
	
	private static String generateFileName(String artifact,String version){
		return artifact+"-"+version+".jar";
	}
	
	private static String generateURL(String repo,String group,String artifact,String version){
		
		StringBuffer sb = new StringBuffer();
		
		if(repo.endsWith("/")){
			sb.append(repo);
		}else{
			sb.append(repo + "/");
		}
		sb.append(group.replaceAll("\\.","/")+"/");
		sb.append(artifact+"/");
		sb.append(version+"/");
		sb.append(generateFileName(artifact,version));
		return sb.toString();
		////http://mavensync.zkoss.org/zk/ee-eval/org/zkoss/theme/breeze/5.0.9.FL.20111017/breeze-5.0.9.FL.20111017.jar
	}
	
	public static void main(String[] args) throws IOException {
		
		
		//http://mavensync.zkoss.org/zk/ee-eval/org/zkoss/theme/breeze/5.0.9.FL.20111017/breeze-5.0.9.FL.20111017.jar
		boolean success= downloadJar(
			"test/",
			"http://mavensync.zkoss.org/zk/ee-eval/",
			"org.zkoss.theme",
			"breeze",
			"5.0.9.FL.20111017"
		);
		System.out.println(success);
		//http://mavensync.zkoss.org/zk/ee-eval/org/zkoss/theme/breeze/
		
	}
}
