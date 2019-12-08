// win_cDemo.cpp : �������̨Ӧ�ó������ڵ㡣
//

#include "../include/NLPIR.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#ifndef OS_LINUX
#pragma comment(lib, "../../../bin/ICTCLAS2013/NLPIR.lib")
#else
#include<pthread.h>
#endif

#include <string>
#include <vector>
#include <list>
#include <deque>
#include <map>
#include <time.h>
#include <algorithm>
#include <sys/types.h>
#include <sys/stat.h>

#ifndef OS_LINUX
#include <io.h>
#include <process.h>
#include <direct.h>
#include <assert.h>
#include <conio.h>
#define makeDirectory _mkdir
#pragma warning(disable:4786)
#define PATH_DELEMETER  "\\"
#define LONG64U __int64
#define DATE_DELEMETER  "/"
//#include <windows.h>
#define SLEEP(x) Sleep(x*1000)
#else
#include <dirent.h>
#include <ctype.h>
#include <unistd.h> 
#include <pthread.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <semaphore.h>
#define makeDirectory(x) mkdir(x, 0777)    // �ⲽ����룬�����ļ���ֻ��
//#define stricmp strcasecmp
//#define strnicmp strncasecmp
#define Sleep(x) sleep(x/1000)
#define min(a,b) (((a)<(b)) ? (a) : (b))
#define _stricmp(X,Y) strcasecmp((X),(Y))
#define stricmp(X,Y) strcasecmp((X),(Y))
#define strnicmp(X,Y,Z) strncasecmp((X),(Y),(Z))
#define _fstat(X,Y)     fstat((X),(Y))
#define _fileno(X)     fileno((X))
#define _stat           stat
#define _getcwd         getcwd
#define _off_t          off_t
#define PATH_DELEMETER  "/"
#define DATE_DELEMETER  "/"
#define LONG64U long long
#include <unistd.h>
#define SLEEP(x) sleep(x)
#endif

void SplitGBK(const char *sInput);
void SplitBIG5();
void SplitUTF8();
void testNewWord(int code);
long ReadFile(const char *sFilename,char **pBuffer);
int testBug()
{

	if(NLPIR_Init("../../")) 
	{ 
		for(int j=0; j<3000; ++j) 
		{ 
			if (j==1963)
			{
				int nDebug=1;
			}
			std::string str = "Я��ת�����������鷶Χ��������������ͬ���ߡ�"; 
			int nCount = 0; 
			const result_t *pVecResult = NLPIR_ParagraphProcessA(str.c_str(), &nCount); 
			//����pVecResult��nCount����ؼ��ʵ� 
			for(int i=0; i<nCount; ++i) 
			{ 
				char szKey[16] = { 0 }; 
				memcpy(szKey, str.c_str() + pVecResult[i].start, (pVecResult[i].length < 16) ? pVecResult[i].length : 16 - 1); 
				//cout<<szKey<<endl; 
			} 

			printf("%d\r",j);
		} 
		NLPIR_Exit(); 
	} 
	else 
	{ 
		//cout<<"nlpir init failed."<<endl; 
		printf("nlpir init failed.\n");
	} 

	return 0; 
}
/*********************************************************************
 *
 *  Func Name  : Read(const char *sFilename,char *pBuffer)
 *  Description: 
 *        Read file to a buffer and return the file size
 *              
 *
 *  Parameters : 
 *               sFilename: filename;
 *               pBuffer: buffer
 *
 *  Returns    : _off_t file size
 *  Author     : Kevin Zhang  
 *  History    : 
 *              1.create 2003-11-28
 *********************************************************************/
long ReadFile(const char *sFilename,char **pBuffer)
{
	FILE *fp;
    struct _stat buf;
    if((fp=fopen(sFilename,"rb"))==NULL)
	   return 0;//read file false
    int nRet=_fstat( _fileno(fp), &buf );//Get file information
    if(nRet != 0)//File length of the lexicon file
	{
	   return 0;
	}
    *pBuffer = new char[buf.st_size+1];
    //The buffer for file
    if(*pBuffer == 0)
	{
	   return 0;
	}
    fread(*pBuffer, buf.st_size, 1, fp);//Read 
	(*pBuffer)[buf.st_size]=0;
    fclose(fp);
	return buf.st_size;
}

void testStrSplitSpeed()
{
	//��ʼ���ִ����
	printf("���յ�ƪ���½��д���\n");
	if(!NLPIR_Init("../../",GBK_CODE))//�����ڵ�ǰ·���£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	printf("Success Init!\n");
	char sInput[1000] = "�ȵǻ���һȰҵ���С�����ʱ�����������ڡ���Խ�ϡ������ձ�����ý��2��18�ձ���";


	clock_t  lStart,lEnd;
	long lTime=0;
	int nLine=0;


	int nSize=strlen(sInput),nTotalSize=0;
	int i=1;
	double fTimeTotal=0.0,fTime=0.0;//Time cost
	double fSpeed,fSpeendMin=100000000.0;
	lStart=clock();//Record the time
	for (int i=0;i<10000;i++)
	{
		const char *pResult=NLPIR_ParagraphProcess(sInput);
	}

	lEnd=clock();//Record the time
	lTime=lEnd-lStart;
	fTime=(double)lTime/(double)CLOCKS_PER_SEC;//Time cost
	fSpeed=(double)nSize*10000/(double)fTime;
		
	printf("speed=%.2lfBytes/sec,length=%ld,time=%.6f\n",fSpeed,nSize*10000,fTime);

	NLPIR_Exit();
}
void testBatchSpeed(const char *sListFilename)
{
	//��ʼ���ִ����
	printf("���յ�ƪ���½��д���\n");
	if(!NLPIR_Init("../../",UTF8_CODE))//�����ڵ�ǰ·���£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	printf("Success Init!\n");

	FILE *fpListFile=fopen(sListFilename,"rt");
	char sInputFile[1025]="../test/test.TXT",sResultFile[1024];

	if (fpListFile==NULL)
	{
		printf("Error open %s\n",sListFilename);
		return ;
	}
	clock_t  lStart,lEnd;
	long lTime=0;
	int nLine=0;

	
	int nSize=0,nTotalSize=0;
	int i=1;
	double fTimeTotal=0.0,fTime=0.0;//Time cost
	double fSpeed,fSpeendMin=100000000.0;

	while(fgets(sInputFile,1024,fpListFile))
	{
		nLine=strlen(sInputFile);
		while(nLine>0&&(sInputFile[nLine-1]=='\r'||sInputFile[nLine-1]=='\n'))
		{
			nLine--;
		}
		sInputFile[nLine]=0;
		//sscanf(sResultFile,sInputFile)
		char *pText=0;
		nSize=ReadFile(sInputFile,&pText);
		if (nSize<=0)
		{
			printf("Error open %s\n",sInputFile);
			continue ;
		}
		nTotalSize+=nSize;
		lStart=clock();//Record the time
		int nCount=NLPIR_GetParagraphProcessAWordCount(pText);

		if (nCount>0)
		{
			result_t *pResult=new result_t[nCount];
			NLPIR_ParagraphProcessAW(nCount,pResult);
			
			//for (int i=0;i<nCount;i++)
			{
				//printf("\nstart=%d length=%d\n",pResult[i].start,pResult[i].length);
			}
			delete [] pResult;
		}
		lEnd=clock();//Record the time
		lTime=lEnd-lStart;
		fTime=(double)lTime/(double)CLOCKS_PER_SEC;//Time cost
		fTimeTotal+=lTime;
		fSpeed=(double)nSize/(double)fTime;
		if (nSize>0&&fSpeed<fSpeendMin)
		{
			fSpeendMin=fSpeed;
			strcpy(sResultFile,sInputFile);
		}
		printf("%d\r",i++);
	}
	
	fclose(fpListFile);
	fTimeTotal=fTimeTotal/CLOCKS_PER_SEC;
	fSpeed=(double)nTotalSize/fTimeTotal;
	printf("speed=%.2lfBytes/sec,length=%ld,time=%.6f\n",fSpeed,nTotalSize,fTimeTotal);
	fSpeed=(double)i/(double)fTimeTotal;
	printf("speed=%.2lfdocs/sec,docs=%ld,time=%.6f\n",fSpeed,i,fTimeTotal);

	printf("MinSpeed=%.2lfBytes/sec,file=%s\n",fSpeendMin,sResultFile);
	NLPIR_Exit();
}
void testSpeed(int nCode)
{
	//��ʼ���ִ����
	printf("���յ�ƪ���½��д���\n");
	if(!NLPIR_Init("../../",nCode))//�����ڵ�ǰ·���£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	printf("Success Init!\n");
	char sInputFile[1024]="../test/test.TXT",sResultFile[1024];
	if (nCode==UTF8_CODE)
	{
		strcpy(sInputFile,"../test/test-utf8.TXT");
	}
	strcpy(sResultFile,sInputFile);
	strcat(sResultFile,"_result.txt");
	clock_t  lStart,lEnd;
	long lTime=0;
	int nLine=0;
	FILE *fpResult=fopen(sResultFile,"wt");
	if (fpResult==NULL)
	{
		printf("Error open %s\n",sResultFile);
	}
	lStart=clock();//Record the time
	char *pText=0;
	int nSize=ReadFile(sInputFile,&pText);
	if (nSize<=0)
	{
		printf("Error open %s\n",sInputFile);
		return ;
	}
	int nCount=NLPIR_GetParagraphProcessAWordCount(pText);
	
	if (nCount>0)
	{
			result_t *pResult=new result_t[nCount];
			NLPIR_ParagraphProcessAW(nCount,pResult);
			lEnd=clock();//Record the time
			for (int i=0;i<nCount;i++)
			{
				fprintf(fpResult,"\"\nstart=%d length=%d word=\"",pResult[i].start,pResult[i].length);
				fwrite(pText+pResult[i].start,sizeof(char),pResult[i].length,fpResult);
			}
			fclose(fpResult);
			delete [] pResult;
	}
	lTime=lEnd-lStart;

	double fTime=(double)lTime/(double)CLOCKS_PER_SEC;//Time cost
	double fSpeed=(double)nSize/(double)fTime;
	printf("speed=%.2lfBytes/s,length=%ld,time=%.2f\n",fSpeed,nSize,fTime);


	strcat(sResultFile,"_file");
	lStart=clock();//Record the time
	NLPIR_FileProcess(sInputFile,sResultFile,0);
	lEnd=clock();//Record the time
	lTime=lEnd-lStart;
	fTime=(double)lTime/(double)CLOCKS_PER_SEC;//Time cost
	fSpeed=(double)nSize/(double)fTime;

	printf("NLPIR_FileProcess speed=%.2lfBytes/s,length=%ld,time=%.2f\n",fSpeed,nSize,fTime);

	NLPIR_Exit();
}

void testNewWord(int nCode)
{
	//��ʼ���ִ����
	if(!NLPIR_Init("../../",nCode))//��������һ��Ŀ¼�£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	char sInputFile[1024]="../test/test.TXT",sResultFile[1024];
	if (nCode==UTF8_CODE)
	{
		strcpy(sInputFile,"../test/test-utf8.TXT");
	}

	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ���Թؼ�����ȡ����
	//////////////////////////////////////////////////////////////////////////
	const char *sResult=NLPIR_GetFileKeyWords(sInputFile,50,true);//���ı��ļ�����ȡ�ؼ���
	FILE *fp=fopen("Result.txt","wb");
	fprintf(fp,sResult);//�����д���ļ�
	fclose(fp);
	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ���Դӵ����ļ�����ȡ�´ʵĹ���
	//////////////////////////////////////////////////////////////////////////
	sResult=NLPIR_GetFileNewWords(sInputFile);//���ı��ļ�����ȡ�´�
	fp=fopen("ResultNew.txt","wb");//�����д���ļ�
	fprintf(fp,sResult);
	fclose(fp);



	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ���Դӵ����ļ�����ȡ�´ʵĹ���
	//////////////////////////////////////////////////////////////////////////
	NLPIR_NWI_Start();//�´�ʶ��ʼ
	NLPIR_NWI_AddFile(sInputFile);//�������������ļ������Բ���ѭ������NLPIR_NWI_AddFile����NLPIR_NWI_AddMem
	NLPIR_NWI_Complete();//�´�ʶ�����ļ�����
	const char *pNewWordlist=NLPIR_NWI_GetResult();//��ȡ�������������ı��ļ���ʶ����´ʽ��
	printf("ʶ������´�Ϊ��%s\n",pNewWordlist);//��ӡ����´�ʶ����
	
	strcpy(sResultFile,sInputFile);
	strcat(sResultFile,"_result1.txt");
	clock_t  lStart,lEnd;
	long lTime=0;
	int nLine=0;

	lStart=clock();//Record the time
	double speed=NLPIR_FileProcess(sInputFile,sResultFile,0);//��ͨ���ı��ļ��ִʹ���
	lEnd=clock();//Record the time
	lTime+=lEnd-lStart;

	float fTime=(float)lTime/(float)CLOCKS_PER_SEC;//Time cost
	printf("speed=%.2lfKB/s,time=%.2f\n",speed,fTime);
	
	NLPIR_NWI_Result2UserDict();//���ϴ��´�ʶ��Ľ����Ϊ�û��ʵ䵼��ϵͳ�У������ķִʽ��Ϊ����Ӧ�ִʽ��

	strcpy(sResultFile,sInputFile);
	strcat(sResultFile,"_result2.txt");
	NLPIR_FileProcess(sInputFile,sResultFile);//�ٴε���ͬ���ĺ��������Ƿִʽ�����Զ���Ӧ�´ʽ��
	
	NLPIR_Exit();//ʶ����ɣ�ϵͳ�˳����ͷ���Դ
}
void SplitGBK(const char *sInput)
{//�ִ���ʾ

	//��ʼ���ִ����
	if(!NLPIR_Init("../../"))//�����ڵ�ǰ·���£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	NLPIR_SetPOSmap(PKU_POS_MAP_SECOND);//���ô��Ա�ע���ϵ����ͣ�Ĭ��Ϊ������������ע��ϵ
	 const char* sample1 = "���ĳɳ��ķ���ѧϰ�����й�֤ȯ�г�";
	 const char* sResult1 = ICTCLAS_ParagraphProcess(sample1); 
	 printf("%s\n", sResult1); 
	 printf("%d\n", ICTCLAS_AddUserWord("���� n"));
	 sResult1 = ICTCLAS_ParagraphProcess(sample1, 1); 
	 printf("%s\n", sResult1); 
	 printf("%d\n", ICTCLAS_DelUsrWord("����"));
	 sResult1 = ICTCLAS_ParagraphProcess(sample1, 1); 
	 printf("%s\n", sResult1); 
	 printf("%d\n", ICTCLAS_AddUserWord("���� n"));
	 sResult1 = ICTCLAS_ParagraphProcess(sample1, 1); 
	 printf("%s\n", sResult1); 


	 const char* sample2 = "������Ƶ��������";
	 const char* sResult2 = ICTCLAS_ParagraphProcess(sample2);
	 printf("%s\n", sResult2); 
	 printf("%d\n", ICTCLAS_AddUserWord("���� ag")); 
	 sResult2 = ICTCLAS_ParagraphProcess(sample2, 1); 
	 printf("%s\n", sResult2);


	char sSentence[5000]="ICTCLAS�ڹ���973ר������֯�������л����˵�һ�����ڵ�һ��������Ĵ����о�����SigHan��֯�������ж�����˶����һ����";
	const char * sResult;


	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ����NLPIR_ParagraphProcessA����
	//////////////////////////////////////////////////////////////////////////
	int nCount;
	strcpy(sSentence,"�ų���ָ�����й�֤ȯ�г����µ��������ԭ�����羭�÷�չ����ĵ����ͷ���ͨ�ɵ��������⣬�Լ��г������ı仯�ȡ�");
	const result_t *pResult=NLPIR_ParagraphProcessA(sSentence,&nCount);
	//�ִʵĸ߼�����
	//�ú�������Ϊ�������ַ���������ִʽ����result_t�ṹ�����飬nCoutΪ�����С
	//���ص���������ϵͳ����ά�����û�ֱ�ӵ��ü���

	printf("nCount=%d\n",nCount);//�ִʽ����Ŀ

	int i=1;
	char *sWhichDic;
	char sWord[100];
	for(i=0;i<nCount;i++)
	{//�����Ӧ�ִʽ��������
		sWhichDic="";
		switch (pResult[i].word_type)
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
		strncpy(sWord,sSentence+pResult[i].start,pResult[i].length);
		sWord[pResult[i].length]=0;
		printf("No.%d:start:%d, length:%d,POS_ID:%d,Word_ID:%d, UserDefine:%s, Word:%s,Weight:%d\n",
			i+1, pResult[i].start, pResult[i].length, pResult[i].iPOS, pResult[i].word_ID, sWhichDic,
			sWord,pResult[i].weight );
	}

	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ�����û��ʵ书��
	//////////////////////////////////////////////////////////////////////////
	//�����û��ʵ�ǰ

	printf("δ�����û��ʵ䣺\n");
	sResult = NLPIR_ParagraphProcess(sSentence, 1);
	printf("%s\n", sResult);

	//�����û��ʵ��
	printf("\n�����û��ʵ��\n");
	nCount = NLPIR_ImportUserDict("userdic.txt");//userdic.txt������ǰ���û��ʵ�
	//�����û��ʵ�
	printf("����%d���û��ʡ�\n", nCount);
	sResult = NLPIR_ParagraphProcess(sSentence, 1);
	printf("%s\n", sResult);

	strcpy(sSentence,"������Ƶ��ݸ��������");
	sResult = NLPIR_ParagraphProcess(sSentence, 1);
	printf("%s\n", sResult);

	//��̬����û���
	printf("\n��̬����û��ʺ�\n");
	NLPIR_AddUserWord("����   ag");
	sResult = NLPIR_ParagraphProcess(sSentence, 1);
	printf("%s\n", sResult);


	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ���Զ��ļ����зִʹ���
	//////////////////////////////////////////////////////////////////////////

	//////////////////////////////////////////////////////////////////////////
	//���濪ʼ���Ի����ĳ��ù���NLPIR_ParagraphProcess����
	//////////////////////////////////////////////////////////////////////////
	while(_stricmp(sSentence,"q")!=0)
	{
		sResult = NLPIR_ParagraphProcess(sSentence);
		//����������Ĺ��ܣ�����һ���ַ���������ִʽ���ַ�����
		//�������Ҫ��ע�����õ�ʱ�����Ϊ�� NLPIR_ParagraphProcess(sSentence,0)
		printf("%s\nInput string now('q' to quit)!\n", sResult);// 
		gets(sSentence);
	}
	//���ļ����зִ�
	NLPIR_FileProcess("test2.txt","test2_result.txt",1);
	NLPIR_FileProcess("testGBK.txt","testGBK_result.txt",1);

	//�ͷŷִ������Դ
	NLPIR_Exit();
}

void SplitGBK_Fanti(const char *sInput)
{//�ִ���ʾ

	//��ʼ���ִ����
	if(!NLPIR_Init("",GBK_FANTI_CODE))//�����ڵ�ǰ·���£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}

	NLPIR_SetPOSmap(ICT_POS_MAP_SECOND);

	char sSentence[5000]="ICTCLAS�ڹ���ר������֯�������л����˵�һ�����ڵ�һ��������Ĵ����о�����SigHan��֯�������ж�����˶����һ����ICTCLAS�ڇ��Ȍ��ҽM�M�����u�y�л�ӫ@���˵�һ�����ڵ�һ�Ç��H����̎���о��C��SigHan�M�����u�y�ж��@���˶�헵�һ����";
	const char * sResult;


	int nCount;
	NLPIR_ParagraphProcessA(sSentence,&nCount);
	printf("nCount=%d\n",nCount);
	int count = NLPIR_GetParagraphProcessAWordCount(sSentence);
	const result_t *pResult=NLPIR_ParagraphProcessA(sSentence,&nCount);

	int i=1;
	char *sWhichDic;
	for(i=0;i<nCount;i++)
	{
		sWhichDic="";
		switch (pResult[i].word_type)
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
		printf("No.%d:start:%d, length:%d,POS_ID:%d,Word_ID:%d, UserDefine:%s, Word:%s\n",
			i+1, pResult[i].start, pResult[i].length, pResult[i].iPOS, pResult[i].word_ID, sWhichDic,sSentence+pResult[i].start );
	}
	while(_stricmp(sSentence,"q")!=0)
	{
		sResult = NLPIR_ParagraphProcess(sSentence,0);
		printf("%s\nInput string now('q' to quit)!\n", sResult);// 
		scanf("%s",sSentence);
	}
	
	//�����û��ʵ�ǰ
	printf("δ�����û��ʵ䣺\n");
	sResult = NLPIR_ParagraphProcess(sInput, 0);
	printf("%s\n", sResult);

	//�����û��ʵ��
	printf("\n�����û��ʵ��\n");
	nCount = NLPIR_ImportUserDict("userdic.txt");//userdic.txt������ǰ���û��ʵ�
	//�����û��ʵ�
	NLPIR_SaveTheUsrDic();
	printf("����%d���û��ʡ�\n", nCount);
	
	sResult = NLPIR_ParagraphProcess(sInput, 1);
	printf("%s\n", sResult);

	//��̬����û���
	printf("\n��̬����û��ʺ�\n");
	NLPIR_AddUserWord("�����ѧԺ   xueyuan");
	NLPIR_SaveTheUsrDic();
	sResult = NLPIR_ParagraphProcess(sInput, 1);
	printf("%s\n", sResult);


	//���ļ����зִ�
	NLPIR_FileProcess("test2.txt","test2_result.txt",1);
	NLPIR_FileProcess("testGBK.txt","testGBK_result.txt",1);


	//�ͷŷִ������Դ
	NLPIR_Exit();
}
void SplitBIG5()
{
	//��ʼ���ִ����
	if(!NLPIR_Init("",BIG5_CODE))//�����ڵ�ǰ·���£�����ΪBIG5����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	NLPIR_FileProcess("testBIG.txt","testBIG_result.txt");
	NLPIR_Exit();
}
void SplitUTF8()
{
	//��ʼ���ִ����
	if(!NLPIR_Init("../../",UTF8_CODE))//�����ڵ�ǰ·���£�����ΪUTF8����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	NLPIR_FileProcess("testUTF.txt","testUTF_result.txt");
//	NLPIR_FileProcess("test.xml","testUTF_result.xml");


	FILE *fp=fopen("D:\\NLPIR\\test\\utf-8-offset.txt","rt");
	if (fp==NULL)
	{
		printf("Error Open TestUTF8-bigfile.txt\n");
		NLPIR_Exit();
		return ;
	}
	FILE *fpResult=fopen("utf-8-offset-result.txt","wt");
	char sLine[10241];
	int i,nCount,nDocID=1;
	result_t res[1000];
	//while (fgets(sLine,10240,fp))
	{
		CICTCLAS *pICTCLAS=new CICTCLAS;
		/*
		int nCountA=pICTCLAS->GetParagraphProcessAWordCount(sLine);
		pICTCLAS->ParagraphProcessAW(nCountA,res);
		for(i=0;i<nCountA;i++)
		{
			fprintf(fpResult,"No.%d:start:%d, length:%d,POS_ID:%d,Word_ID:%d\n",
				i+1, res[i].start, res[i].length, res[i].iPOS, res[i].word_ID);
		}
		*/
	
		fseek(fp,0,SEEK_END);
		int nSize=ftell(fp);

		fseek(fp,0,SEEK_SET);
		fread(sLine,nSize,1,fp);
		sLine[nSize]=0;
		const result_t *pResult=pICTCLAS->ParagraphProcessA(sLine,&nCount);
	    i=1;

		for(i=0;i<500&&i<nCount;i++)
		{
 			fprintf(fpResult,"\nNo.%d:start:%d, length:%d,POS_ID:%d,Word_ID:%d\n",
 				i+1, pResult[i].start, pResult[i].length, pResult[i].iPOS, pResult[i].word_ID);
			fwrite(sLine+pResult[i].start,sizeof(char),pResult[i].length,fpResult);
		}
		delete pICTCLAS;
		fclose(fpResult);
		printf("Processed docs %d\r",nDocID++);
	}

	NLPIR_Exit();
}

#include "../../Utility/global_linux.h"

#include <string>
#include <vector>
struct _thread_argu 
{
	std::string srcFile;
	std::string dsnFile;
	bool bDone;
	_thread_argu(const char *sSrc,const char*sDsn)
	{
		srcFile=sSrc;
		dsnFile=sDsn;
		bDone=false;
	}
};

std::vector<_thread_argu> g_vecArgu;

MUTEXDEFX(m_mutex);

int g_iProcessCount=0,g_iTotalDocCount=0;
#ifdef _WIN32
static void FileWordSegThread(void* lpParam)
#else
static void* FileWordSegThread(void* lpParam)
#endif
{

	printf("start FileWordSegThread\n ");

	if(!NLPIR_Init("../..",UTF8_CODE))//�����ڵ�ǰ·���£�����ΪUTF8����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
#ifdef _WIN32
		return ;
#else
		return NULL;
#endif
	}
	int i=0;
	while(g_iProcessCount<g_iTotalDocCount)
	{
		i=0;
		while(i<g_vecArgu.size()&&g_vecArgu[i].bDone)
		{
			i++;
		}
		if (i<g_vecArgu.size()&&!g_vecArgu[i].bDone)
		{
			//MUTEXLOCKX(theDlg->m_mutex);
			MUTEXLOCKX(m_mutex);
			g_vecArgu[i].bDone=true;
			//MUTEXUNLOCKX(theDlg->m_mutex);
			MUTEXUNLOCKX(m_mutex);
			CNLPIR *pProcessor=GetActiveInstance();//new CNLPIR;//
			char *pContent=0;
			long nSize= ReadFile(g_vecArgu[i].srcFile.c_str(),&pContent);
			int nCount=pProcessor->GetParagraphProcessAWordCount(pContent);
			result_t *pResult=new result_t[nCount+1];
			pProcessor->ParagraphProcessAW(nCount,pResult);
			//pProcessor->FileProcess(g_vecArgu[i].srcFile.c_str(),g_vecArgu[i].dsnFile.c_str());
			printf("�Ѿ����%s->%s\r",g_vecArgu[i].srcFile.c_str(),g_vecArgu[i].dsnFile.c_str());		
			pProcessor->SetAvailable();
			delete pResult;
			delete pContent;
			//delete pProcessor;
		}
		//MUTEXLOCKX(theDlg->m_mutex);
		MUTEXLOCKX(m_mutex);
		++g_iProcessCount;
		printf("�Ѿ����%d\r",g_iProcessCount);
		MUTEXUNLOCKX(m_mutex);

	}

	if (g_iProcessCount==g_iTotalDocCount)
	{//�������
		printf("ϵͳ�ѳɹ�������%dƪ�ĵ�!\n", g_iTotalDocCount);
	}
#ifdef _WIN32
	return ;
#else
	return NULL;
#endif
}

void testMultiThread()
{
	//��ʼ���ִ����
	printf("start testMultiThread\n ");

	FILE *fp=fopen("filelist.txt","rt");
	if (fp==NULL)
	{
		NLPIR_Exit();
		return ;
	}
	char sLine[1024];
	char sDsnFile[1024];
	while(fgets(sLine,1023,fp))
	{
		unsigned int i=strlen(sLine);
		while (i>0&&(sLine[i-1]=='\r'||sLine[i-1]=='\n'||sLine[i-1]==' '||sLine[i-1]=='\t'))
		{
			sLine[i-1]=0;
			i--;
		}
		strcpy(sDsnFile,sLine);
		strcat(sDsnFile,".result");
		//double speed=NLPIR_FileProcess(sLine,sDsnFile,0);
		_thread_argu argu(sLine,sDsnFile);
		//argu.srcFile=sLine;
		//argu.dsnFile=sDsnFile;
		argu.bDone=false;
		g_vecArgu.push_back(argu);
		//printf("process %s, speed=%.2lfKB/s\n",sLine,speed);
	}
	fclose(fp);
	g_iTotalDocCount=g_vecArgu.size();
	printf("iDocCount=%d\n ",g_iTotalDocCount);
	int nThreadCount=30;
	if (nThreadCount>g_iTotalDocCount)
	{
		nThreadCount=g_iTotalDocCount;
	}

	if (nThreadCount>g_vecArgu.size())
	{
		nThreadCount=g_vecArgu.size();
	}
	int i;

#if defined(_WIN32)||defined(WIN32)

	HANDLE *handles=new HANDLE[nThreadCount];
	unsigned long myID;
	for(i=0; i<nThreadCount; ++i)
	{
		handles[i] = CreateThread(NULL, 0,	(LPTHREAD_START_ROUTINE)FileWordSegThread,	NULL, 0, &myID);
		if (handles[i] == NULL)  
		{   
			ExitProcess(i); 
		} 
	}
	int err = WaitForMultipleObjects(nThreadCount, handles,	TRUE, INFINITE);
	printf("Last thread to finish was thread #%d\n", err);
	for(i=0; i<nThreadCount; i++) 
	{   
		CloseHandle(handles[i]); 
	} 
#else		
	pthread_t *handles=new pthread_t[nThreadCount];
	pthread_attr_t stThreadAttr;

	pthread_attr_init(&stThreadAttr);
	pthread_attr_setdetachstate(&stThreadAttr, PTHREAD_CREATE_DETACHED);
	for(i=0; i<nThreadCount; ++i)
	{
		if(int err=pthread_create(&handles[i], &stThreadAttr, &FileWordSegThread, 0) != 0) 
		{	
			printf("can't create thread: %s\n", strerror(err));
			return ;
		}
		else
		{
			printf("create thread : %d\n", i);
		}
	}
	for(i=0; i<nThreadCount; ++i)
	{
		pthread_join(handles[i], NULL);
		//return ;
	}
#endif		
#ifdef _WIN32
	err = WaitForMultipleObjects(nThreadCount, handles,	TRUE, INFINITE);
	printf("Last thread to finish was thread #%d\n", err);
#endif

	Sleep(1000);
	delete [] handles;
	NLPIR_Exit();
}
void ImportUserDict(const char *sFilename)
{
	//��ʼ���ִ����
	if(!NLPIR_Init("../../"))//��������һ��Ŀ¼�£�Ĭ��ΪGBK����ķִ�
	{
		printf("ICTCLAS INIT FAILED!\n");
		return ;
	}
	int nCount=NLPIR_ImportUserDict(sFilename);//�ٴε���ͬ���ĺ��������Ƿִʽ�����Զ���Ӧ�´ʽ��
	printf("Import %d user defined items from file %s\n",nCount,sFilename);
	NLPIR_Exit();//ʶ����ɣ�ϵͳ�˳����ͷ���Դ
}
int main(int argc,char *argv[])
{
	char *pFile;
	if (argc<2)
	{
		SplitGBK("");
		return 1;
	}
	switch(argv[1][0])
	{

	case 'b'://thread
	case 'B'://thread
		testBatchSpeed(argv[1]);
		break;
	case 'f'://thread
	case 'F'://thread
		SplitBIG5();
		break;
	case 'g'://thread
	case 'G'://thread
		SplitGBK("Test");
		break;
	case 'i'://import user dictionary
	case 'I'://
		pFile="../../Data/Userdict.txt";
		if (argc>2)
		{
			pFile=argv[2];
		}
		ImportUserDict(pFile);
		break;
	case 's'://thread
	case 'S'://thread
		testSpeed(0);
		break;
	case 'm'://thread
	case 'M'://thread
		testMultiThread();
		break;
	case 'n'://thread
	case 'N'://thread
		testNewWord(0);
		break;
	case 'u'://thread
	case 'U'://thread
		SplitUTF8();
		break;
	}
	return 0;
}	


