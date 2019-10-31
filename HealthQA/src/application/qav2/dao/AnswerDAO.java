package application.qav2.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class AnswerDAO {
	
	static final Trace t = new Trace().setValid(false, true);
	
	BigInteger id;
	int plength;
	String anno_content;
	String content;
	BigInteger question;
	
	public String generateInsertSql() {
		return "insert into `answer` (`id`, `plength`, `anno_content`, `content`, `question`) values ("
				+ ValueTransfer.SqlValueFor(id) + ","
				+ ValueTransfer.SqlValueFor(plength) + ","
				+ ValueTransfer.SqlValueFor(anno_content) + ","
				+ ValueTransfer.SqlValueFor(content) + ","
				+ ValueTransfer.SqlValueFor(question)
				+ ")";
	}
	
	public void print() {
		t.remind("------answer: "+id+"-------");
		t.remind(plength+"");
		t.remind(anno_content);
		t.remind(content);
		t.remind(question.toString());
	}
	
	public AnswerDAO(BigInteger id, int plength, String anno_content,
			String content, BigInteger question) {
		super();
		this.id = id;
		this.plength = plength;
		this.anno_content = anno_content;
		this.content = content;
		this.question = question;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public int getPlength() {
		return plength;
	}

	public void setPlength(int plength) {
		this.plength = plength;
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

	public BigInteger getQuestion() {
		return question;
	}

	public void setQuestion(BigInteger question) {
		this.question = question;
	}
	

	
	

}
