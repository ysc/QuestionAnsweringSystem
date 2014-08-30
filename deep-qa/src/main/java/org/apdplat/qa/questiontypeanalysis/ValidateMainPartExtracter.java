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

package org.apdplat.qa.questiontypeanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apdplat.qa.questiontypeanalysis.patternbased.MainPartExtracter;
import org.apdplat.qa.questiontypeanalysis.patternbased.QuestionStructure;
import org.apdplat.qa.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据预先标注的语料来判断【主谓宾提取】的准确性
 *
 * @author 杨尚川
 */
public class ValidateMainPartExtracter {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateMainPartExtracter.class);

    public static List<QuestionStructure> parseQuestions(Set<String> questions) {
        LOG.info("解析预先标注的语料的主谓宾");
        List<QuestionStructure> result = new ArrayList<>();
        for (String question : questions) {
            question = question.trim();
            String[] attrs = question.split(":");
            if (attrs == null) {
                LOG.info("问句未标注主谓宾：" + question);
                QuestionStructure qs = new QuestionStructure();
                qs.setQuestion(question);
                result.add(qs);
            } else if (attrs.length == 1) {
                LOG.info("问句未标注主谓宾：" + question);
                QuestionStructure qs = new QuestionStructure();
                qs.setQuestion(attrs[0].trim());
                result.add(qs);
            } else if (attrs.length == 2) {
                String q = attrs[0];
                String m = attrs[1];
                if (m == null || "".equals(m.trim())) {
                    LOG.info("问句未标注主谓宾：" + question);
                } else {
                    String[] p = m.split("\\s+");
                    if (p == null || p.length != 3) {
                        LOG.info("问句未标注主谓宾：" + question);
                    } else {
                        QuestionStructure qs = new QuestionStructure();
                        qs.setQuestion(q);
                        qs.setMainPart(p[0].trim() + " " + p[1].trim() + " " + p[2].trim());
                        result.add(qs);
                    }
                }
            } else {
                LOG.info("问句未标注主谓宾：" + question);
            }
        }
        return result;
    }

    public static void validate() {
        MainPartExtracter mainPartExtracter = new MainPartExtracter();
        String file = "/org/apdplat.qa/questiontypeanalysis/AllTestQuestionsWithMainPart.txt";
        Set<String> questionStr = Tools.getQuestions(file);
        LOG.info("从文件中加载" + questionStr.size() + "个问题：" + file);
        List<QuestionStructure> questions = parseQuestions(questionStr);
        LOG.info("从标注的问句语料库中加载" + questionStr.size() + "条记录");
        LOG.info("成功解析" + questions.size() + "个问句");

        //不能提取
        List<QuestionStructure> no = new ArrayList<>();
        //能提取主谓宾但未标注
        List<QuestionStructure> yes = new ArrayList<>();
        //能提取主谓宾但和标注不一致
        Map<QuestionStructure, String> wrong = new HashMap<>();
        //能提取主谓宾且和标注一致
        List<QuestionStructure> right = new ArrayList<>();
        int human = 0;
        for (QuestionStructure question : questions) {
            QuestionStructure questionStructure = mainPartExtracter.getMainPart(question.getQuestion());
            if (questionStructure == null || questionStructure.getMainPart() == null) {
                //不能提取
                no.add(questionStructure);
            } else {
                //判断问题是否标注
                if (question.getMainPart() != null) {
                    //标注
                    human++;
                    //判断是否提取正确
                    if (questionStructure.getMainPart().equals(question.getMainPart())) {
                        //提取和标注匹配
                        right.add(questionStructure);
                    } else {
                        //提取和标注不匹配
                        String errorInfo;
                        if (question.getMainPart() == null) {
                            errorInfo = "语料未标注主谓宾";
                            wrong.put(questionStructure, errorInfo);
                            continue;
                        }
                        String[] attrs1 = questionStructure.getMainPart().split("\\s+");
                        if (attrs1 == null || attrs1.length != 3) {
                            errorInfo = "主谓宾提取错误";
                            wrong.put(questionStructure, errorInfo);
                            continue;
                        }
                        String[] attrs2 = question.getMainPart().split("\\s+");
                        if (attrs2 == null || attrs2.length != 3) {
                            errorInfo = "主谓宾标注错误";
                            wrong.put(questionStructure, errorInfo);
                            continue;
                        }
                        StringBuilder str = new StringBuilder();
                        if (!attrs1[0].trim().equals(attrs2[0].trim())) {
                            str.append(" 主语提取错误 ");
                        }
                        if (!attrs1[1].trim().equals(attrs2[1].trim())) {
                            str.append(" 谓语提取错误 ");
                        }
                        if (!attrs1[2].trim().equals(attrs2[2].trim())) {
                            str.append(" 宾语提取错误 ");
                        }
                        str.append(" 正确的主谓宾应该为：").append(question.getMainPart());
                        wrong.put(questionStructure, str.toString().trim());
                    }
                } else {
                    //能提取未标注
                    yes.add(questionStructure);
                }
            }
        }
        //两种提取模式结果一致的情况
        int perfect = 0;
        //不能提取
        //能提取主谓宾但未标注
        //能提取主谓宾但和标注不一致
        //能提取主谓宾且和标注一致
        LOG.info("");
        LOG.info("能提取主谓宾但未标注（" + yes.size() + "）：");
        int b = 1;
        for (QuestionStructure item : yes) {
            if (item.perfect()) {
                perfect++;
            }
            LOG.info((b++) + " " + item.getQuestion() + " : " + item.getMainPart());
            for (String den : item.getDependencies()) {
                LOG.info("\t" + den);
            }
        }
        LOG.info("");
        LOG.info("不能提取主谓宾数（" + no.size() + "）：");
        int a = 1;
        for (QuestionStructure item : no) {
            LOG.info((a++) + " " + item.getQuestion());
            for (String den : item.getDependencies()) {
                LOG.info("\t" + den);
            }
        }
        LOG.info("");
        LOG.info("能提取主谓宾但和标注【不一致】数（" + wrong.size() + "）：");
        int c = 1;
        for (QuestionStructure item : wrong.keySet()) {
            if (item.perfect()) {
                perfect++;
            }
            LOG.info((c++) + " " + item.getQuestion() + " " + item.getMainPart());
            for (String den : item.getDependencies()) {
                LOG.info("\t" + den);
            }
            LOG.info("\t" + wrong.get(item));
        }
        LOG.info("");
        LOG.info("能提取主谓宾且和标注【一致】数（" + right.size() + "）：");
        int d = 1;
        for (QuestionStructure item : right) {
            if (item.perfect()) {
                perfect++;
            }
            LOG.info((d++) + " " + item.getQuestion() + " : " + item.getMainPart());
            for (String den : item.getDependencies()) {
                LOG.info("\t" + den);
            }
        }
        int total = right.size() + wrong.size() + yes.size() + no.size();
        LOG.info("主谓宾提取统计");
        LOG.info("两种提取模式结果一致数: " + perfect);
        LOG.info("两种提取模式结果一致率: " + (double) perfect / total * 100 + "%");
        LOG.info("问题总数: " + total);
        LOG.info("识别数: " + (total - no.size()));
        LOG.info("识别率: " + (double) (total - no.size()) / total * 100 + "%");
        LOG.info("未识别数: " + no.size());
        LOG.info("未识别率: " + (double) no.size() / total * 100 + "%");
        LOG.info("人工标注数: " + human);
        LOG.info("人工标注率: " + (double) human / total * 100 + "%");
        LOG.info("识别准确数(人工标注): " + right.size());
        LOG.info("识别准确率(人工标注): " + (double) right.size() / human * 100 + "%");
        LOG.info("识别不准确数(人工标注): " + wrong.size());
        LOG.info("识别不准确率(人工标注): " + (double) wrong.size() / human * 100 + "%");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        validate();
    }
}