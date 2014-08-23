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

import java.util.Objects;

/**
 * 问题类型模式文件
 *
 * @author 杨尚川
 */
public class QuestionTypePatternFile {

    private String file;
    private boolean multiMatch = true;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isMultiMatch() {
        return multiMatch;
    }

    public void setMultiMatch(boolean multiMatch) {
        this.multiMatch = multiMatch;
    }

    @Override
    public int hashCode() {
        return (file + multiMatch).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QuestionTypePatternFile other = (QuestionTypePatternFile) obj;
        if (!Objects.equals(this.file, other.file)) {
            return false;
        }
        if (this.multiMatch != other.multiMatch) {
            return false;
        }
        return true;
    }
}