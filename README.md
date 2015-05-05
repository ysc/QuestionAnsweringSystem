##QuestionAnsweringSystem是一个Java实现的人机问答系统，能够自动分析问题并给出候选答案。IBM人工智能计算机系统"沃森"（Watson）在2011年2月美国热门的电视智力问答节目"危险边缘"（Jeopardy！）中战胜了两位人类冠军选手，QuestionAnsweringSystem就是IBM Watson的Java开源实现。

##工作原理：

    1、判断问题类型（答案类型），当前使用模式匹配的方法，将来支持更多的方法，如朴素贝叶斯分类器。
    2、提取问题关键词。
    3、利用问题关键词搜索多种数据源，当前的数据源主要是人工标注的语料库、谷歌、百度。
    4、从搜索结果中根据问题类型（答案类型）提取候选答案。
    5、结合问题以及搜索结果对候选答案进行打分。
    6、返回得分最高的TopN项候选答案。
	
##目前支持5种问题类型（答案类型）：

    1、人名 
		如：
		APDPlat的作者是谁？
		APDPlat的发起人是谁？
		谁死后布了七十二疑冢？
		习近平最爱的女人是谁？
	2、地名
		如：
		“海的女儿”是哪个城市的城徽？
		世界上流经国家最多的河流是哪一条？
		世界上最长的河流是什么？
		汉城是哪个国家的首都？
	3、机构团体名
		如：
		BMW是哪个汽车公司制造的？
		长城信用卡是哪家银行发行的？
		美国历史上第一所高等学府是哪个学校？
		前身是红色中华通讯社的是什么？
	4、数字
		如：
		全球表面积有多少平方公里？
		撒哈拉有多少平方公里？
		北京大学占地多少平方米？
		撒哈拉有多少平方公里？
	5、时间
		如：
		哪一年第一次提出“大跃进”的口号？
		大庆油田是哪一年发现的？
		澳门是在哪一年回归祖国怀抱的？
		邓小平在什么时候进行南巡讲话？
		
##API接口：

	调用地址：
		http://127.0.0.1/api/ask?n=1&q=APDPlat的作者是谁？
	参数：
		n表示需要返回的答案的个数
		q表示问题
	编码：
		服务端和客户端均使用UTF-8编码
		服务端需要修改tomcat配置文件conf/server.xml，在相应的Connector中加入配置URIEncoding="UTF-8"
	返回json:
		[
			{
				"answer": "杨尚川",
				"score": 1
			}
		]
			
##使用说明：

1、初始化MySQL数据库(MySQL作为数据缓存区使用，此步骤可选)：   

    在MySQL命令行中执行QuestionAnsweringSystem\deep-qa\src\main\resources\mysql\questionanswer.sql文件中的脚本   
    MySQL编码：UTF-8，
    主机：127.0.0.1
    端口：3306
    数据库：questionanswer
    用户名：root
    密码：root
	
2、构建war文件并部署到tomcat：

    cd QuestionAnsweringSystem   
    mvn install   
    cd deep-qa-web\target
    cp deep-qa-web-1.1.war apache-tomcat-7.0.37/webapps/QuestionAnsweringSystem.war   
    启动tomcat
	
3、打开浏览器访问：

    http://localhost:8080/QuestionAnsweringSystem/
	
[可部署war包下载](http://pan.baidu.com/s/1hq9pekc)

##如何在你的应用中集成人机问答系统QuestionAnsweringSystem? 

    QuestionAnsweringSystem提供了两种集成方式，以库的方式嵌入到应用中，以平台的方式独立部署。

    下面说说这两种方式如何做。

    1、以库的方式嵌入到应用中。

    这种方式只支持Java平台，可通过Maven依赖将库加入构建路径，如下所示：

    <dependency>
        <groupId>org.apdplat</groupId>
        <artifactId>deep-qa</artifactId>
        <version>1.1</version>
    </dependency>

    在应用如何使用呢？示例代码如下：
    
    String questionStr = "APDPlat的作者是谁？";
    Question question = SharedQuestionAnsweringSystem.getInstance().answerQuestion(questionStr);
    if (question != null) {
        List<CandidateAnswer> candidateAnswers = question.getAllCandidateAnswer();
        int i=1;
        for(CandidateAnswer candidateAnswer : candidateAnswers){
            System.out.println((i++)+"、"+candidateAnswer.getAnswer()+":"+candidateAnswer.getScore());
        }
    }

    运行程序后会在当前目录下生成目录deep-qa，目录里面又有两个目录dic和questionTypePatterns。
    dic是中文分词组件依赖的词库，questionTypePatterns是问题类别分析依赖的模式定义，可根据自己的需要修改。

    2、以平台的方式独立部署。

    首先在自己的服务器上如192.168.0.1部署好了，然后就可以通过Json Over HTTP的方式提供服务，使用方法如下所示：

    调用地址：
    http://192.168.0.1/api/ask?n=1&q=APDPlat的作者是谁？
    参数：
    n表示需要返回的答案的个数
    q表示问题
    编码：
    UTF-8编码
    返回json:
    [
        {
            "answer": "杨尚川",
            "score": 1
        }
    ]

##深入了解：

    QuestionAnsweringSystem由2个子项目构成，deep-qa和deep-qa-web。
    deep-qa是核心部分，deep-qa-web提供web界面来和用户交互，同时也提供了Json Over HTTP的访问接口，便于异构系统的集成。
    deep-qa是一个jar包，可通过maven引用：
    
    <dependency>
        <groupId>org.apdplat</groupId>
        <artifactId>deep-qa</artifactId>
        <version>1.1</version>
    </dependency>

    示例代码如下：

    String questionStr = "APDPlat的作者是谁？";
    Question question = SharedQuestionAnsweringSystem.getInstance().answerQuestion(questionStr);
    if (question != null) {
        List<CandidateAnswer> candidateAnswers = question.getAllCandidateAnswer();
        int i=1;
        for(CandidateAnswer candidateAnswer : candidateAnswers){
            System.out.println((i++)+"、"+candidateAnswer.getAnswer()+":"+candidateAnswer.getScore());
        }
    }

    运行程序后会在当前目录下生成目录deep-qa，目录里面又有两个目录dic和questionTypePatterns。
    dic是中文分词组件依赖的词库，questionTypePatterns是问题类别分析依赖的模式定义，可根据自己的需要修改。

[测试人机问答系统智能性的3760个问题](http://my.oschina.net/apdplat/blog/401622)

其他人机问答系统介绍：

1、OpenEphyra（Java开源）

    Ephyra is a modular and extensible framework for open domain question answering (QA). 
    The system retrieves accurate answers to natural language questions from the Web and 
    other sources. 
    
[OpenEphyra主页](http://www.ephyra.info/)

2、Watsonsim（Java开源）
    
    Open-domain question answering system from UNCC.
    Watsonsim works using a pipeline of operations on questions, candidate answers, and 
    their supporting passages. 
    In many ways it is similar to IBM's Watson, and Petr's YodaQA. 
    It's not all that similar to more logic based systems like OpenCog or Wolfram Alpha.
    
[Watsonsim主页](https://github.com/SeanTater/uncc2014watsonsim/)

3、YodaQA（Java开源）

    YodaQA is an open source Question Answering system.
    using on-the-fly Information Extraction from various data sources (mainly enwiki).
    YodaQA stands for "Yet anOther Deep Answering pipeline" and 
    the system is inspired by the DeepQA (IBM Watson) papers. 
    It is built on top of the Apache UIMA.
    
[YodaQA主页](https://github.com/brmson/yodaqa/)

4、IBM Watson（商业）

    Watson is built to mirror the same learning process that we have.
    Watson has been learning the language of professions and is trained 
    by experts to work across many different industries. 
        
[IBM Watson主页](http://www.ibm.com/smarterplanet/us/en/ibmwatson/what-is-watson.html)
    
5、Siri（商业）

    Siri /ˈsɪri/ is a part of Apple Inc.'s iOS which works as 
    an intelligent personal assistant and knowledge navigator. 
    The feature uses a natural language user interface to 
    answer questions, make recommendations, and perform actions 
    by delegating requests to a set of Web services. 

[Siri主页](http://www.apple.com/ios/siri/)
    
6、Magi Semantic Search（商业）

    Magi is a search engine that gives you answers instead of references. 
    It's designed to be General, Feasible and Useful. 

[Magi Semantic Search主页](http://www.peak-labs.com/)

7、微软小冰（商业）

    微软小冰是智能聊天机器人，基于微软搜索引擎和大数据积累，所有数据全部来自于公开的互联网网页信息。
    
[微软小冰主页](http://www.msxiaoice.com/)
    