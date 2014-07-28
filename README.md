QuestionAnsweringSystem是一个Java实现的人机问答系统，能够自动分析问题并给出候选答案。


使用说明：

1、初始化MySQL数据库：   
	在MySQL命令行中执行QuestionAnsweringSystem\src\main\resources\mysql\questionanswer.sql文件中的脚本
	MySQL主机：127.0.0.1，端口：3306，数据库：questionanswer，用户名：root，密码：root
	
2、构建war文件并部署到tomcat：
	cd QuestionAnsweringSystem
	mvn install
	cp target\QuestionAnsweringSystem-1.0.war apache-tomcat-7.0.37/webapps/QuestionAnsweringSystem-1.0.war
	启动tomcat
	
3、打开浏览器访问：
	http://localhost:8080/QuestionAnsweringSystem-1.0/