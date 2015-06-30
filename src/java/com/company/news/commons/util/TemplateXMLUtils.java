package com.company.news.commons.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.log4j.Logger;



public class TemplateXMLUtils {
  private static Logger logger = Logger.getLogger(TemplateXMLUtils.class);

  public static String loadTaskFile(){
    StringBuffer sb = new StringBuffer();
    try {
        InputStream is = TemplateXMLUtils.class.getClassLoader().getResourceAsStream("mpc_task_template.xml");
        InputStreamReader ir = new InputStreamReader(is,Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(ir);
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        return sb.toString();
    } catch (Exception e) {
        System.out.println("Message Send Failed!");
        e.printStackTrace();
        return sb.toString();
    }
}

public static String loadTaskFile(String filename){
    StringBuffer sb = new StringBuffer();
    try {
        InputStream is = TemplateXMLUtils.class.getClassLoader().getResourceAsStream(filename);
        InputStreamReader ir = new InputStreamReader(is,Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(ir);
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        return sb.toString();
    } catch (Exception e) {
        logger.error("Message Send Failed!filename="+filename);
        e.printStackTrace();
        return sb.toString();
    }
}
}
