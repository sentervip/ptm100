package com.jxcy.smartsensor.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hndw.smartlibrary.Presenter.IBaseXPresenter;
import com.hndw.smartlibrary.view.BaseActivity;
import com.jxcy.smartsensor.R;
import com.jxcy.smartsensor.view.unit.NurserKnow;

public class WebActivity extends BaseActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_layout);
        mWebView = findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        if (webSettings == null) return;
        // 支持 Js 使用
        webSettings.setJavaScriptEnabled(true);
        // 支持自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        // 设置 WebView 的缓存模式
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 支持启用缓存模式
        webSettings.setAppCacheEnabled(true);
        // 设置 AppCache 最大缓存值(现在官方已经不提倡使用，已废弃)
        webSettings.setAppCacheMaxSize(8 * 1024 * 1024);
        // Android 私有缓存存储，如果你不调用setAppCachePath方法，WebView将不会产生这个目录
        webSettings.setAppCachePath(getCacheDir().getAbsolutePath());
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 设置 UserAgent 属性
        webSettings.setUserAgentString("");
        // 允许加载本地 html 文件/false
        webSettings.setAllowFileAccess(true);
        // 允许通过 file url 加载的 Javascript 读取其他的本地文件,Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        webSettings.setAllowFileAccessFromFileURLs(false);
        // 允许通过 file url 加载的 Javascript 可以访问其他的源，包括其他的文件和 http，https 等其他的源，
        // Android 4.1 之前默认是true，在 Android 4.1 及以后默认是false,也就是禁止
        // 如果此设置是允许，则 setAllowFileAccessFromFileURLs 不起做用
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        //设置在当前WebView继续加载网页
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String url = intent.getStringExtra("ask_uri");
        if (url != null) {
            mWebView.loadUrl(url);
        }
    }

    @Override
    protected void onResume() {
        if (mWebView != null) {
            mWebView.resumeTimers();
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mWebView != null) {
            mWebView.pauseTimers();
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.stopLoading();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.removeAllViews();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            unregisterForContextMenu(mWebView);
            mWebView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public IBaseXPresenter onBindPresenter() {
        return null;
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        //WebView代表是当前的WebView
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            //表示在当前的WebView继续打开网页
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override //监听加载进度
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override//接受网页标题
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            //把当前的Title设置到Activity的title上显示
            setTitle(title);
        }
    }
}
