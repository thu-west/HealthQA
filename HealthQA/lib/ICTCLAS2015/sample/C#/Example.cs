using System;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;


namespace win_csharp
{
	[StructLayout(LayoutKind.Explicit)] 
	public struct result_t
	{ 
		[FieldOffset(0)] public int start; 
		[FieldOffset(4)] public int length;
        [FieldOffset(8)] public int sPos1;
        [FieldOffset(12)] public int sPos2;
        [FieldOffset(16)] public int sPos3;
        [FieldOffset(20)] public int sPos4;
        [FieldOffset(24)] public int sPos5;
        [FieldOffset(28)] public int sPos6;
        [FieldOffset(32)] public int sPos7;
        [FieldOffset(36)] public int sPos8;
        [FieldOffset(40)] public int sPos9;
        [FieldOffset(44)] public int sPos10;
       //[FieldOffset(12)] public int sPosLow;
        [FieldOffset(48)] public int POS_id; 
		[FieldOffset(52)] public int word_ID;
        [FieldOffset(56)] public int word_type;
        [FieldOffset(60)] public int weight;
	}
    /*
    struct result_t{
  int start; //start position,��������������еĿ�ʼλ��
  int length; //length,����ĳ���
  char  sPOS[POS_SIZE];//word type������IDֵ�����Կ��ٵĻ�ȡ���Ա�
  int	iPOS;//���Ա�ע�ı��
  int word_ID; //�ôʵ��ڲ�ID�ţ������δ��¼�ʣ����0����-1
  int word_type; //�����û��ʵ�;1�����û��ʵ��еĴʣ�0�����û��ʵ��еĴ�
  int weight;//word weight,read weight
 };*/

	/// <summary>
	/// Class1 ��ժҪ˵����
	/// </summary>
	class Class1
	{
        const string path = @"NLPIR.dll";//�趨dll��·��
        //�Ժ�����������
        [DllImport(path, CharSet = CharSet.Ansi, CallingConvention = CallingConvention.Winapi, EntryPoint = "NLPIR_Init")]
		public static extern bool NLPIR_Init(String sInitDirPath,int encoding,String sLicenseCode);

        //�ر�ע�⣬C���Եĺ���NLPIR_API const char * NLPIR_ParagraphProcess(const char *sParagraph,int bPOStagged=1);�����Ӧ���������
        [DllImport(path, CharSet = CharSet.Ansi, CallingConvention = CallingConvention.Winapi, EntryPoint = "NLPIR_ParagraphProcess")]
        public static extern IntPtr NLPIR_ParagraphProcess(String sParagraph, int bPOStagged = 1);
	
        [DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_Exit")]
		public static extern bool NLPIR_Exit();
	
		[DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_ImportUserDict")]
		public static extern int NLPIR_ImportUserDict(String sFilename);

		[DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_FileProcess")]
		public static extern bool NLPIR_FileProcess(String sSrcFilename,String sDestFilename,int bPOStagged=1);
		
		[DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_FileProcessEx")]
		public static extern bool NLPIR_FileProcessEx(String sSrcFilename,String sDestFilename);
		
		[DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_GetParagraphProcessAWordCount")]
		static extern int NLPIR_GetParagraphProcessAWordCount(String sParagraph);
		//NLPIR_GetParagraphProcessAWordCount
		[DllImport(path,CharSet=CharSet.Ansi,EntryPoint="NLPIR_ParagraphProcessAW")]
		static extern void NLPIR_ParagraphProcessAW(int nCount, [Out,MarshalAs(UnmanagedType.LPArray)] result_t[] result);

        [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_AddUserWord")]
        static extern int NLPIR_AddUserWord(String sWord);

        [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_SaveTheUsrDic")]
        static extern int NLPIR_SaveTheUsrDic();

        [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_DelUsrWord")]
        static extern int NLPIR_DelUsrWord(String sWord);

       [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_NWI_Start")]
        static extern bool NLPIR_NWI_Start();

       [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_NWI_Complete")]
        static extern bool NLPIR_NWI_Complete();

       [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_NWI_AddFile")]
        static extern bool NLPIR_NWI_AddFile(String sText);

       [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_NWI_AddMem")]
        static extern bool NLPIR_NWI_AddMem(String sText);

       [DllImport(path, CharSet = CharSet.Ansi, CallingConvention = CallingConvention.Winapi, EntryPoint = "NLPIR_NWI_GetResult")]
      public static extern IntPtr NLPIR_NWI_GetResult(bool bWeightOut = false);

      [DllImport(path, CharSet = CharSet.Ansi, EntryPoint = "NLPIR_NWI_Result2UserDict")]
        static extern uint NLPIR_NWI_Result2UserDict();
    
        [DllImport(path, CharSet = CharSet.Ansi,CallingConvention = CallingConvention.Winapi, EntryPoint = "NLPIR_GetKeyWords")]
       public static extern IntPtr NLPIR_GetKeyWords(String sText,int nMaxKeyLimit=50,bool bWeightOut=false);

        [DllImport(path, CharSet = CharSet.Ansi, CallingConvention = CallingConvention.Winapi, EntryPoint = "NLPIR_GetFileKeyWords")]
        public static extern IntPtr NLPIR_GetFileKeyWords(String sFilename, int nMaxKeyLimit = 50, bool bWeightOut = false);
         /// <summary>
		/// Ӧ�ó��������ڵ㡣
		/// </summary>
		[STAThread]
		static void Main(string[] args)
		{
			//
			// TODO: �ڴ˴���Ӵ���������Ӧ�ó���
			//
            if (!NLPIR_Init("../../", 0,""))//����Data�ļ����ڵ�·����ע�����ʵ������޸ġ�
			{
				System.Console.WriteLine("Init ICTCLAS failed!");
				return;
			}
            System.Console.WriteLine("Init ICTCLAS success!");
            
            String s = "�������������ۺϱ������ݶ���˹�����ۺ���������12��27�ձ���������˹��������ó�׷�������12��26�ո����Ѿ�ǩ���ĺ�ͬ�Ͳɹ����򣬶�2012����˶���˹֮����������Ҿ޶�����������������ܽᣬ�Ƴ����������20�������������а�����ӡ�Ȳ����߾Ӱ��ף����һ�6���ϰ񡣾���������£�";

            int count = NLPIR_GetParagraphProcessAWordCount(s);//�ȵõ�����Ĵ���
            System.Console.WriteLine("NLPIR_GetParagraphProcessAWordCount success!");

            result_t[] result = new result_t[count];//�ڿͻ���������Դ
			NLPIR_ParagraphProcessAW(count,result);//��ȡ����浽�ͻ����ڴ���
            int i=1;
            foreach(result_t r in result)
            {
                String sWhichDic="";
                switch (r.word_type)
                {
                    case 0:
                        sWhichDic = "���Ĵʵ�";
                        break;
                    case 1:
                        sWhichDic = "�û��ʵ�";
                        break;
                    case 2:
                        sWhichDic = "רҵ�ʵ�";
                        break;
                    default:
                        break;
                }
                Console.WriteLine("No.{0}:start:{1}, length:{2},POS_ID:{3},Word_ID:{4}, UserDefine:{5}\n", i++, r.start, r.length, r.POS_id, r.word_ID, sWhichDic);//, s.Substring(r.start, r.length)
           }
          StringBuilder sResult = new StringBuilder(600); 
            //׼���洢�ռ�         
   
          IntPtr intPtr =NLPIR_ParagraphProcess(s);//�зֽ������ΪIntPtr����
          String str = Marshal.PtrToStringAnsi(intPtr);//���зֽ��ת��Ϊstring
          Console.WriteLine(str);

          intPtr = NLPIR_GetFileKeyWords("../test/test.TXT", 50, true);
          str = Marshal.PtrToStringAnsi(intPtr);//���зֽ��ת��Ϊstring
          Console.WriteLine(str);

          s = "�������������ۺϱ������ݶ���˹�����ۺ���������12��27�ձ���������˹��������ó�׷�������12��26�ո����Ѿ�ǩ���ĺ�ͬ�Ͳɹ����򣬶�2012����˶���˹֮����������Ҿ޶�����������������ܽᣬ�Ƴ����������20�������������а�����ӡ�Ȳ����߾Ӱ��ף����һ�6���ϰ񡣾���������£�";
  
          intPtr = NLPIR_GetKeyWords(s, 10, true);
          str = Marshal.PtrToStringAnsi(intPtr);//���зֽ��ת��Ϊstring
          Console.WriteLine(str);



          System.Console.WriteLine("Before Userdict imported:");
          String ss;
          Console.WriteLine("insert user dic:");
          ss = Console.ReadLine();
        while (ss[0]!='q'&&ss[0]!='Q')
        {
            //�û��ʵ�����Ӵ�
            int iiii = NLPIR_AddUserWord(ss);//�� ���� example:������� vyou
            intPtr = NLPIR_ParagraphProcess(s, 1);
            str = Marshal.PtrToStringAnsi(intPtr);
            System.Console.WriteLine(str);
            NLPIR_SaveTheUsrDic(); // save the user dictionary to the file

            //ɾ���û��ʵ��еĴ�
            Console.WriteLine("delete usr dic:");
            ss = Console.ReadLine();
            iiii = NLPIR_DelUsrWord(ss);
            str = Marshal.PtrToStringAnsi(intPtr);
            System.Console.WriteLine(str);
            NLPIR_SaveTheUsrDic();                

        }

        //�����´ʷ���������Ӧ�ִʹ���
	    NLPIR_NWI_Start();//�´ʷ��ֹ�������
        NLPIR_NWI_AddFile("../../test/��˿��һ����ͷ�ĵ���.TXT");//���һ���������´ʵ��ļ����ɷ������

        NLPIR_NWI_Complete();//�´ʷ������
            
    
        intPtr = NLPIR_NWI_GetResult();
        str = Marshal.PtrToStringAnsi(intPtr);


        System.Console.WriteLine("�´�ʶ����:");
        System.Console.WriteLine(str);
        NLPIR_FileProcess("../test/��˿��һ����ͷ�ĵ���.TXT", "../test/��˿��һ����ͷ�ĵ���-�ִʽ��.TXT");
        NLPIR_NWI_Result2UserDict();//�´�ʶ��������ִʿ�
        NLPIR_FileProcess("../test/��˿��һ����ͷ�ĵ���.TXT", "../test/��˿��һ����ͷ�ĵ���-����Ӧ�ִʽ��.TXT");
        NLPIR_Exit();
       }
	}
}
