package com.cvnavi.proxy;

import org.apache.http.HttpHost;

import java.util.Random;

public class ProxyProvider {

    static ProxyProviderSource source;
    /**
     * 从有效的代理中随机取一个代理。
     *
     * @return
     */
    public static HttpHost getRandomProxy() {
        if (source.getAliveProxies().size() > 0) {
            int i = new Random().nextInt(source.getAliveProxies().size());
            HttpHost[] temp = new HttpHost[0];
            temp = source.getAliveProxies().toArray(temp);
            return temp[i];
        }
        return null;
    }

    public static void register(ProxyProviderSource s){
        source=s;
    }
}
