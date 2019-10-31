package application.qav2.process_data;

import java.io.File;
import java.sql.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import platform.GlobalSettings;
import platform.util.log.Trace;
import application.qav2.dao.HealthqaDBMgr;

public class Indexer {
	
	static final Trace t = new Trace().setValid(false, true);
	
	static private File AnswerIndexDir = new File( GlobalSettings.contextDir("index/answer.index") );
	static private File QuestionIndexDir = new File( GlobalSettings.contextDir("index/question.index") );
	static private File PieceIndexDir = new File( GlobalSettings.contextDir("index/piece.index") );
	
	static private Analyzer analyzer = new IKAnalyzer();
    static FieldType Type_I_T_S = new FieldType();
    static {
        Type_I_T_S.setIndexed(true);
        Type_I_T_S.setTokenized(true);
        Type_I_T_S.setStored(true);
    }
	
	static Connection con;
	
	static {
		try {
			con = HealthqaDBMgr.getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static Statement newStatement() throws SQLException {
		return con.createStatement();
	}
	
	static void indexAnswer() throws Exception {
		t.remind("Begin to index answers");
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);  
		SimpleFSDirectory directory = new SimpleFSDirectory(AnswerIndexDir);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
        while(true) {
			Statement stat = newStatement();
			ResultSet rs = stat.executeQuery("select * from answer where `index`=0 limit 100000");
			rs.last();
			if(rs.getRow()==0)
				break;
			Trace t1 = new Trace(rs.getRow(), 1000);
			t.remind("fetched "+rs.getRow()+ " answers");
			rs.beforeFirst();
			while(rs.next()) {
				t1.remind("Indexing answers", true);
				Document doc = new Document();
//				t.debug("answer: "+rs.getString("id")+": "+rs.getString("content"));
	            doc.add(new StringField("id", rs.getString("id"), Field.Store.YES));
	            doc.add(new StringField("question", rs.getString("question"), Field.Store.YES));
	            doc.add(new Field("content", rs.getString("content"), Type_I_T_S));
	            indexWriter.addDocument(doc);
	            Statement st = newStatement();
	            st.executeUpdate("update answer set `index`=1 where `id`="+rs.getString("id"));
	            st.close();
			}
			rs.close();
			stat.close();
        }
//		directory.close();
		indexWriter.close();
	}
	
	static void indexPiece() throws Exception {
		t.remind("Begin to index pieces");
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);  
		SimpleFSDirectory directory = new SimpleFSDirectory(PieceIndexDir);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
        while(true) {
			Statement stat = newStatement();
			ResultSet rs = stat.executeQuery("select * from piece where `index`=0 limit 100000");
			rs.last();
			if(rs.getRow()==0)
				break;
			Trace t1 = new Trace(rs.getRow(), 1000);
			t.remind("fetched "+rs.getRow()+ " pieces");
			rs.beforeFirst();
			while(rs.next()) {
				t1.remind("Indexing pieces", true);
				Document doc = new Document();
				t.debug("piece: "+rs.getString("id")+": "+rs.getString("content"));
	            doc.add(new StringField("id", rs.getString("id"), Field.Store.YES));
	            doc.add(new StringField("question", rs.getString("question"), Field.Store.YES));
	            doc.add(new StringField("answer", rs.getString("answer"), Field.Store.YES));
	            doc.add(new Field("content", rs.getString("content"), Type_I_T_S));
	            indexWriter.addDocument(doc);
	            Statement st = newStatement();
	            st.executeUpdate("update piece set `index`=1 where `id`="+rs.getString("id"));
	            st.close();
			}
			rs.close();
			stat.close();
        }
		directory.close();
		indexWriter.close();
	}
	
	static void indexQuestion() throws Exception {
		t.remind("Begin to index questions");
		
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_47, analyzer).setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);  
		SimpleFSDirectory directory = new SimpleFSDirectory(QuestionIndexDir);
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		
		Statement stat = newStatement();
		ResultSet rs = stat.executeQuery("select * from question where `index`=0");
		rs.last();
		Trace t1 = new Trace(rs.getRow(), 100);
		t.remind("fetched "+rs.getRow()+ " questions");
		rs.beforeFirst();
		while(rs.next()) {
			t1.remind("Indexing questions", true);
//			t.debug("question: "+rs.getString("id")+": "+rs.getString("title")+rs.getString("content"));
			Document doc = new Document();
            doc.add(new StringField("id", rs.getString("id"), Field.Store.YES));
            doc.add(new Field("title", rs.getString("title"), Type_I_T_S));
            doc.add(new Field("content", rs.getString("content"), Type_I_T_S));
            indexWriter.addDocument(doc);
            Statement st = newStatement();
            st.executeUpdate("update question set `index`=1 where `id`="+rs.getString("id"));
            st.close();
		}
		rs.close();
		stat.close();
		indexWriter.close();
		directory.close();
	}
	
	public static void IndexAll() throws Exception {
		indexQuestion();
		indexAnswer();
		indexPiece();
	}
	
	public static void main(String[] args) throws Exception {
//		IndexAll();
//		indexQuestion();
		indexAnswer();
		con.close();
	}

}
