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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apdplat.qa.model.CandidateAnswer;
import org.apdplat.qa.model.CandidateAnswerCollection;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.qa.system.ScoreWeight;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对候选答案进行评分 【词频评分组件】 title中出现一次算TITLE_WEIGHT次 snippet中出现一次算1次 候选答案的分值 += 词频 *
 * 权重
 *
 * @author 杨尚川
 */
public class TermFrequencyCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(TermFrequencyCandidateAnswerScore.class);
    private static final int TITLE_WEIGHT = 2;
    private ScoreWeight scoreWeight = new ScoreWeight();
    private Question question;

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("词频评分开始");
        this.question = question;
        Map<String, Integer> map = getWordFrequency(evidence.getTitle(), evidence.getSnippet());
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            Integer wordFrequency = map.get(candidateAnswer.getAnswer());
            if (wordFrequency == null) {
                LOG.debug("没有找到候选答案【" + candidateAnswer.getAnswer() + "】的词频信息");
                continue;
            }
            double score = wordFrequency * scoreWeight.getTermFrequencyCandidateAnswerScoreWeight();
            LOG.debug(candidateAnswer.getAnswer() + " 分值：" + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("词频评分结束");
        LOG.debug("*************************");
    }

    /**
     * 统计文本中出现的候选答案及其词频
     *
     * @return
     */
    private Map<String, Integer> getWordFrequency(String title, String snippet) {
        List<String> titleNames = new ArrayList<>();
        List<String> snippetNames = new ArrayList<>();

        //处理title
        List<Word> words = WordParser.parse(title);
        for (Word word : words) {
            if (word.getPartOfSpeech().getPos().startsWith(question.getQuestionType().getPos())) {
                titleNames.add(word.getText());
            }
        }
        //处理snippet
        words = WordParser.parse(snippet);
        for (Word word : words) {
            if (word.getPartOfSpeech().getPos().startsWith(question.getQuestionType().getPos())) {
                snippetNames.add(word.getText());
            }
        }
	    //统计词频
        //title中出现一次算两次
        //snippet中出现一次算一次
        Map<String, Integer> map = new HashMap<>();
        for (String name : titleNames) {
            Integer count = map.get(name);
            if (count == null) {
                count = TITLE_WEIGHT;
            } else {
                count += TITLE_WEIGHT;
            }
            map.put(name, count);
        }
        for (String name : snippetNames) {
            Integer count = map.get(name);
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(name, count);
        }

        return map;
    }
}