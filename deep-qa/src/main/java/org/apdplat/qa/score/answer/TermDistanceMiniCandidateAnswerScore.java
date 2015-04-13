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
 * 对候选答案进行评分 【最小词距评分组件】 分值=原分值*（1/词距）
 *
 * 候选答案的距离=候选答案和每一个问题词的最小距离之和
 *
 * @author 杨尚川
 */
public class TermDistanceMiniCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(TermDistanceMiniCandidateAnswerScore.class);
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("最小词距评分开始");
        //1、对问题进行分词
        List<String> questionWords = question.getWords();
        //2、对证据进行分词
        List<Word> evidenceWords = Tools.getWords(evidence.getTitle() + "," + evidence.getSnippet());
        for (CandidateAnswer candidateAnswer : candidateAnswerCollection.getAllCandidateAnswer()) {
            //3、计算候选答案的词距
            int distance = 0;
            LOG.debug("计算候选答案 " + candidateAnswer.getAnswer() + " 的最小词距");
            //3.1 计算candidateAnswer的分布
            List<Integer> candidateAnswerOffes = new ArrayList<>();
            for (int i=0; i<evidenceWords.size(); i++) {
                Word evidenceWord = evidenceWords.get(i);
                if (evidenceWord.getText().equals(candidateAnswer.getAnswer())) {
                    candidateAnswerOffes.add(i);
                }
            }
            for (String questionTerm : questionWords) {
                //3.2 计算questionTerm的分布
                List<Integer> questionTermOffes = new ArrayList<>();
                for (int i=0; i<evidenceWords.size(); i++) {
                    Word evidenceWord = evidenceWords.get(i);
                    if (evidenceWord.getText().equals(questionTerm)) {
                        questionTermOffes.add(i);
                    }
                }
                //3.3 计算candidateAnswer和questionTerm的最小词距
                int miniDistance = Integer.MAX_VALUE;
                for (int candidateAnswerOffe : candidateAnswerOffes) {
                    for (int questionTermOffe : questionTermOffes) {
                        int abs = Math.abs(candidateAnswerOffe - questionTermOffe);
                        if (miniDistance > abs) {
                            miniDistance = abs;
                        }
                    }
                }
                if (miniDistance != Integer.MAX_VALUE) {
                    distance += miniDistance;
                }
            }
            double score = candidateAnswer.getScore() / distance;
            score *= scoreWeight.getTermDistanceMiniCandidateAnswerScoreWeight();
            LOG.debug("最小词距:" + distance + " ,分值：" + score);
            candidateAnswer.addScore(score);
        }
        LOG.debug("最小词距评分结束");
        LOG.debug("*************************");
    }
}