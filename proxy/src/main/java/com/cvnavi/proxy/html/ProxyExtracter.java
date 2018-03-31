package com.cvnavi.proxy.html;

import org.apache.http.HttpHost;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  提示网页里的代理ip和端口
 */
public class ProxyExtracter {

    protected static String IP_PATTERN = "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])";
    protected static String PORT_PATTERN = "\\d{2,5}";

    public static HashSet<HttpHost> extractProxy(String html){
        HashSet<HttpHost> set = new HashSet<>();
        Document doc=Jsoup.parse(html);

        doc.body().select("*[style*=display:none]").remove();
        doc.body().select("*[style*=display: none]").remove();

        HtmlToPlainText formatter = new HtmlToPlainText();
        String text=formatter.getPlainText(doc);

        Pattern p1 = Pattern.compile(IP_PATTERN);
        Pattern p2 = Pattern.compile(PORT_PATTERN);
        Matcher m = p1.matcher(text);

        while (m.find()) {
            String ip=m.group();
            String port=null;
            Matcher m2=p2.matcher(text.substring(m.end()+1));
            if(m2.find()){
                port=m2.group();
            }
            if(ip!=null && port!=null){
                set.add(new  HttpHost(ip,Integer.parseInt(port)));
                //System.out.println(new  HttpHost(ip,Integer.parseInt(port)));
            }
        }
        return  set;
    }
}
