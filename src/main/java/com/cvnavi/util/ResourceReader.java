package com.cvnavi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ResourceReader {

	public static List<String> readLines(String file) {
		List<String> list = new ArrayList<>();
		try {
			InputStream is = ResourceReader.class.getResourceAsStream(file);
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0 && !line.startsWith("#")) {
					list.add(line);
				}
			}
			br.close();
			isr.close();
			is.close();
		} catch (IOException e) {
		}
		return list;
	}

	public static Properties readProperties(String file) {
		Properties p = new Properties();
		try {
			InputStream is = ResourceReader.class.getResourceAsStream(file);
			p.load(is);
			is.close();
		} catch (IOException e) {
		}
		return p;
	}
}
