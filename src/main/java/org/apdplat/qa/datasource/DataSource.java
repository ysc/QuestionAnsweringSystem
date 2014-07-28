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

package org.apdplat.qa.datasource;

import java.util.List;

import org.apdplat.qa.model.Question;
import org.apdplat.qa.system.QuestionAnsweringSystem;

/**
 * 获取 问题及其对应证据
 *
 * @author 杨尚川
 */
public interface DataSource {

    /**
     * 获取多个问题以及问题的多个证据
     *
     * @return 多个问题以及问题的多个证据
     */
    public List<Question> getQuestions();

    /**
     * 获取问题以及问题的多个证据
     *
     * @param questionStr 问题
     * @return 问题以及问题的多个证据
     */
    public Question getQuestion(String questionStr);

    /**
     * 边获取问题边进行解答
     *
     * @param questionAnsweringSystem
     * @return
     */
    public List<Question> getAndAnswerQuestions(QuestionAnsweringSystem questionAnsweringSystem);

    /**
     * 获取问题并进行解答
     *
     * @param questionStr
     * @param questionAnsweringSystem
     * @return
     */
    public Question getAndAnswerQuestion(String questionStr, QuestionAnsweringSystem questionAnsweringSystem);

}