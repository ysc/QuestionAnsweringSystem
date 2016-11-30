<%--
   APDPlat - Application Product Development Platform
   Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
   
   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.
   
   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.
   
   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>    
<%@page import="org.apdplat.qa.model.Question"%>
<%@page import="org.apdplat.qa.model.Evidence"%>
<%@page import="org.apdplat.qa.model.CandidateAnswer"%>
<%@page import="org.apdplat.qa.model.QuestionType"%>
<%@page import="org.apdplat.qa.SharedQuestionAnsweringSystem"%>
<%@page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    request.setCharacterEncoding("UTF-8");
    String questionStr = request.getParameter("q");
    Question question = null;
    List<CandidateAnswer> candidateAnswers = null;
    if (questionStr != null && questionStr.trim().length() > 3) {
        question = SharedQuestionAnsweringSystem.getInstance().answerQuestion(questionStr);
        if (question != null) {
            candidateAnswers = question.getAllCandidateAnswer();
        }
    }
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>人机问答系统演示</title>
        <script type="text/javascript">
            function answer(){
                var q = document.getElementById("q").value;
                if(q == ""){
                    return;
                }
                location.href = "index.jsp?q="+q;
            }
        </script>
    </head>
    <body>
        <h1><font color="blue">人机问答系统演示 <a href="https://github.com/ysc/QuestionAnsweringSystem" target="_blank">项目主页</a></font></h1>
        <h2><a href="view.jsp?q=<%=questionStr%>">更多细节</a></h2>
                <%
                    if (questionStr == null || questionStr.trim().length() <= 3) {
                %>      
        <font color="red">请输入问题且长度大于3</font>
            <%
            } else if (candidateAnswers == null || candidateAnswers.size() == 0) {
            %>
        <font color="red">回答问题失败：<%=questionStr%></font><br/>
            <%
                }
                if (candidateAnswers != null && candidateAnswers.size() > 0) {
            %>      
        <font color="red">问题：</font><%=questionStr%><br/>
        <font color="red">答案：</font>
        <table>
            <tr><th>序号</th><th>候选答案</th><th>答案评分</th></tr>
                    <%
                        int i = 0;
                        for (CandidateAnswer candidateAnswer : candidateAnswers) {
                            if ((++i) == 1) {
                    %>			
            <tr><td><font color="red"><%=i%></font></td><td><font color="red"><%=candidateAnswer.getAnswer()%></font></td><td><font color="red"><%=candidateAnswer.getScore()%></font></td></tr>
                        <%
                        } else {
                        %>
            <tr><td><%=i%></td><td><%=candidateAnswer.getAnswer()%></td><td><%=candidateAnswer.getScore()%></td></tr>
            <%
                    }
                }
            %>        
        </table>
        <h2><a href="index.jsp">返回主页</a></h2>
        <%
        } else {
        %>
        <p>
            <b>可以像如下提问：</b><br/>
            1、<a href="index.jsp?q=开源项目APDPlat应用级产品开发平台的作者是谁？">开源项目APDPlat应用级产品开发平台的作者是谁？</a><br/>
            2、<a href="index.jsp?q=APDPlat开源项目的发起人是谁？">APDPlat开源项目的发起人是谁？</a><br/>
            3、<a href="index.jsp?q=谁死后布了七十二疑冢？">谁死后布了七十二疑冢？</a><br/>
            4、<a href="index.jsp?q=谁是资深Nutch搜索引擎专家？">谁是资深Nutch搜索引擎专家？</a><br/>
            5、<a href="index.jsp?q=BMW是哪个汽车公司制造的？">BMW是哪个汽车公司制造的？</a><br/>
        </p>
        <font color="red">输入问题：</font><input id="q" name="q" size="50" maxlength="50">
        <p></p>
        <h2><a href="#" onclick="answer();">查看答案</a></h2>
        <%
            }
        %>  	
        <br/>
        <h2><a href="history_questions.jsp">其他用户曾经问过的问题</a></h2>
    </body>
</html>