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

package org.apdplat.qa.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author 杨尚川
 */
public class Test {

    public static void main(String[] args) {
        List<String> data = new ArrayList<>();
        data.add("1");
        data.add("2");
        data.add("3");
        data.add("4");
        data.add("5");
        List<List<String>> result = getCom(data);

        for (List<String> item : result) {
            for (String sub : item) {
                System.out.print(sub + " ");
            }
            System.out.println("");
        }
    }

    public static <T> List<List<T>> getCom(List<T> list) {
        List<List<T>> result = new ArrayList<>();
        T[] data = (T[]) list.toArray();
        long max = 1 << data.length;
        for (int i = 1; i < max; i++) {
            List<T> sub = new ArrayList<>();
            for (int j = 0; j < data.length; j++) {
                if ((i & (1 << j)) != 0) {
                    sub.add(data[j]);
                }
            }
            result.add(sub);
        }
        return result;
    }

}