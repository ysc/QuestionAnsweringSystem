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
<%@page import="org.apdplat.qa.util.MySQLUtils"%>
<%@page import="org.apdplat.qa.model.Question"%>
<%@page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    List<Question> questions = MySQLUtils.getHistoryQuestionsFromDatabase();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>人机问答系统演示</title>
    </head>
    <body>
        <h1><font color="blue">人机问答系统演示 <a href="https://github.com/ysc/QuestionAnsweringSystem" target="_blank">项目主页</a></font></h1>
        <h2>其他用户曾经问过的问题（<%=questions.size() %>）：</h2>
                <table>
                <%
                    int i = 1;
                    for (Question question : questions) {
                        if(question.getQuestion()==null || question.getQuestion().trim().equals("")){
                            continue;
                        }
                        if(question.getQuestion().length()>50){
                            continue;
                        }
                        if(question.getQuestion().contains("傻逼")){
                            continue;
                        }
                %>
                    <tr><td><font color="red"><%=(i++)%> 、 <%=question.getQuestion()%></font></td><td><a target="_blank" href="index.jsp?q=<%=question.getQuestion().replaceAll("\"","").replaceAll("\'","")%>">简要答案</a></td><td><a target="_blank" href="view.jsp?q=<%=question.getQuestion().replaceAll("\"","").replaceAll("\'","")%>">详细答案</a></td></tr>
                <%
                    }
                %>
                </table>
                <%
                    if(questions.isEmpty()){
                %>
                还没有人问过问题
                <%
                    }
                %>
                <br/>
        <h2><a href="index.jsp">返回主页</a></h2>
    </body>
</html>