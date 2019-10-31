package other.fire;

import java.io.*;

public class Stat {
	
	public static void main(String[] args)  throws Exception {
		String[] fn = new String[]{"res\\manual_test_result\\jzw", "res\\manual_test_result\\tjy", "res\\manual_test_result\\yw"};
		int[] rs = new int[100];
		for( int i=0; i<rs.length; i++) {
			BufferedReader[] brs = new BufferedReader[3];
			int[] temp = new int[3];
			for( int j=0; j<3; j++){
				brs[j] = new BufferedReader(new FileReader( fn[j]+"\\"+(i+1)+".txt"));
				String line = null;
				temp[j] = 100;
				while( (line=brs[j].readLine()) != null ){
					if( line.matches("1")) 
						temp[j] = 1;
					else if( line.matches("-1"))
						temp[j] = -1;
					else if( line.matches("0"))
						temp[j] = 0;
				}
				if(temp[j]==100){
					System.out.println("error in "+fn[j]+" file "+(i+1)+".txt");
				}
			}
			int sum = temp[0] + temp[1] + temp[2];
			if( sum<=-2)
				rs[i] = -1;
			else if( sum>=2)
				rs[i] = 1;
			else if( sum!=0)
				rs[i] = 0;
			else if( (temp[0]==temp[1]) )
				rs[i] = 10;
			else
				rs[i] = 0;
		}
		
		int fu = 0;
		int zh = 0;
		int li = 0;
		int sh = 0;
		
		for( int i=0; i<100; i++) {
			if( rs[i] == -1 )
				fu++;
			else if( rs[i] == 1)
				zh++;
			else if( rs[i]==0)
				li++;
			else
				sh++;
		}
		
		System.out.println("(1):"+zh+"  (-1):"+fu+"  (equal)"+li+"   (unk):"+sh);
	}

}
