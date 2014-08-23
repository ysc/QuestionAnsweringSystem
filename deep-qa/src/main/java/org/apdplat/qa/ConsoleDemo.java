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

package org.apdplat.qa;

import org.apdplat.qa.datasource.BaiduDataSource;
import org.apdplat.qa.datasource.ConsoleDataSource;
import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.system.CommonQuestionAnsweringSystem;
import org.apdplat.qa.system.QuestionAnsweringSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 在控制台输入问题 然后从google搜索证据 然后计算候选答案
 *
 * @author 杨尚川
 */
public class ConsoleDemo {

    private static final Logger LOG = LoggerFactory.getLogger(ConsoleDemo.class);

    public static void main(String[] args) {
	//Google数据源
        //DataSource dataSource = new GoogleDataSource();
        //Baidu数据源
        DataSource dataSource = new BaiduDataSource();
        //控制台数据源
        dataSource = new ConsoleDataSource(dataSource);
        //问答系统
        QuestionAnsweringSystem questionAnsweringSystem = new CommonQuestionAnsweringSystem();
        //指定控制台数据源
        questionAnsweringSystem.setDataSource(dataSource);
        //回答问题
        questionAnsweringSystem.answerQuestions();
        //输出统计信息
        questionAnsweringSystem.showPerfectQuestions();
        questionAnsweringSystem.showNotPerfectQuestions();
        questionAnsweringSystem.showWrongQuestions();
        questionAnsweringSystem.showUnknownTypeQuestions();
    }
}