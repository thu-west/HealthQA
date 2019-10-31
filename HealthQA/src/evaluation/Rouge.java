package evaluation;

import java.util.ArrayList;

import platform.nlp.ao.Word;

public class Rouge {

	public static ArrayList<String> toArray(Word[][] word) {
		ArrayList<String> word2 = new ArrayList<String>();

		for (int i = 0; i < word.length; i++) {
			for (int j = 0; j < word[i].length; j++) {
				word[i][j].cont = word[i][j].cont.replaceAll(
						"(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", ""); // Punctuation
																// shouldn't be
																// counted in.
				if (!(word[i][j].cont.equals(""))) {
					word2.add(word[i][j].cont);
				}
			}
		}
		return word2;
	}

	public static double Rouge1(Word[][] sum, Word[][] refer) { // input one sum
																// and one
																// refer, output
																// R1
		double r1 = 0;
		int match1 = 0;
		int totleRef1 = 0;
		ArrayList<String> Sum1 = new ArrayList<String>();
		ArrayList<String> Ref1 = new ArrayList<String>();
		ArrayList<String> Sum = new ArrayList<String>();       //combine same string
		ArrayList<String> Ref = new ArrayList<String>();       //combine same string
		ArrayList<Integer> num_of_sum = new ArrayList<Integer>();//record num of same string in sum
		ArrayList<Integer> num_of_ref = new ArrayList<Integer>();//record num of same string in ref
		Ref1 = toArray(refer);
		Sum1 = toArray(sum);
		for(int i=0;i<Ref1.size();i++){  
			String find=Ref1.get(i);
			int num_index,num_value;
			if(Ref.contains(find)){     //modify num
				num_index=Ref.indexOf(find);
				num_value=num_of_ref.get(num_index)+1;
				num_of_ref.set(num_index, num_value);
			}
			else{						//add string and num
				Ref.add(Ref1.get(i)); 
				num_of_ref.add(1);
			}
		}
		for(int i=0;i<Sum1.size();i++){  
			String find=Sum1.get(i);
			int num_index,num_value;
			if(Sum.contains(find)){     //modify num
				num_index=Sum.indexOf(find);
				num_value=num_of_sum.get(num_index)+1;
				num_of_sum.set(num_index, num_value);
			}
			else{						//add string and num
				Sum.add(Sum1.get(i)); 
				num_of_sum.add(1);
			}
		}
		totleRef1 = Ref1.size();
		for (int i = 0; i < Sum.size(); i++) {
			if (Ref.contains(Sum.get(i))) {
				int index_ref=Ref.indexOf(Sum.get(i));
				int match_temp=Math.min(num_of_ref.get(index_ref), num_of_sum.get(i));
				match1+=match_temp;
			}
		}
	//	System.out.println("TotleMatch1:"+match1);
		r1 = (double) match1 / totleRef1;
		return r1;
	}

	public static double Rouge2(Word[][] sum, Word[][] refer) {
		double r2 = 0;
		int match2 = 0;
		int totleRef2 = 0;
		ArrayList<String> Sum2 = new ArrayList<String>();
		ArrayList<String> Ref2 = new ArrayList<String>();
		ArrayList<String> newSum2 = new ArrayList<String>();
		ArrayList<String> newRef2 = new ArrayList<String>();
		ArrayList<String> Sum = new ArrayList<String>();
		ArrayList<String> Ref = new ArrayList<String>();
		ArrayList<Integer> num_of_sum = new ArrayList<Integer>();//record num of same string in sum
		ArrayList<Integer> num_of_ref = new ArrayList<Integer>();//record num of same string in ref
		Ref2 = toArray(refer);
		Sum2 = toArray(sum);
		for (int i = 0; i < Ref2.size() - 1; i++) {
			String temp = Ref2.get(i) + Ref2.get(i + 1);
			newRef2.add(temp);
		}
		for (int i = 0; i < Sum2.size() - 1; i++) {
			String temp = Sum2.get(i) + Sum2.get(i + 1);
			newSum2.add(temp);
		}
		for(int i=0;i<newRef2.size();i++){  
			String find=newRef2.get(i);
			int num_index,num_value;
			if(Ref.contains(find)){     //modify num
				num_index=Ref.indexOf(find);
				num_value=num_of_ref.get(num_index)+1;
				num_of_ref.set(num_index, num_value);
			}
			else{						//add string and num
				Ref.add(newRef2.get(i)); 
				num_of_ref.add(1);
			}
		}
		for(int i=0;i<newSum2.size();i++){  
			String find=newSum2.get(i);
			int num_index,num_value;
			if(Sum.contains(find)){     //modify num
				num_index=Sum.indexOf(find);
				num_value=num_of_sum.get(num_index)+1;
				num_of_sum.set(num_index, num_value);
			}
			else{						//add string and num
				Sum.add(newSum2.get(i)); 
				num_of_sum.add(1);
			}
		}
		totleRef2 = newRef2.size();
	//	System.out.println("TotleRef2:"+totleRef2);
		for (int i = 0; i < Sum.size(); i++) {
			if (Ref.contains(Sum.get(i))) {
				int index_ref=Ref.indexOf(Sum.get(i));
				int match_temp=Math.min(num_of_ref.get(index_ref), num_of_sum.get(i));
				match2+=match_temp;
			}
		}
//		System.out.println("TotleMatch2:"+match2);
		r2 = (double) match2 / totleRef2;
		return r2;
	}

	public static double RougeSU4(Word[][] sum, Word[][] refer) {
		double r4 = 0;
		int match4 = 0;
		int matchSkip4 = 0;
		int matchU4 = 0;
		int totleRef4 = 0;
		int totleSum4 = 0;
		ArrayList<String> Sum4 = new ArrayList<String>();
		ArrayList<String> Ref4 = new ArrayList<String>();
		ArrayList<String> Sum = new ArrayList<String>();
		ArrayList<String> Ref = new ArrayList<String>();
		ArrayList<String> newSum4 = new ArrayList<String>();
		ArrayList<String> newRef4 = new ArrayList<String>();
		ArrayList<String> newSum = new ArrayList<String>();
		ArrayList<String> newRef = new ArrayList<String>();
		ArrayList<Integer> num_of_sum = new ArrayList<Integer>();//record num of same string in sum
		ArrayList<Integer> num_of_ref = new ArrayList<Integer>();//record num of same string in ref
		ArrayList<Integer> num_of_sum_new = new ArrayList<Integer>();
		ArrayList<Integer> num_of_ref_new = new ArrayList<Integer>();
		Ref4 = toArray(refer);
		Sum4 = toArray(sum);
		for (int i = 0; i < Ref4.size() - 1; i++) {
			for (int j = 1; j < 6; j++) {
				if (i + j < Ref4.size()) {
					String temp = Ref4.get(i) + Ref4.get(i + j); // put bigram
																	// whose
																	// maximal
																	// distance
																	// is 4 into
																	// arraylist
					newRef4.add(temp);
				}
			}
		}
		for (int i = 0; i < Sum4.size() - 1; i++) {
			for (int j = 1; j < 6; j++) {
				if (i + j < Sum4.size()) {
					String temp = Sum4.get(i) + Sum4.get(i + j); // put bigram
																	// whose
																	// maximal
																	// distance
																	// is 4 into
																	// arraylist
					newSum4.add(temp);
				}
			}
		}
		//Ref and Sum
		for(int i=0;i<Ref4.size();i++){  
			String find=Ref4.get(i);
			int num_index,num_value;
			if(Ref.contains(find)){     //modify num
				num_index=Ref.indexOf(find);
				num_value=num_of_ref.get(num_index)+1;
				num_of_ref.set(num_index, num_value);
			}
			else{						//add string and num
				Ref.add(Ref4.get(i)); 
				num_of_ref.add(1);
			}
		}
		for(int i=0;i<Sum4.size();i++){  
			String find=Sum4.get(i);
			int num_index,num_value;
			if(Sum.contains(find)){     //modify num
				num_index=Sum.indexOf(find);
				num_value=num_of_sum.get(num_index)+1;
				num_of_sum.set(num_index, num_value);
			}
			else{						//add string and num
				Sum.add(Sum4.get(i)); 
				num_of_sum.add(1);
			}
		}
		//newRef and newSum
		for(int i=0;i<newRef4.size();i++){  
			String find=newRef4.get(i);
			int num_index,num_value;
			if(newRef.contains(find)){     //modify num
				num_index=newRef.indexOf(find);
				num_value=num_of_ref_new.get(num_index)+1;
				num_of_ref_new.set(num_index, num_value);
			}
			else{						//add string and num
				newRef.add(newRef4.get(i)); 
				num_of_ref_new.add(1);
			}
		}
		for(int i=0;i<newSum4.size();i++){  
			String find=newSum4.get(i);
			int num_index,num_value;
			if(newSum.contains(find)){     //modify num
				num_index=newSum.indexOf(find);
				num_value=num_of_sum_new.get(num_index)+1;
				num_of_sum_new.set(num_index, num_value);
			}
			else{						//add string and num
				newSum.add(newSum4.get(i)); 
				num_of_sum_new.add(1);
			}
		}
		//
//		for (int i = 0; i < Sum4.size(); i++) {
//			if (Ref4.contains(Sum4.get(i))) {
//				matchU4++;
//			}
//		}
//		for (int i = 0; i < newSum4.size(); i++) {
//			if (newRef4.contains(newSum4.get(i))) {
//				matchSkip4++;
//			}
//		}
		//
		
		for (int i = 0; i < Sum.size(); i++) {
			if (Ref.contains(Sum.get(i))) {
				int index_ref=Ref.indexOf(Sum.get(i));
				int match_temp=Math.min(num_of_ref.get(index_ref), num_of_sum.get(i));
				matchU4+=match_temp;
			}
		}
	//	System.out.println("TotleMatchU:"+matchU4);
		
		
		for (int i = 0; i < newSum.size(); i++) {
			if (newRef.contains(newSum.get(i))) {
				int index_ref=newRef.indexOf(newSum.get(i));
				int match_temp=Math.min(num_of_ref_new.get(index_ref), num_of_sum_new.get(i));
				matchSkip4+=match_temp;
			}
		}
	//	System.out.println("TotleMatchSkip:"+matchSkip4);
		//
		totleRef4 = Ref4.size() + newRef4.size();
		totleSum4 = Sum4.size() + newSum4.size();
		match4 = matchU4 + matchSkip4;
		double R_su = (double) match4 / totleRef4;
		double P_su = (double) match4 / totleSum4;
		double beta = 1;
		if (match4 == 0) {
			r4 = 0;
		} else {
			r4 = (1 + beta * beta) * R_su * P_su / (R_su + beta * beta * P_su);
		}
		return r4;
	}

	public static void main(String[] args) throws Exception {
//		Word[][] sum = platform.Platform.segment("血糖，血糖，血糖。为5.3。有糖尿病风险，血糖。");
//		Word[][] refer = platform.Platform.segment("血糖偏高，血糖。血糖。血糖。达到5.3，患糖尿病风险较高。");
		
		Word[][] sum = platform.Platform.segment("肾病引发糖尿病");
		Word[][] refer = platform.Platform.segment("糖尿病引发肾病");
	
		System.out.println(Word.toSimpleString(sum));
		System.out.println(Word.toSimpleString(refer));
		// Test for ROUGE
		double r1 = Rouge1(sum, refer);
		double r2 = Rouge2(sum, refer);
		double rsu4 = RougeSU4(sum, refer);
		System.out.println("R1:");
		System.out.print(r1);
		System.out.println("");
		System.out.println("R2:");
		System.out.print(r2);
		System.out.println("");
		System.out.println("R_SU4:");
		System.out.print(rsu4);
		System.out.println("");

	}

}
