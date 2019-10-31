package application.qav2.process_data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import application.qav2.ao.Answer;
import application.qav2.ao.Question;
import application.qav2.dao.AnswerDAO;
import application.qav2.dao.Entity;
import application.qav2.dao.EntityMap;
import application.qav2.dao.HealthqaDBMgr;
import application.qav2.dao.PieceDAO;
import application.qav2.dao.QuestionDAO;
import application.qav2.process_answer.classifier.Classifier;
import application.qav2.process_answer.classifier.LabelSet;
import application.qav2.process_answer.entity.AnswerEntity;
import application.qav2.process_answer.wzyy_crf.Wzyy;
import application.qav2.process_question.entity.QuestionEntity;
import application.qav2.process_question.intention.QuestionBlock;
import application.qav2.process_question.intention.QuestionIntention;
import application.qav2.process_question.structual.QuestionParser;
import platform.util.log.Trace;

public class Transform {

	static final Trace t = new Trace().setValid(false, true);

	static class QAPair {
		Question q;
		List<Answer> alist;

		public QAPair() {
			alist = new ArrayList<Answer>();
		}
	}

	static Classifier c_top;
	static Classifier c_d;
	static Classifier c_i;
	static Classifier c_m;
	static Classifier c_t;
	static Classifier c_y;

	static Connection c;

	static BigInteger piece_max_id;

	public static EntityMap entity_map;

	static final Pattern EntityPattern = Pattern.compile("([^ ]+)\\\\([a-z]+)");
	static final String EffectEntity = "^[dmctoiyq]$";

	static {
		try {
			c_top = new Classifier("top", LabelSet.TOP_LABEL_SET);
			c_d = new Classifier("d", LabelSet.D_LABEL_SET);
			c_i = new Classifier("i", LabelSet.I_LABEL_SET);
			c_m = new Classifier("m", LabelSet.M_LABEL_SET);
			c_t = new Classifier("t", LabelSet.T_LABEL_SET);
			c_y = new Classifier("y", LabelSet.Y_LABEL_SET);
			c = HealthqaDBMgr.getConnection();
			Statement sp = c.createStatement();
			ResultSet rs = sp.executeQuery("select max(`id`) from `piece`");
			if (rs.next()) {
				BigDecimal temp = rs.getBigDecimal(1);
				piece_max_id = temp == null ? new BigInteger("0") : temp.toBigInteger();
			}
			rs.close();
			sp.close();
			entity_map = new EntityMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static BigInteger getAvailablePieceId() throws SQLException {
		piece_max_id = piece_max_id.add(new BigInteger("1"));
		return piece_max_id;
	}

	public static Set<Entity> extractEntities(String anno_content) {
		Set<Entity> set = new HashSet<Entity>();
		Matcher m = EntityPattern.matcher(anno_content);
		while (m.find()) {
			String word = m.group(1);
			String tag = m.group(2);
			if (Pattern.matches(EffectEntity, tag))
				set.add(new Entity(word, tag));
		}

		return set;
	}

	static void transform(QAPair qa) throws Exception {
		Question q = qa.q;
		List<Answer> alist = qa.alist;
		String anno_q = QuestionEntity.annotate(qa.q.raw_content);
		Set<Entity> question_entities = extractEntities(anno_q);
		JSONObject strutrual = QuestionParser.structualRepresent(anno_q);
		List<QuestionBlock> qb_list = QuestionIntention.extractBody(qa.q.raw_content);

		QuestionDAO qdao = null;
		if (qb_list.size() > 0) {
			QuestionBlock qb = qb_list.get(0);
			qdao = new QuestionDAO(new BigInteger(q.id), anno_q, q.raw_content, strutrual, q.raw_title, qb.question,
					qb.interrogative, null, qb.type, qb.target_type);
		} else {
			qdao = new QuestionDAO(new BigInteger(q.id), anno_q, q.raw_content, strutrual, q.raw_title, null, null,
					null, null, null);
		}

		// t.remind(anno_q);
		// t.remind(strutrual.toString(1));
		// for(QuestionBlock qb1 : qb_list) {
		// t.remind(qb1.toString());
		// }

		for (Answer a : alist) {
			String anno_content = null;
			String wzyy_content = null;
			try {
				// entity annotating
				anno_content = AnswerEntity.annotate(a.raw_content);
				// split paragraph to pieces
				wzyy_content = Wzyy.annotate(anno_content);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			String[] answer_pieces = wzyy_content.replace("。", "。###").split("###");
			// put answer into answer db
			AnswerDAO adao = new AnswerDAO(new BigInteger(a.id), answer_pieces.length, wzyy_content, a.raw_content,
					new BigInteger(q.id));
			Statement st2 = c.createStatement();
			st2.executeUpdate(adao.generateInsertSql());
			st2.close();

			Set<Entity> answer_entities = extractEntities(anno_content);
			for (Entity ae : answer_entities) {
				// put entities into answer entity map
				entity_map.putAnswer(ae, a.id);
				// put question entities into qa entity map
				entity_map.putQA(ae, a.id);
			}
			// put question entities into qa entity map
			for (Entity qe : question_entities) {
				entity_map.putQA(qe, a.id);
			}
			// split answer into pieces and classification
			String neighbor_id = null;
			for (int pos = 0; pos < answer_pieces.length; pos++) {
				String piece = answer_pieces[pos];
				// classifying
				String label = c_top.predict(piece);
				String label_2 = "";
				if (label.equals("d")) {
					label_2 = c_d.predict(piece);
				} else if (label.equals("i")) {
					label_2 = c_i.predict(piece);
				} else if (label.equals("m")) {
					label_2 = c_m.predict(piece);
				} else if (label.equals("t")) {
					label_2 = c_t.predict(piece);
				} else if (label.equals("y")) {
					label_2 = c_y.predict(piece);
				}
				int context = label_2.startsWith("c") && label_2.length() > 1 ? 1 : 0;
				String raw_content = piece.replaceAll("\\\\[a-z]+", " ").replace(" ", "");
				BigInteger pid = getAvailablePieceId();
				String pid_s = pid.toString();
				PieceDAO pdao = new PieceDAO(pid, pos, label, label_2, context, piece, raw_content,
						new BigInteger(q.id), new BigInteger(a.id));
				Statement st3 = c.createStatement();
				st3.executeUpdate(pdao.generateInsertSql());
				st3.close();
				Set<Entity> piece_entities = extractEntities(piece);
				for (Entity pe : piece_entities) {
					// put entities to piece entity map
					entity_map.putPiece(pe, pid_s);
					// put entities to neighbor entity map as the neighbor start
					if ((pos + 1) < answer_pieces.length)
						entity_map.putNeighbor(pe, pid_s); // the final piece cannot start a neighbor because it has no
															// successor
					// put entities to neighbor entity map as the neighbor end
					if (neighbor_id != null)
						entity_map.putNeighbor(pe, neighbor_id); // the first piece cannot end a neighbor because it has
																	// no prior
				}
				neighbor_id = pid_s;
			}

		}
		Statement st1 = c.createStatement();
		st1.executeUpdate(qdao.generateInsertSql());
		st1.close();
	}

	public static void main(String[] args) throws Exception {
//		String s = "有糖尿病的话去旅行一般是没有什么危险的。但是有一些注意事项。首先是要注意检测血糖的仪器做自我监测。其次是一定要在包里备好药物或者是胰岛素。并且要另外备一些糖块以防止低血糖。去旅游的时候还是得注意饮食的规律并且运动量如果大了要适当补充糖分。根据你的情况糖尿病人如果控制好血糖，按时用药控制饮食和正常人是一样的不限制出游。根据你的描述建议查血糖在正常范围内，可以出游但要注意不要太劳累注意饮食和按时吃药。";
//		String q = "得了糖尿病，应该吃什么东西？";
//		QAPair qa = new QAPair();
//		qa.alist.add(new Answer(null, null, s, null));
//		qa.q = new Question(null, null, q);
//		transform(qa);
		String string = "糖尿病能打胰岛素吗？";
		String anno_q = QuestionEntity.annotate(string);
		System.out.println(anno_q);
		Set<Entity> question_entities = extractEntities(anno_q);
		for (Entity entity : question_entities) {
			System.out.println(entity.name);
		}
	}

}
