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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apdplat.qa.datasource.DataSource;
import org.apdplat.qa.datasource.FileDataSource;
import org.apdplat.qa.model.Evidence;
import org.apdplat.qa.model.Question;
import org.apdplat.qa.parser.WordParser;
import org.apdplat.word.segmentation.Word;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author 杨尚川
 */
public class Tools {

    private static final Logger LOG = LoggerFactory.getLogger(Tools.class);
    private static Map<String, Integer> map = new HashMap<>();

    public static String getTimeDes(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder str = new StringBuilder();
        if (day > 0) {
            str.append(day).append("天,");
        }
        if (hour > 0) {
            str.append(hour).append("小时,");
        }
        if (minute > 0) {
            str.append(minute).append("分钟,");
        }
        if (second > 0) {
            str.append(second).append("秒,");
        }
        if (milliSecond > 0) {
            str.append(milliSecond).append("毫秒,");
        }
        if (str.length() > 0) {
            str = str.deleteCharAt(str.length() - 1);
        }

        return str.toString();
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

    public static void extractQuestions(String file) {
        //从material中提取questions
        DataSource dataSource = new FileDataSource(file);
        List<Question> questions = dataSource.getQuestions();
        for (Question question : questions) {
            System.out.println(question.getQuestion().trim() + ":" + question.getExpectAnswer());
        }
    }

    public static void extractPatterns(String file, String pattern) {
        //从material中提取questions
        DataSource dataSource = new FileDataSource(file);
        List<Question> questions = dataSource.getQuestions();
        for (Question question : questions) {
            System.out.println(pattern + " " + question.getQuestion().trim());
        }
    }

    public static int getIDF(String term) {
        Integer idf = map.get(term);
        if (idf == null) {
            return 0;
        }
        LOG.info("idf " + term + ":" + idf);
        return idf;
    }

    public static List<Map.Entry<String, Integer>> initIDF(List<Question> questions) {
        map = new HashMap<>();
        for (Question question : questions) {
            List<Evidence> evidences = question.getEvidences();
            for (Evidence evidence : evidences) {
                Set<String> set = new HashSet<>();
                List<Word> words = WordParser.parse(evidence.getTitle() + evidence.getSnippet());
                for (Word word : words) {
                    set.add(word.getText());
                }
                for (String item : set) {
                    Integer doc = map.get(item);
                    if (doc == null) {
                        doc = 1;
                    } else {
                        doc++;
                    }
                    map.put(item, doc);
                }
            }
        }
        List<Map.Entry<String, Integer>> list = Tools.sortByIntegerValue(map);
        for (Map.Entry<String, Integer> entry : list) {
            LOG.debug(entry.getKey() + " " + entry.getValue());
        }
        return list;
    }

    public static String getHTMLContent(String url) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            StringBuilder html = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                html.append(line).append("\n");
                line = reader.readLine();
            }
            String content = TextExtract.parse(html.toString());
            return content;
        } catch (Exception e) {
            LOG.debug("解析URL失败：" + url, e);
        }
        return null;
    }

    /**
     * 文本分词
     *
     * @param text
     * @return
     */
    public static List<Word> getWords(String text) {
        List<Word> result = new ArrayList<>();
        List<Word> words = WordParser.parse(text);
        for (Word word : words) {
            result.add(word);
        }
        return result;
    }

    /**
     * 在text中包含的pattern数目
     *
     * @param text
     * @param pattern
     * @return
     */
    public static int countsForSkipBigram(String text, String pattern) {
        int count = 0;
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            LOG.debug("正则表达式匹配：" + matcher.group());
            count++;
        }
        return count;
    }

    /**
     * 在text中包含的pattern数目
     *
     * @param text
     * @param pattern
     * @return
     */
    public static int countsForBigram(String text, String pattern) {
        int count = 0;
        int index = -1;
        while (true) {
            index = text.indexOf(pattern, index + 1);
            if (index > -1) {
                LOG.debug("模式: " + pattern + " 出现在文本中的位置：" + index);
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    /**
     * 根据MAP的VALUE进行排序，升序
     *
     * @param map
     * @return
     */
    public static <K> List<Map.Entry<K, Integer>> sortByIntegerValue(Map<K, Integer> map) {
        List<Map.Entry<K, Integer>> orderList = new ArrayList<>(map.entrySet());
        Collections.sort(orderList, new Comparator<Map.Entry<K, Integer>>() {
            @Override
            public int compare(Map.Entry<K, Integer> o1, Map.Entry<K, Integer> o2) {
                return (o1.getValue() - o2.getValue());
            }
        });
        return orderList;
    }

    /**
     * 根据MAP的VALUE进行排序，升序
     *
     * @param map
     * @return
     */
    public static <K> List<Map.Entry<K, Double>> sortByDoubleValue(Map<K, Double> map) {
        List<Map.Entry<K, Double>> orderList = new ArrayList<>(map.entrySet());
        Collections.sort(orderList, new Comparator<Map.Entry<K, Double>>() {
            @Override
            public int compare(Map.Entry<K, Double> o1, Map.Entry<K, Double> o2) {
                double abs = o1.getValue() - o2.getValue();
                if (abs < 0) {
                    return -1;
                }
                if (abs > 0) {
                    return 1;
                }

                return 0;
            }
        });
        return orderList;
    }

    public static void createAndWriteFile(String path, String text) {
        BufferedWriter writer = null;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            writer.write(text);
        } catch (Exception ex) {
            LOG.error("文件操作失败", ex);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    LOG.error("文件关闭失败", ex);
                }
            }
        }
    }

    public static String getRewindEvidenceText(String question, String answer) {
        //1、先从本地缓存里面找
        String rewindEvidenceText = MySQLUtils.getRewindEvidenceText(question, answer);
        if (rewindEvidenceText != null) {
            //数据库中存在
            LOG.info("从数据库中查询到回带文本：" + question + " " + answer);
            return rewindEvidenceText;
        }
        //2、本地缓存里面没有再查询google
        StringBuilder text = new StringBuilder();
        String query = question + answer;
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("url构造失败", e);
            return null;
        }
        query = "http://ajax.googleapis.com/ajax/services/search/web?start=0&rsz=large&v=1.0&q=" + query;
        try {
            HostConfiguration hcf = new HostConfiguration();
            hcf.setProxy("127.0.0.1", 8087);

            HttpClient httpClient = new HttpClient();
            GetMethod getMethod = new GetMethod(query);

            //httpClient.executeMethod(hcf, getMethod);
            httpClient.executeMethod(getMethod);
            getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler());

            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("Method failed: " + getMethod.getStatusLine());
            }
            byte[] responseBody = getMethod.getResponseBody();
            String response = new String(responseBody, "UTF-8");
            LOG.debug("搜索返回数据：" + response);
            JSONObject json = new JSONObject(response);
            String totalResult = json.getJSONObject("responseData").getJSONObject("cursor").getString("estimatedResultCount");
            int totalResultCount = Integer.parseInt(totalResult);
            LOG.info("搜索返回记录数： " + totalResultCount);

            JSONArray results = json.getJSONObject("responseData").getJSONArray("results");

            LOG.debug(" Results:");
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String title = result.getString("titleNoFormatting");
                LOG.debug(title);
                //从URL中提取正文
                String url = result.get("url").toString();
                String content = null;//Tools.getHTMLContent(url);
                if (content == null) {
                    //提取摘要
                    content = result.get("content").toString();
                    content = content.replaceAll("<b>", "");
                    content = content.replaceAll("</b>", "");
                    content = content.replaceAll("\\.\\.\\.", "");
                }
                LOG.debug(content);
                text.append(title).append(content);
            }
            LOG.info("将回带文本：" + question + " " + answer + " 加入MySQL数据库");
            MySQLUtils.saveRewindEvidenceText(question, answer, text.toString());
            return text.toString();
        } catch (Exception e) {
            LOG.debug("执行搜索失败：", e);
        }
        return null;
    }

    public static byte[] readAll(InputStream in) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            for (int n; (n = in.read(buffer)) > 0;) {
                out.write(buffer, 0, n);
            }
        } catch (IOException ex) {
            LOG.error("读取失败", ex);
        }
        return out.toByteArray();
    }

    public static String getAppPath(Class cls) {
        // 检查用户传入的参数是否为空
        if (cls == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        ClassLoader loader = cls.getClassLoader();
        // 获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        // 获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        // 如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            // 此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax.")) {
                throw new IllegalArgumentException("不要传送系统类！");
            }
            // 在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            // 判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(".") < 0) {
                path = packName + "/";
            } else {
                // 否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + "/";
            }
        }
        // 调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        URL url = loader.getResource(path + clsName);
        // 从URL对象中获取路径信息
        String realPath = url.getPath();
        // 去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) {
            realPath = realPath.substring(pos + 5);
        }
        // 去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        // 如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!")) {
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
        }
        /*------------------------------------------------------------  
         ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径  
         中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要  
         的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的  
         中文及空格路径  
         -------------------------------------------------------------*/
        try {
            realPath = URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //统一转换到类路径下
        if(realPath.endsWith("/lib")){
            realPath = realPath.replace("/lib", "/classes");
        }
        //处理maven中的依赖JAR
        if(realPath.contains("/org/apdplat/deep-qa/")){
            int index = realPath.lastIndexOf("/");
            String version = realPath.substring(index+1);
            String jar = realPath+"/deep-qa-"+version+".jar";
            LOG.info("maven jar："+jar);
            ZipUtils.unZip(jar, "dic", "deep-qa/dic", true);
            ZipUtils.unZip(jar, "questionTypePatterns", "deep-qa/questionTypePatterns", true);
            realPath = "deep-qa";
        }
        return realPath;
    }

    public static Set<String> getQuestions(String file) {
        //用Set数据结构，这样可以避免重复的问题
        Set<String> result = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(Tools.class.getResourceAsStream(file), "utf-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                //去除空格和？号
                line = line.trim().replace("?", "").replace("？", "");
                if (line.equals("") || line.startsWith("#") || line.indexOf("#") == 1 || line.length() < 3) {
                    continue;
                }
                result.add(line);
            }
        } catch (Exception e) {
            LOG.error("读文件错误", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LOG.error("关闭文件错误", e);
                }
            }
        }
        return result;
    }
}