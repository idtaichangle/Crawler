package com.cvnavi.task;

import com.cvnavi.ocr.ShenZhenTongCaptcha;
import com.cvnavi.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ShenZhenTongTask extends AbstractDailyTask {

    static Logger log = LogManager.getLogger(ShenZhenTongTask.class);
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
    Calendar c= Calendar.getInstance();
    int cardnum=330320000;
    String date= null;
    boolean needCaptcha=true;
    HashMap<String,String> cookies=new HashMap<String, String>();
    String resultFile="C:\\szt.log";

    public ShenZhenTongTask(){
        InputStream is= getClass().getResourceAsStream("/ocr.properties");
        Properties p=new Properties();
        try {
            p.load(is);
            cardnum=Integer.parseInt(p.getProperty("cardnum"));
            resultFile=p.getProperty("resultFile");
            is.close();
        } catch (IOException e) {
            log.error(e);
        }
        c.add(Calendar.DAY_OF_MONTH,-80);
        date=sdf.format(c.getTime());

        File f=new File(resultFile);
        if(!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    public Schedule[] initSchedules() {
        return emptySchedules;
    }

    private void nextDay(){
        c.add(Calendar.DAY_OF_MONTH,1);
        if(System.currentTimeMillis()-c.getTimeInMillis()<7*24*3600*1000){
            cardnum+=1;
            c=Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH,-80);
            needCaptcha=true;
            log.info("cardnum:"+cardnum);
        }
        date=sdf.format(c.getTime());
    }

    public void doTask() {
        nextDay();
        if(needCaptcha){
            needCaptcha=false;
            if(!checkCaptcha()){
                checkCaptcha();
            }
        }
        String url="https://www.shenzhentong.com/service/fplist_101007009_"+cardnum+"_"+date+".html";
        String s=HttpUtil.doHttpGet(url,null,cookies,null);
        Element ele=Jsoup.parse(s).selectFirst(".listtable");

        if(ele.select("tr").size()>1){
            Element  amount=ele.select("tr").get(1).selectFirst(".tdtjamt");
            try {
                String line=cardnum+","+date+","+amount.text()+"\r\n";
                FileUtils.write(new File(resultFile),line,"UTF-8",true);
            } catch (IOException e) {
                log.error(e);
            }
        }
    }

    private boolean checkCaptcha(){
        String captcha= null;
        try {
            captcha = getCaptcha();
            if(captcha==null){
                captcha=getCaptcha();
            }
            if(captcha!=null){
                String url="https://www.shenzhentong.com//Ajax/ElectronicInvoiceAjax.aspx";
                HashMap<String,String> params=new HashMap<String, String>();
                params.put("tp","1");
                params.put("yzm",captcha);
                params.put("cardnum",cardnum+"");
                String s=HttpUtil.doHttpPost(url,params,null,cookies,null);
                log.debug(s);
                if(s.contains("100")){
                    return true;
                }
            }
        } catch (IOException e) {
            log.error(e);
        }
        return false;
    }

    public String getCaptcha() throws IOException {
        HttpGet get=new HttpGet("https://www.shenzhentong.com/ajax/WaterMark.ashx");
        CloseableHttpResponse resp=HttpUtil.sendHttp(get,null,null,null,5000, Level.INFO);
        Header header= resp.getFirstHeader("Set-Cookie");
        if(header!=null){
            String key=header.getValue().split(";")[0].split("=")[0];
            String value=header.getValue().split(";")[0].split("=")[1];
            cookies.put(key,value);
        }
        HttpEntity entity= resp.getEntity();
        BufferedImage img= ImageIO.read(entity.getContent());
        EntityUtils.consume(entity);
        String s=ShenZhenTongCaptcha.doOcr(img);
        s=s.replaceAll(" ","").replaceAll("\n","");
        if(s.length()==3){
            int c1=s.charAt(0)-48;
            int c2=s.charAt(1)-48;
            int c3=s.charAt(2)-48;
            int c=c1+c2+c3;
            return c+"";
        }
        return null;
    }

    public static void main(String[] args) {
        new ShenZhenTongTask().doTask();
    }
}
