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
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.util.MySQLUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 从Baidu搜索问题的证据
 *
 * @author 杨尚川
 */
public class BaiduDataSource implements DataSource {

    private static final Logger LOG = LoggerFactory.getLogger(BaiduDataSource.class);

    private static final String ACCEPT = "text/html, */*; q=0.01";
    private static final String ENCODING = "gzip, deflate";
    private static final String LANGUAGE = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
    private static final String CONNECTION = "keep-alive";
    private static final String HOST = "www.baidu.com";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:31.0) Gecko/20100101 Firefox/31.0";

    //获取多少页
    private static final int PAGE = 1;
    private static final int PAGESIZE = 10;
    //使用摘要还是全文
    //使用摘要
    private static final boolean SUMMARY = true;
    //使用全文
    //private static final boolean SUMMARY = false;
    private final List<String> files = new ArrayList<>();

    public BaiduDataSource() {
    }

    public BaiduDataSource(String file) {
        this.files.add(file);
    }

    public BaiduDataSource(List<String> files) {
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
            LOG.info("从Question文件" + file + "中加载Question，从baidu中检索到了 " + questions.size() + " 个Question");
        }
        return questions;
    }

    @Override
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem) {
        //1、先从本地缓存里面找
        Question question = MySQLUtils.getQuestionFromDatabase("baidu:", questionStr);
        if (question != null) {
            //数据库中存在
            LOG.info("从数据库中查询到Question：" + question.getQuestion());
            //回答问题
            if (questionAnsweringSystem != null) {
                questionAnsweringSystem.answerQuestion(question);
            }
            return question;
        }
        //2、本地缓存里面没有再查询baidu
        question = new Question();
        question.setQuestion(questionStr);

        String query = "";
        try {
            query = URLEncoder.encode(question.getQuestion(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("url构造失败", e);
            return null;
        }
        String referer = "http://www.baidu.com/";
        for (int i = 0; i < PAGE; i++) {
            query = "http://www.baidu.com/s?tn=monline_5_dg&ie=utf-8&wd=" + query+"&oq="+query+"&usm=3&f=8&bs="+query+"&rsv_bp=1&rsv_sug3=1&rsv_sug4=141&rsv_sug1=1&rsv_sug=1&pn=" + i * PAGESIZE;
            LOG.debug(query);
            List<Evidence> evidences = searchBaidu(query, referer);
            referer = query;
            if (evidences != null && evidences.size() > 0) {
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
        //3、将baidu查询结果加入本地缓存
        if (question.getEvidences().size() > 7) {
            LOG.info("将Question：" + question.getQuestion() + " 加入MySQL数据库");
            MySQLUtils.saveQuestionToDatabase("baidu:", question);
        }

        //回答问题
        if (questionAnsweringSystem != null) {
            questionAnsweringSystem.answerQuestion(question);
        }
        return question;
    }

    private List<Evidence> searchBaidu(String url, String referer) {
        List<Evidence> evidences = new ArrayList<>();
        try {
            Document document = Jsoup.connect(url)
                    .header("Accept", ACCEPT)
                    .header("Accept-Encoding", ENCODING)
                    .header("Accept-Language", LANGUAGE)
                    .header("Connection", CONNECTION)
                    .header("User-Agent", USER_AGENT)
                    .header("Host", HOST)
                    .header("Referer", referer)
                    .get();
            String resultCssQuery = "html > body > div > div > div > div > div";
            Elements elements = document.select(resultCssQuery);
            for (Element element : elements) {
                Elements subElements = element.select("h3 > a");
                if(subElements.size() != 1){
                    LOG.debug("没有找到标题");
                    continue;
                }
                String title =subElements.get(0).text();
                if (title == null || "".equals(title.trim())) {
                    LOG.debug("标题为空");
                    continue;
                }
                subElements = element.select("div.c-abstract");
                if(subElements.size() != 1){
                    LOG.debug("没有找到摘要");
                    continue;
                }
                String snippet =subElements.get(0).text();
                if (snippet == null || "".equals(snippet.trim())) {
                    LOG.debug("摘要为空");
                    continue;
                }
                Evidence evidence = new Evidence();
                evidence.setTitle(title);
                evidence.setSnippet(snippet);
                
                evidences.add(evidence);
            }
        } catch (Exception ex) {
            LOG.error("搜索出错", ex);
        }
        return evidences;
    }

    public static void main(String args[]) {
        Question question = new BaiduDataSource(FilesConfig.personNameQuestions).getQuestion("APDPlat的创始人是谁？");
        LOG.info(question.toString());
    }
}