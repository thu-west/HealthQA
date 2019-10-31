package application.qav2.dao;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import platform.GlobalSettings;
import platform.util.log.Trace;

public class EntityMap_Old_InFile {
	
	public static String root_folder = GlobalSettings.contextDir("index/entity");

	static final Trace t = new Trace().setValid(true, true);
	
	Map<String, EntityAppearance> map;

	public EntityMap_Old_InFile() {
		map = new HashMap<String, EntityAppearance>();
	}
	
	public EntityMap_Old_InFile(String[] entities) throws IOException {
		map = new HashMap<String, EntityAppearance>();
		for(String e: entities) {
			if(map.containsKey(e)) continue;
			EntityAppearance ea = new EntityAppearance();
			String entity_folder = root_folder+"/"+e.substring(0, 1)+"/"+e+"/";
			File file = new File(entity_folder+"neighbor");
			t.debug("test: "+file.getAbsolutePath());
			if(file.exists()) {
				t.debug("reading: "+file.getAbsolutePath());
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while( (line=br.readLine()) != null ) {
					ea.neighbor.add(line);
				}
				br.close();
			}
			file = new File(entity_folder+"answer");
			t.debug("test: "+file.getAbsolutePath());
			if(file.exists()) {
				t.debug("reading: "+file.getAbsolutePath());
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while( (line=br.readLine()) != null ) {
					ea.answer.add(line);
				}
				br.close();
			}
			file = new File(entity_folder+"piece");
			t.debug("test: "+file.getAbsolutePath());
			if(file.exists()) {
				t.debug("reading: "+file.getAbsolutePath());
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while( (line=br.readLine()) != null ) {
					ea.piece.add(line);
				}
				br.close();
			}
			file = new File(entity_folder+"qa");
			t.debug("test: "+file.getAbsolutePath());
			if(file.exists()) {
				t.debug("reading: "+file.getAbsolutePath());
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while( (line=br.readLine()) != null ) {
					ea.qa.add(line);
				}
				br.close();
			}
			file = new File(entity_folder+"context");
			t.debug("test: "+file.getAbsolutePath());
			if(file.exists()) {
				t.debug("reading: "+file.getAbsolutePath());
				BufferedReader br = new BufferedReader( new InputStreamReader(new FileInputStream(file)));
				String line = null;
				while( (line=br.readLine()) != null ) {
					ea.context.add(line);
				}
				br.close();
			}
		}
	}

	public void putNeighbor(Entity entity, String id) {
		EntityAppearance ea = map.get(entity.name);
		if (ea == null) {
			ea = new EntityAppearance();
			ea.neighbor.add(id);
			ea.tag.add(entity.tag);
			map.put(entity.name, ea);
		} else {
			ea.neighbor.add(id);
			ea.tag.add(entity.tag);
		}
	}

	public void putPiece(Entity entity, String id) {
		EntityAppearance ea = map.get(entity.name);
		if (ea == null) {
			ea = new EntityAppearance();
			ea.piece.add(id);
			ea.tag.add(entity.tag);
			map.put(entity.name, ea);
		} else {
			ea.piece.add(id);
			ea.tag.add(entity.tag);
		}
	}

	public void putContext(Entity entity, String id) {
		EntityAppearance ea = map.get(entity.name);
		if (ea == null) {
			ea = new EntityAppearance();
			ea.context.add(id);
			ea.tag.add(entity.tag);
			map.put(entity.name, ea);
		} else {
			ea.context.add(id);
			ea.tag.add(entity.tag);
		}
	}

	public void putQA(Entity entity, String id) {
		EntityAppearance ea = map.get(entity.name);
		if (ea == null) {
			ea = new EntityAppearance();
			ea.qa.add(id);
			ea.tag.add(entity.tag);
			map.put(entity.name, ea);
		} else {
			ea.qa.add(id);
			ea.tag.add(entity.tag);
		}
	}

	public void putAnswer(Entity entity, String id) {
		EntityAppearance ea = map.get(entity.name);
		if (ea == null) {
			ea = new EntityAppearance();
			ea.answer.add(id);
			ea.tag.add(entity.tag);
			map.put(entity.name, ea);
		} else {
			ea.answer.add(id);
			ea.tag.add(entity.tag);
		}
	}

	void merge(Set<String> set, String filename) throws Exception {
		if(set.isEmpty()) return;
		t.debug("merging "+filename);
		File f = new File(filename);
		if (f.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "utf8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				set.add(line);
			}
			br.close();
			f.delete();
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename), "utf8"));
			for (String s : set) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			t.error("error occur when writing file: "+filename);
			e.printStackTrace();
		}
		set.clear();
	}
	
	public void append(Set<String> set, String filename) throws IOException {
		if(set.isEmpty()) return;
//		t.debug("appending "+filename);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(filename, true), "utf8"));
			for (String s : set) {
				bw.write(s);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {
			t.error("error occur when appending file: "+filename);
			e.printStackTrace();
		}
		set.clear();
	}

	public void toFile() throws Exception {
		for (Entry<String, EntityAppearance> e : map.entrySet()) {
			EntityAppearance ea = e.getValue();
			String entity = e.getKey().replace("/", "-");
			String entity_folder = root_folder + "/" + entity.charAt(0)+"/"+entity + "/";
			new File(entity_folder).mkdirs();
//			merge(ea.piece, entity_folder+"piece");
//			merge(ea.neighbor, entity_folder+"neighbor");
//			merge(ea.answer, entity_folder+"answer");
//			merge(ea.context, entity_folder+"context");
//			merge(ea.qa, entity_folder+"qa");
//			merge(ea.tag, entity_folder+"tag");
			append(ea.piece, entity_folder+"piece");
			append(ea.neighbor, entity_folder+"neighbor");
			append(ea.answer, entity_folder+"answer");
			append(ea.context, entity_folder+"context");
			append(ea.qa, entity_folder+"qa");
			append(ea.tag, entity_folder+"tag");
		}
	}

	public static void main(String[] args) throws Exception {
//		EntityMap em = new EntityMap();
//		em.toFile();
		
//		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
//				new FileOutputStream("D:\\a.txt", true), "utf8"));
//		bw.write("fasfas");
//		bw.newLine();
//		bw.close();
	}
}
