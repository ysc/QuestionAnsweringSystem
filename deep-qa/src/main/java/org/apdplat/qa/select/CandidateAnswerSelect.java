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

package org.apdplat.qa.select;

import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;

/**
 * 候选答案提取组件
 *
 * @author 杨尚川
 */
public interface CandidateAnswerSelect {

    /**
     * 提取候选答案 候选答案存储在evidence对象里面
     *
     * @param question 问题
     * @param evidence 证据
     */
    public void select(Question question, Evidence evidence);
}