package application.qav2.dao;

import java.math.BigInteger;

import org.json.JSONObject;

import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class QuestionDAO {
	
	static final Trace t = new Trace().setValid(false, true);
	
	BigInteger id;
	String anno_content;
	String content;
	JSONObject structure;
	String title;
	String interrogative;
	String interrogative_word;
	String keyword;
	String type;
	String answer_type;
	
	
	public String generateInsertSql() {
		return "insert into question (`id`,`title`,`anno_content`,`content`,`structure`,`interrogative`,`interrogative_word`,`keyword`,`type`,`answer_type`) values ("
				+ ValueTransfer.SqlValueFor(id) + ","
				+ ValueTransfer.SqlValueFor(title) + ","
				+ ValueTransfer.SqlValueFor(anno_content) + ","
				+ ValueTransfer.SqlValueFor(content) + ","
				+ ValueTransfer.SqlValueFor(structure.toString()) + ","
				+ ValueTransfer.SqlValueFor(interrogative) + ","
				+ ValueTransfer.SqlValueFor(interrogative_word) + ","
				+ ValueTransfer.SqlValueFor(keyword) + ","
				+ ValueTransfer.SqlValueFor(type) + ","
				+ ValueTransfer.SqlValueFor(answer_type)
				+ ")";
	}
	
	public void print() {
		t.remind("------question: "+id+"-------");
		t.remind(type+"|"+answer_type+"|"+interrogative);
		t.remind(title);
		t.remind(anno_content);
		t.remind(content);
	}

	public QuestionDAO(BigInteger id, String anno_content, String content,
			JSONObject structure, String title, String interrogative,
			String interrogative_word, String keyword, String type,
			String answer_type) {
		super();
		this.id = id;
		this.anno_content = anno_content;
		this.content = content;
		this.structure = structure;
		this.title = title;
		this.interrogative = interrogative;
		this.interrogative_word = interrogative_word;
		this.keyword = keyword;
		this.type = type;
		this.answer_type = answer_type;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public String getAnno_content() {
		return anno_content;
	}

	public void setAnno_content(String anno_content) {
		this.anno_content = anno_content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public JSONObject getStructure() {
		return structure;
	}

	public void setStructure(JSONObject structure) {
		this.structure = structure;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInterrogative() {
		return interrogative;
	}

	public void setInterrogative(String interrogative) {
		this.interrogative = interrogative;
	}

	public String getInterrogative_word() {
		return interrogative_word;
	}

	public void setInterrogative_word(String interrogative_word) {
		this.interrogative_word = interrogative_word;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAnswer_type() {
		return answer_type;
	}

	public void setAnswer_type(String answer_type) {
		this.answer_type = answer_type;
	}

}
