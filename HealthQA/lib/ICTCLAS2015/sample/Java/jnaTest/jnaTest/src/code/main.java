package code;

import java.io.UnsupportedEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public class main {

	// 定义接口CLibrary，继承自com.sun.jna.Library
	public interface CLibrary extends Library {

		// 定义并初始化接口的静态变量
		CLibrary Instance = (CLibrary) Native.loadLibrary(
				"E:\\java\\JNI\\jnaTest\\NLPIR", CLibrary.class);

		// printf函数声明
		public boolean NLPIR_Init(byte[] sDataPath, int encoding,
				byte[] sLicenceCode);

		public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);
		
		public String NLPIR_GetKeyWords(String sLine,int nMaxKeyLimit,boolean bWeightOut);
		
		
		public void NLPIR_Exit();
	}

	public static String transString(String aidString, String ori_encoding,
			String new_encoding) {
		try {
			return new String(aidString.getBytes(ori_encoding), new_encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		String argu = "";
		// String system_charset = "GBK";//GBK----0
		String system_charset = "UTF-8";
		int charset_type = 1;
		// int charset_type = 0;
		// 调用printf打印信息
		if (!CLibrary.Instance.NLPIR_Init(argu.getBytes(system_charset),
				charset_type, "0".getBytes(system_charset))) {
			System.err.println("初始化失败！");
		}

		String sInput = "东方网12月4日消息：2009年10月21日,辽宁省阜新市委收到举报信,举报以付玉红为首吸毒、强奸、聚众淫乱,阜新市委政法委副书记于洋等参与吸毒、强奸、聚众淫乱等。对此,阜新市委高度重视,责成阜新市公安局立即成立调查组,抽调精干力量展开调查。　　调查期间,署名举报人上官宏祥又通过尹东方(女)向阜新市公安局刑警支队提供书面举报,举报于洋等参与吸毒、强奸、聚众淫乱。11月19日,正义网发表上官宏祥接受记者专访,再次实名举报于洋等参与吸毒、强奸、聚众淫乱,引起网民广泛关注。对此辽宁省政法委、省公安厅高度重视。当日,责成有关领导专程赴阜新听取案件调查情况。为加强对案件的督办和指导,省有关部门迅速成立工作组,赴阜新督办、指导案件调查工作,并将情况上报有关部门。　　经前一段调查证明,举报事实不存在,上官宏祥行为触犯《刑法》第243条,涉嫌诬告陷害罪。根据《刑事诉讼法》有关规定,阜新市公安局已于11月27日依法立案侦查。上官宏祥已于2009年12月1日到案,12月2日阜新市海州区人大常委会已依法停止其代表资格,阜新市公安局对其进行刑事拘留,并对同案人尹东方进行监视居住。现侦查工作正在进行中。";

		String nativeBytes = null;
		try {
			nativeBytes = CLibrary.Instance.NLPIR_ParagraphProcess(sInput, 3);
			// String nativeStr = new String(nativeBytes, 0,
			// nativeBytes.length,"utf-8");
			System.out.println("分词结果为： " + nativeBytes);
//			System.out.println("分词结果为： "
//					+ transString(nativeBytes, system_charset, "UTF-8"));
			//
			// System.out.println("分词结果为： "
			// + transString(nativeBytes, "gb2312", "utf-8"));
			
			
			
			int nCountKey = 0;
			String nativeByte = CLibrary.Instance.NLPIR_GetKeyWords(sInput,10,false);

			System.out.print("关键词提取结果是："+nativeByte);
			
//			int nativeElementSize = 4 * 6 +8;//size of result_t in native code
//			int nElement = nativeByte.length / nativeElementSize;
//			ByteArrayInputStream(nativeByte));
//
//			nativeByte = new byte[nativeByte.length];
//			nCountKey = testNLPIR30.NLPIR_KeyWord(nativeByte, nElement);
//
//			Result[] resultArr = new Result[nCountKey];
//			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(nativeByte));
//			for (int i = 0; i < nCountKey; i++)
//			{
//				resultArr[i] = new Result();
//				resultArr[i].start = Integer.reverseBytes(dis.readInt());
//				resultArr[i].length = Integer.reverseBytes(dis.readInt());
//				dis.skipBytes(8);
//				resultArr[i].posId = Integer.reverseBytes(dis.readInt());
//				resultArr[i].wordId = Integer.reverseBytes(dis.readInt());
//				resultArr[i].word_type = Integer.reverseBytes(dis.readInt());
//				resultArr[i].weight = Integer.reverseBytes(dis.readInt());
//					}
//			dis.close();
//
//			for (int i = 0; i < resultArr.length; i++)
//			{
//				System.out.println("start=" + resultArr[i].start + ",length=" + resultArr[i].length + "pos=" + resultArr[i].posId + "word=" + resultArr[i].wordId + "  weight=" + resultArr[i].weight);
//			}

			CLibrary.Instance.NLPIR_Exit();
			

		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

	}
}
