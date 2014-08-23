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

import org.apdplat.qa.model.CandidateAnswerCollection;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.ScoreWeight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组合候选答案评分组件
 *
 * @author 杨尚川
 */
public class CombinationCandidateAnswerScore implements CandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(CombinationCandidateAnswerScore.class);
    private final List<CandidateAnswerScore> candidateAnswerScores = new ArrayList<>();
    private ScoreWeight scoreWeight = new ScoreWeight();

    @Override
    public void setScoreWeight(ScoreWeight scoreWeight) {
        this.scoreWeight = scoreWeight;
    }

    public void addCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore) {
        candidateAnswerScores.add(candidateAnswerScore);
    }

    public void removeCandidateAnswerScore(CandidateAnswerScore candidateAnswerScore) {
        candidateAnswerScores.remove(candidateAnswerScore);
    }

    public void clear() {
        candidateAnswerScores.clear();
    }

    @Override
    public void score(Question question, Evidence evidence, CandidateAnswerCollection candidateAnswerCollection) {
        for (CandidateAnswerScore candidateAnswerScore : candidateAnswerScores) {
            candidateAnswerScore.score(question, evidence, candidateAnswerCollection);
        }
    }
}