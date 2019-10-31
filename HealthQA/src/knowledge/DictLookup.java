package knowledge;

import java.util.Scanner;
import java.util.Set;

public class DictLookup {
	/*
	 * 百度百科辞典查询程序
	 */
	static void baikeQuery() throws Exception {
		Set<String> set = DictReadWrite.loadDictInStringSet(DictGallary.baike);
		Scanner scan = new Scanner( System.in );
		while( true ) {
			System.out.println();
			System.out.print("INPUT: ");
			String temp = scan.next();
			if(set.contains(temp)) 
				System.out.println("--> YES");
			else
				System.out.println("--> NO");
		}
	}
	
	public static void main(String[] args) throws Exception {
		baikeQuery();
	}

}
