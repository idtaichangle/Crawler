package com.cvnavi.browser.wb;

import com.webrenderer.swing.event.NetworkAdapter;
import com.webrenderer.swing.event.NetworkEvent;

public class ListenerAdapter extends NetworkAdapter {
	private ListenerAdapter listener;
	public String result = " ";

	public void setListener(ListenerAdapter listener) {
		this.listener = listener;
	}

	@Override
	public void onDocumentComplete(NetworkEvent event) {
		if (listener != null) {
			listener.onDocumentComplete(event);
		}
	}

	@Override
	public void onHTTPResponse(NetworkEvent event) {
		if (listener != null) {
			listener.onHTTPResponse(event);
		}
	}
}
