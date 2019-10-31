package application.qav2.ao;

public class Keyword implements Comparable<Keyword> {
	public String word;
	public float weight;
	
	public Keyword(String word, float weight) {
		this.word = word;
		this.weight = weight;
	}

	@Override
	public int compareTo(Keyword o) {
		return new Float(weight).compareTo(new Float(o.weight));
	}
}
