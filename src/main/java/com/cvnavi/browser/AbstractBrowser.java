package com.cvnavi.browser;

import javax.swing.JComponent;

public abstract class AbstractBrowser {
	public static final int HTTP_GET = 1;
	public static final int HTTP_POST = 2;

	public abstract JComponent getBrowserView();
	
	public abstract void dispose();
	
	public abstract String loadURL(String url, String proxy);

	public abstract String getScode(String proxy);

	public abstract String loginMarinecircle(String proxy);

	public abstract String loginShipxy(String userName, String password,String proxy);
}
