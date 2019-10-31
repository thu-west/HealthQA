专病QA系统
=========
* 运行环境
* jdk 1.8.0_221
* Eclipse 4.7
* node v6.17.1

## Linux端部署

- 1.首先将根目录下的所有文件拷贝至服务器。
- 2.安装jdk
- 3.安装nodejs(注意版本匹配)
- 4.完成项目迁移
通过https://cloud.tsinghua.edu.cn/d/db3a115119654000b8f6/下载相应的index文件并放到HealthQA下,
下载相应的数据库表并在conf/healthqa.properties进行数据库配置,
相关数据库配置文件说明见HealthQA/doc/QA安装开发文档.
- 5.程序运行入口src.application.qav2.Ask.java
- 6.在项目目录/HealthQA/web目录下输入命令 npm start
- 7.在浏览器访问http://localhost:3000即可显示健康问答系统


