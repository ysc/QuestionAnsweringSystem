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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apdplat.qa.score.answer.CandidateAnswerScore;
import org.apdplat.qa.score.answer.CombinationCandidateAnswerScore;
import org.apdplat.qa.score.answer.HotCandidateAnswerScore;
import org.apdplat.qa.score.answer.MoreTextualAlignmentCandidateAnswerScore;
import org.apdplat.qa.score.answer.RewindTextualAlignmentCandidateAnswerScore;
import org.apdplat.qa.score.answer.TermDistanceCandidateAnswerScore;
import org.apdplat.qa.score.answer.TermDistanceMiniCandidateAnswerScore;
import org.apdplat.qa.score.answer.TermFrequencyCandidateAnswerScore;
import org.apdplat.qa.score.answer.TextualAlignmentCandidateAnswerScore;
import org.apdplat.qa.score.evidence.BigramEvidenceScore;
import org.apdplat.qa.score.evidence.CombinationEvidenceScore;
import org.apdplat.qa.score.evidence.EvidenceScore;
import org.apdplat.qa.score.evidence.SkipBigramEvidenceScore;
import org.apdplat.qa.score.evidence.TermMatchEvidenceScore;
import org.apdplat.qa.select.CandidateAnswerSelect;
import org.apdplat.qa.select.CommonCandidateAnswerSelect;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.apdplat.qa.system.ScoreWeight;
import org.apdplat.qa.util.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用于寻找最佳评分组件权重
 *
 * @author 杨尚川
 */
public class BestScoreWeightSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(BestScoreWeightSearcher.class);

    public void search() {
        Map<String, Double> map = new HashMap<>();
        for (int i = 1; i <= 10; i++) {
            //1、生成随机的评分组件权重
            StringBuilder par = new StringBuilder();
            ScoreWeight scoreWeight = new ScoreWeight();
            int random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setBigramEvidenceScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setSkipBigramEvidenceScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setTermMatchEvidenceScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setHotCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setMoreTextualAlignmentCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setRewindTextualAlignmentCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setTermDistanceCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setTermDistanceMiniCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setTermFrequencyCandidateAnswerScoreWeight(random);
            random = new Random().nextInt(10) + 1;
            par.append(random).append(" ");
            scoreWeight.setTextualAlignmentCandidateAnswerScoreWeight(random);

            //2、候选答案提取器(不可以同时使用多个提取器)
            CandidateAnswerSelect candidateAnswerSelect = new CommonCandidateAnswerSelect();

			//3、证据评分组件(可以同时使用多个组件)
            //***********************
            //3.1、TermMatch评分组件
            EvidenceScore termMatchEvidenceScore = new TermMatchEvidenceScore();
            termMatchEvidenceScore.setScoreWeight(scoreWeight);
            //3.2、二元模型评分组件
            EvidenceScore bigramEvidenceScore = new BigramEvidenceScore();
            bigramEvidenceScore.setScoreWeight(scoreWeight);
            //3.3、跳跃二元模型评分组件
            EvidenceScore skipBigramEvidenceScore = new SkipBigramEvidenceScore();
            skipBigramEvidenceScore.setScoreWeight(scoreWeight);
            //3.4、组合证据评分组件
            CombinationEvidenceScore combinationEvidenceScore = new CombinationEvidenceScore();
            combinationEvidenceScore.addEvidenceScore(termMatchEvidenceScore);
            combinationEvidenceScore.addEvidenceScore(bigramEvidenceScore);
            combinationEvidenceScore.addEvidenceScore(skipBigramEvidenceScore);

			//4、候选答案评分组件(可以同时使用多个组件)
            //***********************
            //4.1、词频评分组件
            CandidateAnswerScore termFrequencyCandidateAnswerScore = new TermFrequencyCandidateAnswerScore();
            termFrequencyCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.2、词距评分组件
            CandidateAnswerScore termDistanceCandidateAnswerScore = new TermDistanceCandidateAnswerScore();
            termDistanceCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.3、词距评分组件(只取候选词和问题词的最短距离)
            CandidateAnswerScore termDistanceMiniCandidateAnswerScore = new TermDistanceMiniCandidateAnswerScore();
            termDistanceMiniCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.4、文本对齐评分组件
            CandidateAnswerScore textualAlignmentCandidateAnswerScore = new TextualAlignmentCandidateAnswerScore();
            textualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.5、文本对齐评分组件
            CandidateAnswerScore moreTextualAlignmentCandidateAnswerScore = new MoreTextualAlignmentCandidateAnswerScore();
            moreTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.6、回带文本对齐评分组件
            CandidateAnswerScore rewindTextualAlignmentCandidateAnswerScore = new RewindTextualAlignmentCandidateAnswerScore();
            rewindTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.7、热词评分组件
            CandidateAnswerScore hotCandidateAnswerScore = new HotCandidateAnswerScore();
            hotCandidateAnswerScore.setScoreWeight(scoreWeight);
            //4.8、组合候选答案评分组件
            CombinationCandidateAnswerScore combinationCandidateAnswerScore = new CombinationCandidateAnswerScore();
            combinationCandidateAnswerScore.addCandidateAnswerScore(termFrequencyCandidateAnswerScore);
            combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceCandidateAnswerScore);
            combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceMiniCandidateAnswerScore);
            combinationCandidateAnswerScore.addCandidateAnswerScore(textualAlignmentCandidateAnswerScore);
            combinationCandidateAnswerScore.addCandidateAnswerScore(moreTextualAlignmentCandidateAnswerScore);
            //combinationCandidateAnswerScore.addCandidateAnswerScore(rewindTextualAlignmentCandidateAnswerScore);
            combinationCandidateAnswerScore.addCandidateAnswerScore(hotCandidateAnswerScore);

            //组装问答系统
            QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
            //1、指定问答系统的 候选答案提取器
            questionAnsweringSystem.setCandidateAnswerSelect(candidateAnswerSelect);
            //2、指定问答系统的 证据评分组件
            questionAnsweringSystem.setEvidenceScore(combinationEvidenceScore);
            //3、指定问答系统的 候选答案评分组件
            questionAnsweringSystem.setCandidateAnswerScore(combinationCandidateAnswerScore);
            //回答问题
            questionAnsweringSystem.answerQuestions();
            //获取MRR值
            double MRR = questionAnsweringSystem.getMRR();

            par.append(":").append(questionAnsweringSystem.getPerfectCount()).append(":").append(questionAnsweringSystem.getNotPerfectCount()).append(":").append(questionAnsweringSystem.getWrongCount());
            map.put(par.toString(), MRR);
        }
        int i = 1;
        List<Map.Entry<String, Double>> entrys = Tools.sortByDoubleValue(map);
        for (Map.Entry<String, Double> entry : entrys) {
            LOG.info((i++) + "、" + entry.getKey() + " " + entry.getValue());
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new BestScoreWeightSearcher().search();
    }
}