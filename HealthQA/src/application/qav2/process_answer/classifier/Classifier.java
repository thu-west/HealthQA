package application.qav2.process_answer.classifier;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import platform.GlobalSettings;
import platform.Platform;
import platform.mltools.svm.MultiClassSVM;
import platform.nlp.ao.Word;
import platform.util.MyArray;
import platform.util.log.Trace;
import application.qav2.process_answer.classifier.DataHandler.Item;

public class Classifier {
	
	static final double MIN_TFIDF_THRESHOLD  = 0.4;
	static final double ENTITY_FACTOR  = 0.4;
	static final Trace t = new Trace();
	
	public static String workDir(String filename) {
		return work_dir + filename;
	}
	
	static String work_dir = GlobalSettings.contextDir("res/data/minimal-piece-classification/train/");
	
	String[][] label_set;
	String classifier_name;
	MultiClassSVM svm;
	Keyword keyword;
	Keyword common_keyword;
	int common_dim;
	int dim;
	Map<String, Integer> label_ktoi;
	Map<Integer, String> label_itok;
	
	static class Result {
		static final DecimalFormat df = new DecimalFormat("0.0");
		static class Value implements Comparable<Value> {
			public String label;
			public Double prob;
			public Value(String _label, double _prob) {
				label = _label;
				prob = _prob;
			}
			@Override
			public int compareTo(Value arg0) {
				return -1*this.prob.compareTo(arg0.prob);
			}
		}
		TreeSet<Value> judges;
		public Result() {
			judges = new TreeSet<Value>();
		}
		public void add(String label, double prob) {
			judges.add(new Value(label, prob));
		}
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();
			for(Value r: judges) {
				sb.append(r.label + "|" + df.format(r.prob) +" ");
			}
			return sb.toString().replaceAll(" $", "");
		}
	}
	
	public Classifier(String _classifier_name, String[][] _label_set) throws IOException {
		classifier_name = _classifier_name;
		label_set = _label_set;
		svm = new MultiClassSVM(workDir("../model/"+classifier_name+".model"));
		keyword = Keyword.read(workDir("../keyword/"+classifier_name+".keyword"));
		common_keyword = Keyword.read(workDir("../keyword/common.keyword"));
		common_dim = common_keyword.all.size();
		dim = keyword.all.size() + common_dim;
		label_ktoi = TrainClassifier.calLabelKTOI(label_set);
		label_itok = TrainClassifier.calLabelITOK(label_set);
	}
	
	public void test_with_probability (String input_file, String output_file) throws Exception {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output_file), "utf8"));
		// read test dataset
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(input_file), "utf8"));
		String content = null; 
		while( (content=br.readLine()) != null ) {
			String r = predict_with_probability(content);
			bw.write(r + "\t" + content );
			bw.newLine();
		}
		br.close();
		bw.close();
	}
	
	public void test (String input_file, String output_file) throws Exception {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(output_file), "utf8"));
		// read test dataset
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(input_file), "utf8"));
		String content = null; 
		while( (content=br.readLine()) != null ) {
			String real_label = "";
			if(content.contains("\t")) {
				real_label = content.split("\t")[0];
				content = content.split("\t")[1];
			}
			String r = predict(content);
			if(real_label.isEmpty()) 
				bw.write(r + "\t" + content);
			else
				bw.write(real_label+"\t"+r + "\t" + content );
			bw.newLine();
		}
		br.close();
		bw.close();
	}
	
	public String predict (String content) throws Exception {
		String temp = predict_with_probability(content);
		return temp.split("\\|")[0];
	}
	
	public String predict_with_probability (String content) throws Exception {
		// analysis content
		Item item = new Item();
		Pattern p = Pattern.compile("\\\\([a-z]+)");
		Matcher m = p.matcher(content);
		while(m.find()) {
			String temp_tag = m.group(1);
			item.addTag(temp_tag);
		}
		item.plain_text = content.replaceAll("\\\\[a-z]+", "").replace(" ", "");
		item.seg_word = Platform.segment(content);
		// generate vector
		double[] input_vector = new double[dim];
		for( Word[] sent: item.seg_word ) {
			for(Word w: sent) {
				if(keyword.all.containsKey(w.cont)) {
					int index = keyword.ktoi.get(w.cont);
					double factor = keyword.all.get(w.cont).getFactor();
					input_vector[index+common_dim] += factor*1;
				}
			}
		}
		for( int j=0; j<item.plain_text.length(); j++) {
			String ch = item.plain_text.charAt(j)+"";
			if(keyword.all.containsKey(ch)) {
				int index = keyword.ktoi.get(ch);
				double factor = keyword.all.get(ch).getFactor();
				input_vector[index+common_dim] += factor*1;
			}
		}
		for(Entry<String, Integer> ie : item.tag_appearance.entrySet()) {
			String tag = ie.getKey();
			int times = ie.getValue();
			if(common_keyword.all.containsKey(tag))
				input_vector[common_keyword.ktoi.get(tag)] += times*common_keyword.all.get(tag).getFactor();
		}
		
		Result r = new Result();
		double[] probabilities = svm.predict_probability(input_vector);
		double max_prob = probabilities[MyArray.findMaxIndex(probabilities)];
		for(int i=0; i<probabilities.length; i++) {
			if(probabilities[i]>max_prob/2) {
				r.add(label_itok.get(svm.get_label(i)), probabilities[i]);
			}
		}
		return r.toString();
	}
	
	public static void main(String[] args) throws Exception {
		Classifier t = new Classifier(LabelSet.TOP, LabelSet.TOP_LABEL_SET);
		String r = t.predict("如果患者的身体素质比较好，是可以考虑 放化疗\\t 的治疗的。");
		System.out.println(r);
		//t.test("res/data/minimal-piece-classification/test/classify.1.test", "res/data/minimal-piece-classification/test/classify.1.test.o");
	}
	
}
