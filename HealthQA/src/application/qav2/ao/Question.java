package application.qav2.ao;

import java.util.*;
import java.util.Map.Entry;

import org.apache.lucene.index.Term;
import org.json.JSONObject;

import application.qav2.dao.Entity;
import application.qav2.process_data.Transform;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;
import platform.Platform;
import platform.nlp.ao.Word;
import platform.nlp.ltp.LTPSettings;
import platform.util.log.Trace;

public class Question {
	
	public String id;				// 数据库唯一ID -> QuestionDAO.id
	public String raw_content;		// 描述（分词之前） ->  QuestionDAO.content
	public String raw_title;		// 问题题目 ->  QuestionDAO.title
	public String entity_content;	// 带实体标注content ->  QuestionDAO.anno_content
	public JSONObject structure;	// 结构化表示结果  ->  QuestionDAO.structure
	public String interrogative;	// 疑问句  ->  QuestionDAO.interrogative
	public String interrogative_word;	// 疑问词  ->  QuestionDAO.interrogative_word
	public String type;				// 问题类型（开放/封闭）  ->  QuestionDAO.type
	public String answer_type;		// 问题类型（根据关键词）  ->  QuestionDAO.answer_type
	
	public Word[][] seg_content;	// 描述（分词之后）
	public float score;
	public Keyword[] kws;
	public Entity[] entities;
	
	final Trace t = new Trace().setValid(false, false);
	
	public Question( String id, String title, String content ){
		this.id = id;
		this.raw_title = title;
		this.raw_content = content;
	}
	
	public Question(String id, String raw_content, String raw_title,
			String entity_content, JSONObject structure, String interrogative,
			String interrogative_word, String type, String answer_type) {
		super();
		this.id = id;
		this.raw_content = raw_content;
		this.raw_title = raw_title;
		this.entity_content = entity_content;
		this.structure = structure;
		this.interrogative = interrogative;
		this.interrogative_word = interrogative_word;
		this.type = type;
		this.answer_type = answer_type;
	}
	
	public void setScore(float score) {
		this.score = score;
	}

	public void segContent() throws Exception {
		seg_content = Platform.segment(raw_content);
	}

	public Entity[] findEntities() throws Exception {
		if(entity_content==null)
			entity_content = QuestionEntity.annotate(raw_content);
		Set<Entity> entities_set = Transform.extractEntities(entity_content);
		entities = new Entity[entities_set.size()];
		entities_set.toArray(entities);
		return entities;
	}
	
	public JSONObject findStructure() throws Exception {
		if(entity_content==null)
			entity_content = QuestionEntity.annotate(raw_content);
		structure = application.qav2.process_question.structual.QuestionParser.structualRepresent(entity_content);
		return structure;
	}
	
	public Keyword[] findKeywordsV2() throws Exception {
		if(seg_content==null) {
			seg_content = Platform.segment(raw_content);
		}
		List<Keyword> al = new ArrayList<Keyword>();
		Map<String, Integer> map = new HashMap<String, Integer>();
		
		int max_tf = 1;
		for( Word[] sent : seg_content) {
			for( Word w : sent ){
				if(map.containsKey(w.cont)) {
					int tf = map.get(w.cont)+1;
					max_tf = tf>max_tf?tf:max_tf;
					map.put(w.cont, tf);
				} else {
					map.put(w.cont, 1);
				}
			}
		}
		for(Entry<String, Integer> e : map.entrySet()) {
			Term t = new Term("content", e.getKey());
			int df = QuestionSearcher.indexReader.docFreq(t)+1;
			double tf = (0.5+0.5*e.getValue()/max_tf);
			double idf = Math.log( (1.0 + QuestionSearcher.doc_num) / (1.0 + df));
			double tfidf = tf * idf;
			al.add(new Keyword(e.getKey(), (float)tfidf));
		}
		
		Keyword[] kws = new Keyword[map.entrySet().size()];
		al.toArray(kws);
		Collections.sort(al);
		
		float weight_sum = 0;
		t.debug("-------------------------------------");
		for( Keyword k : al ) {
			weight_sum += k.weight;
			t.debug("----"+k.weight);
		}
		t.debug("weight_sum"+weight_sum);
		for( Keyword k : al ) {
			k.weight /= weight_sum;
		}
		kws = new Keyword[al.size()];
		al.toArray(kws);
		return kws;
	}
	
	public Keyword[] findKeywords() throws Exception {
		segContent();
		ArrayList<Keyword> al = new ArrayList<Keyword>();
		for( Word[] sent : seg_content) {
			for( Word w : sent ){
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
						this.t.debug("///////");
						kw = new Keyword(w.cont, Math.max(
								QuestionSearcher.dictDF2Boost(df),
								QuestionSearcher.pos2Boost(QuestionSearcher.doc_num
										/ (float) df)));
						al.add(kw);
					} else if (in_dict) {
						this.t.debug("-----////");
						kw = new Keyword(w.cont, QuestionSearcher.dictDF2Boost(df));
						al.add(kw);
					} else if (is_nvabzd) {
						this.t.debug("////--"+df+"--///"+QuestionSearcher.doc_num+"////"+QuestionSearcher.pos2Boost(QuestionSearcher.doc_num
								/ (float) df));
						kw = new Keyword(w.cont,
								QuestionSearcher.pos2Boost(QuestionSearcher.doc_num
										/ (float) df));
						al.add(kw);
					}
				}catch(Exception e){e.printStackTrace();}
			}
		}
		float weight_sum = 0;
		t.debug("-------------------------------------");
		for( Keyword k : al ) {
			weight_sum += k.weight;
			t.debug("----"+k.weight);
		}
		t.debug("weight_sum"+weight_sum);
		for( Keyword k : al ) {
			k.weight /= weight_sum;
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
	
	public Question( String id, String title, String content, float score ) throws Exception{
		this.id = id;
//		this.raw_title = title;
		this.raw_content = content;
//		if ( LTPSettings.enable_offline_segmentor ) {
//			this.content = Platform.offlineSegmentQuestion(id);
//		} else {
////			this.title = Platform.segment(raw_title);
//			this.content = Platform.segment(raw_content);
//			t.debug(raw_content+" ===>  "+Word.toSimpleString(this.content));
//		}
		this.score = score;
	}
	   
	public String toString(boolean a) {
		return String.format("ID: %s\n\t题目：%s", id, /*Word.toSimpleString(title),*/ Word.toSimpleString(seg_content));
	}
	
	@Override
	public String toString() {
		return String.format("ID: %s\n\t题目：%s", id,/* raw_title,*/ raw_content);
	}
}
