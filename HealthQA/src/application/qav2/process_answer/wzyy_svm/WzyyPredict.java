package application.qav2.process_answer.wzyy_svm;

import java.io.IOException;

import platform.mltools.svm.MultiClassSVM;
import application.qav2.process_answer.wzyy_svm.WzyyData.Keyword;

public class WzyyPredict {
	
	static class Result {
		public String punc;
		public double prob;
		public double bili;
		public Result(String _punc, double probability, double _bili) {
			punc = _punc;
			prob = probability;
			bili = _bili;
		}
		public Result() {}
		@Override
		public String toString() {
			return "["+punc+"|"+prob+"|"+bili+"]";
		}
	}
	
	public static WzyyData wd = new WzyyData();
	public static MultiClassSVM svm_front = null;
	public static MultiClassSVM svm_before = null;
	public static MultiClassSVM svm_after = null;
	static {
		try {
			wd.loadKeywords();
			svm_front = new MultiClassSVM(WzyyData.workDir("model/front.model"));
			svm_before = new MultiClassSVM(WzyyData.workDir("model/before.model"));
			svm_after = new MultiClassSVM(WzyyData.workDir("model/after.model"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	String wzyy (String line) {
		StringBuffer punc_line = new StringBuffer();
		
		String[] pieces = line.split(" ");
		for(int i=0; i<pieces.length; i++) {
			// Get neighbors.
			String s = pieces[i];
			String front = null;
			String before = null;
			String after = null;
			Result front_result = new Result();
			Result before_result = new Result();
			Result after_result = new Result();
			if(s.length()<=WzyyData.gram) {
				front = before = s;
			} else {
				front = s.substring(0, WzyyData.gram);
				before = s.substring(s.length()-WzyyData.gram, s.length());
			}
			if(i<pieces.length-1) {
				if(pieces[i+1].length()<=WzyyData.gram) {
					after = pieces[i+1];
				} else {
					after = pieces[i+1].substring(0, WzyyData.gram);
				}
			}
			
			// input vector
			MultiClassSVM tsvm = svm_front;
			Keyword tf = wd.front_keyword;
			Result r = front_result;
			double[] input_vector = new double[tf.size];
			String ts = front;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(tf.ktoi.containsKey(key)) {
					input_vector[tf.ktoi.get(key)]++;
				}
			}
			double[] rs = tsvm.predict_probability(input_vector);
			if(rs[0]>rs[1]) {
				r.punc = (Math.abs(tsvm.labels[0]+1)<1e-4)?"，":"。";
				r.prob = rs[0];
				r.bili = rs[0]/rs[1];
			} else {
				r.punc = (Math.abs(tsvm.labels[0]+1)<1e-4)?"，":"。";
				r.prob = rs[1];
				r.bili = rs[1]/rs[0];
			}
			
			
			tsvm = svm_before;
			tf = wd.before_keyword;
			r = before_result;
			input_vector = new double[tf.size];
			ts = before;
			for(int k=0; k<ts.length(); k++) {
				String key = ts.charAt(k)+"";
				if(tf.ktoi.containsKey(key)) {
					input_vector[tf.ktoi.get(key)]++;
				}
			}
			rs = tsvm.predict_probability(input_vector);
			if(rs[0]>rs[1]) {
				r.punc = (Math.abs(tsvm.labels[0]+1)<1e-4)?"，":"。";
				r.prob = rs[0];
				r.bili = rs[0]/rs[1];
			} else {
				r.punc = (Math.abs(tsvm.labels[0]+1)<1e-4)?"，":"。";
				r.prob = rs[1];
				r.bili = rs[1]/rs[0];
			}
			
			if(after!=null) {
				tsvm = svm_after;
				tf = wd.after_keyword;
				r = after_result;
				input_vector = new double[tf.size];
				ts = after;
				for(int k=0; k<ts.length(); k++) {
					String key = ts.charAt(k)+"";
					if(tf.ktoi.containsKey(key)) {
						input_vector[tf.ktoi.get(key)]++;
					}
				}
				rs = tsvm.predict_probability(input_vector);
				if(rs[0]>rs[1]) {
					r.punc = (Math.abs(tsvm.labels[0]+1)<1e-4)?"，":"。";
					r.prob = rs[0];
					r.bili = rs[0]/rs[1];
				} else {
					r.punc = (Math.abs(tsvm.labels[1]+1)<1e-4)?"，":"。";
					r.prob = rs[1];
					r.bili = rs[1]/rs[0];
				}
			}
			
//			System.out.println(tsvm.labels[0]+"\t"+tsvm.labels[1]+"\t"+tsvm.labels[2]);
			// predict
			System.out.println(s);
			System.out.println(front_result.toString()+before_result.toString()+after_result.toString());
		}
		return punc_line.toString();
	}
	
	public static void main(String[] args) {
		String r  = new WzyyPredict().wzyy("你是空腹血糖还是餐后血糖 不管哪一种 随机血糖大于11.2 诊断成立 你的血脂也很高 应该综合治疗 糖尿病是基础疾病可以并发各种心脑血管 神经病变 建议 糖尿病的任何治疗都是以饮食和运动治疗为基础 严格控制饮食 根据你的体重 劳动量来决定你能量的摄入 如果肥胖首选双胍类降糖药 你的血脂很高增加降血脂药辛伐他丁 最好在专业医师的指导下用药");
		System.out.println(r);
		System.out.println("--------------------------");
		r = new WzyyPredict().wzyy("这个还是较高 空腹血糖正常值是3.9-6.1 超过7.0就可以诊断 餐后2小时血糖超过11.1就可以诊断 最好是饮食注意控制 尤其是主食 多参加运动 饮食避免辛辣刺激 注意检查血糖 如果通过饮食控制和运动还是不能控制好血糖的话 就要在医生指导下按血糖服用药物治疗 避免长时间在电脑电视前 注意歇息避免劳累 注意检查血压血脂 ");
		System.out.println(r);
	}

}
