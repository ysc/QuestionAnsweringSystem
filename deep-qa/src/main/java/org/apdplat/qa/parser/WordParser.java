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

package org.apdplat.qa.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.ansj.domain.Term;
import org.ansj.library.UserDefineLibrary;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;
import org.ansj.util.MyStaticValue;
import org.apdplat.qa.util.Tools;
import org.apdplat.qa.util.ZipUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分词器
 *
 * @author 杨尚川
 */
public class WordParser {

    private static final Logger LOG = LoggerFactory.getLogger(WordParser.class);

    static {
        LOG.info("开始设置默认词典路径");
        String appPath = Tools.getAppPath(WordParser.class);
        String userLibrary = appPath + "/dic/default/default.dic";
        LOG.info("default.dic：" + userLibrary);
        String ambiguityLibrary = appPath + "/dic/default/ambiguity.dic";
        LOG.info("ambiguity.dic：" + ambiguityLibrary);
        //先加载词典
        MyStaticValue.userLibrary = userLibrary;
        MyStaticValue.ambiguityLibrary = ambiguityLibrary;
        //避免控制台信息输出混乱
        parse("");
        LOG.info("开始初始化自定义细分词性配置");
        int total = 0;
        HashMap<String, String> updateDic = FilterModifWord.getUpdateDic();
        //忽略空白词，对主谓宾识别至关重要
        updateDic.put("　", "_stop");
        updateDic.put("#", "_stop");

        String path = appPath + "/dic/custom/";
        LOG.info("自定义词典目录：" + path);
        File dir = new File(path);
        File[] files = null;
        if (dir.isDirectory()) {
            files = dir.listFiles();
        } else {
            LOG.error("自定义词典目录不存在：" + path);
        }
        for (File file : files) {
            BufferedReader reader = null;
            try {
                InputStream in = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.equals("") || line.startsWith("#") || line.startsWith("//")) {
                        LOG.info("忽略空行：" + line);
                        continue;
                    }
                    String[] split = line.split("\\t+");
                    if (split != null && split.length == 3) {
                        String keyword = split[0].trim();
                        String nature = split[1].trim();
                        String freq = split[2].trim();
                        //修正词性
                        updateDic.put(keyword, nature);
                        LOG.debug(keyword + " " + nature);
                        //加入自定义词典
                        UserDefineLibrary.insertWord(keyword, nature, Integer.parseInt(freq));
                        total++;
                    } else {
                        LOG.error("自定义细分词性配置词典错误：" + line);
                    }
                }
            } catch (IOException e) {
                LOG.error("流读取失败：", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        LOG.error("文件关闭失败：", e);
                    }
                }
            }
        }
        LOG.info("初始化自定义词数：" + total);
        LOG.info("完成初始化自定义细分词性配置");
    }

    /**
     * 带词性标注（包括细分词性标注）的分析方法
     *
     * @param str 需要分词的文本
     * @return 分词结果
     */
    public static List<Term> parse(String str) {
        //分词
        //有4种分词方式
        //1、基本分词 BaseAnalysis
        //http://ansjsun.github.io/ansj_seg/content.html?name=%E5%9F%BA%E6%9C%AC%E5%88%86%E8%AF%8D
        //2、精准分词 ToAnalysis
        //http://ansjsun.github.io/ansj_seg/content.html?name=%E7%B2%BE%E5%87%86%E5%88%86%E8%AF%8D
        //3、NLP分词 NlpAnalysis
        //http://ansjsun.github.io/ansj_seg/content.html?name=nlp%E5%88%86%E8%AF%8D
        //4、面向索引的分词 IndexAnalysis
        //http://ansjsun.github.io/ansj_seg/content.html?name=%E9%9D%A2%E5%90%91%E7%B4%A2%E5%BC%95%E7%9A%84%E5%88%86%E8%AF%8D
        List<Term> terms = ToAnalysis.parse(str);
        //词性标注
        new NatureRecognition(terms).recognition();
        //细分词性标注，接受返回的terms才能有去除停用词的效果
        terms = FilterModifWord.modifResult(terms);
        return terms;
    }

    public static void main(String[] args) {
        List<Term> parse = parse("在河边一排排梨树下面有许多的非洲象和熊猫，还有很多的桉树，红色的金鱼在水里游来游去，猎豹在绿色的草地上跑来跑去!");
        System.out.println(parse);
        parse = parse("布什是个什么样的人");
        System.out.println(parse);
        parse = parse("张三和");
        System.out.println(parse);
        parse = parse("哈雷彗星的发现者是六小龄童和伦琴,专访微软亚洲研究院院长洪小文");
        System.out.println(parse);
        String str = " 《创业邦》杂志记者对微软亚洲研究院院长洪小文进行了专访。 《创业邦》：微软亚洲  研究院 ... 从研发的角度来说，研究院是一个战略性的部门。因为一家公司最后成功与   ...";
        parse = parse(str);
        System.out.println(parse);
    }
}