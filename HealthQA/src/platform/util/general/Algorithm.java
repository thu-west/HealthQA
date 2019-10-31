package platform.util.general;

public class Algorithm {

	public static String longestCommmonSubSquence(String str1, String str2) {
		char[] a = str1.toCharArray();
		char[] b = str2.toCharArray();
		int a_length = a.length;
		int b_length = b.length;
		int[][] lcs = new int[a_length + 1][b_length + 1];
		// 初始化数组
		for (int i = 0; i <= b_length; i++) {
			for (int j = 0; j <= a_length; j++) {
				lcs[j][i] = 0;
			}
		}
		for (int i = 1; i <= a_length; i++) {
			for (int j = 1; j <= b_length; j++) {
				if (a[i - 1] == b[j - 1]) {
					lcs[i][j] = lcs[i - 1][j - 1] + 1;
				}
				if (a[i - 1] != b[j - 1]) {
					lcs[i][j] = lcs[i][j - 1] > lcs[i - 1][j] ? lcs[i][j - 1]
							: lcs[i - 1][j];
				}
			}
		}
		// // 输出数组结果进行观察
		// for (int i = 0; i <= a_length; i++) {
		// for (int j = 0; j <= b_length; j++) {
		// System.out.print(lcs[i][j]+",");
		// }
		// // System.out.println("");
		// }
		// 由数组构造最小公共字符串
		int max_length = lcs[a_length][b_length];
		char[] comStr = new char[max_length];
		int i = a_length, j = b_length;
		while (max_length > 0) {
			if (lcs[i][j] != lcs[i - 1][j - 1]) {
				if (lcs[i - 1][j] == lcs[i][j - 1]) {// 两字符相等，为公共字符
					comStr[max_length - 1] = a[i - 1];
					max_length--;
					i--;
					j--;
				} else {// 取两者中较长者作为A和B的最长公共子序列
					if (lcs[i - 1][j] > lcs[i][j - 1]) {
						i--;
					} else {
						j--;
					}
				}
			} else {
				i--;
				j--;
			}
		}
		return new String(comStr);
	}
}
