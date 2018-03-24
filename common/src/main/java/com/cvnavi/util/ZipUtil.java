package com.cvnavi.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipUtil {
	static Logger log=LogManager.getLogger(ZipUtil.class);
	
	public static byte[] gZip(byte[] data) {
		byte[] b = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(bos);
			gzip.write(data);
			gzip.finish();
			gzip.close();
			b = bos.toByteArray();
			bos.close();
		} catch (Exception e) {
			log.error(e);
		}
		return b;
	}

	/**
	 * zlib解压缩byte数组
	 * 
	 * @param object
	 * @return
	 */
	public static byte[] unZlib(byte[] bytesToDecompress) {
 
		ByteArrayInputStream bais = new ByteArrayInputStream(bytesToDecompress);
        InflaterInputStream iis = new InflaterInputStream(bais);

        byte[] result=new byte[0];
        byte[] buffer = new byte[1024];
        int rlen = -1;
        try {
			while ((rlen = iis.read(buffer)) != -1) {
				byte[] temp=new byte[result.length+rlen];
				System.arraycopy(result, 0, temp, 0, result.length);
				System.arraycopy(buffer, 0, temp,  result.length, rlen);
				result=temp;
			}
		} catch (IOException e) {
			log.error(e);
		}
        return result;
	}
}
