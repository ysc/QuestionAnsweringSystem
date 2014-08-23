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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 模式匹配结果
 *
 * @author 杨尚川
 */
public class PatternMatchResult {

    private static final Logger LOG = LoggerFactory.getLogger(PatternMatchResult.class);

    private final Map<QuestionTypePatternFile, List<PatternMatchResultItem>> map = new HashMap<>();

    /**
     * 从宽松到紧凑
     *
     * @return
     */
    public List<QuestionTypePatternFile> getQuestionTypePatternFilesFromLooseToCompact() {
        LOG.info("从宽松到紧凑进行处理");
        return fromCompactToLoose(false);
    }

    /**
     * 从紧凑到宽松
     *
     * @return
     */
    public List<QuestionTypePatternFile> getQuestionTypePatternFilesFromCompactToLoose() {
        LOG.info("从紧凑到宽松进行处理");
        return fromCompactToLoose(true);
    }

    private List<QuestionTypePatternFile> fromCompactToLoose(boolean compactToLoose) {
        Map<String, QuestionTypePatternFile> tempMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        for (QuestionTypePatternFile file : map.keySet()) {
            list.add(file.getFile());
            tempMap.put(file.getFile(), file);
        }
        //排序(从小到大，文件名称的数字从小到大，表示从紧凑到宽松)
        Collections.sort(list);
        if (!compactToLoose) {
            Collections.reverse(list);
        }
        List<QuestionTypePatternFile> result = new ArrayList<>();
        for (String item : list) {
            result.add(tempMap.get(item));
        }
        return result;
    }

    public void addPatternMatchResult(QuestionTypePatternFile file, List<PatternMatchResultItem> items) {
        List<PatternMatchResultItem> value = map.get(file);
        if (value == null) {
            value = items;
        } else {
            value.addAll(items);
        }
        map.put(file, value);
    }

    public List<PatternMatchResultItem> getPatternMatchResult(QuestionTypePatternFile file) {
        return map.get(file);
    }

    public List<PatternMatchResultItem> getAllPatternMatchResult() {
        List<PatternMatchResultItem> value = new ArrayList<>();
        for (List<PatternMatchResultItem> v : map.values()) {
            value.addAll(v);
        }
        return value;
    }
}