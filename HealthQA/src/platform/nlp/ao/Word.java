package platform.nlp.ao;

import java.util.List;

import platform.nlp.ltp.LTPSettings;

public class Word {

	public Integer id; // 内部索引ID
	public String cont; // 内容
	public String pos; // 词性
	public String ne; // 命名实体说明
	public Integer parent; // 父亲索引ID
	public String relate; // 与父亲的语法关系

	public Word(Integer id, String cont, String pos, String ne, Integer parent,
			String relate) {
		this.id = id;
		this.cont = cont;
		this.pos = pos;
		this.ne = ne;
		this.parent = parent;
		this.relate = relate;
	}

	public static String toPlainString(Word[][] para) {
		String text = "";
		for (int j = 0; j < para.length; j++) {
			Word[] sent = para[j];
			for (int k = 0; k < sent.length; k++) {
				Word word = sent[k];
				if (j < para.length - 1) {
					text = text + word.cont + " ";
				} else {
					text = text + word.cont;
				}
			}
		}
		return text;
	}

	public static String toPlainString(Word[] sent) {
		String text = "";
		for (int k = 0; k < sent.length; k++) {
			Word word = sent[k];
			text = text + word.cont;
		}
		return text;
	}

	public static String toSimpleString(Word[][] para) {
		String text = "";
		for (int j = 0; j < para.length; j++) {
			Word[] sent = para[j];
			text = text + LTPSettings.piece_seperator_l;
			for (int k = 0; k < sent.length; k++) {
				Word word = sent[k];
				if (k < sent.length - 1) {
					text = text + word.cont + "/" + word.pos + " ";
				} else {
					text = text + word.cont + "/" + word.pos;
				}

			}
			text = text + LTPSettings.piece_seperator_r;
		}
		return text;
	}

	public static String toFullString(Word[][] para) {
		String text = "";
		for (int j = 0; j < para.length; j++) {
			Word[] sent = para[j];
			text = text + LTPSettings.piece_seperator_l;
			for (int k = 0; k < sent.length; k++) {
				Word word = sent[k];
				if (k < sent.length - 1) {
					if (word.parent == null)
						text = text + word.cont + "@" + word.pos + "."
								+ word.ne + LTPSettings.word_seperator;
					else
						text = text + word.cont + "@" + word.pos + "."
								+ word.ne + "." + word.parent + "-"
								+ word.relate + LTPSettings.word_seperator;
				} else {
					if (word.parent == null)
						text = text + word.cont + "@" + word.pos + "."
								+ word.ne;
					else
						text = text + word.cont + "@" + word.pos + "."
								+ word.ne + "." + word.parent + "-"
								+ word.relate;
				}

			}
			text = text + LTPSettings.piece_seperator_r;
		}
		return text;
	}

	public static String toSimpleString(Word[] sent) {
		String text = "{ ";
		for (int k = 0; k < sent.length; k++) {
			Word word = sent[k];
			if (k < sent.length - 1) {
				text = text + word.cont + "/" + word.pos + " ";
			} else {
				text = text + word.cont + "/" + word.pos;
			}

		}
		text = text + LTPSettings.piece_seperator_r;
		return text;
	}

	public static String toFullString(Word[] sent) {
		String text = "";
		for (int k = 0; k < sent.length; k++) {
			Word word = sent[k];
			if (k < sent.length - 1) {
				if (word.parent == null)
					text = text + word.cont + "@" + word.pos + "." + word.ne
							+ LTPSettings.word_seperator;
				else
					text = text + word.cont + "@" + word.pos + "." + word.ne
							+ "." + word.parent + "-" + word.relate
							+ LTPSettings.word_seperator;
			} else {
				if (word.parent == null)
					text = text + word.cont + "@" + word.pos + "." + word.ne;
				else
					text = text + word.cont + "@" + word.pos + "." + word.ne
							+ "." + word.parent + "-" + word.relate;
			}
		}
		return text;
	}

	@Override
	public String toString() {
		return String.format("[%d: cont@%s, pos@%s, ne@%s, %s->%d]", id, cont,
				pos, ne, relate, parent);
	}

	/*
	 * 暂时不用
	 */
	public List<Arg> arg; // 语义角色标注参数

	public class Arg // 语义角色标注参数
	{
		public Integer id; // 索引ID
		public String type; // 角色名称
		public Integer beg; // 开始的词序号
		public Integer end; // 结束的词序号
	}

}
