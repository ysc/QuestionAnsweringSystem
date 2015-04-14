/**
 * 
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, 杨尚川, yang-shangchuan@qq.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.apdplat.qa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 杨尚川
 */
public class MySQLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLUtils.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    // 防止出现Java中连接数据库时汉字都变成问号问题
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/questionanswer?useUnicode=true&characterEncoding=utf8";
    //private static final String URL = "jdbc:mysql://127.0.0.1:3306/questionanswer_fulltext";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOG.error("MySQL驱动加载失败：", e);
        }
    }

    private MySQLUtils() {
    }

    public static String getRewindEvidenceText(String question, String answer) {
        String sql = "SELECT text FROM rewind where question=?";
        Connection con = getConnection();
        if(con == null){
            return null;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            //1、查询问题
            pst = con.prepareStatement(sql);
            pst.setString(1, question + answer);
            rs = pst.executeQuery();
            if (rs.next()) {
                String text = rs.getString(1);
                return text;
            }
        } catch (SQLException e) {
            LOG.error("查询回带文本失败", e);
        } finally {
            close(con, pst, rs);
        }

        return null;
    }

    public static void saveRewindEvidenceText(String question, String answer, String text) {
        String sql = "insert into rewind (question, text) values (?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(sql);
            pst.setString(1, question + answer);
            pst.setString(2, text);
            ////1、保存回带文本
            int count = pst.executeUpdate();
            if (count == 1) {
                LOG.info("保存回带文本成功");
            } else {
                LOG.error("保存回带文本失败");
            }
        } catch (SQLException e) {
            LOG.debug("保存回带文本失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static List<Question> getHistoryQuestionsFromDatabase() {
        List<Question> questions = new ArrayList<>();
        String questionSql = "select question from question";
        Connection con = getConnection();
        if(con == null){
            return questions;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            //查询问题
            pst = con.prepareStatement(questionSql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String que = rs.getString(1);
                int index = que.indexOf(":");
                if(index > 0){
                    que = que.substring(index+1);
                }
                if(que == null || "".equals(que.trim())){
                    continue;
                }
                Question question = new Question();
                question.setQuestion(que);
                questions.add(question);
            }
        } catch (SQLException e) {
            LOG.error("查询问题失败", e);
        } finally {
            close(con, pst, rs);
        }
        return questions;
    }

    public static List<Question> getQuestionsFromDatabase() {
        List<Question> questions = new ArrayList<>();
        String questionSql = "select id,question from question";
        String evidenceSql = "select title,snippet from evidence where question=?";
        Connection con = getConnection();
        if(con == null){
            return questions;
        }
        PreparedStatement pst = null;
        PreparedStatement pst2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        try {
            //1、查询问题
            pst = con.prepareStatement(questionSql);
            rs = pst.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String que = rs.getString(2);
                Question question = new Question();
                question.setQuestion(que);

                //2、查询证据
                pst2 = con.prepareStatement(evidenceSql);
                pst2.setInt(1, id);
                rs2 = pst2.executeQuery();
                while (rs2.next()) {
                    String title = rs2.getString(1);
                    String snippet = rs2.getString(2);
                    Evidence evidence = new Evidence();
                    evidence.setTitle(title);
                    evidence.setSnippet(snippet);
                    //3、关联问题很证据
                    question.addEvidence(evidence);
                }
                questions.add(question);
                close(null, pst2, rs2);
            }
        } catch (SQLException e) {
            LOG.error("查询问题失败", e);
        } finally {
            close(con, pst, rs);
        }
        return questions;
    }

    public static Question getQuestionFromDatabase(String pre, String questionStr) {
        String questionSql = "select id,question from question where question=?";
        String evidenceSql = "select title,snippet from evidence where question=?";
        Connection con = getConnection();
        if(con == null){
            return null;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            //1、查询问题
            pst = con.prepareStatement(questionSql);
            pst.setString(1, pre + questionStr.trim().replace("?", "").replace("？", ""));
            rs = pst.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                //去掉前缀
                String que = rs.getString(2).replace(pre, "");
                Question question = new Question();
                question.setQuestion(que);
                close(pst, rs);
                //2、查询证据
                pst = con.prepareStatement(evidenceSql);
                pst.setInt(1, id);
                rs = pst.executeQuery();
                while (rs.next()) {
                    String title = rs.getString(1);
                    String snippet = rs.getString(2);
                    Evidence evidence = new Evidence();
                    evidence.setTitle(title);
                    evidence.setSnippet(snippet);
                    //3、关联问题很证据
                    question.addEvidence(evidence);
                }
                return question;
            } else {
                LOG.info("没有从数据库中查询到问题：" + questionStr);
            }
        } catch (SQLException e) {
            LOG.error("查询问题失败", e);
        } finally {
            close(con, pst, rs);
        }
        return null;
    }

    public static void saveQuestionToDatabase(String pre, Question question) {
		//如果问题已经保存

        String questionSql = "insert into question (question) values (?)";
        String evidenceSql = "insert into evidence (title, snippet, question) values (?, ?, ?)";
        Connection con = getConnection();
        if(con == null){
            return ;
        }
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, pre + question.getQuestion().trim().replace("?", "").replace("？", ""));
            ////1、保存问题
            int count = pst.executeUpdate();
            if (count == 1) {
                LOG.info("保存问题成功");
                ////2、获取自动生成的主键值
                rs = pst.getGeneratedKeys();
                long primaryKey = 0;
                if (rs.next()) {
                    primaryKey = (Long) rs.getObject(1);
                }
                //关闭pst和rs
                close(pst, rs);
                if (primaryKey == 0) {
                    LOG.error("获取问题自动生成的主键失败");
                    return;
                }
                int i = 1;
                ////3、保存证据
                for (Evidence evidence : question.getEvidences()) {
                    try {
                        pst = con.prepareStatement(evidenceSql);
                        pst.setString(1, evidence.getTitle());
                        pst.setString(2, evidence.getSnippet());
                        pst.setLong(3, primaryKey);
                        count = pst.executeUpdate();
                        if (count == 1) {
                            LOG.info("保存证据 " + i + " 成功");
                        } else {
                            LOG.info("保存证据 " + i + " 失败");
                        }
                        close(null, pst, null);
                    } catch (Exception e) {
                        LOG.error("保存证据 " + i + " 出错：", e);
                    }
                    i++;
                }

            } else {
                LOG.error("保存问题失败");
            }
        } catch (SQLException e) {
            LOG.error("保存问题失败", e);
        } finally {
            close(con, pst, rs);
        }
    }

    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            LOG.debug("MySQL获取数据库连接失败：", e);
        }
        return con;
    }

    public static void close(Statement st) {
        close(null, st, null);
    }

    public static void close(Statement st, ResultSet rs) {
        close(null, st, rs);
    }

    public static void close(Connection con, Statement st, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
                rs = null;
            }
            if (st != null) {
                st.close();
                st = null;
            }
            if (con != null) {
                con.close();
                con = null;
            }
        } catch (SQLException e) {
            LOG.error("数据库关闭失败", e);
        }
    }

    public static void close(Connection con, Statement st) {
        close(con, st, null);
    }

    public static void close(Connection con) {
        close(con, null, null);
    }

    public static void main(String[] args) throws Exception {
        Question question = MySQLUtils.getQuestionFromDatabase("google:", "APDPlat的发起人是谁？");
        if (question != null) {
            System.out.println(question);
        } else {
            System.out.println("问题不在数据库中：APDPlat的发起人是谁？");
        }
    }
}