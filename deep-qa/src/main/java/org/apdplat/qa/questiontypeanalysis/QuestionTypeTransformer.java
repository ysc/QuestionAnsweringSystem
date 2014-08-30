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

import org.apdplat.qa.model.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 杨尚川
 */
public class QuestionTypeTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionTypeTransformer.class);

    public static QuestionType transform(String questionType) {
        LOG.debug("问题类型转换：" + questionType);
        if (questionType.contains("Person")) {
            return QuestionType.PERSON_NAME;
        }
        if (questionType.contains("Location")) {
            return QuestionType.LOCATION_NAME;
        }
        if (questionType.contains("Organization")) {
            return QuestionType.ORGANIZATION_NAME;
        }
        if (questionType.contains("Number")) {
            return QuestionType.NUMBER;
        }
        if (questionType.contains("Time")) {
            return QuestionType.TIME;
        }
        /**
         * 下面两种问题类型目前还不能回答
        if (questionType.contains("Object")) {
            return QuestionType.OBJECT;
        }
        if (questionType.contains("Definition")) {
            return QuestionType.DEFINITIION;
        }
        */
        LOG.error("问题类型转换失败，默认人名：" + questionType);
        return QuestionType.PERSON_NAME;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(transform("Person->Multi5"));
    }
}