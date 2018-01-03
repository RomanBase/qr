package com.ankhrom.base.custom.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ankhrom.base.R;

public class InternalWebView extends WebView {

    private int fontSize;
    private boolean isJavaScriptEnabled;

    public InternalWebView(Context context) {
        super(context);
    }

    public InternalWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(context, attrs);
    }

    @Deprecated
    public InternalWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InternalWebView, 0, 0);
        try {
            fontSize = (int) ta.getDimension(R.styleable.InternalWebView_fontSize, 0);
            isJavaScriptEnabled = ta.getBoolean(R.styleable.InternalWebView_js_enabled, false);
        } finally {
            ta.recycle();
        }

        setWebSettings();
        setWebClient();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected WebSettings setWebSettings() {

        final WebSettings webSettings = getSettings();
        if (fontSize > 0) {
            webSettings.setDefaultFontSize(fontSize);
        }
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setAppCacheEnabled(false);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setGeolocationEnabled(false);
        webSettings.setNeedInitialFocus(false);
        webSettings.setSaveFormData(false);
        if (isJavaScriptEnabled) {
            webSettings.setJavaScriptEnabled(true);
        }

        return webSettings;
    }

    protected WebViewClient setWebClient() {

        WebViewClient webClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };

        setWebViewClient(webClient);

        return webClient;
    }
}
