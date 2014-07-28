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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 候选答案集合 包含多个候选答案
 *
 * @author 杨尚川
 */
public class CandidateAnswerCollection {

    private static final Logger LOG = LoggerFactory.getLogger(CandidateAnswerCollection.class);
    private final List<CandidateAnswer> candidateAnswers = new ArrayList<>();

    public boolean isEmpty() {
        return candidateAnswers.isEmpty();
    }

    /**
     * 获取所有候选答案
     *
     * @return
     */
    public List<CandidateAnswer> getAllCandidateAnswer() {
        //按CandidateAnswer的分值排序
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        return candidateAnswers;
    }

    public void showAll() {
        for (CandidateAnswer candidateAnswer : getAllCandidateAnswer()) {
            LOG.debug(candidateAnswer.getAnswer() + " " + candidateAnswer.getScore());
        }
    }

    public void showTopN(int topN) {
        for (CandidateAnswer candidateAnswer : getTopNCandidateAnswer(topN)) {
            LOG.debug(candidateAnswer.getAnswer() + " " + candidateAnswer.getScore());
        }
    }

    public List<CandidateAnswer> getTopNCandidateAnswer(int topN) {
        //按CandidateAnswer的分值排序，返回topN
        List<CandidateAnswer> result = new ArrayList<>();
        Collections.sort(candidateAnswers);
        Collections.reverse(candidateAnswers);
        int len = candidateAnswers.size();
        if (topN > len) {
            topN = len;
        }
        for (int i = 0; i < candidateAnswers.size(); i++) {
            result.add(candidateAnswers.get(i));
        }

        return result;
    }

    public void addAnswer(CandidateAnswer candidateAnswer) {
        if (!candidateAnswers.contains(candidateAnswer)) {
            candidateAnswers.add(candidateAnswer);
        }
    }

    public void removeAnswer(CandidateAnswer candidateAnswer) {
        candidateAnswers.remove(candidateAnswer);
    }
}