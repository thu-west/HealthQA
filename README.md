专病QA系统
=========
## 运行环境<br>
* jdk 1.8.0_221
* Eclipse 4.7
* node v6.17.1

## 目录说明<br>
- ./bin<br>
	　　存储生成的class文件

- ./doc<br>
	　　存储相关文档

- ./index<br>
	　　存储Lucene索引文件

- ./lib<br>
	　　存储第三方Java库

- ./resource<br>
	　　存储程序资源文件

- ./src<br>
	　　存储源代码

- ./other<br>
	　　存储其他杂物<br>
* 目录详细说明见Healthqa/doc/QA安装开发文档

## Linux端部署

- 1.首先将根目录下的所有文件拷贝至服务器。
- 2.安装jdk
- 3.安装nodejs(注意版本匹配)
- 4.进入./lib,运行命令<br>
```Bash
tar -xzvf CRF++-0.58.tar.gz
cd CRF++-0.58.tar.gz
./configure
make
```
- 5.完成项目迁移<br>
　　创建数据库ishc_data和healthqa，分别将post和reply_post表导入ishc_data，并将healthqa表结构导入healthqa<br>
　　更改conf文件目录下的healthqa.properties，配置数据库相关信息<br>
- 6.运行以下命令生成healthqa数据库中的问答对
```Bash
java -c ask_preprocess.jar
```
- 7.运行以下命令生成索引文件
```Bash
java -c ask_index.jar
```
- 8.程序运行入口:src.application.qav2.Ask.java
- 9.在项目目录/HealthQA/web目录下输入命令
```Bash
npm start
```
- 10.在浏览器访问 http://localhost:3000 即可显示健康问答系统

## Citation
--------


If you want to refer to our work, please cite our paper as:

<p><i>Yanshen Yin, Yong Zhang, Xiao Liu, Yan Zhang, Chunxiao Xing, Hsinchun Chen: HealthQA: A Chinese QA Summary System for Smart Health. ICSH 2014: 51-62</i></p>

or
```
@inproceedings{DBLP:conf/icsh/YinZLZXC14,
  author    = {Yanshen Yin and
               Yong Zhang and
               Xiao Liu and
               Yan Zhang and
               Chunxiao Xing and
               Hsinchun Chen},
  title     = {HealthQA: {A} Chinese {QA} Summary System for Smart Health},
  booktitle = {Smart Health - International Conference, {ICSH} 2014, Beijing, China,
               July 10-11, 2014. Proceedings},
  pages     = {51--62},
  year      = {2014},
  crossref  = {DBLP:conf/icsh/2014},
  url       = {https://doi.org/10.1007/978-3-319-08416-9\_6},
  doi       = {10.1007/978-3-319-08416-9\_6},
  timestamp = {Tue, 14 May 2019 10:00:46 +0200},
  biburl    = {https://dblp.org/rec/conf/icsh/YinZLZXC14.bib},
  bibsource = {dblp computer science bibliography, https://dblp.org}
}
```
## 联系方式<br>
shengming@tsinghua.edu
