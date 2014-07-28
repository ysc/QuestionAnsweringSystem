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

import org.apdplat.qa.files.FilesConfig;
import org.apdplat.qa.util.Tools;

/**
 * 
 * @author 杨尚川
 */
public class GenerateQuestions {

    /**
     * @param args
     */
    public static void main(String[] args) {
	//Tools.extractQuestions(FilesConfig.personNameMaterial);
        //Tools.extractQuestions(FilesConfig.locationNameMaterial);
        //Tools.extractQuestions(FilesConfig.orgnizationNameMaterial);
        //Tools.extractQuestions(FilesConfig.numberMaterial);
        //Tools.extractQuestions(FilesConfig.timeMaterial);

	//Tools.extractPatterns(FilesConfig.personNameMaterial, "Person");
        //Tools.extractPatterns(FilesConfig.locationNameMaterial, "Location");
        //Tools.extractPatterns(FilesConfig.orgnizationNameMaterial, "Orgnization");
        //Tools.extractPatterns(FilesConfig.numberMaterial, "Number");
        Tools.extractPatterns(FilesConfig.timeMaterial, "Time");
    }

}