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

package org.apdplat.qa.model;

/**
 * 
 * @author 杨尚川
 */
public enum QuestionType {

    NULL, PERSON_NAME, LOCATION_NAME, ORGANIZATION_NAME, NUMBER, TIME, DEFINITIION, OBJECT;

    public String getNature() {
        String nature = "unknown";
		//nr 人名
        //nr1 汉语姓氏
        //nr2 汉语名字
        //nrj 日语人名
        //nrf 音译人名
        if (QuestionType.PERSON_NAME == this) {
            nature = "nr";
        }
    	//ns 地名
        //nsf 音译地名
        if (QuestionType.LOCATION_NAME == this) {
            nature = "ns";
        }
        //nt 机构团体名
        if (QuestionType.ORGANIZATION_NAME == this) {
            nature = "nt";
        }
		//m 数词
        //mq 数量词
        if (QuestionType.NUMBER == this) {
            nature = "m";
        }
		//t 时间词
        //tg 时间词性语素
        if (QuestionType.TIME == this) {
            nature = "t";
        }

        return nature;
    }
}