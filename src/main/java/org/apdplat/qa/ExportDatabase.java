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

import org.apdplat.qa.model.Question;
import org.apdplat.qa.util.MySQLUtils;
import org.apdplat.qa.util.Tools;

/**
 * 导出缓存在MySQL中的问题及证据
 *
 * @author 杨尚川
 */
public class ExportDatabase {

    public static void main(String[] args) {
        StringBuilder str = new StringBuilder();
        int i = 1;
        for (Question question : MySQLUtils.getQuestionsFromDatabase()) {
            str.append(question.toString((i++)));
        }
        Tools.createAndWriteFile("d:/material_export.txt", str.toString());
    }
}