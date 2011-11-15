package org.zkoss.fiddle.maven;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ZKMavenDownloadAgent {

	private static final String ZK6 = "zk6";
	private static final String THEME_SAPPHIRE = "sapphire";
	private static final String THEME_SILVERTAIL = "silvertail";
	private static final String THEME_BREEZE = "breeze";
	private static final String DW_PROPERTES = "dw.propertes";
	private static final String LATEST = "latest/";

	private static List<MavenArtifact> getZKEEPackage(boolean zbind) {
		List<MavenArtifact> list = new ArrayList<MavenArtifact>();

		list.add(new MavenArtifact("org.zkoss.common", "zweb"));
		list.add(new MavenArtifact("org.zkoss.zk", "zk"));
		list.add(new MavenArtifact("org.zkoss.zk", "zul"));
		list.add(new MavenArtifact("org.zkoss.zk", "zhtml"));
		list.add(new MavenArtifact("org.zkoss.zk", "zkex"));
		list.add(new MavenArtifact("org.zkoss.zk", "zkmax"));
		list.add(new MavenArtifact("org.zkoss.zk", "zkplus"));
		list.add(new MavenArtifact("org.zkoss.zk", "zml"));
		list.add(new MavenArtifact("org.zkoss.common", "zcommon"));

		if(zbind){
			list.add(new MavenArtifact( "org.zkoss.zk", "zbind"));
		}
		return list;
	}

	public static boolean checkZKEEPackage(String version,String artificatPrefix) throws IOException {
		String repo = "http://mavensync.zkoss.org/zk/ee-eval/";

		boolean success = MavenDownloadAgent.isFileExist(repo, "org.zkoss.zk", "zuljsp", "1.5");
		if(!success ) {
			return false;
		}
		success = MavenDownloadAgent.isFileExist(repo, "org.zkoss.zkforge.el", "zcommons-el", "1.1.0") && success;
		if(!success ) {
			return false;
		}
		
		List<MavenArtifact> artifacts = getZKEEPackage(ZK6.equals(version));
		for (MavenArtifact artifact : artifacts) {

			boolean newsuccess = MavenDownloadAgent.isFileExist(repo, artifact.getGroup(), artifact.getArtifact(),
					artificatPrefix);

			success = success && newsuccess;
			if(!success ) {
				System.out.println("Can't find "+artifact.getArtifact());
				return false;
			}
		}
		return success;

	}

	public static boolean downloadZKEE(String storage,String version, String artifactPrefix) throws IOException {

		String repo = "http://mavensync.zkoss.org/zk/ee-eval/";

		boolean success = MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.zk", "zuljsp", "1.5");
		
		if(ZK6.equals(version)){
			success = MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.common", "zel", "2.2.1")
			&& success;
			
		}else{
			success = MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.zkforge.el", "zcommons-el", "1.1.0")
			&& success;			
		}

		List<MavenArtifact> artifacts = getZKEEPackage(ZK6.equals(version));
		
		for (MavenArtifact artifact : artifacts) {

			boolean newsuccess = MavenDownloadAgent.downloadJar(storage, repo, artifact.getGroup(),
					artifact.getArtifact(), artifactPrefix);

			success = success && newsuccess;

		}
		
		//Download all theme if exist
		MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.theme", THEME_BREEZE, artifactPrefix);
		MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.theme", THEME_SILVERTAIL, artifactPrefix);
		MavenDownloadAgent.downloadJar(storage, repo, "org.zkoss.theme", THEME_SAPPHIRE, artifactPrefix);
		
		return success;

	}

	private static String readFile(String file) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

		String ret = br.readLine();
		br.close();
		return ret;
	}

	private static SimpleDateFormat dateformatter = new SimpleDateFormat("yyyyMMdd");;

	public static void cleanLastest(String target) {

		File f = new File(target + LATEST);
		if (!f.exists()) {
			f.mkdirs();
		} else {
			for (File childfile : f.listFiles()) {
				childfile.delete();
			}
		}
	}

	/**
	 * Two folder must endwith "/".
	 * @param folder1
	 * @param folder2
	 * @throws FileNotFoundException
	 */
	public static void copyFolder(String folder1, String folder2) throws FileNotFoundException {
		File f = new File(folder1);
		for (File childfile : f.listFiles()) {
			InputStream is = new FileInputStream(childfile);

			FileOutputStream fos = new FileOutputStream(folder2 + childfile.getName());

			int read;
			try {
				read = is.read();
				while (read != -1) {
					fos.write(read);
					read = is.read();
				}
				is.close();
				fos.close();
			} catch (IOException e) {
				
				//This won't happen.
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, ParseException {
		
		String targetFolder = args[0];//"test/";
		String prefix = args[1];//"5.0.10.FL.";
		String version = args.length > 1 ? args[2] : null;
		
		Date lastdate = null;

		try {
			String lastdatestr = readFile(targetFolder + DW_PROPERTES);
			if (lastdatestr != null) {
				lastdate = dateformatter.parse(lastdatestr);
			}

		} catch (FileNotFoundException fnfe) {
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (lastdate == null) {
			//remove the hours and minutes information
			lastdate = dateformatter.parse(dateformatter.format(new Date()));
			lastdate.setMonth(lastdate.getMonth() - 1);
		}

		Date index = dateformatter.parse(dateformatter.format(new Date()));

		while (lastdate.before(index)) {
			String artificat = prefix + dateformatter.format(index);
			if (checkZKEEPackage(version,artificat)) {
				boolean download = downloadZKEE(targetFolder + artificat + "/" ,version, artificat);

				if (download) {
					cleanLastest(targetFolder);
					FileWriter fw = new FileWriter(targetFolder + DW_PROPERTES, false);
					fw.write(dateformatter.format(index));
					fw.close();
					copyFolder(targetFolder + artificat + "/",targetFolder+ LATEST);
					break;
				}
			} else {
				System.out.println("Skip:" + artificat + " failed");
			}

			index.setDate(index.getDate() - 1);
		}
	}
}
