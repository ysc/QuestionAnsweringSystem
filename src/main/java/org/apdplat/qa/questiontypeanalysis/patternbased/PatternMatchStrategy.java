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

package org.apdplat.qa.questiontypeanalysis.patternbased;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 杨尚川
 */
public class PatternMatchStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PatternMatchStrategy.class);

    private final List<String> questionTypePatternFiles = new ArrayList<>();
    private final List<QuestionPattern> questionPatterns = new ArrayList<>();

    public boolean validate() {
        if (questionTypePatternFiles.isEmpty() || questionPatterns.isEmpty()) {
            return false;
        }
        return true;
    }

    public void addQuestionTypePatternFile(String questionTypePatternFile) {
        this.questionTypePatternFiles.add(questionTypePatternFile);
    }

    public void addQuestionPattern(QuestionPattern questionPattern) {
        this.questionPatterns.add(questionPattern);
    }

    public boolean enableQuestionTypePatternFile(String questionTypePatternFile) {
        if (this.questionTypePatternFiles.contains(questionTypePatternFile)) {
            return true;
        }
        return false;
    }

    public boolean enableQuestionPattern(QuestionPattern questionPattern) {
        if (this.questionPatterns.contains(questionPattern)) {
            return true;
        }
        return false;
    }

    public String getStrategyDes() {
        StringBuilder str = new StringBuilder();
        for (String questionTypePatternFile : questionTypePatternFiles) {
            str.append(questionTypePatternFile).append(":");
        }
        for (QuestionPattern questionPattern : questionPatterns) {
            str.append(questionPattern).append(":");
        }
        return str.toString();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        PatternMatchStrategy patternMatchStrategy = new PatternMatchStrategy();
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Question);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.TermWithNatures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.Natures);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartPattern);
        patternMatchStrategy.addQuestionPattern(QuestionPattern.MainPartNaturePattern);
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel1_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel2_true.txt");
        patternMatchStrategy.addQuestionTypePatternFile("QuestionTypePatternsLevel3_true.txt");

        LOG.info("策略有效？" + patternMatchStrategy.validate());
        LOG.info("策略描述：" + patternMatchStrategy.getStrategyDes());
    }
}