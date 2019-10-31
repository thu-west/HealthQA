package application.qav2.process_answer.classifier;

public class Parameter {

	/*
	 * use in DataHandler.getKeywordWithHighTfidf() as parameter.
	 */
	static final double MIN_TFIDF_THRESHOLD  = 0.4;
	/*
	 * use in TrainClassifier.getCommonKeyword()
	 */
	static final double ENTITY_FACTOR  = 1.5;
	/*
	 * use in Keyword.getFactor()
	 */
	static final double TFIDF_FACTOR = 8;
}
