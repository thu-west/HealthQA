package application.qav2.summarization.util;

import application.qav2.ao.ScoredPiece;

import java.util.*;

import platform.mltools.TFIDFProcessor;
import platform.nlp.ao.Word;

public class PieceTFIDF {
	
	public static HashMap<String, Double>[] tfidf ( List<ScoredPiece> pieces )  {
		
		// init pieces
		String[][] documents = new String[pieces.size()][];
		for( int i=0; i<documents.length; i++) {
			Word[] ws = pieces.get(i).piece.seg_content;
			int len = 0;
			for( Word w : ws ){
				if( w.pos.matches("n.*")
						|| w.pos.matches("v.*")
						|| w.pos.matches("a.*")
						|| w.pos.matches("b.*")
						|| w.pos.matches("z.*")
						|| w.pos.matches("d.*")){
					len++;
				}
			}
			documents[i] = new String[ len ];
			int j=-1;
			for( Word w : ws ){
				if( w.pos.matches("n.*")
						|| w.pos.matches("v.*")
						|| w.pos.matches("a.*")
						|| w.pos.matches("b.*")
						|| w.pos.matches("z.*")
						|| w.pos.matches("d.*")){
					documents[i][++j] = w.cont;
				}
			}
		}
		
		// tfidf
		TFIDFProcessor tip = null;
		try {
			tip = new TFIDFProcessor(documents, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return tip.documents_tfidf;
	
	}
	
}
