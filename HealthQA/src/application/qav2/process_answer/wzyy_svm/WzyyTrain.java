package application.qav2.process_answer.wzyy_svm;

import platform.mltools.svm.MultiClassSVM;

import java.util.*;
import java.io.*;
import application.qav2.process_answer.wzyy_svm.WzyyData.Keyword;
import application.qav2.process_answer.wzyy_svm.WzyyData.Sample;

public class WzyyTrain {

	void trainFront () throws IOException {
		
		WzyyData wd = new WzyyData();
		wd.read();
		wd.getSamples();
		wd.generatekeywords();
		
		ArrayList<Sample> samples = wd.samples;
		int dim = wd.front_keyword.size;
		
		MultiClassSVM svm = new MultiClassSVM(samples.size(), dim, true);
		int i = -1;
		for(Sample s : samples ) {
			double[] input_vector = new double[dim];
			String ts  =s.front;
			Keyword tf = wd.front_keyword;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(tf.ktoi.containsKey(key)) {
					input_vector[tf.ktoi.get(key)]++;
				}
			}
			svm.addDataRow(++i, input_vector, s.label);
		}
		
		svm.train("-t 0 -c 1");
		svm.saveModel(WzyyData.workDir("model/front.model"));
		
	}
	
	void trainBefore () throws IOException {
		
		WzyyData wd = new WzyyData();
		wd.read();
		wd.getSamples();
		wd.generatekeywords();
		
		ArrayList<Sample> samples = wd.samples;
		int dim = wd.before_keyword.size;
		
		MultiClassSVM svm = new MultiClassSVM(samples.size(), dim, true);
		int i = -1;
		for(Sample s : samples ) {
			double[] input_vector = new double[dim];
			String ts  =s.before;
			Keyword tf = wd.before_keyword;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(tf.ktoi.containsKey(key)) {
					input_vector[tf.ktoi.get(key)]++;
				}
			}
			System.out.println(s.label);
			svm.addDataRow(++i, input_vector, s.label);
		}
		
		svm.train("-t 0 -c 1");
		svm.saveModel(WzyyData.workDir("model/before.model"));
		
	}
	
	void trainAfter () throws IOException {
		
		WzyyData wd = new WzyyData();
		wd.read();
		wd.getSamples();
		wd.generatekeywords();
		
		ArrayList<Sample> samples = wd.samples;
		int dim = wd.after_keyword.size;
		
		MultiClassSVM svm = new MultiClassSVM(samples.size(), dim, true);
		int i = -1;
		for(Sample s : samples ) {
			double[] input_vector = new double[dim];
			if(s.after!=null) {
				String ts  =s.after;
				Keyword tf = wd.after_keyword;
				for(int k=0; k<ts.length(); k++) {
					String key = ts.charAt(k)+"";
					if(tf.ktoi.containsKey(key)) {
						input_vector[tf.ktoi.get(key)]++;
					}
				}
			}
			System.out.println(s.label);
			svm.addDataRow(++i, input_vector, s.label);
		}
		
		svm.train("-t 0 -c 1 -h 0");
		svm.saveModel(WzyyData.workDir("model/after.model"));
		
	}
	
	public static void main(String[] args) throws IOException {
		WzyyTrain t = new WzyyTrain();
		t.trainFront();
		t.trainBefore();
		t.trainAfter();
	}
}
