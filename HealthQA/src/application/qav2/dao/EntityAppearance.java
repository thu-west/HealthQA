package application.qav2.dao;

import java.util.TreeSet;
import java.util.Set;

public class EntityAppearance {
	Set<String> tag;
	Set<String> piece;
	Set<String> neighbor;
	Set<String> context;
	Set<String> answer;
	Set<String> qa;

	public EntityAppearance () {
		piece = new TreeSet<String>();
		neighbor = new TreeSet<String>();
		context = new TreeSet<String>();
		answer = new TreeSet<String>();
		qa = new TreeSet<String>();
		tag = new TreeSet<String>();
	}
}

