package application.qav2.ao;

import java.util.ArrayList;
import java.util.Set;

import org.apache.lucene.index.Term;
import application.qav2.dao.Entity;
import application.qav2.process_data.Transform;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.retrieval.text_retrieval.AnswerSearcher;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;

public class Answer {
	
	public String id;				// 数据库唯一ID -> AnswerDAO.id
	public String question_id;		// 标识其回复的问题ID -> AnswerDAO.question
	public String raw_content;		// 正文（分词之前） -> AnswerDAO.content
	public int piece_number;		// 片段数量 -> AnswerDAO.plength
	public String entity_content;	// 标注好实体的content -> AnswerDAO.anno_content
	
	public Word[][] seg_content;	// 正文（分词之后）
	public Question question;	// 回答的问题
	public Keyword[] kws;
	
	public Entity[] entities;
	
	public Answer(String id, String r_id, String content,/* String type,*/ Question question) throws Exception {
		this.id = id;
		this.question_id = r_id;
		this.raw_content = content;
		this.question = question;
	}
	
	public Answer(String id, String question_id, String raw_content,
			int piece_number, String entity_content) {
		super();
		this.id = id;
		this.question_id = question_id;
		this.raw_content = raw_content;
		this.piece_number = piece_number;
		this.entity_content = entity_content;
	}

	public void setQuestion(Question q) {
		this.question = q;
	}
	
	public void segContent() throws Exception {
		seg_content = Platform.segment(raw_content);
	}


	public String printKeywords() {
		String line = "";
		for( Keyword kw : kws ) {
			line = line + kw.word + "("+kw.weight+")";
		}
		return line;
	}
	
	public Entity[] findEntities() throws Exception {
		if(entity_content==null)
			entity_content = QuestionEntity.annotate(raw_content);
		Set<Entity> entities_set = Transform.extractEntities(entity_content);
		entities = new Entity[entities_set.size()];
		entities_set.toArray(entities);
		return entities;
	}
	
	public Keyword[] findKeywords() throws Exception {
		segContent();
		ArrayList<Keyword> al = new ArrayList<Keyword>();
		for( Word[] sent : seg_content) {
			for( Word w : sent ){
				boolean in_dict = LTPSettings.ext_dict_df.containsKey(w.cont);
				boolean is_nvabzd = (w.pos.matches("n.*")
//						|| w.pos.matches("v.*")
						|| w.pos.matches("a.*")
						|| w.pos.matches("b.*")
						|| w.pos.matches("z.*")
						|| w.pos.matches("d.*"));
				Term t = new Term("content", w.cont);
				try{
					int df = AnswerSearcher.indexReader.docFreq(t)+1;
					Keyword kw = null;
					if (in_dict && is_nvabzd) {
						kw = new Keyword(w.cont, Math.max(
								AnswerSearcher.dictDF2Boost(df),
								AnswerSearcher.pos2Boost(AnswerSearcher.doc_num
										/ (float) df)));
						al.add(kw);
					} else if (in_dict) {
						kw = new Keyword(w.cont, AnswerSearcher.dictDF2Boost(df));
						al.add(kw);
					} else if (is_nvabzd) {
						kw = new Keyword(w.cont,
								AnswerSearcher.pos2Boost(AnswerSearcher.doc_num
										/ (float) df));
						al.add(kw);
					}
					
				}catch(Exception e){e.printStackTrace();}
			}
		}
		float weight_sum = 0;
		for( Keyword k : al ) {
			weight_sum += k.weight;
		}
		for( Keyword k : al ) {
			k.weight /= weight_sum;
		}
		kws = new Keyword[al.size()];
		al.toArray(kws);
		return kws;
	}
	
	public String toString(boolean a) {
		return String.format("ID: %s\treply_ID: %s\n\t答案：%s\n\t题目：%s", id, question_id, Word.toFullString(seg_content), /*Word.toFullString(question.title)+" | "+*/Word.toFullString(question.seg_content));
	}
	
	@Override
	public String toString() {
		return String.format("ID: %s\treply_ID: %s\n\t答案：%s\n\t题目：%s", id, question_id, raw_content, /*question.raw_title+" | "+*/question.raw_content);
	}
}
