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

package org.apdplat.qa.datasource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.util.MySQLUtils;
import org.apdplat.qa.util.Tools;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从Google搜索问题的证据
 *
 * @author 杨尚川
 */
public class GoogleDataSource implements DataSource {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleDataSource.class);
    private String file;
    private int googleSearchTimes = 0;
    private static final int GOOGLESEARCHLIMIT = 10;
    //获取多少页
    private static final int PAGE = 1;
    private static final int PAGESIZE = 8;
    //使用摘要还是全文
    //使用摘要
    private static final boolean SUMMARY = true;
    //使用全文
    //private static final boolean SUMMARY = false;
    private List<String> files = new ArrayList<>();

    public GoogleDataSource() {
    }

    public GoogleDataSource(String file) {
        this.files.add(file);
    }

    public GoogleDataSource(List<String> files) {
        this.files.addAll(files);
    }

    @Override
    public Question getQuestion(String questionStr) {
        return getAndAnswerQuestion(questionStr, null);
    }

    @Override
    public List<Question> getQuestions() {
        return getAndAnswerQuestions(null);
    }

    @Override
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem) {
        List<Question> questions = new ArrayList<>();

        for (String file : files) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(file), "utf-8"));
                String line = reader.readLine();
                while (line != null) {
                    if (line.trim().equals("") || line.trim().startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                        //读下一行
                        line = reader.readLine();
                        continue;
                    }
                    LOG.info("从类路径的 " + file + " 中加载Question:" + line.trim());
                    if (googleSearchTimes % GOOGLESEARCHLIMIT == (GOOGLESEARCHLIMIT - 1)) {
                        LOG.info("请更换IP：");
                        try {
                            Thread.sleep(30000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        LOG.info("继续");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String questionStr = null;
                    String expectAnswer = null;
                    String[] attrs = line.trim().split("[:|：]");
                    if (attrs == null) {
                        questionStr = line.trim();
                    }
                    if (attrs != null && attrs.length == 1) {
                        questionStr = attrs[0];
                    }
                    if (attrs != null && attrs.length == 2) {
                        questionStr = attrs[0];
                        expectAnswer = attrs[1];
                    }
                    LOG.info("Question:" + questionStr);
                    LOG.info("ExpectAnswer:" + expectAnswer);

                    Question question = getQuestion(questionStr);
                    if (question != null) {
                        question.setExpectAnswer(expectAnswer);
                        questions.add(question);
                    }

                    //回答问题
                    if (questionAnsweringSystem != null && question != null) {
                        questionAnsweringSystem.answerQuestion(question);
                    }

                    //读下一行
                    line = reader.readLine();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            LOG.info("从Question文件" + file + "中加载Question，从google中检索到了 " + questions.size() + " 个Question");
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        //1、先从本地缓存里面找
        Question question = MySQLUtils.getQuestionFromDatabase("google:", questionStr);
        if (question != null) {
            //数据库中存在
            LOG.info("从数据库中查询到Question：" + question.getQuestion());
            //回答问题
            if (questionAnsweringSystem != null) {
                questionAnsweringSystem.answerQuestion(question);
            }
            return question;
        }
        //2、本地缓存里面没有再查询google
        question = new Question();
        question.setQuestion(questionStr);

        String query = "";
        try {
            query = URLEncoder.encode(question.getQuestion(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("url构造失败", e);
            return null;
        }
        for (int i = 0; i < PAGE; i++) {
            query = "http://ajax.googleapis.com/ajax/services/search/web?start=" + i * PAGESIZE + "&rsz=large&v=1.0&q=" + query;
            List<Evidence> evidences = search(query);
            if (evidences.size() > 0) {
                question.addEvidences(evidences);
            } else {
                LOG.error("结果页 " + (i + 1) + " 没有搜索到结果");
                break;
            }
        }
        LOG.info("Question：" + question.getQuestion() + " 搜索到Evidence " + question.getEvidences().size() + " 条");
        if (question.getEvidences().isEmpty()) {
            return null;
        }
        //3、将google查询结果加入本地缓存
        if (question.getEvidences().size() > 7) {
            LOG.info("将Question：" + question.getQuestion() + " 加入MySQL数据库");
            MySQLUtils.saveQuestionToDatabase("google:", question);
        }
        googleSearchTimes++;

        //回答问题
        if (questionAnsweringSystem != null) {
            questionAnsweringSystem.answerQuestion(question);
        }
        return question;
    }

    private List<Evidence> search(String query) {
        List<Evidence> evidences = new ArrayList<>();
        try {
            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(query);
            
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());

            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("Method failed: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            String response = new String(responseBody, "UTF-8");
            LOG.debug("搜索返回数据：" + response);
            JSONObject json = new JSONObject(response);
            String totalResult = json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount");
            int totalResultCount = Integer.parseInt(totalResult);
            LOG.info("搜索返回记录数： " + totalResultCount);

            JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

            LOG.debug(" Results:");
            for (int i = 0; i < results.length(); i++) {
                Evidence evidence = new Evidence();
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("titleNoFormatting");
                LOG.debug(title);
                evidence.setTitle(title);
                if (SUMMARY) {
                    String content = result.get("content").toString();
                    content = content.replaceAll("<b>", "");
                    content = content.replaceAll("</b>", "");
                    content = content.replaceAll("\\.\\.\\.", "");
                    LOG.debug(content);
                    evidence.setSnippet(content);
                } else {
                    //从URL中提取正文
                    String url = result.get("url").toString();
                    String content = Tools.getHTMLContent(url);
                    if (content == null) {
                        content = result.get("content").toString();
                        content = content.replaceAll("<b>", "");
                        content = content.replaceAll("</b>", "");
                        content = content.replaceAll("\\.\\.\\.", "");
                    }
                    evidence.setSnippet(content);
                    LOG.debug(content);
                }
                evidences.add(evidence);
            }
        } catch (Exception e) {
            LOG.error("执行搜索失败：", e);
        }
        return evidences;
    }

    public static void main(String args[]) {
        Question question = new GoogleDataSource(FilesConfig.personNameQuestions).getQuestion("APDPlat的发起人是谁？");
        LOG.info(question.toString());
    }
}
