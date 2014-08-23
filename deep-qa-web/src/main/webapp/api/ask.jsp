<%-- 
    Document   : ask.jsp
    Created on : 2014-8-2, 12:37:51
    Author     : 杨尚川
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>API 接口演示</title>
    </head>
    <body>
        <form action="ask" method="get">
            <font color="red">输入问题：</font><input name="q" size="100" maxlength="100"><br/>
            答案数目：<input name="n" size="15" maxlength="15">
            <p></p>
            <input type="submit" value="查看答案"/>
        </form>
    </body>
</html>
