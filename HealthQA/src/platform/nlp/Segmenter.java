package platform.nlp;

import platform.nlp.ao.Word;

public interface Segmenter {
	public Word[][] segParagraph( String aParagraph ) throws Exception;
	
//	public Set<String> findNoun( String s );
}
