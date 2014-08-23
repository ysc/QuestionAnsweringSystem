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
<%@page import="org.apdplat.qa.model.Evidence"%>
<%@page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
    StringBuilder str = new StringBuilder();
    List<Question> questions = MySQLUtils.getQuestionsFromDatabase();
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>人机问答系统演示</title>
    </head>
    <body>
        <h1><font color="blue">人机问答系统演示 <a href="https://github.com/ysc/QuestionAnsweringSystem" target="_blank">项目主页</a></font></h1>
                <%
                    int i = 1;
                    for (Question question : questions) {
                %>
                <font color="red">Question <%=(i++)%> : <%=question.getQuestion()%></font><br/>
                    <%
                        int j = 1;
                        for (Evidence evidence : question.getEvidences()) {
                    %>
                <font color="red"> Title <%=j%> : </font> <%=evidence.getTitle()%><br/>
                <font color="red"> Snippet <%=j%> : </font> <%=evidence.getSnippet()%><br/>
                    <%
                            j++;
                        }
                    %>
                <br/>
                <br/>
                <%
                    }
                    if(questions.isEmpty()){
                %>
                MySQL中没有缓存任何数据或MySQL未启动
                <%
                    }
                %>
    </body>
</html>