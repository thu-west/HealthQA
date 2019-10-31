package application.qav2.ao;

import java.util.ArrayList;
import java.util.Set;

import org.apache.lucene.index.Term;

import platform.Platform;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import application.qav2.dao.Entity;
import application.qav2.process_data.Transform;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;


public class Piece {
	
	public String id;				// 片段id -> PieceDAO.id
	public String question_id;		// 问题id -> PieceDAO.question
	public String answer_id;		// 答案id -> PieceDAO.answer
	public int position;			// 片段在句子中的位置，从0开始计算 -> PieceDAO.pos
	public String category;			// 片段一级分类 -> PieceDAO.category
	public String category_2;		// 片段二级分类 -> PieceDAO.category_2
	public boolean context;			// 片段是否是上下文相关的 -> PieceDAO.context
	public String entity_content;	// 标注过实体的content -> PieceDAO.anno_content
	public String raw_content;		// 原始文本  -> PieceDAO.content

	public Question question;
	public Answer answer;
	
	public Entity[] entities;
	
	public Word[] seg_content;
	public Keyword[] kws;
	
	public Piece(String id, String question_id, String answer_id, int position,
			String category, String category_2, boolean context,
			String entity_content, String raw_content) {
		super();
		this.id = id;
		this.question_id = question_id;
		this.answer_id = answer_id;
		this.position = position;
		this.category = category;
		this.category_2 = category_2;
		this.context = context;
		this.entity_content = entity_content;
		this.raw_content = raw_content;
	}
	
	public Piece (Answer answer, Word[] content) {
		this.answer = answer;
		this.seg_content = content;
		this.raw_content = "";
		for(Word w : content ) {
			this.raw_content += w.cont;
		}
	}
	
	public void segContent() throws Exception {
		seg_content = Platform.segment(raw_content)[0];
	}
	
	public Keyword[] findKeywords() throws Exception {
		segContent();
		ArrayList<Keyword> al = new ArrayList<Keyword>();
		for (Word w : seg_content) {			
			boolean in_dict = LTPSettings.ext_dict_df.containsKey(w.cont);
			boolean is_nvabzd = (w.pos.matches("n.*")
					|| w.pos.matches("v.*")
					|| w.pos.matches("a.*")
					|| w.pos.matches("b.*")
					|| w.pos.matches("z.*")
					|| w.pos.matches("d.*"));
			Term t = new Term("content", w.cont);
			try{
				int df = QuestionSearcher.indexReader.docFreq(t)+1;
				Keyword kw = null;
				if (in_dict && is_nvabzd) {
					kw = new Keyword(w.cont, Math.max(
							QuestionSearcher.dictDF2Boost(df),
							QuestionSearcher.pos2Boost(QuestionSearcher.doc_num
									/ (float) df)));
					al.add(kw);
				} else if (in_dict) {
					kw = new Keyword(w.cont, QuestionSearcher.dictDF2Boost(df));
					al.add(kw);
				} else if (is_nvabzd) {
					kw = new Keyword(w.cont,
							QuestionSearcher.pos2Boost(QuestionSearcher.doc_num
									/ (float) df));
					al.add(kw);
				}
			}catch(Exception e){e.printStackTrace();}
		}
		kws = new Keyword[al.size()];
		al.toArray(kws);
		return kws;
	}
	
	public Entity[] findEntities() throws Exception {
		if(entity_content==null)
			entity_content = QuestionEntity.annotate(raw_content);
		Set<Entity> entities_set = Transform.extractEntities(entity_content);
		entities = new Entity[entities_set.size()];
		entities_set.toArray(entities);
		return entities;
	}
	
	public Keyword[] findKeywords_2() {
		ArrayList<Keyword> al = new ArrayList<Keyword>();
		for (Word w : seg_content) {
			boolean in_dict = LTPSettings.ext_dict_df.containsKey(w.cont);
			boolean is_nvabzd = (w.pos.matches("n.*") || w.pos.matches("v.*")
					|| w.pos.matches("a.*") || w.pos.matches("b.*")
					|| w.pos.matches("z.*") || w.pos.matches("d.*"));
			if( in_dict || is_nvabzd ) {
				Keyword kw = new Keyword(w.cont, 1);
				al.add(kw);
			}
		}
		kws = new Keyword[al.size()];
		al.toArray(kws);
		return kws;
	}
	
	public String printKeywords() {
		String line = "";
		for( Keyword kw : kws ) {
			line = line + kw.word + "("+kw.weight+")";
		}
		return line;
	}
}
