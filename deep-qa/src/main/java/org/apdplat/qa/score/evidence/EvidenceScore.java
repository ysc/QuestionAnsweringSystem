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

package org.apdplat.qa.score.evidence;

import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.ScoreWeight;

/**
 * 证据评分组件
 *
 * @author 杨尚川
 */
public interface EvidenceScore {

    /**
     * 对证据进行评分 证据的分值存储在evidence对象里面
     *
     * @param question 问题
     * @param evidence 证据
     */
    public void score(Question question, Evidence evidence);

    /**
     * 评分组件权重
     *
     * @param scoreWeight
     */
    public void setScoreWeight(ScoreWeight scoreWeight);
}