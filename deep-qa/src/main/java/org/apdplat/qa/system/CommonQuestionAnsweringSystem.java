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

package org.apdplat.qa.system;

import java.util.ArrayList;
import java.util.List;

import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.datasource.FileDataSource;
import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.questiontypeanalysis.patternbased.DefaultPatternMatchResultSelector;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternBasedMultiLevelQuestionClassifier;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchResultSelector;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchStrategy;
import org.apdplat.qa.questiontypeanalysis.QuestionClassifier;
import org.apdplat.qa.questiontypeanalysis.patternbased.QuestionPattern;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用问答系统实现
 *
 * @author 杨尚川
 */
public class CommonQuestionAnsweringSystem extends QuestionAnsweringSystemImpl {

    private static final Logger LOG = LoggerFactory.getLogger(CommonQuestionAnsweringSystem.class);

    public CommonQuestionAnsweringSystem() {
        LOG.info("开始构造问答系统");
        //1、默认评分组件权重
        ScoreWeight scoreWeight = new ScoreWeight();
        //2、问答系统默认文件数据源		
        List<String> files = new ArrayList<>();
        files.add(FilesConfig.personNameMaterial);
        files.add(FilesConfig.locationNameMaterial);
        files.add(FilesConfig.organizationNameMaterial);
        files.add(FilesConfig.numberMaterial);
        files.add(FilesConfig.timeMaterial);
        DataSource dataSource = new FileDataSource(files);

        super.setDataSource(dataSource);

        //3、候选答案提取器(不可以同时使用多个提取器)
        CandidateAnswerSelect candidateAnswerSelect = new CommonCandidateAnswerSelect();

        super.setCandidateAnswerSelect(candidateAnswerSelect);

        //4、证据评分组件(可以同时使用多个组件)
        //***********************
        //4.1、TermMatch评分组件
        EvidenceScore termMatchEvidenceScore = new TermMatchEvidenceScore();
        termMatchEvidenceScore.setScoreWeight(scoreWeight);
        //4.2、二元模型评分组件
        EvidenceScore bigramEvidenceScore = new BigramEvidenceScore();
        bigramEvidenceScore.setScoreWeight(scoreWeight);
        //4.3、跳跃二元模型评分组件
        EvidenceScore skipBigramEvidenceScore = new SkipBigramEvidenceScore();
        skipBigramEvidenceScore.setScoreWeight(scoreWeight);
        //4.4、组合证据评分组件
        CombinationEvidenceScore combinationEvidenceScore = new CombinationEvidenceScore();
        combinationEvidenceScore.addEvidenceScore(termMatchEvidenceScore);
        combinationEvidenceScore.addEvidenceScore(bigramEvidenceScore);
        combinationEvidenceScore.addEvidenceScore(skipBigramEvidenceScore);

        super.setEvidenceScore(combinationEvidenceScore);

        //5、候选答案评分组件(可以同时使用多个组件)
        //***********************
        //5.1、词频评分组件
        CandidateAnswerScore termFrequencyCandidateAnswerScore = new TermFrequencyCandidateAnswerScore();
        termFrequencyCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.2、词距评分组件
        CandidateAnswerScore termDistanceCandidateAnswerScore = new TermDistanceCandidateAnswerScore();
        termDistanceCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.3、词距评分组件(只取候选词和问题词的最短距离)
        CandidateAnswerScore termDistanceMiniCandidateAnswerScore = new TermDistanceMiniCandidateAnswerScore();
        termDistanceMiniCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.4、文本对齐评分组件
        CandidateAnswerScore textualAlignmentCandidateAnswerScore = new TextualAlignmentCandidateAnswerScore();
        textualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.5、文本对齐评分组件
        CandidateAnswerScore moreTextualAlignmentCandidateAnswerScore = new MoreTextualAlignmentCandidateAnswerScore();
        moreTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.6、回带文本对齐评分组件
        CandidateAnswerScore rewindTextualAlignmentCandidateAnswerScore = new RewindTextualAlignmentCandidateAnswerScore();
        rewindTextualAlignmentCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.7、热词评分组件
        CandidateAnswerScore hotCandidateAnswerScore = new HotCandidateAnswerScore();
        hotCandidateAnswerScore.setScoreWeight(scoreWeight);
        //5.8、组合候选答案评分组件
        CombinationCandidateAnswerScore combinationCandidateAnswerScore = new CombinationCandidateAnswerScore();
        combinationCandidateAnswerScore.addCandidateAnswerScore(termFrequencyCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(termDistanceMiniCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(textualAlignmentCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(moreTextualAlignmentCandidateAnswerScore);
        //combinationCandidateAnswerScore.addCandidateAnswerScore(rewindTextualAlignmentCandidateAnswerScore);
        combinationCandidateAnswerScore.addCandidateAnswerScore(hotCandidateAnswerScore);

        super.setCandidateAnswerScore(combinationCandidateAnswerScore);

        //6、问题分类器
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
        super.setQuestionClassifier(questionClassifier);

        LOG.info("问答系统构造完成");
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        questionAnsweringSystem.answerQuestions();
    }
}