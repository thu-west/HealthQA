package platform.annotate;

import java.util.Iterator;
import java.util.Set;

import platform.util.log.Trace;
import knowledge.DictGallary;
import knowledge.DictReadWrite;
import knowledge.DictTree;
import knowledge.DictTree.Node;

public class DictTreeHelper {
	
	Node tree = new Node(null, null);
	
	static Trace t = new Trace().setValid(false, true);
	
	public DictTreeHelper(int type) throws Exception {
		if(type==0) {
			init_0();
		} else {
			init_1();
		}
	}
	
	void init_0() throws Exception {
		t.debug("Loading full dict disease/check/sign/organ/organ_des/medicine/indicator/indicator_des/treat ...");
		Set<String> disease = DictReadWrite.loadDictInStringSet(DictGallary.disease);
		Set<String> check = DictReadWrite.loadDictInStringSet(DictGallary.check);
		Set<String> sign = DictReadWrite.loadDictInStringSet(DictGallary.sign);
		Set<String> organ = DictReadWrite.loadDictInStringSet(DictGallary.organ);
		Set<String> organ_des = DictReadWrite.loadDictInStringSet(DictGallary.organ_des);
		Set<String> medicine = DictReadWrite.loadDictInStringSet(DictGallary.medicine);
		Set<String> indicator = DictReadWrite.loadDictInStringSet(DictGallary.indicator);
		Set<String> indicator_des = DictReadWrite.loadDictInStringSet(DictGallary.indicator_des);
		Set<String> treat = DictReadWrite.loadDictInStringSet(DictGallary.treat);
		t.debug("Constructing the dict tree...");
		Iterator<String> it = disease.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "d");
		}
		it = check.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "c");
		}
		it = sign.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "s");
		}
		it = organ.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "o");
		}
		it = organ_des.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "os");
		}
		it = medicine.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "m");
		}
		it = indicator.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "i");
		}
		it = indicator_des.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "is");
		}
		it = treat.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "t");
		}
		t.debug("Done");
	}

	void init_1() throws Exception {
		t.debug("Loading pure dict disease/check/organ/medicine/indicator/treat ...");
		Set<String> disease = DictReadWrite.loadDictInStringSet(DictGallary.disease);
		Set<String> check = DictReadWrite.loadDictInStringSet(DictGallary.check);
		Set<String> organ = DictReadWrite.loadDictInStringSet(DictGallary.organ);
		Set<String> food = DictReadWrite.loadDictInStringSet(DictGallary.food);
		Set<String> medicine = DictReadWrite.loadDictInStringSet(DictGallary.medicine);
		Set<String> indicator = DictReadWrite.loadDictInStringSet(DictGallary.indicator);
		Set<String> treat = DictReadWrite.loadDictInStringSet(DictGallary.treat);
		t.debug("Constructing the dict tree...");
		Iterator<String> it = disease.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "d");
		}
		it = check.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "c");
		}
		it = organ.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "o");
		}
		it = medicine.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "m");
		}
		it = indicator.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "i");
		}
		it = food.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "y");
		}
		it = treat.iterator();
		while( it.hasNext() ) {
			DictTree.insertWithType(it.next(), tree, "t");
		}
		t.debug("Done");
	}
}
