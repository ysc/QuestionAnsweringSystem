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

package org.apdplat.qa.score.answer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apdplat.qa.model.CandidateAnswer;
import org.apdplat.qa.model.CandidateAnswerCollection;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.ScoreWeight;
import org.apdplat.qa.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对候选答案进行评分 【文本对齐评分组件】
 *
 * @author 杨尚川
 */
public class TextualAlignmentCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(TextualAlignmentCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    protected List<String> getQuestionTerms(Question question) {
        return question.getWords();
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("文本对齐评分开始");
        //1、对问题进行分词
        List<String> questionTerms = getQuestionTerms(question);
        int questionTermsSize = questionTerms.size();
        //2、获取证据文本
        String evidenceText = evidence.getTitle() + evidence.getSnippet();
        //将每一个候选答案都放到问题的每一个位置，查找在证据中是否有匹配文本
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            //3、计算候选答案的文本对齐
            LOG.debug("计算候选答案 " + candidateAnswer.getAnswer() + " 的文本对齐");
            for (int i = 0; i < questionTermsSize; i++) {
                StringBuilder textualAlignment = new StringBuilder();
                for (int j = 0; j < questionTermsSize; j++) {
                    if (i == j) {
                        textualAlignment.append(candidateAnswer.getAnswer());
                    } else {
                        textualAlignment.append(questionTerms.get(j));
                    }
                }
                String textualAlignmentPattern = textualAlignment.toString();
                if (question.getQuestion().trim().equals(textualAlignmentPattern.trim())) {
                    LOG.debug("文本对齐模式和原问题相同，忽略：" + textualAlignmentPattern);
                    continue;
                }
                //4、演化为多个模式，支持模糊匹配
                List<Word> textualAlignmentPatternTerms = Tools.getWords(textualAlignmentPattern);
                List<String> patterns = new ArrayList<>();
                patterns.add(textualAlignmentPattern);
                StringBuilder str = new StringBuilder();
                int len = textualAlignmentPatternTerms.size();
                for (int t = 0; t < len; t++) {
                    str.append(textualAlignmentPatternTerms.get(t).getText());
                    if (t < len - 1) {
                        str.append(".{0,5}");
                    }
                }
                patterns.add(str.toString());
                //5、判断文本是否对齐
                int count = 0;
                int length = 0;
                for (String pattern : patterns) {
                    //LOG.debug("模式："+pattern);
                    Pattern p = Pattern.compile(pattern);
                    Matcher matcher = p.matcher(evidenceText);
                    while (matcher.find()) {
                        String text = matcher.group();
                        LOG.debug("对齐的文本：" + text);
                        LOG.debug("对齐的模式：" + pattern);
                        count++;
                        length += text.length();
                    }
                }
                //6、打分
                if (count > 0) {
                    double avgLen = (double) length / count;
					//问题长度questionLen为正因子
                    //匹配长度avgLen为负因子
                    int questionLen = question.getQuestion().length();
                    double score = questionLen / avgLen;
                    score *= scoreWeight.getTextualAlignmentCandidateAnswerScoreWeight();
                    candidateAnswer.addScore(score);
                    LOG.debug("文本对齐 " + count + " 次,分值：" + score);
                }
            }
        }
        LOG.debug("文本对齐评分结束");
        LOG.debug("*************************");
    }
}