package platform.mltools.ann;

import java.io.FileInputStream;
import java.io.IOException;
import org.neuroph.core.*;
import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;

public class Classifier {

	void test2 () throws IOException {
	
		// create training set
		DataSet trainingSet = TrainData.read("res/data/fann.txt");
		System.out.println(trainingSet.toCSV());
		// create new perceptron network
		NeuralNetwork neuralNetwork = new MultiLayerPerceptron(trainingSet.getInputSize(),(trainingSet.getInputSize()+trainingSet.getOutputSize())/2, trainingSet.getOutputSize());	
//		NeuralNetwork neuralNetwork = new Perceptron(trainingSet.getInputSize(), trainingSet.getOutputSize());
//		 learn the training set
		neuralNetwork.learn(trainingSet);
		// save the trained network into file
		neuralNetwork.save("or_perceptron.nnet"); 
		
		// load the saved network
		NeuralNetwork neuralNetwork1 = NeuralNetwork.load(new FileInputStream("or_perceptron.nnet"));
		// set network input
		neuralNetwork1.setInput(1, 0, 0, 1000);
		// calculate network
		neuralNetwork1.calculate();
		// get network output
		double[] networkOutput = neuralNetwork1.getOutput();
		for(double d:networkOutput) {
			System.out.println(d);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		new Classifier().test2();
	}

}
