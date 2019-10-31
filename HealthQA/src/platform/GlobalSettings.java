package platform;

import java.io.*;
import java.util.regex.Pattern;

import platform.util.MyProperties;

public class GlobalSettings {
	
	public static String OS = null;
	public static String ProjectContextDir = null;
	public static String SystemEncode = null;

	public static String database_url = null;
	public static String healthqa_db_url = null;
	public static String meddict_db_url = null;
	public static String database_username = null;
	public static String database_password = null;
	
	static MyProperties global_props = null;
	
	static {
		try {
			OS = getOS();
			System.out.println("|-- OS: "+OS);
//      ProjectContextDir = getProjectContextDir();
      ProjectContextDir = System.getProperty("user.dir").replace("\\", "/");
//      ProjectContextDir = ProjectContextDir.substring(0, ProjectContextDir.lastIndexOf("/") + 1);
      System.out.println("|-- ProjectContextDir: "+ProjectContextDir);
      System.out.println("|-- user.dir: "+System.getProperty("user.dir"));
      System.out.println("|-- java.class.path: "+System.getProperty("java.class.path"));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			global_props = new MyProperties(GlobalSettings.contextDir("conf/healthqa.properties"));
			database_url = global_props.getString("ishc_data_db_url");
			healthqa_db_url = global_props.getString("healthqa_db_url");;
			meddict_db_url = global_props.getString("meddict_db_url");;
			database_username = global_props.getString("username");
			database_password = global_props.getString("password");
			System.out.println("|-- Database: "+database_url);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/* =============================================
		GlobalSettings Method
	   ============================================= */
	
	public static String contextDir ( String path ) {
		return (ProjectContextDir+("/"+path)).replace("\\", "/").replaceAll("[/]+", "/");
	}
	
	/**
	 * 获得操作系统的类型   win64  win32  linux64   linux32		macos
	 * @return
	 * @throws Exception
	 */
	private static String getOS() throws Exception {
		String name = System.getProperty("os.name");
		String version = System.getProperty("os.version");
		String arch = System.getProperty("os.arch");
		if(name.contains("Windows")) {
			SystemEncode = "GBK";
			if(arch.contains("64"))
				return "win64";
			else if(arch.contains("32") || arch.contains("x86"))
				return "win32";
		} else if (name.contains("Linux")) {
			SystemEncode = "UTF8";
			if(arch.contains("64"))
				return "linux64";
			else if(arch.contains("32") || arch.contains("x86"))
				return "linux32";
		}
		else
			return "macos";
		throw new Exception("Cannot judge with os version: ["+name+"/"+version+"/"+arch+"]");
	}
	
	/**
	 * 若干种情况：
	 * 1. Eclipse中：user_path是工程目录，class_path是工程的bin目录以及其他classpath目录用;或者:间隔
	 * 2. 命令行执行.class文件：
	 * 3. 命令行执行java -jar A/B/C.jar：user_path是执行命令的目录，class_path是A/B/C.jar（可分为绝对路径和相对路径，绝对路径在linux下以/开头，在windows下以D:/开头
	 * 4. 命令行执行java -classpath A/B/C.jar Main：
	 */
	
	private static String getProjectContextDir() {
		String path_seperator = File.pathSeparator;		// windows下是";",linux下是":"
		String user_path = System.getProperty("user.dir").replace("\\", "/");           // 运行java命令时用户所在的目录，全部使用/式分隔符
		String class_path = System.getProperty("java.class.path").replace("\\", "/");   // 运行的java命令中指定的classpath或者jar包的路径
		// 相对路径转绝对路径
		if( ! (class_path.startsWith("/") || Pattern.matches("^[a-zA-Z]{1}:/.*", class_path+"/")) ) {
			class_path = user_path+"/"+class_path;
		}
		// 去除多重class_path
		if(class_path.contains(path_seperator)) {
			class_path = class_path.substring(0, class_path.indexOf(path_seperator));
		}
		if (class_path.endsWith(".jar")) {
			// 如果是执行jar包
			class_path = class_path.substring(0, class_path.lastIndexOf("/") + 1);
		} else {
			// 路径类型的class_path，这个classpath应该是class文件所在目录，理论上不应该存储任何资源，资源应该存储在父目录中，所以统统采用父目录作为真正的工程根目录，在Eclipse中也不例外
			class_path = class_path+"/。。/";
		}
		return (class_path+"/").replaceAll("[/]+", "/").replace("/./", "/").replaceAll("[^/]+/../", "");
	}

}
