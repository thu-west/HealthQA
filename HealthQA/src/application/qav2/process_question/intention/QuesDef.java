package application.qav2.process_question.intention;

public class QuesDef {
	
	static String[] END = new String[] { "吗", "呢" };
	static String[] BEGIN = new String[] { "请问", "不知道", "问一下" }; // 不作为判断问句的标准
	static String[] nengbuneng = new String[] { "能不能", "能否", "可否" };
	static String[] shibushi = new String[] { "是不是", "是否" };
	static String[] ruhe = new String[] { "如何", "怎么", "怎样" };
	static String[] duoshao = new String[] { "多少", "多高", "多大", "多长时间", "多久" };
	static String[] huibuhui = new String[] { "会不会" };
	static String[] na = new String[] { "哪" };
	static String[] shenme = new String[] { "什么" };
	static String[] other = new String[] { "算不算", "怎么", "怎么办", "怎么样", "怎样",
			"如何", "怎么回事", "怎么会", "咋", "咋回事", "为什么", "为何", "什么回事", "什么原因",
			"因为什么", "何", "啥", "哪些", "哪里", "哪种", "哪个", "哪方面" };

	static String[] M = {"药","药物","吃","喝"};
	static String[] Y  = {"食物","饮食","营养","吃","喝"};
	static String[] T = {"注意","措施","治","康复","预防","法"};
	static String[] I = {"检查","诊断","标准"};
	static String[] R = {"原因","为什么","为何"};
	static String[] W = {"害","副作用","作用","效果","影响","危险"};
	static String[] D = {"病","状态","情况","阶段"};

}
