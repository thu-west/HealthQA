package platform.mltools.svm;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

class point {
	point(double x, double y, int value) {
		this.x = x;
		this.y = y;
		this.value = value;
	}

	double x, y;
	int value;
}

public class MultiClassSVM {
	Vector<point> point_list;
	public int dimension;
	int length;
	svm_model model;
	svm_problem prob;
	svm_parameter param;
	boolean out_probability;
	int label_dimension;
	public int[] labels;

	public MultiClassSVM(String model_file) throws IOException {
		model = svm.svm_load_model(model_file);
//		System.out.println(svm.svm_get_nr_sv(model));
		label_dimension = model.nr_class;
		labels = new int[label_dimension];
		svm.svm_get_labels(model, labels);
	}
	
	public MultiClassSVM(int data_length, int data_dimension) {
		point_list = new Vector<point>();
		param = null;
		model = null;
		prob = null;
		dimension = data_dimension;
		length = data_length;
		prob = new svm_problem();
		prob.l = length;
		prob.y = new double[prob.l];
		prob.x = new svm_node[prob.l][dimension];
		out_probability = false;
	}
	
	public MultiClassSVM(int data_length, int data_dimension, boolean _out_probability) {
		point_list = new Vector<point>();
		param = null;
		model = null;
		prob = null;
		dimension = data_dimension;
		length = data_length;
		prob = new svm_problem();
		prob.l = length;
		prob.y = new double[prob.l];
		prob.x = new svm_node[prob.l][dimension];
		out_probability = _out_probability;
	}
	
	private static double atof(String s) {
		return Double.valueOf(s).doubleValue();
	}

	private static int atoi(String s) {
		return Integer.parseInt(s);
	}

	private svm_parameter parseParameter(String args) {
		param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.C_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = 0;
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 40;
		param.C = 1;
		param.eps = 1e-3;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = out_probability?1:0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];
		// parse options
		StringTokenizer st = new StringTokenizer(args);
		String[] argv = new String[st.countTokens()];
		for (int i = 0; i < argv.length; i++)
			argv[i] = st.nextToken();
		for (int i = 0; i < argv.length; i++) {
			if (argv[i].charAt(0) != '-')
				break;
			if (++i >= argv.length) {
				System.err.print("unknown option\n");
				break;
			}
			switch (argv[i - 1].charAt(1)) {
			case 's':
				param.svm_type = atoi(argv[i]);
				break;
			case 't':
				param.kernel_type = atoi(argv[i]);
				break;
			case 'd':
				param.degree = atoi(argv[i]);
				break;
			case 'g':
				param.gamma = atof(argv[i]);
				break;
			case 'r':
				param.coef0 = atof(argv[i]);
				break;
			case 'n':
				param.nu = atof(argv[i]);
				break;
			case 'm':
				param.cache_size = atof(argv[i]);
				break;
			case 'c':
				param.C = atof(argv[i]);
				break;
			case 'e':
				param.eps = atof(argv[i]);
				break;
			case 'p':
				param.p = atof(argv[i]);
				break;
			case 'h':
				param.shrinking = atoi(argv[i]);
				break;
			case 'b':
				param.probability = atoi(argv[i]);
				break;
			case 'w':
				++param.nr_weight;
				{
					int[] old = param.weight_label;
					param.weight_label = new int[param.nr_weight];
					System.arraycopy(old, 0, param.weight_label, 0,
							param.nr_weight - 1);
				}

				{
					double[] old = param.weight;
					param.weight = new double[param.nr_weight];
					System.arraycopy(old, 0, param.weight, 0,
							param.nr_weight - 1);
				}

				param.weight_label[param.nr_weight - 1] = atoi(argv[i - 1]
						.substring(2));
				param.weight[param.nr_weight - 1] = atof(argv[i]);
				break;
			default:
				System.err.print("unknown option\n");
			}
		}
		// not consider svm param not supported
		if (param.kernel_type == svm_parameter.PRECOMPUTED) {
			System.out.println("not supported");
			System.exit(0);
		} else if (param.svm_type == svm_parameter.EPSILON_SVR
				|| param.svm_type == svm_parameter.NU_SVR) {
			System.out.println("not supported");
			System.exit(0);
		}
		// only for multiclass svm
		if (param.gamma == 0)
			param.gamma = 0.5;
		return param;
	}

	public boolean addDataRow(int index, double[] input_vector, double label) {
		if (index >= length) {
			System.out.println("the index is out of range!");
			return false;
		}

		if (input_vector.length != dimension) {
			System.out.println("the dimension of data doesn't match!");
			return false;
		}

		for (int i = 0; i < dimension; i++) {
			prob.x[index][i] = new svm_node();
			prob.x[index][i].index = i+1;
			prob.x[index][i].value = input_vector[i];
		}
		prob.y[index] = label;

		return true;
	}

	public void saveModel(String model_file){
		try{
			svm.svm_save_model(model_file, model);
		}catch(IOException e){e.printStackTrace();}
	}
	
	public svm_model train(String args) {
		// guard
		if (args == null) {
			System.out.println("args empty");
			return null;
		}

		// parse parameter
		svm_parameter param = parseParameter(args);
		// build problem
		
		// build model
		System.out.println(prob);
		model = svm.svm_train(prob, param);
		label_dimension = model.nr_class;
		labels = new int[label_dimension];
		svm.svm_get_labels(model, labels);
		return model;
	}

	public int get_label(int i) {
		int[] labels = new int[svm.svm_get_nr_class(model)];
		svm.svm_get_labels(model, labels);
		return labels[i];
	}

	public double predict(double[] input_vector) {
//		if (input_vector.length != dimension) {
//			System.out.println("the dimension of data doesn't match!");
//			return -10000;
//		}

		svm_node[] target = new svm_node[dimension];
		for (int i = 0; i < dimension; i++) {
			target[i] = new svm_node();
			target[i].value = input_vector[i];
			target[i].index = i+1;
		}
		return svm.svm_predict(model, target);
	}

	public double[] predict_probability(double[] input_vector) {
//		if (input_vector.length != dimension) {
//			System.out.println("the dimension of data doesn't match!");
//			return null;
//		}
		
		int dimension = input_vector.length;

		svm_node[] target = new svm_node[dimension];
		for (int i = 0; i < dimension; i++) {
			target[i] = new svm_node();
			target[i].value = input_vector[i];
			target[i].index = i+1;
		}
		double[] rd = new double[model.nr_class];
		svm.svm_predict_probability(model, target, rd);
		return rd;
	}
	
	public static void main(String[] args) {
		int len = 3;
		int dim = 2;
		MultiClassSVM mc_svm = new MultiClassSVM(len, dim, true);
		mc_svm.addDataRow(0, new double[] { 0, 2 }, 0);
		mc_svm.addDataRow(1, new double[] { 1, 1 }, 3);
		mc_svm.addDataRow(2, new double[] { 2, 0 }, 100);
		
//		mc_svm.addDataRow(2, new double[] { -1, -2 }, 1);
//		mc_svm.addDataRow(3, new double[] { -5, -7 }, 1);
		
		System.out.println("begin train");
		mc_svm.train("-t 0 -c 100");
		
		// predict
		double[] sample = new double[]{1,1};
		double predict = mc_svm.predict( sample );
		double[] predict_prob = mc_svm.predict_probability(sample);
		System.out.println(predict + "");
		for(double d: predict_prob) {
			System.out.print(d);
			System.out.print(" | ");
		}
		System.out.println();
		for(int i: mc_svm.labels) {
			System.out.println(i);
		}
	}
}
