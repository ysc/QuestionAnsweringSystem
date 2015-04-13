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

package org.apdplat.qa.questiontypeanalysis.patternbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apdplat.qa.parser.WordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.international.pennchinese.ChineseTreebankLanguagePack;
import org.apdplat.word.segmentation.Word;

/**
 * 提取主谓宾
 *
 * @author 杨尚川
 */
public class MainPartExtracter {

    private static final Logger LOG = LoggerFactory.getLogger(MainPartExtracter.class);
    private static final LexicalizedParser LP;
    private static final GrammaticalStructureFactory GSF;

    static {
        //模型
        String models = "models/chineseFactored.ser.gz";
        LOG.info("模型：" + models);
        LP = LexicalizedParser.loadModel(models);
        //汉语
        TreebankLanguagePack tlp = new ChineseTreebankLanguagePack();
        GSF = tlp.grammaticalStructureFactory();
    }

    public String getQuestionMainPartNaturePattern(String question, String mainPart) {
        Map<String, String> map = new HashMap<>();
        //分词
        List<Word> words = WordParser.parse(question);
        for (Word word : words) {
            map.put(word.getText(), word.getPartOfSpeech().getPos());
        }
        StringBuilder patterns = new StringBuilder();
        String[] items = mainPart.split(" ");
        int i = 0;
        for (String item : items) {
            if ((i++) > 0) {
                patterns.append("/");
            }
            patterns.append(map.get(item));
        }
        return patterns.toString().trim();
    }

    public String getQuestionMainPartPattern(String question, String mainPart) {
        Map<String, String> map = new HashMap<>();
        //分词
        List<Word> words = WordParser.parse(question);
        for (Word word : words) {
            map.put(word.getText(), word.getPartOfSpeech().getPos());
        }
        StringBuilder patterns = new StringBuilder();
        String[] items = mainPart.split(" ");
        for (String item : items) {
            patterns.append(item).append("/").append(map.get(item)).append(" ");
        }
        return patterns.toString().trim();
    }

    /**
     * 获取句子的主谓宾
     *
     * @param question 问题
     * @return 问题结构
     */
    public QuestionStructure getMainPart(String question) {
        question = question.replace("\\s+", "");
        String questionWords = questionParse(question);
        return getMainPart(question, questionWords);
    }

    /**
     * 获取句子的主谓宾
     *
     * @param question 问题
     * @param questionWords 问题词序，相互之间以空格分割
     * @return 问题结构
     */
    public QuestionStructure getMainPart(String question, String questionWords) {
        List<edu.stanford.nlp.ling.Word> words = new ArrayList<>();
        String[] qw = questionWords.split("\\s+");
        for (String item : qw) {
            item = item.trim();
            if ("".equals(item)) {
                continue;
            }
            words.add(new edu.stanford.nlp.ling.Word(item));
        }
        return getMainPart(question, words);
    }

    /**
     * 获取句子的主谓宾
     *
     * @param question 问题
     * @param words HasWord列表
     * @return 问题结构
     */
    public QuestionStructure getMainPart(String question, List<edu.stanford.nlp.ling.Word> words) {
        QuestionStructure questionStructure = new QuestionStructure();
        questionStructure.setQuestion(question);

        Tree tree = LP.apply(words);
        LOG.info("句法树: ");
        tree.pennPrint();
        questionStructure.setTree(tree);

        GrammaticalStructure gs = GSF.newGrammaticalStructure(tree);
        if(gs == null){
            return null;
        }
        //获取依存关系
        Collection<TypedDependency> tdls = gs.typedDependenciesCCprocessed(true);
        questionStructure.setTdls(tdls);
        Map<String, String> map = new HashMap<>();
        String top = null;
        String root = null;
        LOG.info("句子依存关系：");
        //依存关系
        List<String> dependencies = new ArrayList<>();
        for (TypedDependency tdl : tdls) {
            String item = tdl.toString();
            dependencies.add(item);
            LOG.info("\t" + item);
            if (item.startsWith("top")) {
                top = item;
            }
            if (item.startsWith("root")) {
                root = item;
            }
            int start = item.indexOf("(");
            int end = item.lastIndexOf(")");
            item = item.substring(start + 1, end);
            String[] attr = item.split(",");
            String k = attr[0].trim();
            String v = attr[1].trim();
            String value = map.get(k);
            if (value == null) {
                map.put(k, v);
            } else {
                //有值
                value += ":";
                value += v;
                map.put(k, value);
            }
        }
        questionStructure.setDependencies(dependencies);

        String mainPartForTop = null;
        String mainPartForRoot = null;
        if (top != null) {
            mainPartForTop = topPattern(top, map);
        }
        if (root != null) {
            mainPartForRoot = rootPattern(root, map);
        }
        questionStructure.setMainPartForTop(mainPartForTop);
        questionStructure.setMainPartForRoot(mainPartForRoot);

        if (questionStructure.getMainPart() == null) {
            LOG.error("未能识别主谓宾：" + question);
        } else {
            LOG.info("主谓宾：" + questionStructure.getMainPart());
        }
        return questionStructure;
    }

    private String rootPattern(String pattern, Map<String, String> map) {
        //root识别模式
        int start = pattern.indexOf("(");
        int end = pattern.lastIndexOf(")");
        pattern = pattern.substring(start + 1, end);
        String[] attr = pattern.split(",");
        String v = attr[1].trim();
        String first = null;
        //临时谓语
        String second = v.split("-")[0];
        int secondIndex = Integer.parseInt(v.split("-")[1]);
        String third = "";

        String value = map.get(v);
        if(value == null){
            return null;
        }
        //判断是否是多值
        String[] values = value.split(":");
        if (values != null && values.length > 0) {
            if (values.length > 1) {
                first = values[0].split("-")[0];
                third = values[values.length - 1].split("-")[0];
            } else {
                String k = values[0];
                String t = k.split("-")[0];
                int tIndex = Integer.parseInt(k.split("-")[1]);
                if (secondIndex < tIndex) {
                    //谓语 调整为 主语
                    first = second;
                    second = t;
                } else {
                    first = t;
                }
                //没有宾语，再次查找
                String val = map.get(k);
                if (val != null) {
                    //找到宾语
                    String[] vals = val.split(":");
                    if (vals != null && vals.length > 0) {
                        third = vals[vals.length - 1].split("-")[0];
                    } else {
                        LOG.info("宾语获取失败: " + first + " " + second);
                    }
                } else {
                    //找不到宾语，降级为主谓结构
                    third = "";
                }
            }
        } else {
            LOG.error("root模式未找到主语和宾语, " + v + " 只有依赖：" + value);
        }
        //支持主谓宾和主谓结构
        if (first != null && second != null) {
            String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
            mainPart = mainPart.trim();
            return mainPart;
        }
        return null;
    }

    private String topPattern(String pattern, Map<String, String> map) {
        //top识别模式
        int start = pattern.indexOf("(");
        int end = pattern.lastIndexOf(")");
        pattern = pattern.substring(start + 1, end);
        String[] attr = pattern.split(",");
        String k = attr[0].trim();
        String v = attr[1].trim();
        String first = v.split("-")[0];
        String second = k.split("-")[0];
        String value = map.get(k);
        //判断是否是多值
        String[] values = value.split(":");
        String candidate;
        if (values != null && values.length > 0) {
            candidate = values[values.length - 1];
        } else {
            candidate = value;
        }
        String third = candidate.split("-")[0];
        String mainPart = first.trim() + " " + second.trim() + " " + third.trim();
        mainPart = mainPart.trim();
        return mainPart;
    }

    /**
     * 对问题进行分词 如：APDPlat的发起人是谁？ 分词之后返回：apdplat 的 发起人 是 谁 ？
     *
     * @param question 问题
     * @return 分词之后的用空格顺序连接的结果
     */
    private String questionParse(String question) {
        //分词
        LOG.info("对问题进行分词：" + question);
        List<Word> words = WordParser.parse(question);
        StringBuilder wordStr = new StringBuilder();
        for (Word word : words) {
            wordStr.append(word.getText()).append(" ");
        }
        LOG.info("分词结果为：" + wordStr.toString().trim());
        return wordStr.toString().trim();
    }

    /**
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
        MainPartExtracter mainPartExtracter = new MainPartExtracter();
        QuestionStructure qs = mainPartExtracter.getMainPart("《毛泽东选集》出版发行在哪一年");
        LOG.info(qs.getQuestion());
        LOG.info(qs.getMainPart());
        for (String d : qs.getDependencies()) {
            LOG.info("\t" + d);
        }
    }
}