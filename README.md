QuestionAnsweringSystem是一个Java实现的人机问答系统，能够自动分析问题并给出候选答案。

工作原理：

    1、判断问题类型（答案类型），当前使用模式匹配的方法，将来支持更多的方法，如朴素贝叶斯分类器。
    2、提取问题关键词。
    3、利用问题关键词搜索多种数据源，当前的数据源主要是人工标注的语料库、谷歌、百度。
    4、从搜索结果中根据问题类型（答案类型）提取候选答案。
    5、结合问题以及搜索结果对候选答案进行打分。
    6、返回得分最高的TopN项候选答案。

使用说明：

1、初始化MySQL数据库：   

    在MySQL命令行中执行QuestionAnsweringSystem\src\main\resources\mysql\questionanswer.sql文件中的脚本   
    主机：127.0.0.1
    端口：3306
    数据库：questionanswer
    用户名：root
    密码：root
	
2、构建war文件并部署到tomcat：

    cd QuestionAnsweringSystem   
    mvn install   
    cp target\QuestionAnsweringSystem-1.0.war apache-tomcat-7.0.37/webapps/QuestionAnsweringSystem-1.0.war   
    启动tomcat
	
3、打开浏览器访问：

    http://localhost:8080/QuestionAnsweringSystem-1.0/
	
[可部署war包下载](http://pan.baidu.com/s/1hq9pekc)