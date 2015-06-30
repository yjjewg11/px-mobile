package com.company.news.commons.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import javax.imageio.ImageIO;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.company.news.ProjectProperties;
import com.company.news.SystemConstants;
import com.company.news.rest.util.SmbFileUtil;

public class FileUtils {
  private static Logger logger = LoggerFactory.getLogger(FileUtils.class);

  public static String createDirIfNoExists(String name) {
    return createDir(name, null);
  }

  public static String createDir(String path, String name) {
    path = path == null ? FilenameUtils.separatorsToSystem("") : path;
    File file = new File(FilenameUtils.separatorsToSystem(path));
    if (!file.exists()) {
      logger.info("create dir:"+path);
      file.mkdirs();
    }
    return file.getPath();
  }

  public static String getCurrentDate() {
    Calendar c = Calendar.getInstance();
    return String.valueOf(c.get(Calendar.YEAR))
        + ((c.get(Calendar.MONTH) + 1) < 10 ? "0" + (c.get(Calendar.MONTH) + 1) : (c
            .get(Calendar.MONTH) + 1))
        + (c.get(Calendar.DATE) < 10 ? "0" + c.get(Calendar.DATE) : c.get(Calendar.DATE));
  }



  /**
   * 保存附件到指定服务器
   * 
   * @param fileForm
   * @param path
   * @param filename
   * @return
   * @see
   */
  static public boolean saveFile( byte[] b, String path, String filename) {

    if (logger.isDebugEnabled()) {
      logger.debug("saveFile(FormFile, String, String) - start");
      logger.debug(" path: " + path);
    }

    try {
      String filePath = null;
      int bytesRead = 0;
      boolean isupdate = false;
      byte[] buffer = new byte[8192];
      if (SmbFileUtil.isSmbFileFormat(path)) {
        SmbFileUtil.mkSmbDirIfNoExist(path);
      } else {
        SmbFileUtil.mkDirIfNoExist(path);
      }

      char spliter = path.charAt(path.length() - 1);
      if (spliter == '\\' || spliter == '/')
        filePath = path + filename;
      else
        filePath = path + "/" + filename;

      if (SmbFileUtil.isSmbFileFormat(filePath)) {
        SmbFileOutputStream bos = null;
        SmbFile file = null;
        SmbFile existFile = new SmbFile(filePath);// 存在同名文件
        if (existFile.exists()) {// 存在则覆盖,数据更新
          isupdate = Boolean.TRUE;
          file = new SmbFile(filePath + ".tmp");
        } else {
          isupdate = Boolean.FALSE;
          file = existFile;
        }
        try {
          bos = new SmbFileOutputStream(file);
          bos.write(b, 0, b.length);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (bos != null) bos.close();
        }
        if (isupdate) {
          existFile.delete();
          file.renameTo(existFile);
        }
      } else {// 非smb协议文件报存
        OutputStream bos = null;
        File file = null;
        File existFile = new File(filePath);// 存在同名文件
        if (existFile.exists()) {// 存在则覆盖,数据更新
          isupdate = Boolean.TRUE;
          file = new File(filePath + ".tmp");
        } else {
          isupdate = Boolean.FALSE;
          file = existFile;
        }
        try {
          bos = new FileOutputStream(file);
          bos.write(b, 0, b.length);
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (bos != null) bos.close();
        }
        if (isupdate) {
          existFile.delete();
          file.renameTo(existFile);
        }
      }
      if (logger.isDebugEnabled()) {
        logger.debug("saveFile byte() - end");
      }
      return true;
    } catch (Exception e) {
      logger.error("saveFile byte()", e);

      if (logger.isDebugEnabled()) {
        logger.debug("saveFile byte() - end");
      }
      return false;
    }
  }

  

  /**
   * 保存附件到指定服务器
   * 
   * @param fileForm
   * @param path
   * @param filename
   * @return
   * @see
   */
  static public boolean saveFile(InputStream stream, String path, String filename) {

    if (logger.isDebugEnabled()) {
      logger.debug("saveFile(FormFile, String, String) - start");
      logger.debug(" path: " + path);
    }

    try {
      String filePath = null;
      int bytesRead = 0;
      boolean isupdate = false;
      byte[] buffer = new byte[8192];
      if (SmbFileUtil.isSmbFileFormat(path)) {
        SmbFileUtil.mkSmbDirIfNoExist(path);
      } else {
        SmbFileUtil.mkDirIfNoExist(path);
      }

      char spliter = path.charAt(path.length() - 1);
      if (spliter == '\\' || spliter == '/')
        filePath = path + filename;
      else
        filePath = path + "/" + filename;

      if (SmbFileUtil.isSmbFileFormat(filePath)) {
        SmbFileOutputStream bos = null;
        SmbFile file = null;
        SmbFile existFile = new SmbFile(filePath);// 存在同名文件
        if (existFile.exists()) {// 存在则覆盖,数据更新
          isupdate = Boolean.TRUE;
          file = new SmbFile(filePath + ".tmp");
        } else {
          isupdate = Boolean.FALSE;
          file = existFile;
        }
        try {
          bos = new SmbFileOutputStream(file);
          while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            bos.write(buffer, 0, bytesRead);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (bos != null) bos.close();
          if (stream != null) stream.close();
        }
        if (isupdate) {
          existFile.delete();
          file.renameTo(existFile);
        }
      } else {// 非smb协议文件报存
        OutputStream bos = null;
        File file = null;
        File existFile = new File(filePath);// 存在同名文件
        if (existFile.exists()) {// 存在则覆盖,数据更新
          isupdate = Boolean.TRUE;
          file = new File(filePath + ".tmp");
        } else {
          isupdate = Boolean.FALSE;
          file = existFile;
        }
        try {
          bos = new FileOutputStream(file);
          while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            bos.write(buffer, 0, bytesRead);
          }
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
          if (bos != null) bos.close();
          if (stream != null) stream.close();
        }
        if (isupdate) {
          existFile.delete();
          file.renameTo(existFile);
        }
        bos.close();
      }
      stream.close();
      if (logger.isDebugEnabled()) {
        logger.debug("saveFile() - end");
      }
      return true;
    } catch (Exception e) {
      logger.error("saveFile()", e);

      if (logger.isDebugEnabled()) {
        logger.debug("saveFile() - end");
      }
      return false;
    }
  }
  public static void main(String[] args) throws FileNotFoundException {
    File file = new File("H:/work_资料/流媒体播放.jpg");
    InputStream in = new FileInputStream(file);
    makeThumbnail(in, "H:/AttachmentUpload/", "myhead_temp.jpg");
    // String s = "{\"type\":\"jpg\",\"duration\":\"\",\"size\":32362}";
    // JSONObject jsonObject = JSONObject.fromObject(s);
    // System.out.println(jsonObject.get("type"));

  }

  
  /**
   * 制作缩略图
   * 
   * @param inputStream
   * @param path
   * @param filename
   */
  public static boolean makeThumbnail(InputStream inputStream, String path, String filename,Integer maxWidth) {
    long    startTime = System.currentTimeMillis();
    logger.info("makeThumbnail:");
    OutputStream outputStream = null;
    try {
      // 读取图片数据
      BufferedImage image = ImageIO.read(inputStream);
     // int maxWidth =Integer.valueOf(ProjectProperties.getProperty("UploadFilePath_image_max_width", "200"));
      if (image.getWidth() <= maxWidth) {
        logger.info("makeThumbnail:no Scaled,image.getWidth()=" + image.getWidth());
        return saveFile(inputStream, path, filename);
      }

      int width = maxWidth;
      int height = image.getHeight() / (image.getWidth() / maxWidth);

      // System.out.println("高是："+width+"宽是："+height);

      // 写图片信息
      Image img = image.getScaledInstance(width, height, Image.SCALE_DEFAULT);

      BufferedImage oImage = new BufferedImage(width, height, Image.SCALE_DEFAULT);

      oImage.getGraphics().drawImage(img, 0, 0, null);
      if (SmbFileUtil.isSmbFileFormat(path)) {
        SmbFile file = new SmbFile(path + filename);
        if (!file.exists()) {
          file.createNewFile();
        }
        outputStream = file.getOutputStream();
        logger.info("upload file success.="+path + filename);
        ImageIO.write(oImage, SystemConstants.UploadFile_imgtype, outputStream);
      } else {
        File file = new File(path + filename);
        ImageIO.write(oImage, SystemConstants.UploadFile_imgtype, file);
        logger.info("upload file success.="+file.getAbsolutePath());
      }
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("", e);
      return false;
    } finally {
      long endTime = System.currentTimeMillis() - startTime;
      logger.info("makeThumbnail  count time(ms)="+endTime);
      if (outputStream != null) {
        
        try {
          outputStream.close();
        } catch (IOException e) {
          logger.error("", e);
        }
      }
    }

  }
  /**
   * 制作缩略图
   * 
   * @param inputStream
   * @param path
   * @param filename
   */
  public static boolean makeThumbnail(InputStream inputStream, String path, String filename) {
    String img_max_with=ProjectProperties.getProperty("img_max_with", "200");
    return makeThumbnail(inputStream,path,filename,Integer.valueOf(img_max_with));
   

  }

  /**
   * 删除文件
   * 
   * @param path
   * @return
   */
  public static String deleteFile(String path) {
    String msg = null;
    File file = new File(path);
    if (!file.exists()) {
      logger.error("删除失败，文件不存在：" + path);
    } else if (file.isDirectory()) {
      logger.error("目标地址是一个文件夹：" + path);
      msg = "目标地址是一个文件夹";
    }
    boolean d = file.delete();
    if (!d) {
      msg = "删除失败";
    }
    return msg;
  }

  /** 添加smb删除文件 **/
  /**
   * 删除文件
   * 
   * @param path
   * @param username
   * @param password
   * @return
   */
  public static String deletesmbFile(String path, String username, String password) {
    String msg = null;

    if (!SmbFileUtil.isSmbFileFormat(path)) {
      path = path.replaceAll("\\\\", "/").trim();
      if (!StringUtils.isEmpty(username)) {
        while (path.startsWith("/")) {
          path = StringUtils.removeStart(path, "/");
        }
        path = "smb://" + username + ":" + password + "@" + path;
      } else
        path = "smb:" + path;
    }

    System.out.println(path);

    try {
      SmbFile file = new SmbFile(path);
      if (!file.exists()) {
        logger.error("删除失败，文件不存在：" + path);
      } else if (file.isDirectory()) {
        logger.error("目标地址是一个文件夹：" + path);
        msg = "目标地址是一个文件夹";
      } else {
        file.delete();
        logger.error("删除文件成功：" + file.getPath());
      }
    } catch (Exception e) {
      logger.error("删除文件出错：" + e.getMessage());
      msg = "删除文件出错";
      e.printStackTrace();
    }

    return msg;

  }
}
