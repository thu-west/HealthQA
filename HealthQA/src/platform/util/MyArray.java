package platform.util;

public class MyArray {
	
	public static int findMaxIndex(double[] array) {
		double max = 0;
		int index = 0;
		for( int i=0; i<array.length; i++ ) {
			if(array[i] > max) {
				max = array[i];
				index = i;
			}
		}
		return index;
	}
	
	public static void main(String[] args) {
		System.out.println(findMaxIndex(new double[]{1,2,30,4,1}));
	}

}
