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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对候选答案进行评分 【宽松文本对齐评分组件】 
 * 忽略问题中长度为1的词
 * 匹配更多的内容
 *
 * @author 杨尚川
 */
public class MoreTextualAlignmentCandidateAnswerScore extends TextualAlignmentCandidateAnswerScore {

    private static final Logger LOG = LoggerFactory.getLogger(MoreTextualAlignmentCandidateAnswerScore.class);

    @Override
    public void score(Question question, Evidence evidence,
            CandidateAnswerCollection candidateAnswerCollection) {
        LOG.debug("*************************");
        LOG.debug("宽松文本对齐评分开始");
        super.score(question, evidence, candidateAnswerCollection);
        LOG.debug("宽松文本对齐评分结束");
        LOG.debug("*************************");
    }

    @Override
    protected List<String> getQuestionTerms(Question question) {
        List<String> list = question.getWords();
        List<String> result = new ArrayList<>();
        for (String item : list) {
            if (item.length() > 1) {
                result.add(item);
            }
        }
        return result;
    }
}