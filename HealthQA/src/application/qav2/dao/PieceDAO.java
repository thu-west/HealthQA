package application.qav2.dao;

import java.math.BigInteger;

import platform.util.database.ValueTransfer;
import platform.util.log.Trace;

public class PieceDAO {
	
	static final Trace t = new Trace().setValid(false, true);
	
	BigInteger id;
	int pos;
	String category;
	String category_2;
	int context;
	String anno_content;
	String content;
	BigInteger question;
	BigInteger answer;
	
	public String generateInsertSql () {
		return "insert into piece (`id`,`pos`,`category`,`category_2`,`context`,`anno_content`,`content`,`question`,`answer`) values ("
				+ ValueTransfer.SqlValueFor(id) + ","
				+ ValueTransfer.SqlValueFor(pos) + ","
				+ ValueTransfer.SqlValueFor(category) + ","
				+ ValueTransfer.SqlValueFor(category_2) + ","
				+ ValueTransfer.SqlValueFor(context) + ","
				+ ValueTransfer.SqlValueFor(anno_content) + ","
				+ ValueTransfer.SqlValueFor(content) + ","
				+ ValueTransfer.SqlValueFor(question) + ","
				+ ValueTransfer.SqlValueFor(answer)
				+ ")";
	}
	
	public void print() {
		t.remind("------piece-------");
		t.remind(category+"|"+category_2+"|"+(context==1?"true":"false")+"|"+pos);
		t.remind(anno_content);
		t.remind(content);
		t.remind(question+"<-"+answer);
	}

	public PieceDAO(BigInteger id, int pos, String category, String category_2,
			int context, String anno_content, String content,
			BigInteger question, BigInteger answer) {
		super();
		this.id = id;
		this.pos = pos;
		this.category = category;
		this.category_2 = category_2;
		this.context = context;
		this.anno_content = anno_content;
		this.content = content;
		this.question = question;
		this.answer = answer;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory_2() {
		return category_2;
	}

	public void setCategory_2(String category_2) {
		this.category_2 = category_2;
	}

	public int getContext() {
		return context;
	}

	public void setContext(int context) {
		this.context = context;
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

	public BigInteger getAnswer() {
		return answer;
	}

	public void setAnswer(BigInteger answer) {
		this.answer = answer;
	}
	
	
	
}
