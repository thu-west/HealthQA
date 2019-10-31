package application.qav2.summarization.util;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import application.qav2.ao.Answer;

public class DumpRemove {          //答案去重； idf和tf-idf去除停用词
	
	public static  List<Answer> AnsdupRemove( List<Answer> alist){  //用于答案集的去重，问题和答案都相同的才去除
		List<String> temp=new ArrayList<String>();
		List<Answer> dup=new ArrayList<Answer>();
		for( Answer a : alist ) {
			String x = a.raw_content/* + a.question.raw_content*/;
			if(temp.contains(x)){
				continue;
			}
			else{
				temp.add(x);
				dup.add(a);
			}
		}
		return dup;
	}
	
	public static HashMap<String, Float> idfStopRemove(HashMap<String, Float> idf) throws FileNotFoundException, IOException{
		List<String> stopword = new ArrayList<String>();
		BufferedReader br = new BufferedReader( new FileReader( "./src/application/zy/stopword.dic" ));
		String line = null;
		while( (line=br.readLine()) != null ) {
			stopword.add(line);
		}
		br.close();
		HashMap<String,Float> afterRemove= new HashMap<String,Float>();
	//	Map<String, Float> temp= new Map<String, Float>();
	//	Map.Entry<K, V>idf.entrySet();
	
		return afterRemove;
	}

}
