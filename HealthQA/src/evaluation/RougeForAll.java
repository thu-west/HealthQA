package evaluation;

import application.qav2.ao.Answer;
import application.qav2.ao.Question;

public class RougeForAll
{
  public static double EvaluateRouge1(Answer[] sum, Answer[][] refer) //input all sum and refer as ArrayList(refer[i] form all references of sum[i]), output R1
  {
    int numOfSum = sum.length;
    double[] r1 = new double[numOfSum];
    for (int i = 0; i < numOfSum; i++) {
      r1[i] = 0.0D;
      double temp = 0.0D;
      for (int j = 0; j < refer[i].length; j++) {
       	temp = Rouge.Rouge1(sum[i].seg_content, refer[i][j].seg_content);
        r1[i] = Math.max(r1[i], temp);
      }
    }
    double r1_avg=Average(r1);
    return r1_avg;
  }

  public static double EvaluateRouge2(Answer[] sum, Answer[][] refer) {
    int numOfSum = sum.length;
    double[] r2 = new double[numOfSum];
    for (int i = 0; i < numOfSum; i++) {
      r2[i] = 0.0D;
      double temp = 0.0D;
      for (int j = 0; j < refer[i].length; j++) {
        temp = Rouge.Rouge2(sum[i].seg_content, refer[i][j].seg_content);
        r2[i] = Math.max(r2[i], temp);
      }
    }
    double r2_avg=Average(r2);
    return r2_avg;
  }

  public static double EvaluateRougeSU4(Answer[] sum, Answer[][] refer) {
    int numOfSum = sum.length;
    double[] r4 = new double[numOfSum];
    for (int i = 0; i < numOfSum; i++) {
      r4[i] = 0.0D;
      double temp = 0.0D;
      for (int j = 0; j < refer[i].length; j++) {
        temp = Rouge.RougeSU4(sum[i].seg_content, refer[i][j].seg_content);
        r4[i] = Math.max(r4[i], temp);
      }
    }
    double r4_avg=Average(r4);
    return r4_avg;
  }
  
  public static double Average(double[] rouge){
	  int num=rouge.length;
	  double totle=0;
	  for(double s:rouge){
		  totle+=s;
	  }
	  double average=totle/num;
	  return average;
  }
  
  public static void main(String[] args) throws Exception {
		//set example of sum[] and refer[][]
		Answer[][] refer=new Answer[1][3];
		Answer[] sum=new Answer[1];
		Question q=new Question("id","title","question");
		sum[0]=new Answer("001","001","血糖为5.3。有糖尿病风险。",q);
		refer[0][0]=new Answer("002","0001","血糖偏高，达到5.3。患糖尿病风险较高。",q);
		refer[0][1]=new Answer("003","0001","血糖达到5.3,患糖尿病风险较高。",q);
		refer[0][2]=new Answer("004","0001","血糖偏高患糖尿病风险较高。",q);
		double r1,r2,rsu4;
		//test for evaluate with ROUGE-1, ROUGE-2,ROUGE—SU4
		r1=RougeForAll.EvaluateRouge1(sum, refer);
		r2=RougeForAll.EvaluateRouge2(sum, refer);
		rsu4=RougeForAll.EvaluateRougeSU4(sum, refer);
		System.out.print(r1);
		System.out.println("\n");
		System.out.print(r2);
		System.out.println("\n");
		System.out.print(rsu4);
		System.out.println("\n");
	}
  
}