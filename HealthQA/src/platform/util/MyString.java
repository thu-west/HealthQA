package platform.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyString {
	
	public static String toSemiangle(String s) {
		return s.replace("[　]+", " ");
	}
	
	public static String reverse( String a ) {
		StringBuffer sb = new StringBuffer();
		for( int i=a.length()-1; i>=0; i-- )
			sb.append(a.charAt(i));
		return sb.toString();
	}
	
	public static String md5(String str){
		String temp = null;
		char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};       
		byte[] btinput = str.getBytes();
		try {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(btinput);
		byte[] mds = md.digest();
		int j = mds.length;
        char strs[] = new char[j * 2];
        int k = 0;
        for (int i = 0; i < j; i++) {
             byte byte0 = mds[i];
             strs[k++] = hexDigits[byte0 >>> 4 & 0xf];
             strs[k++] = hexDigits[byte0 & 0xf];
         }
        temp = new String(strs);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

}
