package com.cvnavi.browser.wb;

import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

/**
 * 某些情况下，会弹出对话框（例如下载文件，或者key到期）。
 * 需要关闭对话框后继续。本类开启后台线程监控。
 * 
 * @author lixy
 *
 */
public class DialogWatcher implements Runnable {
	boolean running = true;

	@Override
	public void run() {
		while (running) {
			try {
				Thread.sleep(30);
			} catch (Exception ex) {
			}
			for (Window w : Window.getWindows()) {
				if (w instanceof JDialog) {
					closeDialog((JDialog) w);
				}
			}
		}
	}

	/**
	 * 某些情况下，会弹出对话框（例如下载文件，或者key到期）。需要关闭对话框后继续。
	 */
	protected void closeDialog(final JDialog dialog) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
	}

	public void startWatch() {
		running = true;
		new Thread(this).start();
	}

	public void stopWatch() {
		running = false;
	}
}
