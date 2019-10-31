package application.qav2.process_answer.classifier;

import platform.mltools.svm.MultiClassSVM;
import platform.nlp.ao.Word;
import platform.util.log.Trace;

import java.util.*;
import java.util.Map.Entry;

import application.qav2.process_answer.classifier.DataHandler.Item;
import application.qav2.process_answer.classifier.Keyword.Value;

public class TrainClassifier {
	
	static final String work_dir = "res/data/minimal-piece-classification/train/";
	static final Trace t = new Trace();
	
	static Map<String, HashSet<Item>> train_data_collection = null;
	
	public static String workDir(String filename) {
		return work_dir + filename;
	}
	
	String[][] label_set;
	String classifier_name;
	
	public TrainClassifier(String _classifier_name, String[][] _label_set) {
		classifier_name = _classifier_name;
		label_set = _label_set;
	}
	
	static Map<String, Integer> calLabelKTOI( String[][] label_set ) {
		Map<String, Integer> k2i = new HashMap<String, Integer>();
		int index = 0;
		for(String[] ss: label_set) {
			for(String s: ss) {
				k2i.put(s, index);
			}
			index++;
		}
		return k2i;
	}
	
	static Map<Integer, String> calLabelITOK( String[][] label_set ) {
		Map<Integer, String> i2k = new HashMap<Integer, String>();
		int index = 0;
		for(String[] ss: label_set) {
			for(String s: ss) {
				String sss = i2k.get(index);
				if(sss==null)
					i2k.put(index, s);   // reserve the first label as the final label
			}
			index++;
		}
		return i2k;
	}

	static Keyword getCommonKeyword() {
		Keyword kw = new Keyword();
		kw.addChar("y", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("t", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("q", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("o", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("m", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("c", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("k", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("i", new Value("common", Parameter.ENTITY_FACTOR));
		kw.addChar("d", new Value("common", Parameter.ENTITY_FACTOR));
		return kw;
	}
	
	void train() throws Exception {
		t.remind("Training classifier: "+classifier_name);
		// set classifier context
		Map<String, Integer> label_ktoi = calLabelKTOI(label_set);
		train_data_collection = DataHandler.readRichText(workDir("classify.train"), label_ktoi);
		Keyword keyword = DataHandler.getKeywordWithHighTfidf(train_data_collection, label_set, Parameter.MIN_TFIDF_THRESHOLD);
		keyword.write(workDir("../keyword/"+classifier_name+".keyword"));
		Keyword common_keyword = getCommonKeyword();
		common_keyword.write(workDir("../keyword/common.keyword"));
		int dim = common_keyword.all.size() + keyword.all.size();
		int common_dim = common_keyword.all.size();
		// deal with data collection
		int data_length = 0;
		for(Entry<String, HashSet<Item>> e:train_data_collection.entrySet()) {
			data_length += e.getValue().size();
		}
		MultiClassSVM svm = new MultiClassSVM(data_length, dim, true);
		int i = 0;
		for(Entry<String, HashSet<Item>> e : train_data_collection.entrySet() ) {
			if(!label_ktoi.containsKey(e.getKey())) continue;
			int label = label_ktoi.get(e.getKey());
			for( Item item : e.getValue() ) {
				double[] input_vector = new double[dim];
				// deal with feature in word level
				for( Word[] sent: item.seg_word ) {
					for(Word w: sent) {
						if(keyword.all.containsKey(w.cont)) {
							int index = keyword.ktoi.get(w.cont);
							double factor = keyword.all.get(w.cont).getFactor();
							input_vector[index+common_dim] += factor*1;
						}
					}
				}
				// deal with feature in charactor level
				for( int j=0; j<item.plain_text.length(); j++) {
					String ch = item.plain_text.charAt(j)+"";
					if(keyword.all.containsKey(ch)) {
						int index = keyword.ktoi.get(ch);
						double factor = keyword.all.get(ch).getFactor();
						input_vector[index+common_dim] += factor*1;
					}
				}
				// deal with feature in tag level
				for(Entry<String, Integer> ie : item.tag_appearance.entrySet()) {
					String tag = ie.getKey();
					int times = ie.getValue();
					if(common_keyword.all.containsKey(tag))
						input_vector[common_keyword.ktoi.get(tag)] += times*common_keyword.all.get(tag).getFactor();
				}
				// add features to svm problem
				svm.addDataRow(i++, input_vector, label);
			}
		}
		svm.train("-t 0 -c 1");
		svm.saveModel(workDir("../model/"+classifier_name+".model"));
		
		System.out.println("===============================");
		System.out.println("input_dim: "+dim);
		System.out.println("data length: "+data_length);
	}

	public static void main(String[] args) throws Exception {
		new TrainClassifier(LabelSet.TOP, LabelSet.TOP_LABEL_SET).train();
		new TrainClassifier(LabelSet.D, LabelSet.D_LABEL_SET).train();
		new TrainClassifier(LabelSet.I, LabelSet.I_LABEL_SET).train();
		new TrainClassifier(LabelSet.M, LabelSet.M_LABEL_SET).train();
		new TrainClassifier(LabelSet.Y, LabelSet.Y_LABEL_SET).train();
		new TrainClassifier(LabelSet.T, LabelSet.T_LABEL_SET).train();
	}
}
