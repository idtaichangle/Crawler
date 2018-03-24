package com.cvnavi.ais.myships;

import java.text.SimpleDateFormat;
import java.util.List;

public abstract class AbstractCmd {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public abstract String getCmdUrl(String... args);

	public abstract List<?> doCmd(String... args);
	
	/**
	 * 将度、分、秒转换成　带小数的度。
	 * @param src
	 * @return
	 */
	public String convertDegree(String src){
		int i=Integer.parseInt(src.split("°")[0]);
		String fra=src.split("°")[1];
		if(fra.contains("\"")){
			fra=fra.substring(0,fra.indexOf("\""));
		}
		if(Character.isLetter(fra.charAt(fra.length()-1))){
			fra=fra.substring(0,fra.length()-1);
		}
		return (i+(Float.parseFloat(fra.split("'")[0])+(Float.parseFloat(fra.split("'")[1])/60))/60)+"";
	}
}
