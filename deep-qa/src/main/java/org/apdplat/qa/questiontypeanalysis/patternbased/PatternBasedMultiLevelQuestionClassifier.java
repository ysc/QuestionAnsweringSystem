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

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apdplat.qa.model.Question;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.qa.questiontypeanalysis.AbstractQuestionClassifier;
import org.apdplat.qa.questiontypeanalysis.QuestionClassifier;
import org.apdplat.qa.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用【模式匹配】的方法判断问题类型
 *
 * 模式匹配的5种方式：
 * 1、直接和【问题】匹配，如：APDPlat的发起人是谁？
 * 2、和问题分词及词性标注之后的【词和词性序列】进行匹配，如：apdplat/en 的/uj 发起人/n 是/v 谁/RW.RWPersonSingle ？/w
 * 3、和问题分词及词性标注之后的【词性序列】进行匹配，如：en/uj/n/v/RW.RWPersonSingle/w
 * 4、利用问题的【主谓宾 词 和 词性】进行匹配，如：发起人/n 是/v 谁/RW.RWPersonSingles
 * 5、利用问题的【主谓宾词性】进行匹配，如：n/v/RW.RWPersonSingle
 * 
 * @author 杨尚川
 */
public class PatternBasedMultiLevelQuestionClassifier extends AbstractQuestionClassifier {

    private static final Logger LOG = LoggerFactory.getLogger(PatternBasedMultiLevelQuestionClassifier.class);

    private static final Map<String, String> questionPatternCache = new HashMap<>();
    private static final Map<String, QuestionTypePattern> questionTypePatternCache = new HashMap<>();

    private static final MainPartExtracter mainPartExtracter = new MainPartExtracter();
    private final List<QuestionTypePatternFile> questionTypePatternFiles = new ArrayList<>();

    public PatternBasedMultiLevelQuestionClassifier(final PatternMatchStrategy patternMatchStrategy, PatternMatchResultSelector patternMatchResultSelector) {
        //设置模式匹配策略
        super.setPatternMatchStrategy(patternMatchStrategy);
        //设置模式匹配结果选择器
        super.setPatternMatchResultSelector(patternMatchResultSelector);
        //读取问题类型模式
        String appPath = Tools.getAppPath(PatternBasedMultiLevelQuestionClassifier.class);
        String path = appPath + "/questionTypePatterns/";
        LOG.info("模式文件目录：" + path);
        File dir = new File(path);
        if (dir.isDirectory()) {
            //过滤文件
            String[] files = dir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    //检查模式匹配策略是否启用该文件
                    if (patternMatchStrategy.enableQuestionTypePatternFile(name)) {
                        LOG.info("模式匹配策略启用文件：" + name);
                        return true;
                    } else {
                        LOG.info("模式匹配策略禁用文件：" + name);
                    }
                    return false;
                }

            });
            List<String> list = new ArrayList<>();
            list.addAll(Arrays.asList(files));
            Collections.sort(list);
            for (String item : list) {
                LOG.info("\t模式文件：" + item);
                String[] attr = item.split("_");
                QuestionTypePatternFile file = new QuestionTypePatternFile();
                file.setFile(item);
                if (attr != null && attr.length == 2) {
                    String match = attr[1].split("\\.")[0];
                    boolean multiMatch = Boolean.parseBoolean(match);
                    LOG.info("\t是否允许多匹配：" + multiMatch);
                    file.setMultiMatch(multiMatch);
                }
                questionTypePatternFiles.add(file);
            }
        } else {
            LOG.error("模式文件目录不存在：" + path);
        }
    }

    @Override
    public Question classify(Question question) {
        String questionStr = question.getQuestion();
        LOG.info("使用【模式匹配】的方法判断问题类型： " + questionStr);
        PatternMatchStrategy patternMatchStrategy = getPatternMatchStrategy();
        if (!patternMatchStrategy.validate()) {
            LOG.error("没有指定模式匹配策略：" + questionStr);
            return question;
        }
        List<String> questionPatterns = extractQuestionPatternFromQuestion(questionStr, patternMatchStrategy);
        if (questionPatterns.isEmpty()) {
            LOG.error("提取【问题模式】失败：" + questionStr);
            return question;
        }
        PatternMatchResult patternMatchResult = new PatternMatchResult();
        for (QuestionTypePatternFile qtpfile : questionTypePatternFiles) {
            String questionTypePatternFile = "/questionTypePatterns/" + qtpfile.getFile();
            LOG.info("");
            LOG.info("处理问题类型模式文件： " + questionTypePatternFile);
            QuestionTypePattern questionTypePattern = extractQuestionTypePattern(questionTypePatternFile);
            if (questionTypePattern != null) {
                List<PatternMatchResultItem> patternMatchResultItems = getPatternMatchResultItems(questionPatterns, questionTypePattern);
                if (patternMatchResultItems.isEmpty()) {
                    LOG.info("在问题类型模式文件中[未找到]匹配项： " + questionTypePatternFile);
                } else {
                    patternMatchResult.addPatternMatchResult(qtpfile, patternMatchResultItems);
                    LOG.info("在问题类型模式文件中[找到]匹配项： " + questionTypePatternFile);
                }
            } else {
                LOG.info("处理问题类型模式文件失败: " + questionTypePatternFile);
            }
        }
        List<PatternMatchResultItem> patternMatchResultItems = patternMatchResult.getAllPatternMatchResult();
        if (patternMatchResultItems.isEmpty()) {
            LOG.info("问题【" + questionStr + "】没有匹配到任何模式：");
            return question;
        }
        if (patternMatchResultItems.size() > 1) {
            LOG.info("问题【" + questionStr + "】匹配到多个模式：");
            int i = 1;
            for (PatternMatchResultItem item : patternMatchResultItems) {
                LOG.info("序号：" + i++);
                LOG.info("\t问题 : " + item.getOrigin());
                LOG.info("\t模式 : " + item.getPattern());
                LOG.info("\t分类 : " + item.getType());
            }
        }
        for (QuestionTypePatternFile file : patternMatchResult.getQuestionTypePatternFilesFromCompactToLoose()) {
            LOG.info("问题类型模式【" + file.getFile() + "】匹配情况，是否允许匹配多个类型：" + file.isMultiMatch());
            int i = 1;
            for (PatternMatchResultItem item : patternMatchResult.getPatternMatchResult(file)) {
                LOG.info("序号：" + i++);
                LOG.info("\t问题 : " + item.getOrigin());
                LOG.info("\t模式 : " + item.getPattern());
                LOG.info("\t分类 : " + item.getType());
            }
        }
        return getPatternMatchResultSelector().select(question, patternMatchResult);
    }

    private List<PatternMatchResultItem> getPatternMatchResultItems(List<String> questionPatterns, QuestionTypePattern questionTypePattern) {
        if (questionPatterns == null || questionPatterns.isEmpty()) {
            LOG.error("模式匹配之前至少指定一个【问题模式】");
            return null;
        }
        if (questionTypePattern == null || questionTypePattern.getPatterns().isEmpty()) {
            LOG.error("模式匹配之前至少指定一个【问题类型模式】");
            return null;
        }
        List<PatternMatchResultItem> patternMatchResultItems = new ArrayList<PatternMatchResultItem>();
        //处理所有的模式
        List<Pattern> patterns = questionTypePattern.getPatterns();
        List<String> types = questionTypePattern.getTypes();
        int len = patterns.size();
        for (int i = 0; i < len; i++) {
            Pattern pattern = patterns.get(i);
            for (String questionPattern : questionPatterns) {
                Matcher m = pattern.matcher(questionPattern);
                if (m.matches()) {
                    LOG.info("匹配成功: " + questionPattern + " : " + m.pattern() + " : " + types.get(i));
                    PatternMatchResultItem item = new PatternMatchResultItem();
                    item.setOrigin(questionPattern);
                    item.setPattern(pattern.pattern());
                    item.setType(types.get(i));
                    patternMatchResultItems.add(item);
                }
            }
        }
        return patternMatchResultItems;
    }

    /**
     * 从问题类型模式文件中提取问题类型模式
     *
     * @param questionTypePatternFile 问题类型模式文件
     * @return 问题类型模式
     */
    private QuestionTypePattern extractQuestionTypePattern(String questionTypePatternFile) {
        QuestionTypePattern value = questionTypePatternCache.get(questionTypePatternFile);
        if (value != null) {
            return value;
        }
        //统计不重复的【问题类型】和【问题模式】
        Set<String> questionTypesForSet = new HashSet<>();
        Set<Pattern> questionPatternsForSet = new HashSet<>();
        //顺序存储，【问题类型】和【问题模式】一一对应
        List<String> types = new ArrayList<>();
        List<Pattern> patterns = new ArrayList<>();
        BufferedReader reader = null;
        try {
            InputStream in = PatternBasedMultiLevelQuestionClassifier.class.getResourceAsStream(questionTypePatternFile);
            reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
                LOG.debug("处理模式" + (i++) + "：" + line);
                String[] tokens = line.split("\\s+", 3);
                types.add(tokens[0]);
                questionTypesForSet.add(tokens[0]);
                patterns.add(Pattern.compile(tokens[1], Pattern.CASE_INSENSITIVE));
                questionPatternsForSet.add(Pattern.compile(tokens[1], Pattern.CASE_INSENSITIVE));
            }
        } catch (Exception e) {
            LOG.error("问题模式文件读取失败: " + questionTypePatternFile);
            LOG.debug("问题模式文件读取失败: " + questionTypePatternFile, e);
            return null;
        } finally {
            try {
                reader.close();
            } catch (Exception e) {
                LOG.error("问题模式文件关闭失败: " + questionTypePatternFile);
                LOG.debug("问题模式文件关闭失败: " + questionTypePatternFile, e);
                return null;
            }
        }
        if (patterns.isEmpty() || patterns.size() != types.size()) {
            LOG.info("问题模式文件为空：" + questionTypePatternFile);
            return null;
        }
        LOG.debug("问题模式文件加载成功");
        LOG.debug("所有问题类型：");
        int i = 1;
        for (String type : questionTypesForSet) {
            LOG.debug("类型" + (i++) + ": " + type);
        }
        LOG.debug("所有问题模式：");
        i = 1;
        for (Pattern pattern : questionPatternsForSet) {
            LOG.debug("模式" + (i++) + ": " + pattern.pattern());
        }
        QuestionTypePattern questionTypePattern = new QuestionTypePattern();
        questionTypePattern.setPatterns(patterns);
        questionTypePattern.setTypes(types);

        questionTypePatternCache.put(questionTypePatternFile, questionTypePattern);

        return questionTypePattern;
    }

    /**
     * 从问题中提取问题模式
     *
     * @param question 问题
     * @return 问题模式
     */
    private List<String> extractQuestionPatternFromQuestion(String question, PatternMatchStrategy patternMatchStrategy) {
        List<String> questionPatterns = new ArrayList<>();
        //去除问题中的前后空白字符
        question = question.trim();
        LOG.info("问题：" + question);
        if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.Question)) {
            questionPatterns.add(question);
        }
        if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.TermWithNatures)
                || patternMatchStrategy.enableQuestionPattern(QuestionPattern.Natures)) {
            String termWithNature = questionPatternCache.get(question + "termWithNature");
            String nature = questionPatternCache.get(question + "nature");

            if (termWithNature == null || nature == null) {
                //提取问题词性以便和模式进行匹配
                List<Word> words = WordParser.parse(question);
                //APDPlat的发起人是谁？
                //apdplat/en 的/uj 发起人/n 是/v 谁/RW.RWPersonSingle ？/w 
                StringBuilder termWithNatureStrs = new StringBuilder();
                //APDPlat的发起人是谁？
                //en/uj/n/v/RW.RWPersonSingle/w		
                StringBuilder natureStrs = new StringBuilder();
                int i = 0;
                for (Word word : words) {
                    termWithNatureStrs.append(word.getText()).append("/").append(word.getPartOfSpeech().getPos()).append(" ");
                    if ((i++) > 0) {
                        natureStrs.append("/");
                    }
                    natureStrs.append(word.getPartOfSpeech().getPos());
                }
                termWithNature = termWithNatureStrs.toString();
                nature = natureStrs.toString();
                questionPatternCache.put(question + "termWithNature", termWithNature);
                questionPatternCache.put(question + "nature", nature);
            }

            if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.TermWithNatures)) {
                questionPatterns.add(termWithNature);
                LOG.info("词和词性序列：" + termWithNature);
            }
            if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.Natures)) {
                questionPatterns.add(nature);
                LOG.info("词性序列：" + nature);
            }
        }
        if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartPattern)
                || patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartNaturePattern)) {
            String mainPart = questionPatternCache.get(question + "mainPart");
            if (mainPart == null) {
                //提取主谓宾及词性以便和模式进行匹配
                QuestionStructure questionStructure = mainPartExtracter.getMainPart(question);
                if(questionStructure != null){
                    mainPart = questionStructure.getMainPart();
                    questionPatternCache.put(question + "mainPart", mainPart);
                }
            }
            if (mainPart != null) {
                if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartPattern)) {
                    String questionMainPartPattern = questionPatternCache.get(question + "questionMainPartPattern");
                    if (questionMainPartPattern == null) {
                        questionMainPartPattern = mainPartExtracter.getQuestionMainPartPattern(question, mainPart);
                        questionPatternCache.put(question + "questionMainPartPattern", questionMainPartPattern);
                    }
                    questionPatterns.add(questionMainPartPattern);
                    LOG.info("主谓宾词和词性序列：" + questionMainPartPattern);
                }
                if (patternMatchStrategy.enableQuestionPattern(QuestionPattern.MainPartNaturePattern)) {
                    String questionMainPartNaturePattern = questionPatternCache.get(question + "questionMainPartNaturePattern");
                    if (questionMainPartNaturePattern == null) {
                        questionMainPartNaturePattern = mainPartExtracter.getQuestionMainPartNaturePattern(question, mainPart);
                        questionPatternCache.put(question + "mainPartPattern", questionMainPartNaturePattern);
                    }
                    questionPatterns.add(questionMainPartNaturePattern);
                    LOG.info("主谓宾词性序列：" + questionMainPartNaturePattern);
                }
            }
        }
        return questionPatterns;
    }

    /**
     * 【问题类型模式】 -- 由用户指定的【问题类型】和【问题模式】的关系 如： 1、Person->Multi2
     * .*(V.).*(RW.RWOrdinaryMulti).*(N.Person).* 2、PersonOfDis->Multi1
     * .*(RW.RWOrdinaryMulti)/(N.Person).* 3、Person->Multi1
     * .*(RW.RWPersonMulti).* 前部分是【问题类型】，后部分是【问题模式】
     *
     * @author ysc
     *
     */
    class QuestionTypePattern {

        //顺序存储所有【问题类型】
        private List<String> types = new ArrayList<>();
        //顺序存储所有【问题模式】
        private List<Pattern> patterns = new ArrayList<>();

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }

        public List<Pattern> getPatterns() {
            return patterns;
        }

        public void setPatterns(List<Pattern> patterns) {
            this.patterns = patterns;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");

        PatternMatchResultSelector patternMatchResultSelector = new DefaultPatternMatchResultSelector();

        QuestionClassifier questionClassifier = new PatternBasedMultiLevelQuestionClassifier(patternMatchStrategy, patternMatchResultSelector);

        Question question = questionClassifier.classify("Who is the author of apdplat?");

        if (question != null) {
            LOG.info("问题【" + question.getQuestion() + "】的类型为：" + question.getQuestionType() + " 候选类型为：" + question.getCandidateQuestionTypes());
        }
    }
}