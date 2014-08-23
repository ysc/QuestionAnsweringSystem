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

package org.apdplat.qa.questiontypeanalysis;

import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchResultSelector;
import org.apdplat.qa.questiontypeanalysis.patternbased.PatternMatchStrategy;
import org.apdplat.qa.model.Question;

/**
 * 
 * @author 杨尚川
 */
public abstract class AbstractQuestionClassifier implements QuestionClassifier {

    private PatternMatchStrategy patternMatchStrategy;
    private PatternMatchResultSelector patternMatchResultSelector;

    @Override
    public Question classify(String question) {
        Question q = new Question();
        q.setQuestion(question);
        return classify(q);
    }

    @Override
    public PatternMatchStrategy getPatternMatchStrategy() {
        return patternMatchStrategy;
    }

    @Override
    public void setPatternMatchStrategy(PatternMatchStrategy patternMatchStrategy) {
        this.patternMatchStrategy = patternMatchStrategy;
    }

    @Override
    public PatternMatchResultSelector getPatternMatchResultSelector() {
        return patternMatchResultSelector;
    }

    @Override
    public void setPatternMatchResultSelector(PatternMatchResultSelector patternMatchResultSelector) {
        this.patternMatchResultSelector = patternMatchResultSelector;
    }
}