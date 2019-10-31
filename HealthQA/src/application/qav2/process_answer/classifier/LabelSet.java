package application.qav2.process_answer.classifier;

public class LabelSet {

	public static final String TOP = "top";
	public static final String D = "d";
	public static final String I = "i";
	public static final String M = "m";
	public static final String Y = "y";
	public static final String T = "t";
	public static final String[][] TOP_LABEL_SET = new String[][]{
		new String[]{"d", "cd", "dr", "cdr", "dw", "cdw"},
		new String[]{"i", "iz", "ciz", "iq"},
		new String[]{"k"},
		new String[]{"m", "cm", "mw"},
		new String[]{"y", "cy", "yw"},
		new String[]{"t", "ct", "tq"},
		new String[]{"cq"},
		new String[]{"fw"},
		new String[]{"ab"},
	};
	public static final String[][] D_LABEL_SET = new String[][] {
		new String[]{"d"},
		new String[]{"cd"},
		new String[]{"dr"}, 
		new String[]{"cdr"}, 
		new String[]{"dw"},
		new String[]{"cdw"}
	};
	public static final String[][] I_LABEL_SET = new String[][] {
		new String[]{"i"},
		new String[]{"iz"},
		new String[]{"ciz"}, 
		new String[]{"iq"} 
	};
	public static final String[][] M_LABEL_SET = new String[][] {
		new String[]{"m"},
		new String[]{"cm"},
		new String[]{"mw"}, 
	};
	public static final String[][] Y_LABEL_SET = new String[][] {
		new String[]{"y"},
		new String[]{"cy"},
		new String[]{"yw"}, 
	};
	public static final String[][] T_LABEL_SET = new String[][] {
		new String[]{"t"},
		new String[]{"ct"},
		new String[]{"tq"}, 
	};
	
}
