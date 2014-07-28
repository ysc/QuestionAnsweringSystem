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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apdplat.qa.model.Evidence;
import org.cyberneko.html.parsers.DOMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * 
 * @author 杨尚川
 */
public class NekoHTMLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(NekoHTMLUtils.class);

    public static List<String> parse(String url, String xpathExpression) {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return parse(in, xpathExpression);
        } catch (Exception e) {
            LOG.error("错误", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("错误", e);
                }
            }
        }
        return null;
    }

    public static List<String> parse(InputStream in, String xpathExpression) {
        return parse(in, xpathExpression, "UTF-8");
    }

    public static List<String> parse(InputStream in, String xpathExpression, String encoding) {
        DOMParser parser = new DOMParser();
        List<String> list = new ArrayList<String>();
        try {
            // 设置网页的默认编码
            parser.setProperty(
                    "http://cyberneko.org/html/properties/default-encoding",
                    encoding);
            /*
             * The Xerces HTML DOM implementation does not support namespaces
             * and cannot represent XHTML documents with namespace information.
             * Therefore, in order to use the default HTML DOM implementation
             * with NekoHTML's DOMParser to parse XHTML documents, you must turn
             * off namespace processing.
             */
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(new InputSource(new BufferedReader(new InputStreamReader(in, encoding))));
            Document doc = parser.getDocument();
            NodeList products = XPathAPI.selectNodeList(doc, xpathExpression.toUpperCase());
            for (int i = 0; i < products.getLength(); i++) {
                Node node = products.item(i);
                list.add(node.getTextContent());
            }
        } catch (Exception e) {
            LOG.error("错误", e);
        }
        return list;
    }

    public static List<Evidence> searchBaidu(String url) {
        InputStream in = null;
        try {
            in = new URL(url).openStream();
            return searchBaidu(in);
        } catch (Exception e) {
            LOG.error("错误", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.error("错误", e);
                }
            }
        }
        return null;
    }

    public static List<Evidence> searchBaidu(InputStream in) {
        //保证只读一次
        byte[] datas = Tools.readAll(in);
        if (LOG.isDebugEnabled()) {
            try {
                LOG.debug("内容：" + new String(datas, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOG.error("错误", e);
            }
        }
        in = new ByteArrayInputStream(datas);
        String totalXpathExpression = "//html/body/div/div[4]/div/p/span";
        List<String> totals = parse(in, totalXpathExpression);
        int total;
        int len = 10;
        if (totals != null && !totals.isEmpty()) {
            String str = totals.get(totals.size()-1);
            int start = 10;
            if (str.indexOf("约") != -1) {
                start = 11;
            }
            total = Integer.parseInt(str.substring(start).replace(",", "").replace("个", ""));
            LOG.info("搜索结果数：" + total);
        } else {
            return null;
        }
        if (total < 1) {
            return null;
        }
        if (total < 10) {
            len = total;
        }
        List<Evidence> evidences = new ArrayList<>();
        for (int i = 0; i < len; i++) {                    
            String titleXpathExpression =   "//html/body/div/div[4]/div/div[2]/div[" + (i + 1) + "]/h3/a";
            String contentXpathExpression = "//html/body/div/div[4]/div/div[2]/div[" + (i + 1) + "]/div[1]";
            LOG.debug("titleXpathExpression:" + titleXpathExpression);
            LOG.debug("contentXpathExpression:" + contentXpathExpression);
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> titles = parse(in, titleXpathExpression);
            //重新构造输入流
            in = new ByteArrayInputStream(datas);
            List<String> snippets = parse(in, contentXpathExpression);
            
            if (titles != null && titles.size() == 1 && snippets != null && snippets.size() == 1) {
                Evidence evidence = new Evidence();
                evidence.setTitle(titles.get(0));
                evidence.setSnippet(snippets.get(0));
                evidences.add(evidence);
            } else {
                LOG.error("获取搜索结果列表项出错:" + titles + " - " + snippets);
            }
        }
        if (evidences.isEmpty()) {
            return null;
        }
        return evidences;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String url = "http://www.baidu.com/s?pn=0&wd=java";
        List<Evidence> evidences = searchBaidu(url);
        if (evidences != null) {
            for (Evidence evidence : evidences) {
                LOG.info(evidence.getTitle());
                LOG.info(evidence.getSnippet());
                LOG.info("");
            }
        } else {
            LOG.error("没有搜索到结果");
        }
    }
}