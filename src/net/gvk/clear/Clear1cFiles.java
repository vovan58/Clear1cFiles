package net.gvk.clear;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Clear1cFiles {
	
	private static boolean Windows = false;
	private static boolean Linux = false; 
	private static String UserName;
	private static boolean Verbose = false;
	private static String fileseparator;
	
	
	private static void CleareCache() {
		// ������� ��� 1� ������ ��� ������ ������������
		if (Windows) {
			if (Verbose) {
				System.out.println("- cache Windows");
			}
			
			//String userhome = System.getProperty(new String("user.home"));
			
			Map <String,?> mapenv = System.getenv();
			String AppData = new String( (String) mapenv.get(new String("APPDATA")) + fileseparator +
					"1C"+fileseparator+"1cv8");
			Clear1cCacheDirectory(AppData);
			String LocalAppData = new String( (String) mapenv.get(new String("LOCALAPPDATA")) + fileseparator +
					"1C"+fileseparator+"1cv8");
			Clear1cCacheDirectory(LocalAppData);
		}
	}

	private static int ClearAllUsers() {
		// ������� ��� 1� ��� ���� �������������
		if (Windows) {
			if (Verbose) {
				System.out.println("-allusers Windows");
			}
			String userhome = System.getProperty(new String("user.home"));
			String username = System.getProperty("user.name");
			int lastindex = userhome.lastIndexOf(username);
			String alluserspath = userhome.substring(0, lastindex-1);
			
			Clear1CCacheForAllUsers(alluserspath);
			
		}
		return 0;
	}
	// ����� ����� � ������� ���������� 
	private static boolean find_args(String[] args, String value ) {
		boolean res = false;
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(value)) {
				res = true;
				break;
			}
		}
		return res;
	}
	// �������� �� ������ ���������� ���� ������������ 1�
	public static boolean isCache1C(String fileName) {
		// ������ : 43a197e9-785a-40be-b401-313b0ea67de5
		String pattern_string = "[a-f0-9]{8}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{4}\\-[a-f0-9]{12}";
		Pattern pattern = Pattern.compile(pattern_string);
		Matcher matcher = pattern.matcher(fileName);
		
		return matcher.matches();
	}
	// �������� ���� ������ �� ���������� (����������)
	public static boolean ClearDirectory(File directory) {
		boolean res = true;
		if (directory.isDirectory()) {
			File [] listFiles = directory.listFiles();
			for (int i = 0; i < listFiles.length; i++) {
				boolean candelete = true;
				if (listFiles[i].isDirectory()) {
					candelete = ClearDirectory(listFiles[i]);
				}
				if (candelete) {
					String filename = listFiles[i].getName();
					if (listFiles[i].delete()) {
						if (Verbose) {
							System.out.println(" - ���� "+filename + " ������");
						}
					} else {
						res = false; 
						if (Verbose) {
							System.out.println("! - ���� "+filename + " ��  ������");
						}
					}
				}
				
			}
		}
		return res;
	}
	
	// ������� ���������� ���� 1� ��� ����� ����������
	public static int Clear1cCacheDirectory(String pathname) {
		if (Verbose) {
			System.out.println("������� 1� ���� ���������� " + pathname);
		}
		File dir = new File(pathname);
		if (dir.isDirectory()) {
			
		} else {
			if (Verbose) {
				System.out.println(" - �� ����������!");
			}
			return 8;
		}
		
		File[] listFiles = dir.listFiles();
		for (int i = 0; i < listFiles.length; i++) {
			if (listFiles[i].isDirectory() & isCache1C(listFiles[i].getName()) ) {
				String filename = listFiles[i].getName();
				if (Verbose) {
					System.out.println("  - ��������� "+filename);
				}
				ClearDirectory(listFiles[i]);
				
				;
				
				if (listFiles[i].delete()) {
					System.out.println("���������� "+ filename + "- �������! ");
				}
				else {
					System.out.println("���������� "+ filename + "- �� �������! ");
				}
			}
		}
		
		return 0;
	}
	
	// ������� ���� ����� �������������
	private static int Clear1CCacheForAllUsers(String userspath) {
		int res = 0;
		File dirusers = new File(userspath);
		if (dirusers.isDirectory()) {
			
		} else {
			if (Verbose) {
				System.out.println(" - �� ����������!");
			}
			return 8;
		}
		String lastpath = fileseparator + "AppData";  // \Local
		File[] listusers = dirusers.listFiles();
		for (int i = 0; i < listusers.length; i++) {
			if (listusers[i].isDirectory() ) {
				if (listusers[i].canRead()) {
					if (Verbose) {
						System.out.println(" - user path: " + listusers[i].getName());
					}
					
					String path1cuser = userspath + fileseparator + listusers[i].getName() 
							+ lastpath + fileseparator + "Local" + fileseparator + "1C" 
							+ fileseparator + "1cv8"; //C:\Users\USR1CV8\AppData\Local\1C\1cv8
					File dir1cuser = new File(path1cuser);
					if (dir1cuser.exists()) {
						int res1 = Clear1cCacheDirectory(path1cuser);
					} else {
						if (Verbose) {
							System.out.println(" - ���������� �� ���������� :" + path1cuser);
						}
					}
					path1cuser = userspath + fileseparator + listusers[i].getName() 
							+ lastpath + fileseparator + "Roaming" + fileseparator + "1C" 
							+ fileseparator + "1cv8"; //C:\Users\USR1CV8\AppData\Roaming
					dir1cuser = new File(path1cuser);
					if (dir1cuser.exists()) {
						int res2 = Clear1cCacheDirectory(path1cuser);
					} else {
						if (Verbose) {
							System.out.println(" - ���������� �� ���������� :" + path1cuser);
						}
					}
					
				} else {
					if (Verbose) {
						System.out.println(" ��� ����������� ��������� " + listusers[i].getName());
					}
				}
			}
		}
		
		return res;
	}
	
	private static int ClearUsersTemp(String userspath) {

		// ������� ��������� ���������� �������������
		int res = 0;
		File dirusers = new File(userspath);
		if (dirusers.isDirectory()) {
			
		} else {
			if (Verbose) {
				System.out.println(" - �� ����������!");
			}
			return 8;
		}
		String lastpath = fileseparator + "AppData" + fileseparator + "Local" + fileseparator + "Temp";  // \Local
		File[] listusers = dirusers.listFiles();
		for (int i = 0; i < listusers.length; i++) {
			if (listusers[i].isDirectory() ) {
				if (listusers[i].canRead()) {
					if (Verbose) {
						System.out.println(" - user path: " + listusers[i].getName());
					}
					
					String pathusertemp = userspath + fileseparator + listusers[i].getName() 
							+ lastpath; //C:\Users\USR1CV8\AppData\Local\1C\1cv8
					File dirusertemp = new File(pathusertemp);
					if (dirusertemp.exists()) {
						boolean res1 = ClearDirectory(dirusertemp);
					} else {
						if (Verbose) {
							System.out.println(" - ���������� �� ���������� :" + pathusertemp);
						}
					}
					
				} else {
					if (Verbose) {
						System.out.println(" ��� ����������� ��������� " + listusers[i].getName());
					}
				}
			}
		}
		
		return res;
		
	}

	private static int ClearUserTempFiles() {
		if (Windows) {
			if (Verbose) {
				System.out.println("- usertemp Windows");
			}
			// �������� ��������� ��������� ������ - ������ ������������
			Map <String,?> mapenv = System.getenv();
			String temppath = new String( (String) mapenv.get(new String("LOCALAPPDATA")) + 
					fileseparator + "TEMP");
			File tempdir = new File(temppath);
			if (Verbose) {
				System.out.println(" ������� ���������� " + temppath);
			}
			ClearDirectory(tempdir);
		}
		return 0;
	}
	
	private static int ClearTemp() {
		if (Windows) {
			if (Verbose) {
				System.out.println("- temp Windows");
			}
			// �������� ��������� ��������� ������ - �����
			Map <String,?> mapenv = System.getenv();
			String temppath = (String) mapenv.get("TEMP");
			File tempdir = new File(temppath);
			if (Verbose) {
				System.out.println(" ������� ���������� " + temppath);
			}
			ClearDirectory(tempdir);
			if (!temppath.equals((String) mapenv.get("TMP"))) {
				temppath = (String) mapenv.get("TMP");
				tempdir = new File(temppath);
				if (Verbose) {
					System.out.println(" ������� ���������� " + temppath);
				}
				ClearDirectory(tempdir);
			}
			// � ������ �� �������������
			String userhome = System.getProperty(new String("user.home"));
			String username = System.getProperty("user.name");
			int lastindex = userhome.lastIndexOf(username);
			String alluserspath = userhome.substring(0, lastindex-1);
			
			ClearUsersTemp(alluserspath);
		}
		
		return 0;
	}

	private static int OutHelp() {
		System.out.println("������ �� ��������� ");
		System.out.println("����� :");
		System.out.println(" -help - ��� �������");
		System.out.println(" -allusers - �������� ��������� ������ ���� �������������(�� root)");
		System.out.println(" -cache - �������� ��������� ������ �������� ������������");
		System.out.println(" -temp  - ������� ���������� ��������� ������");
		System.out.println(" -usertemp - ������� ���������� ��������� ������ ������������");
		System.out.println("������������:");
		System.out.println(" --verbose - �������� �������������� ��������� � ������ ���������");
		return 0;
	}
	
	public static void main(String[] args) {
		// TODO ������������� ��������� �������� ������
		System.out.println("��������� ������������ 1� - �������� ��������� ������ v.m. = 1.0.1");
		System.out.println("(C) ��� ��� - ������ �.�. - 2017 ");
		
		// ��������� �����
		String osname = System.getProperty(new String("os.name"));
		if (osname.substring(0, 7).equals(new String("Windows"))) {
			Windows = true;
		}
		else {
			if (osname.substring(0, 5).equals(new String("Linux"))) {
				Linux = true;
			}
			else {
				System.exit(4); 
			}
		}
		Verbose = find_args(args, new String("--verbose"));
		fileseparator = System.getProperty("file.separator");
		
		// ������ �������
		if (find_args(args, "-help") || args.length == 0) OutHelp();
		
		if (find_args(args, new String("-allusers")))  ClearAllUsers();
		
		if (find_args(args, new String("-cache")))  CleareCache(); 
			
		// �������� ��������� ������	
		if (find_args(args, "-temp")) ClearTemp();
		
		// �������� ��������� ������ �������� ������������	
		if (find_args(args, "-usertemp")) ClearUserTempFiles();
		
	}

}
