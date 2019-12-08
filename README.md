专病QA系统
=========
* 运行环境
* jdk 1.8.0_221
* Eclipse 4.7
* node v6.17.1

## 目录说明<br>
- /bin<br>
	　　存储生成的class文件

- /doc<br>
	　　存储相关文档

- /index<br>
	　　存储Lucene索引文件

- /lib<br>
	　　存储第三方Java库

- /resource<br>
	　　存储程序资源文件

- /src<br>
	　　存储源代码

- /other<br>
	　　存储其他杂物<br>
* 目录详细说明见Healthqa/doc/QA安装开发文档

## Linux端部署

- 1.首先将根目录下的所有文件拷贝至服务器。
- 2.安装jdk
- 3.安装nodejs(注意版本匹配)
- 4.完成项目迁移<br>
　　创建数据库ishc_data和healthqa，分别将post和reply_post表导入ishc_data，并将healthqa表结构导入healthqa<br>
　　更改conf文件目录下的healthqa.conf，配置数据库相关信息<br>
- 5.运行java -c ask_preprocess.java生成healthqa数据库中的问答对
- 6.运行java -c ask_index.java生成索引文件
- 5.程序运行入口src.application.qav2.Ask.java
- 6.在项目目录/HealthQA/web目录下输入命令 npm start
- 7.在浏览器访问http://localhost:3000即可显示健康问答系统


