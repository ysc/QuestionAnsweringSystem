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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *将文件或是文件夹打包压缩成zip格式
 * @author 杨尚川
 */
public class ZipUtils {

    private static final Logger LOG = LoggerFactory.getLogger(TextExtract.class);
        
    private ZipUtils(){};
    
    /**
     * APDPlat中的重要打包机制
     * 将jar文件中的某个文件夹里面的内容复制到某个文件夹
     * @param jar 包含静态资源的jar包
     * @param subDir jar中包含待复制静态资源的文件夹名称
     * @param loc 静态资源复制到的目标文件夹
     * @param force 目标静态资源存在的时候是否强制覆盖
     */
    public static void unZip(String jar, String subDir, String loc, boolean force){
        try {
            File base=new File(loc);
            if(!base.exists()){
                base.mkdirs();
            }
            
            ZipFile zip=new ZipFile(new File(jar));
            Enumeration<? extends ZipEntry> entrys = zip.entries();
            while(entrys.hasMoreElements()){
                ZipEntry entry = entrys.nextElement();
                String name=entry.getName();
                if(!name.startsWith(subDir)){
                    continue;
                }
                //去掉subDir
                name=name.replace(subDir,"").trim();
                if(name.length()<2){
                    LOG.debug(name+" 长度 < 2");
                    continue;
                }
                if(entry.isDirectory()){
                    File dir=new File(base,name);
                    if(!dir.exists()){
                        dir.mkdirs();
                        LOG.debug("创建目录");
                    }else{
                        LOG.debug("目录已经存在");
                    }
                    LOG.debug(name+" 是目录");
                }else{
                    File file=new File(base,name);
                    if(file.exists() && force){
                        file.delete();
                    }
                    if(!file.exists()){
                        InputStream in=zip.getInputStream(entry);
                        Files.copy(in, file.toPath());
                        LOG.debug("创建文件");
                    }else{
                        LOG.debug("文件已经存在");
                    }
                    LOG.debug(name+" 不是目录");
                }
            }
        } catch (ZipException ex) {
            LOG.error("文件解压失败",ex);
        } catch (IOException ex) {
            LOG.error("文件操作失败",ex);
        }
    }
    
   /**
     * 创建ZIP文件
     * @param sourcePath 文件或文件夹路径
     * @param zipPath 生成的zip文件存在路径（包括文件名）
     */
    public static void createZip(String sourcePath, String zipPath) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        try {
            fos = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(fos);
            writeZip(new File(sourcePath), "", zos);
        } catch (FileNotFoundException e) {
            LOG.error("创建ZIP文件失败",e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
            } catch (IOException e) {
                LOG.error("创建ZIP文件失败",e);
            }

        }
    }
    
    private static void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if(file.exists()){
            if(file.isDirectory()){//处理文件夹
                parentPath+=file.getName()+File.separator;
                File [] files=file.listFiles();
                for(File f:files){
                    writeZip(f, parentPath, zos);
                }
            }else{
                FileInputStream fis=null;
                try {
                    fis=new FileInputStream(file);
                    ZipEntry ze = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(ze);
                    byte [] content=new byte[1024];
                    int len;
                    while((len=fis.read(content))!=-1){
                        zos.write(content,0,len);
                        zos.flush();
                    }
                    
                
                } catch (FileNotFoundException e) {
                    LOG.error("创建ZIP文件失败",e);
                } catch (IOException e) {
                    LOG.error("创建ZIP文件失败",e);
                }finally{
                    try {
                        if(fis!=null){
                            fis.close();
                        }
                    }catch(IOException e){
                        LOG.error("创建ZIP文件失败",e);
                    }
                }
            }
        }
    }    
    public static void main(String[] args) {
        ZipUtils.unZip("/Users/apple/.m2/repository/org/apdplat/deep-qa/1.1/deep-qa-1.1.jar", "dic", "/Users/apple/dic", true);
        ZipUtils.unZip("/Users/apple/.m2/repository/org/apdplat/deep-qa/1.1/deep-qa-1.1.jar", "questionTypePatterns", "/Users/apple/questionTypePatterns", true);
    }
}