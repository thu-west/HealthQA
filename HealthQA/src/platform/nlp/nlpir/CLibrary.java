package platform.nlp.nlpir;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CLibrary extends Library {
	
	// 定义并初始化接口的静态变量
	CLibrary Instance = (CLibrary) Native.loadLibrary(
			NLPIRSettings.NLPIR_DLL_SO , CLibrary.class);

	public int NLPIR_Init(String sDataPath, int encoding, String sLicenceCode);

	public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

	public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
			boolean bWeightOut);

	public int NLPIR_AddUserWord(String sWord);// add by qp 2008.11.10

	public int NLPIR_DelUsrWord(String sWord);// add by qp 2008.11.10

	public int NLPIR_ImportUserDict(String dict);

	public void NLPIR_Exit();
	
	public String NLPIR_GetFileKeyWords(String sTextFile, int
			nMaxKeyLimit, boolean bWeightOut );

}
