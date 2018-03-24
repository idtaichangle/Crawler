package com.cvnavi.browser;

import java.util.List;

import com.teamdev.jxbrowser.chromium.AuthRequiredParams;
import com.teamdev.jxbrowser.chromium.BeforeRedirectParams;
import com.teamdev.jxbrowser.chromium.BeforeSendHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeSendProxyHeadersParams;
import com.teamdev.jxbrowser.chromium.BeforeURLRequestParams;
import com.teamdev.jxbrowser.chromium.Cookie;
import com.teamdev.jxbrowser.chromium.DataReceivedParams;
import com.teamdev.jxbrowser.chromium.HeadersReceivedParams;
import com.teamdev.jxbrowser.chromium.NetworkDelegate;
import com.teamdev.jxbrowser.chromium.PACScriptErrorParams;
import com.teamdev.jxbrowser.chromium.RequestCompletedParams;
import com.teamdev.jxbrowser.chromium.RequestParams;
import com.teamdev.jxbrowser.chromium.ResponseStartedParams;
import com.teamdev.jxbrowser.chromium.SendHeadersParams;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;

/**
 * 通过JxBrowser访问网页后，如何返回数据。默认情况用DefaultPageHandlerr返回网页的html代码
 */
public abstract  class ListenerAdapter implements LoadListener, NetworkDelegate {

	public byte[] lock = new byte[0];

	private ListenerAdapter listener;


	public void setListener(ListenerAdapter listener) {
		this.listener = listener;
	}

	@Override
	public void onBeforeURLRequest(BeforeURLRequestParams arg0) {
		if (listener != null) {
			listener.onBeforeURLRequest(arg0);
		}
	}

	@Override
	public void onDataReceived(DataReceivedParams arg0) {
		if (listener != null) {
			listener.onDataReceived(arg0);
		}
	}

	@Override
	public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
		if (listener != null) {
			listener.onFinishLoadingFrame(arg0);
		}
	}

	@Override
	public boolean onAuthRequired(AuthRequiredParams arg0) {
		return false;
	}

	@Override
	public void onBeforeRedirect(BeforeRedirectParams arg0) {
	}

	@Override
	public void onBeforeSendHeaders(BeforeSendHeadersParams arg0) {
	}

	@Override
	public void onBeforeSendProxyHeaders(BeforeSendProxyHeadersParams arg0) {
	}

	@Override
	public boolean onCanGetCookies(String arg0, List<Cookie> arg1) {
		return true;
	}

	@Override
	public boolean onCanSetCookies(String arg0, List<Cookie> arg1) {
		return true;
	}

	@Override
	public void onCompleted(RequestCompletedParams arg0) {
	}

	@Override
	public void onDestroyed(RequestParams arg0) {
	}

	@Override
	public void onHeadersReceived(HeadersReceivedParams arg0) {
	}

	@Override
	public void onPACScriptError(PACScriptErrorParams arg0) {
	}

	@Override
	public void onResponseStarted(ResponseStartedParams arg0) {
	}

	@Override
	public void onSendHeaders(SendHeadersParams arg0) {
	}

	@Override
	public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
	}

	@Override
	public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
	}

	@Override
	public void onFailLoadingFrame(FailLoadingEvent arg0) {
	}

	@Override
	public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {
	}

	@Override
	public void onStartLoadingFrame(StartLoadingEvent arg0) {
	}

	public abstract String getResult();
}
