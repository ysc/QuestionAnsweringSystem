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

package org.apdplat.qa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apdplat.qa.filter.CandidateAnswerCanNotInQustionFilter;
import org.apdplat.qa.filter.CandidateAnswerFilter;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.qa.util.Tools;
import org.apdplat.word.segmentation.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 问题有多个证据 证据用于提取候选答案
 *
 * @author 杨尚川
 */
public class Question {

    private static final Logger LOG = LoggerFactory.getLogger(Question.class);
    private String question;
    private final List<Evidence> evidences = new ArrayList<>();

    private QuestionType questionType = QuestionType.PERSON_NAME;
    private String expectAnswer;
    private CandidateAnswerFilter candidateAnswerFilter = new CandidateAnswerCanNotInQustionFilter();

    //候选的问题类型，对问题进行分类的时候，可能会有多个类型
    private final Set<QuestionType> candidateQuestionTypes = new HashSet<>();

    public void clearCandidateQuestionType() {
        candidateQuestionTypes.clear();
    }

    public void addCandidateQuestionType(QuestionType questionType) {
        candidateQuestionTypes.add(questionType);
    }

    public void removeCandidateQuestionType(QuestionType questionType) {
        candidateQuestionTypes.remove(questionType);
    }

    public Set<QuestionType> getCandidateQuestionTypes() {
        return candidateQuestionTypes;
    }

    public Map.Entry<String, Integer> getHot() {
        List<String> questionWords = getWords();
        Map<String, Integer> map = new HashMap<>();
        List<Word> words = WordParser.parse(getText());
        for (Word word : words) {
            Integer count = map.get(word.getText());
            if (count == null) {
                count = 1;
            } else {
                count++;
            }
            map.put(word.getText(), count);
        }
        Map<String, Integer> questionMap = new HashMap<>();
        for (String questionWord : questionWords) {
            Integer count = map.get(questionWord);
            if (questionWord.length() > 1 && count != null) {
                questionMap.put(questionWord, count);
                LOG.debug("问题热词统计: " + questionWord + " " + map.get(questionWord));
            }
        }
        List<Map.Entry<String, Integer>> list = Tools.sortByIntegerValue(questionMap);
        Collections.reverse(list);
        if (!list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }

    public int getExpectAnswerRank() {
        if (expectAnswer == null) {
            LOG.info("未指定期望的答案");
            return -2;
        }
        List<CandidateAnswer> candidateAnswers = this.getAllCandidateAnswer();
        int len = candidateAnswers.size();
        for (int i = 0; i < len; i++) {
            CandidateAnswer candidateAnswer = candidateAnswers.get(i);
            if (expectAnswer.trim().equals(candidateAnswer.getAnswer().trim())) {
                return (i + 1);
            }
        }
        return -1;
    }

    /**
     * 对问题进行分词
     *
     * @return 分词结果
     */
    public List<String> getWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(question.replace("?", "").replace("？", ""));
        for (Word word : words) {
            result.add(word.getText());
        }
        return result;
    }

    /**
     * 获取所有候选答案
     *
     * @return 所有候选答案
     */
    public List<CandidateAnswer> getAllCandidateAnswer() {
        Map<String, Double> map = new HashMap<>();
        for (Evidence evidence : evidences) {
            for (CandidateAnswer candidateAnswer : evidence.getCandidateAnswerCollection().getAllCandidateAnswer()) {
                Double score = map.get(candidateAnswer.getAnswer());
                //候选答案的分值和证据的分值 用于计算最终的候选答案分值
                Double candidateAnswerFinalScore = candidateAnswer.getScore() + evidence.getScore();
                if (score == null) {
                    score = candidateAnswerFinalScore;
                } else {
                    score += candidateAnswerFinalScore;
                }
                map.put(candidateAnswer.getAnswer(), score);
            }
        }

        //组装候选答案
        List<CandidateAnswer> candidateAnswers = new ArrayList<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            String answer = entry.getKey();
            Double score = entry.getValue();
            if (answer != null && score != null && score > 0 && score < Double.MAX_VALUE) {
                CandidateAnswer candidateAnswer = new CandidateAnswer();
                candidateAnswer.setAnswer(answer);
                candidateAnswer.setScore(score);
                candidateAnswers.add(candidateAnswer);
            }
        }
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        //过滤候选答案
        if (candidateAnswerFilter != null) {
            candidateAnswerFilter.filter(this, candidateAnswers);
        }
        //分值归一化
        if (candidateAnswers.size() > 0) {
            double baseScore = candidateAnswers.get(0).getScore();
            for (CandidateAnswer candidateAnswer : candidateAnswers) {
                double score = candidateAnswer.getScore() / baseScore;
                candidateAnswer.setScore(score);
            }
        }

        return candidateAnswers;
    }

    /**
     * 获取topN候选答案
     *
     * @param topN
     * @return topN候选答案
     */
    public List<CandidateAnswer> getTopNCandidateAnswer(int topN) {
        List<CandidateAnswer> topNcandidateAnswers = new ArrayList<>();
        List<CandidateAnswer> allCandidateAnswers = getAllCandidateAnswer();
        if (topN > allCandidateAnswers.size()) {
            topN = allCandidateAnswers.size();
        }
        for (int i = 0; i < topN; i++) {
            topNcandidateAnswers.add(allCandidateAnswers.get(i));
        }
        return topNcandidateAnswers;
    }

    public String getText() {
        StringBuilder text = new StringBuilder();
        for (Evidence evidence : evidences) {
            text.append(evidence.getTitle()).append(evidence.getSnippet());
        }
        return text.toString();
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Evidence> getEvidences() {
        return this.evidences;
    }

    public void addEvidences(List<Evidence> evidences) {
        this.evidences.addAll(evidences);
    }

    public void addEvidence(Evidence evidence) {
        this.evidences.add(evidence);
    }

    public void removeEvidence(Evidence evidence) {
        this.evidences.remove(evidence);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("?. ").append(question).append("\n\n");
        for (Evidence evidence : this.evidences) {
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("Snippet: ").append(evidence.getSnippet()).append("\n\n");
        }

        return result.toString();
    }

    public String toString(int index) {
        StringBuilder result = new StringBuilder();
        result.append("?").append(index).append(". ").append(question).append("\n\n");
        for (Evidence evidence : this.evidences) {
            result.append("Title: ").append(evidence.getTitle()).append("\n");
            result.append("Snippet: ").append(evidence.getSnippet()).append("\n\n");
        }

        return result.toString();
    }

    public String getExpectAnswer() {
        return expectAnswer;
    }

    public void setExpectAnswer(String expectAnswer) {
        this.expectAnswer = expectAnswer;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public CandidateAnswerFilter getCandidateAnswerFilter() {
        return candidateAnswerFilter;
    }

    public void setCandidateAnswerFilter(CandidateAnswerFilter candidateAnswerFilter) {
        this.candidateAnswerFilter = candidateAnswerFilter;
    }
}