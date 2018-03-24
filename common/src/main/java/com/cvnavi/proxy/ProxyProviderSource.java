package com.cvnavi.proxy;

import org.apache.http.HttpHost;

import java.util.Collection;

public interface ProxyProviderSource {
    public Collection<HttpHost> getAliveProxies();
}
