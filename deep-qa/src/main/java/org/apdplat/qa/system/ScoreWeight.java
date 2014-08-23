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

/**
 * 评分组件权重
 *
 * @author 杨尚川
 */
public class ScoreWeight {

    private double termFrequencyCandidateAnswerScoreWeight = 1;
    private double termDistanceCandidateAnswerScoreWeight = 1;
    private double termDistanceMiniCandidateAnswerScoreWeight = 1;
    private double textualAlignmentCandidateAnswerScoreWeight = 1;
    private double moreTextualAlignmentCandidateAnswerScoreWeight = 1;
    private double rewindTextualAlignmentCandidateAnswerScoreWeight = 1;
    private double hotCandidateAnswerScoreWeight = 1;

    private double termMatchEvidenceScoreWeight = 1;
    private double bigramEvidenceScoreWeight = 1;
    private double skipBigramEvidenceScoreWeight = 1;

    public double getTermFrequencyCandidateAnswerScoreWeight() {
        return termFrequencyCandidateAnswerScoreWeight;
    }

    public void setTermFrequencyCandidateAnswerScoreWeight(
            double termFrequencyCandidateAnswerScoreWeight) {
        this.termFrequencyCandidateAnswerScoreWeight = termFrequencyCandidateAnswerScoreWeight;
    }

    public double getTermDistanceCandidateAnswerScoreWeight() {
        return termDistanceCandidateAnswerScoreWeight;
    }

    public void setTermDistanceCandidateAnswerScoreWeight(
            double termDistanceCandidateAnswerScoreWeight) {
        this.termDistanceCandidateAnswerScoreWeight = termDistanceCandidateAnswerScoreWeight;
    }

    public double getTermDistanceMiniCandidateAnswerScoreWeight() {
        return termDistanceMiniCandidateAnswerScoreWeight;
    }

    public void setTermDistanceMiniCandidateAnswerScoreWeight(
            double termDistanceMiniCandidateAnswerScoreWeight) {
        this.termDistanceMiniCandidateAnswerScoreWeight = termDistanceMiniCandidateAnswerScoreWeight;
    }

    public double getTextualAlignmentCandidateAnswerScoreWeight() {
        return textualAlignmentCandidateAnswerScoreWeight;
    }

    public void setTextualAlignmentCandidateAnswerScoreWeight(
            double textualAlignmentCandidateAnswerScoreWeight) {
        this.textualAlignmentCandidateAnswerScoreWeight = textualAlignmentCandidateAnswerScoreWeight;
    }

    public double getMoreTextualAlignmentCandidateAnswerScoreWeight() {
        return moreTextualAlignmentCandidateAnswerScoreWeight;
    }

    public void setMoreTextualAlignmentCandidateAnswerScoreWeight(
            double moreTextualAlignmentCandidateAnswerScoreWeight) {
        this.moreTextualAlignmentCandidateAnswerScoreWeight = moreTextualAlignmentCandidateAnswerScoreWeight;
    }

    public double getRewindTextualAlignmentCandidateAnswerScoreWeight() {
        return rewindTextualAlignmentCandidateAnswerScoreWeight;
    }

    public void setRewindTextualAlignmentCandidateAnswerScoreWeight(
            double rewindTextualAlignmentCandidateAnswerScoreWeight) {
        this.rewindTextualAlignmentCandidateAnswerScoreWeight = rewindTextualAlignmentCandidateAnswerScoreWeight;
    }

    public double getHotCandidateAnswerScoreWeight() {
        return hotCandidateAnswerScoreWeight;
    }

    public void setHotCandidateAnswerScoreWeight(
            double hotCandidateAnswerScoreWeight) {
        this.hotCandidateAnswerScoreWeight = hotCandidateAnswerScoreWeight;
    }

    public double getTermMatchEvidenceScoreWeight() {
        return termMatchEvidenceScoreWeight;
    }

    public void setTermMatchEvidenceScoreWeight(double termMatchEvidenceScoreWeight) {
        this.termMatchEvidenceScoreWeight = termMatchEvidenceScoreWeight;
    }

    public double getBigramEvidenceScoreWeight() {
        return bigramEvidenceScoreWeight;
    }

    public void setBigramEvidenceScoreWeight(double bigramEvidenceScoreWeight) {
        this.bigramEvidenceScoreWeight = bigramEvidenceScoreWeight;
    }

    public double getSkipBigramEvidenceScoreWeight() {
        return skipBigramEvidenceScoreWeight;
    }

    public void setSkipBigramEvidenceScoreWeight(double skipBigramEvidenceScoreWeight) {
        this.skipBigramEvidenceScoreWeight = skipBigramEvidenceScoreWeight;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }
}