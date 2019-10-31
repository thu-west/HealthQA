package other.basic;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import application.qav2.retrieval.text_retrieval.util.*;
import application.qav2.ao.Question;
import application.qav2.retrieval.text_retrieval.QuestionSearcher;
import platform.Platform;
import platform.GlobalSettings;
import platform.db.database.ISHCDBConfig;
import platform.db.database.ISHCDataOperator;
import platform.util.log.Trace;

public class QuestionCluster {

	static SimpleDateFormat datef = new SimpleDateFormat("MMdd-HHmm");
	static Trace t = new Trace().setValid(false, false);

	// store: offline the calculated similarity
	static HashMap<String, Double> offline_similarity = new HashMap<String, Double>();
	// store: all question
	static HashMap<String, String> questions = new HashMap<String, String>();
	// store: all question IDs
	static ArrayList<String> qIDs;
	static int total_questions;

	// store: which cluster is the question(ID) located.
	HashMap<String, Set<Integer>> cid_set_by_qid = new HashMap<String, Set<Integer>>();
	HashMap<Integer, Set<String>> qid_set_by_cid = new HashMap<Integer, Set<String>>();
	// store: the core question(ID) of the cluster.
	HashMap<Integer, String> core_qid_by_cid = new HashMap<Integer, String>();

	// the threshold of the similarity to judge whether two question is in the
	// same cluster.
	double threshold = 0.9;
	int maxSearchResultCount = 1000;

	static {
		try {
//			t.debug("loading offline similarity");
//			load_offline_similarity();
			t.debug("loading all questions");
			load_all_questions();
			qIDs = new ArrayList<String>(questions.keySet());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void load_offline_similarity() throws NumberFormatException,
			IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"./io/similarity.txt"));
		String line = null;
		while ((line = br.readLine()) != null) {
			String[] sa = line.split("\t");
			offline_similarity.put(sa[0], Double.valueOf(sa[1]));
		}
		br.close();
	}

	static void save_offline_similarity() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./res/similarity.txt"));
		Iterator<String> it = offline_similarity.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			bw.write(key + "\t" + offline_similarity.get(key));
			bw.newLine();
		}
		bw.close();
	}

	static void load_all_questions() throws SQLException {
		ISHCDBConfig config = new ISHCDBConfig(GlobalSettings.database_url,
				GlobalSettings.database_username, GlobalSettings.database_password);
		ISHCDataOperator op = new ISHCDataOperator(config);
		ResultSet rs = op
				.query("select `ID`, `content` from `question` order by `ID` limit 5000");
		rs.last();
		total_questions = rs.getRow();
		rs.beforeFirst();

		Trace t2 = new Trace(total_questions, total_questions / 10);
		while (rs.next()) {
			t2.debug("loading questions", true);
			questions.put(rs.getString(1), rs.getString(2));
		}
	}

	static void load_all_seg_questions() throws SQLException {
		ISHCDBConfig config = new ISHCDBConfig(GlobalSettings.database_url,
				GlobalSettings.database_username, GlobalSettings.database_password);
		ISHCDataOperator op = new ISHCDataOperator(config);
		ResultSet rs = op
				.query("select `ID`, `content` from `question_after_seg`");
		rs.last();
		total_questions = rs.getRow();
		rs.beforeFirst();

		Trace t2 = new Trace(total_questions, total_questions / 5);
		while (rs.next()) {
			t2.debug("loading seg_questions", true);
			questions.put(rs.getString(1), rs.getString(2));
		}
	}

	static Double similarity (String id1, String id2) {
		Double sim = 0.0;
//		sim = offline_similarity.get(id1 + "-" + id2);
//		if (sim != null)
//			return sim;
//		sim = offline_similarity.get(id2 + "-" + id1);
//		if (sim != null)
//			return sim;

		long t1 = System.currentTimeMillis();
		sim = SimilarityUtil.similarityBetweenWordarray(
				Platform.offlineSegmentQuestion(id1),
				Platform.offlineSegmentQuestion(id2));
		long t2 = System.currentTimeMillis();
//		t.debug(" -> online sim cal cost: "+(t2-t1));
		offline_similarity.put(id1+"-"+id2, sim);
		return sim;

	}

	void saveCidSetByQid() throws IOException {
		String thres = String.format("%.1f", threshold);
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./io/cluster/CidSetByQid_"+thres+"_"+maxSearchResultCount+"-"+ datef.format(new Date()) +".txt"));
		Iterator<String> it = cid_set_by_qid.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String temp = key+"\t";
			for( int id : cid_set_by_qid.get(key)) {
				temp += id + " ";
			}
			bw.write( temp );
			bw.newLine();
		}
		bw.close();
	}
	
	void saveQidSetByCid() throws IOException {
		String thres = String.format("%.1f", threshold);
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./io/cluster/QidSetByQid_"+thres+"_"+maxSearchResultCount+"-"+ datef.format(new Date()) +".txt"));
		Iterator<Integer> it = qid_set_by_cid.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			String temp = key+"\t";
			for( String id : qid_set_by_cid.get(key)) {
				temp += id + " ";
			}
			bw.write( temp );
			bw.newLine();
		}
		bw.close();
	}
	
	void saveCore() throws IOException {
		String thres = String.format("%.1f", threshold);
		BufferedWriter bw = new BufferedWriter(new FileWriter(
				"./io/core_"+thres+"_"+maxSearchResultCount+"-"+ datef.format(new Date()) +".txt"));
		Iterator<Integer> it = core_qid_by_cid.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			bw.write( key + core_qid_by_cid.get(key) );
			bw.newLine();
		}
		bw.close();
	}
	
	void test() {
		int num = (int) (Math.random() * total_questions);
		t.debug("processing question "+num+" / "+total_questions);
		String core_qID = qIDs.get(num);
		
		// fetch the similar content
		String core_qContent = questions.get(core_qID);
		List<Question> ql = null;
		try{
			ql = QuestionSearcher.searchQuestion(core_qContent,
			maxSearchResultCount);
		}catch ( Exception e ){
			e.printStackTrace();
		}
		
		// process the similar questions
		Trace t2 = new Trace(ql.size(), ql.size()/5);
		Set<Double> sd = new TreeSet<Double>();
		int newq = 0;
		Set<Double> ss = new TreeSet<Double>();
		for( Question q : ql ) {
			double d = similarity(core_qID, q.id);
			t.debug(core_qContent);
			t.debug(q.raw_content);
			t.debug(""+d);
			ss.add(d);
			System.out.println();
		}
		
		for( double d : ss ){
			System.out.println(""+d);
		}
	}
	
	void clusterByLucene() {
		
		t.debug("total " + total_questions + " questions");
		
		int num = (int) (Math.random() * total_questions);
		
		int cluster_ID = -1;
		Trace tr = new Trace( total_questions, total_questions/10);
		for ( num=0; num < total_questions; num++ ) {
			tr.debug("clustering", true);
			
			String core_qID = qIDs.get(num);
			if( cid_set_by_qid.containsKey(core_qID) )
				continue;
			
			// put into core map
			core_qid_by_cid.put((++cluster_ID), core_qID);
			
			// put into clusters
			Set<Integer> set = new TreeSet<Integer>();
			set.add(cluster_ID);
			cid_set_by_qid.put(core_qID, set);
			
			// fetch the similar content
			String core_qContent = questions.get(core_qID);
			List<Question> ql = new ArrayList<Question>();
			long stm = System.currentTimeMillis();
			try{
				ql = QuestionSearcher.searchQuestion(core_qContent,
				maxSearchResultCount, 1);
			}catch ( Exception e ){
				e.printStackTrace();
				continue;
			}
			
			// process the similar questions
			Trace t2 = new Trace(ql.size(), ql.size()/5);
			Set<String> qids = new TreeSet<String>();
			for ( Question q : ql ) {
				Set<Integer> cids = cid_set_by_qid.get(q.id);
				if( cids == null ){
					cids = new TreeSet<Integer>();
					cids.add(cluster_ID);
					cid_set_by_qid.put(q.id, cids);
				} else {
					cids.add(cluster_ID);
				}
				qids.add(q.id);
			}
			qid_set_by_cid.put(cluster_ID, qids);
		}
		try{
			saveCore();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveCidSetByQid();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			saveQidSetByCid();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		QuestionCluster qc = new QuestionCluster();
		qc.clusterByLucene();
//		qc.test();
	}
}
