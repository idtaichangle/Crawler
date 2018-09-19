package com.cvnavi.task;

import com.cvnavi.ocr.ShenZhenTongCaptcha;
import com.cvnavi.util.HttpUtil;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShenZhenTongTask extends AbstractDailyTask {

    static Logger log = LogManager.getLogger(ShenZhenTongTask.class);

    public Schedule[] initSchedules() {
        return emptySchedules;
    }

    int cardnum=330312859;
    public void doTask() {
        cardnum+=1;
        log.info("cardnum:"+cardnum);
        String url="https://www.shenzhentong.com/service/fplist_101007009_"+cardnum+"_20180915.html";
        String s=HttpUtil.doHttpGet(url);//,null,null,HttpUtil.RANDOM_PROXY);
        if(s.contains("充值金额") && !s.contains("class=\"listtable\" style=\"display:none;\"")){
            log.error(s);
            try {
                List<String> list= Files.readAllLines(Paths.get("E:\\szt.log"));
                list.add(cardnum+"");
                FileUtils.writeLines(new File("E:\\szt.log"),list);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void check(String cardnum){
        CloseableHttpClient hc= HttpClientBuilder.create().build();
        try {
            String captcha=getCaptcha(hc);
            if(captcha==null){
                captcha=getCaptcha(hc);
            }

            if(captcha!=null){
                HttpPost post=new HttpPost("https://www.shenzhentong.com//Ajax/ElectronicInvoiceAjax.aspx");
                //装填参数
                List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
                nvps.add(new BasicNameValuePair("tp","1"));
                nvps.add(new BasicNameValuePair("yzm",captcha));
                nvps.add(new BasicNameValuePair("cardnum",cardnum));
                post.setEntity(new UrlEncodedFormEntity(nvps));
                CloseableHttpResponse resp=hc.execute(post);
                HttpEntity entity= resp.getEntity();
                System.out.println(EntityUtils.toString(entity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCaptcha(CloseableHttpClient client) throws IOException {
        CloseableHttpResponse resp=client.execute(new HttpGet("https://www.shenzhentong.com/ajax/WaterMark.ashx"));
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
