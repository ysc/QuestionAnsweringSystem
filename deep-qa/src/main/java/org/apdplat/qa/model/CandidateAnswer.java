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

/**
 * 候选答案 每一个候选答案都包含答案名称以及分值
 *
 * @author 杨尚川
 */
public class CandidateAnswer implements Comparable<CandidateAnswer> {

    private String answer;
    private double score = 1.0;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void addScore(double score) {
        this.score += score;
    }

    @Override
    public int compareTo(CandidateAnswer o) {
        if (o != null && o instanceof CandidateAnswer) {
            CandidateAnswer a = (CandidateAnswer) o;
            if (this.score < a.score) {
                return -1;
            }
            if (this.score > a.score) {
                return 1;
            }
            if (this.score == a.score) {
                return 0;
            }
        }
        throw new RuntimeException("无法比较大小");
    }

    @Override
    public int hashCode() {
        return this.answer.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CandidateAnswer)) {
            return false;
        }
        CandidateAnswer a = (CandidateAnswer) obj;
        return this.answer.equals(a.answer);
    }
}