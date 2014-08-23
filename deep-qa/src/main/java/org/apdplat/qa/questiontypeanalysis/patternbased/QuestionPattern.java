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

/**
 * 问题模式
 *
 * @author 杨尚川
 */
public enum QuestionPattern {

    //1、直接和【问题】匹配，如：APDPlat的发起人是谁？
    Question,
    //2、和问题分词及词性标注之后的【词和词性序列】进行匹配，如：apdplat/en 的/uj 发起人/n 是/v 谁/RW.RWPersonSingle 
    TermWithNatures,
    //3、和问题分词及词性标注之后的【词性序列】进行匹配，如：en/uj/n/v/RW.RWPersonSingle
    Natures,
    //4、和问题的【主谓宾 词 和 词性】进行匹配，如：发起人/n 是/v 谁/RW.RWPersonSingle
    MainPartPattern,
    //5、和问题的【主谓宾  词性】进行匹配，如：n/v/RW.RWPersonSingle
    MainPartNaturePattern
}