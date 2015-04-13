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
import java.util.List;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.word.segmentation.Word;

/**
 * 证据由title和snippet组成 对于同一个问题来说，不同的证据的重要性是不一样的，所以证据有分值 证据有多个候选答案
 *
 * @author 杨尚川
 */
public class Evidence {

    private String title;
    private String snippet;
    private double score = 1.0;
    private CandidateAnswerCollection candidateAnswerCollection;

    public List<String> getTitleWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(title);
        for (Word word : words) {
            result.add(word.getText());
        }
        return result;
    }

    public List<String> getSnippetWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(snippet);
        for (Word word : words) {
            result.add(word.getText());
        }
        return result;
    }

    /**
     * 对证据进行分词
     *
     * @return 分词结果
     */
    public List<String> getWords() {
        List<String> result = new ArrayList<>();
        List<Word> words = WordParser.parse(title + snippet);
        for (Word word : words) {
            result.add(word.getText());
        }
        return result;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getScore() {
        return score;
    }

    public void addScore(double score) {
        this.score += score;
    }

    public CandidateAnswerCollection getCandidateAnswerCollection() {
        return candidateAnswerCollection;
    }

    public void setCandidateAnswerCollection(CandidateAnswerCollection candidateAnswerCollection) {
        this.candidateAnswerCollection = candidateAnswerCollection;
    }
}