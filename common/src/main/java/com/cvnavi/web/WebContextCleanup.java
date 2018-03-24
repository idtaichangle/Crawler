package com.cvnavi.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * web context销毁时，做清理工作
 */
public class WebContextCleanup {

    static Logger log = LogManager.getLogger(WebContextCleanup.class);

    private static List<AutoCloseable> list = new ArrayList<>();


    /**
     * 注册。当web app context销毁时，会关闭这些可关闭对象。
     *
     * @param closeable
     */
    public static void registeCloseable(AutoCloseable closeable) {
        if (list.indexOf(closeable) == -1) {
            list.add(closeable);
        }
    }

    public static void doClose(){
        for (AutoCloseable closeable : list) {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
