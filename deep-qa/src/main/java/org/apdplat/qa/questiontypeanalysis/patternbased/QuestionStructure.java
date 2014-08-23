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

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 问题结构
 *
 * @author 杨尚川
 */
public class QuestionStructure {

    private String question;
    private String mainPart;
    private String mainPartForTop = null;
    private String mainPartForRoot = null;
    private List<String> dependencies = new ArrayList<>();
    private Collection<TypedDependency> tdls;
    private Tree tree;

    public boolean perfect() {
        //如果两种模式都有结果且一致，主谓宾提取的准确性应该是最高的
        if (mainPartForTop != null && mainPartForTop.equals(mainPartForRoot)) {
            return true;
        }
        return false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getMainPart() {
        if (mainPart != null) {
            return mainPart;
        }
        //top的优先级高于root
        if (mainPartForTop == null) {
            //如果没有top的值，则返回root的值
            return mainPartForRoot;
        }
        return mainPartForTop;
    }

    public void setMainPart(String mainPart) {
        this.mainPart = mainPart;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public Collection<TypedDependency> getTdls() {
        return tdls;
    }

    public void setTdls(Collection<TypedDependency> tdls) {
        this.tdls = tdls;
    }

    public Tree getTree() {
        return tree;
    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public String getMainPartForTop() {
        return mainPartForTop;
    }

    public void setMainPartForTop(String mainPartForTop) {
        this.mainPartForTop = mainPartForTop;
    }

    public String getMainPartForRoot() {
        return mainPartForRoot;
    }

    public void setMainPartForRoot(String mainPartForRoot) {
        this.mainPartForRoot = mainPartForRoot;
    }
}