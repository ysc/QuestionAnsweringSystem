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

package org.apdplat.qa.searcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apdplat.qa.model.Question;
import org.apdplat.qa.model.QuestionType;
import org.apdplat.qa.questiontypeanalysis.patternbased.DefaultPatternMatchResultSelector;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternBasedMultiLevelQuestionClassifier;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchResultSelector;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchStrategy;
import org.apdplat.qa.questiontypeanalysis.QuestionClassifier;
import org.apdplat.qa.questiontypeanalysis.patternbased.QuestionPattern;
import org.apdplat.qa.questiontypeanalysis.QuestionTypeTransformer;
import org.apdplat.qa.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 根据预先标注的语料来判断【模式识别】的准确性
 *
 * @author 杨尚川
 */
public class BestClassifierSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(BestClassifierSearcher.class);

    private static final Map<String, Double> map = new HashMap<>();
    private static final Map<String, Integer> map2 = new HashMap<>();

    private static void classify2() {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");
        //计算分类
        classify(patternMatchStrategy);
        //输出统计结果
        showResult();
    }

    private static void classify() {
        List<QuestionPattern> allQuestionPatterns = new ArrayList<>();
        allQuestionPatterns.add(QuestionPattern.Question);
        allQuestionPatterns.add(QuestionPattern.TermWithNatures);
        allQuestionPatterns.add(QuestionPattern.Natures);
        allQuestionPatterns.add(QuestionPattern.MainPartPattern);
        allQuestionPatterns.add(QuestionPattern.MainPartNaturePattern);

        List<String> allQuestionTypePatternFiles = new ArrayList<>();
        //allQuestionTypePatternFiles.add("QuestionTypePatternsLevel1_true.txt");
        allQuestionTypePatternFiles.add("QuestionTypePatternsLevel2_true.txt");
        allQuestionTypePatternFiles.add("QuestionTypePatternsLevel3_true.txt");

        List<List<QuestionPattern>> allQuestionPatternCom = Tools.getCom(allQuestionPatterns);
        LOG.info("问题模式组合种类：" + allQuestionPatternCom.size());
        List<List<String>> allQuestionTypePatternFileCom = Tools.getCom(allQuestionTypePatternFiles);
        LOG.info("问题类型模式组合种类：" + allQuestionTypePatternFileCom.size());
        LOG.info("需要计算" + allQuestionPatternCom.size() * allQuestionTypePatternFileCom.size() + "种组合");
        classify(allQuestionPatternCom, allQuestionTypePatternFileCom);
    }

    private static void classify(List<List<QuestionPattern>> allQuestionPatternCom, List<List<String>> allQuestionTypePatternFileCom) {
        for (List<QuestionPattern> questionPatternCom : allQuestionPatternCom) {
            for (List<String> questionTypePatternFileCom : allQuestionTypePatternFileCom) {
                PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
                //设置策略
                LOG.info("设置问题类型模式文件");
                for (String questionTypePatternFile : questionTypePatternFileCom) {
                    LOG.info("\t" + questionTypePatternFile);
                    patternMatchStrategy.addQuestionTypePatternFile(questionTypePatternFile);
                }
                LOG.info("设置问题模式");
                for (QuestionPattern questionPattern : questionPatternCom) {
                    LOG.info("\t" + questionPattern.name());
                    patternMatchStrategy.addQuestionPattern(questionPattern);
                }
                LOG.info("计算策略：" + patternMatchStrategy.getStrategyDes());
                //计算分类
                classify(patternMatchStrategy);
                //输出统计结果
                showResult();
            }
        }
    }

    private static void showResult() {
        List<Map.Entry<String, Double>> entrys = Tools.sortByDoubleValue(map);
        int i = 1;
        for (Map.Entry<String, Double> entry : entrys) {
            LOG.info("");
            LOG.info("组合 " + (i++) + " 结果：");
            LOG.info("\t策略：" + entry.getKey());
            LOG.info("\t准确数：" + map2.get(entry.getKey()));
            LOG.info("\t准确率：" + entry.getValue() + "%");
        }
    }

    private static void classify(PatternMatchStrategy patternMatchStrategy) {
        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();
        QuestionClassifier questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);
        String file = "/org/apdplat.qa/questiontypeanalysis/AllTestQuestions.txt";
        Set<String> questions = Tools.getQuestions(file);
        LOG.info("从文件中加载" + questions.size() + "个问题：" + file);
        List<String> no = new ArrayList<String>();
        List<String> wrong = new ArrayList<String>();
        List<String> right = new ArrayList<String>();
        List<String> yes = new ArrayList<String>();
        List<String> canNotSelect = new ArrayList<String>();

        int i = 1;
        int human = 0;
        for (String q : questions) {
            QuestionType type = null;
            //判断问题是否标注
            String[] attrs = q.split(":");
            if (attrs != null && attrs.length == 2) {
                human++;
                q = attrs[0].trim();
                type = QuestionTypeTransformer.transform(attrs[1].trim());
            }
            Question question = questionClassifier.classify(q);
            if (question != null && question.getQuestionType() != null) {
                QuestionType questionType = question.getQuestionType();
                if (type != null) {
                    //有人工标注
                    if (type == questionType) {
                        //分类和标注一致
                        right.add("问题" + (i++) + "【" + q + "】分类和标注【一致】，类型为：" + questionType.name());
                    } else {
                        //分类和标注不一致
                        wrong.add("问题" + (i++) + "【" + q + "】分类和标注【不一致】，类型为：" + questionType.name() + " 应该为：" + type + " 候选类型为：" + question.getCandidateQuestionTypes());
                    }
                } else {
                    //没有人工标注但能识别分类
                    yes.add("问题" + (i++) + "【" + q + "】的类型为：" + questionType.name());
                }
            } else {
				//不能识别分类
                //原因有两种：一是确实不能识别，而是识别了但是无法从候选类别中选择主类别
                if (question != null && question.getCandidateQuestionTypes() != null && question.getCandidateQuestionTypes().size() > 0) {
                    canNotSelect.add("问题" + (i++) + "【" + q + "】的类型为：NULL" + " 应该为：" + type + "，候选类型为：" + question.getCandidateQuestionTypes());
                } else {
                    no.add("问题" + (i++) + "【" + q + "】的类型为：null" + " 应该为：" + type);
                }
            }
        }
        LOG.info("");
        LOG.info("分类和标注一致的问题（" + right.size() + "）：");
        int a = 1;
        for (String item : right) {
            LOG.info((a++) + " " + item);
        }
        LOG.info("");
        LOG.info("分类和标注不一致的问题（" + wrong.size() + "）：");
        int b = 1;
        for (String item : wrong) {
            LOG.info((b++) + " " + item);
        }
        LOG.info("");
        LOG.info("没有人工标注但能识别分类（" + yes.size() + "）：");
        int c = 1;
        for (String item : yes) {
            LOG.info((c++) + " " + item);
        }
        LOG.info("");
        LOG.info("能识别分类，能不能选择主分类（" + canNotSelect.size() + "）：");
        int d = 1;
        for (String item : canNotSelect) {
            LOG.info((d++) + " " + item);
        }
        LOG.info("");
        LOG.info("不能识别分类（" + no.size() + "）：");
        int e = 1;
        for (String item : no) {
            LOG.info((e++) + " " + item);
        }

        int total = right.size() + wrong.size() + yes.size() + canNotSelect.size() + no.size();
        LOG.info("问题分类识别统计");
        LOG.info("问题总数: " + total);
        LOG.info("识别数: " + (total - no.size()));
        LOG.info("识别率: " + (double) (total - no.size()) / total * 100 + "%");
        LOG.info("未选择主分类数: " + canNotSelect.size());
        LOG.info("未选择主分类率: " + (double) canNotSelect.size() / total * 100 + "%");
        LOG.info("未识别数: " + no.size());
        LOG.info("未识别率: " + (double) no.size() / total * 100 + "%");
        LOG.info("人工标注数: " + human);
        LOG.info("人工标注率: " + (double) human / total * 100 + "%");
        LOG.info("识别准确数(人工标注): " + right.size());
        LOG.info("识别准确率(人工标注): " + (double) right.size() / human * 100 + "%");
        LOG.info("识别不准确数(人工标注): " + wrong.size());
        LOG.info("识别不准确率(人工标注): " + (double) wrong.size() / human * 100 + "%");

        map.put(patternMatchStrategy.getStrategyDes(), (double) right.size() / human * 100);
        map2.put(patternMatchStrategy.getStrategyDes(), right.size());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        //寻找最佳组合
        classify();
        //运行特定组合
        classify2();
        long cost = System.currentTimeMillis() - start;
        LOG.info("");
        LOG.info("执行时间：" + Tools.getTimeDes(cost));
    }

}