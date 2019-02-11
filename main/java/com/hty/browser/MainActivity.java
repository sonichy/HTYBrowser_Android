package com.hty.browser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TextView findCount;
    EditText editText1, findText;
    ImageButton btnGo, btnBack, btnForward, btnMenu, findPrev, findNext, findClose;
    WebView webView1;
    // RelativeLayout RelativeLayout1;
    LinearLayout LinearLayout1, LinearLayout2;
    FrameLayout video, findbar;
    ImageView imageView1;
    InputMethodManager IMM;
    ProgressBar pgb1;
    String urlo = "", HTRE = "", ptitle = "", urln = "";
    String urlVersion = "https://raw.githubusercontent.com/sonichy/Android_HTYBrowser/master/version";
    String urlUpdate = "https://raw.githubusercontent.com/sonichy/Android_HTYBrowser/master/app.apk";
    CustomViewCallback customViewCallback;
    boolean isFullScreen, isManualCheckUpdate = false;
    static File dir;
    SharedPreferences sharedPreferences;
    Thread CU;
    long downloadIdUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        IMM = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "HTYBrowser";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        setContentView(R.layout.activity_main);
        //ptitle = "百度";
        //urln = "http//www.baidu.com";
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        LinearLayout2 = (LinearLayout) findViewById(R.id.LinearLayout2);
        // RelativeLayout1 = (RelativeLayout)
        // findViewById(R.id.RelativeLayout1);
        video = (FrameLayout) findViewById(R.id.video);
        findbar = (FrameLayout) findViewById(R.id.findbar);
        pgb1 = (ProgressBar) findViewById(R.id.progressBar1);
        // if (VERSION.SDK_INT > 18) {
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // getWindow().addFlags(
        // WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        // LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
        // LinearLayout1
        // .getLayoutParams();
        // lp.topMargin = 45;
        // LinearLayout1.setLayoutParams(lp);
        // // RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
        // // RelativeLayout1
        // // .getLayoutParams();
        // // lp.topMargin = 45;
        // // RelativeLayout1.setLayoutParams(lp);
        // }
        btnGo = (ImageButton) findViewById(R.id.ImageButtonGo);
        btnBack = (ImageButton) findViewById(R.id.ImageButtonBack);
        btnForward = (ImageButton) findViewById(R.id.ImageButtonForward);
        btnMenu = (ImageButton) findViewById(R.id.ImageButtonMenu);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        findText = (EditText) findViewById(R.id.findText);
        findCount = (TextView) findViewById(R.id.findCount);
        findPrev = (ImageButton) findViewById(R.id.findPrev);
        findNext = (ImageButton) findViewById(R.id.findNext);
        findClose = (ImageButton) findViewById(R.id.findClose);
        // imageView1.setAlpha(100);
        btnGo.setOnClickListener(new ButtonListener());
        btnBack.setOnClickListener(new ButtonListener());
        btnForward.setOnClickListener(new ButtonListener());
        btnMenu.setOnClickListener(new ButtonListener());
        imageView1.setOnClickListener(new ButtonListener());
        findPrev.setOnClickListener(new ButtonListener());
        findNext.setOnClickListener(new ButtonListener());
        findClose.setOnClickListener(new ButtonListener());
        editText1 = (EditText) findViewById(R.id.EditText1);
        webView1 = (WebView) findViewById(R.id.WebView1);
        registerForContextMenu(webView1);
        // 开启JS
        webView1.getSettings().setJavaScriptEnabled(true);
        // 开启JS能打开窗口
        webView1.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启缓存
        webView1.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // 自适应屏幕
        webView1.getSettings().setUseWideViewPort(true);
        webView1.getSettings().setLoadWithOverviewMode(true);
        // 支持缩放
        webView1.getSettings().setSupportZoom(true);
        // 启用内置缩放控件
        webView1.getSettings().setBuiltInZoomControls(true);
        // 隐藏缩放控件
        webView1.getSettings().setDisplayZoomControls(false);
        // 开启访问文件
        webView1.getSettings().setAllowFileAccess(true);
        // 开启数据库
        webView1.getSettings().setDatabaseEnabled(true);
        // 开启localStorage
        webView1.getSettings().setDomStorageEnabled(true);
        // 开启定位
        webView1.getSettings().setGeolocationEnabled(true);
        // 支持多窗口
        webView1.getSettings().supportMultipleWindows();
        // 支持获取手势焦点
        webView1.requestFocusFromTouch();

        webView1.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("OverrideUrlLoading", url);
                // 协议过滤
                if(url.startsWith("http")){
                    view.loadUrl(url);
                }
				/*
				else if(url.startsWith("tbopen://")){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    intent.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
                    startActivity(intent);
                }
                */
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                editText1.setText(url);
                urln = url;
                btnBack.setEnabled(true);
                IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
                pgb1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pgb1.setVisibility(View.INVISIBLE);
                String js = "";
                // 链接关键字链接高亮、屏蔽
                js = "javascript:var a=document.getElementsByTagName('a');for(var i=0;i<a.length;i++){if(a[i].textContent.indexOf('国产')!=-1){a[i].style.color='white';a[i].style.backgroundColor='#DA3434';}if(a[i].textContent.indexOf('习近平')!=-1 || a[i].textContent.indexOf('总书记')!=-1){a[i].textContent='';}}";
                view.loadUrl(js);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(getApplicationContext(), "ReceivedError:" + errorCode, Toast.LENGTH_SHORT).show();
                if (isNetworkConnected()) {
                    switch(errorCode){
                        case WebViewClient.ERROR_HOST_LOOKUP:   // 找不到主机，跳转百度搜索
                            Log.e("ErrorHostLookup", failingUrl);
                            String url = "http://m.baidu.com/s?word=" + urlo;
                            editText1.setText(url);
                            webView1.loadUrl(url);
                            urln = url;
                            break;
                        case WebViewClient.ERROR_UNSUPPORTED_SCHEME:
                            Log.e("ErrorUnsupportedScheme",failingUrl);
                    }
                } else {
                    webView1.loadDataWithBaseURL(
                            "",
                            "<html><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/><style>h1{font-size:10vw;margin:40vh auto;text-align:center;}</style><h1>网络未连接</h1></html>",
                            "text/html", "utf-8", "");
                }
            }

        });

        webView1.setWebChromeClient(new WebChromeClient() {

            // JS的alert('')提示信息转换成安卓控件提示信息
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
            }

            // HTML5.input.file转换为安卓文件选择器
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }

            // 进度条
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                pgb1.setProgress(newProgress);
				/*
				if(!isFullScreen) {
					if (newProgress == 100) {
						pgb1.setVisibility(View.INVISIBLE);
					} else {
						pgb1.setVisibility(View.VISIBLE);
					}
				}
				*/
                if(sharedPreferences.getBoolean("switch_adBlock",true)){
                    ADBlock();
                }
                if(sharedPreferences.getBoolean("switch_iframeBlock",false)) {
                    //Log.e("line292", "progress:" + newProgress);
                    if (!view.getUrl().contains("baidu.com")){
                        iframeBlock();
                    }
                }
            }

            // 获取网页标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                ptitle = title;
            }

            // 播放网络视频时全屏会被调用的方法
            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                Log.e("onShowCustomView", "onShowCustomView");
                //Toast.makeText(getApplicationContext(), "onShowCustomView", Toast.LENGTH_SHORT).show();
                customViewCallback = callback;
                // 将video放到当前视图中
                video.addView(view);
                // 设置全屏
                setFullScreen();
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }

            // 视频播放退出全屏会被调用的
            @Override
            public void onHideCustomView() {
                Log.e("onHideCustomView", "onHideCustomView");
                //Toast.makeText(getApplicationContext(), "onHideCustomView", Toast.LENGTH_SHORT).show();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                // 退出全屏
                // quitFullScreen();
            }

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("onConsoleMessage", consoleMessage.message() + " at " + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber());
                return super.onConsoleMessage(consoleMessage);
            }

            // 定位权限
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

        });

        webView1.setDownloadListener(new MyWebViewDownLoadListener());

        webView1.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                webView1.requestFocus();
                IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
                return false;
            }
        });

        editText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    loadPage(editText1.getText().toString());
                    return true;
                }
                return false;
            }
        });

        findText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    webView1.findAllAsync(findText.getText().toString());
                    IMM.hideSoftInputFromWindow(findText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        findText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                // text 输入框中改变后的字符串信息
                // start 输入框中改变后的字符串的起始位置
                // before 输入框中改变前的字符串的位置 默认为0
                // count 输入框中改变后的一共输入字符串的数量
                webView1.findAllAsync(text.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
                // text 输入框中改变前的字符串信息
                // start 输入框中改变前的字符串的起始位置
                // count 输入框中改变前后的字符串改变数量一般为0
                // after 输入框中改变后的字符串与起始位置的偏移量
            }

            @Override
            public void afterTextChanged(Editable edit) {
                // edit 输入结束呈现在输入框中的信息
            }
        });

        Intent intent = getIntent();
        if (intent.ACTION_VIEW.equals(intent.getAction())) {
            urln = intent.getDataString();
            loadPage(urln);
        }else{
            loadPage(sharedPreferences.getString("homepage","http://www.baidu.com"));
        }

        webView1.setFindListener(new FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches,
                                             boolean isDoneCounting) {
                findCount.setText(activeMatchOrdinal + "/" + numberOfMatches);
            }
        });


        CU = new Thread() {
            @Override
            public void run() {
                checkUpdate();
            }
        };
        new Thread(CU).start();

        DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(receiver, intentFilter);
    }

    private void setFullScreen() {
        // RelativeLayout1.setVisibility(View.GONE);
        LinearLayout1.setVisibility(View.GONE);
        video.setVisibility(View.VISIBLE);
        // imageView1.setVisibility(View.VISIBLE);
        // 横屏显示
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 全屏下的状态码：1098974464
        // 窗口下的状态吗：1098973440
        isFullScreen = true;
    }

    // 退出全屏
    private void quitFullScreen() {
        if (customViewCallback != null) {
            // 隐藏掉
            customViewCallback.onCustomViewHidden();
        }
        // RelativeLayout1.setVisibility(View.VISIBLE);
        LinearLayout1.setVisibility(View.VISIBLE);
        LinearLayout2.setVisibility(View.VISIBLE);
        pgb1.setVisibility(View.VISIBLE);
        video.setVisibility(View.GONE);
        // imageView1.setVisibility(View.GONE);
        // 用户当前的首选方向
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        isFullScreen = false;
    }

    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;
    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    class ButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ImageButtonGo:
                    loadPage(editText1.getText().toString());
                    break;
                case R.id.ImageButtonBack:
                    if (webView1.canGoBack()) {
                        webView1.goBack();
                        btnForward.setEnabled(true);
                    } else {
                        btnBack.setEnabled(false);
                    }
                    break;
                case R.id.ImageButtonForward:
                    if (webView1.canGoForward()) {
                        webView1.goForward();
                        btnBack.setEnabled(true);
                    } else {
                        btnForward.setEnabled(false);
                    }
                    break;
                case R.id.ImageButtonMenu:
                    MenuDialog();
                    break;
                case R.id.imageView1:
                    playVideo();
                    imageView1.setVisibility(View.GONE);
                    break;
                case R.id.findPrev:
                    webView1.findNext(false);
                    break;
                case R.id.findNext:
                    webView1.findNext(true);
                    break;
                case R.id.findClose:
                    findText.setText("");
                    IMM.hideSoftInputFromWindow(findText.getWindowToken(), 0);
                    findbar.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            //			Uri uri = Uri.parse(url);
            //			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //			startActivity(intent);
            downloadBySystem(url,"","");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        WebView w = (WebView) v;
        HitTestResult result = w.getHitTestResult();
        HTRE = result.getExtra();
        menu.setHeaderTitle(HTRE);
        if (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            menu.add(0, 0, 0, "查看图片");
            menu.add(0, 1, 1, "复制图片");
            menu.add(0, 2, 2, "保存图片");
            menu.add(0, 3, 3, "复制链接");
            menu.add(0, 4, 4, "屏蔽图片");
            menu.add(0, 5, 5, "隐藏图片");
        }
        if (result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
            menu.add(0, 2, 2, "下载");
            menu.add(0, 3, 3, "复制链接");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                webView1.loadUrl(HTRE);
                break;
            case 1:
                ClipboardManager mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ContentValues values = new ContentValues(2);
                values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg");
                values.put(MediaStore.Images.Media.DATA, HTRE);
                ContentResolver theContent = getContentResolver();
                Uri imageUri = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                ClipData theClip = ClipData.newUri(getContentResolver(), "Image", imageUri);
                mClipboard.setPrimaryClip(theClip);
                break;
            case 2:
                downloadBySystem(HTRE,"","");
                break;
            case 3:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("link", HTRE));
                Toast.makeText(getApplicationContext(), "链接已复制", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                DialogBlock(HTRE);
                break;
            case 5:
                String js = "javascript:var imgs=document.getElementsByTagName('img');for(i=0;i<imgs.length;i++){if(imgs[i].src=='" + HTRE +"'){imgs[i].style.display='none';break;}}";
                webView1.loadUrl(js);
            	break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFullScreen) {
                imageView1.setVisibility(View.GONE);
                quitFullScreen();
            } else {
                if (webView1.canGoBack()) {
                    webView1.goBack();
                } else {
                    // moveTaskToBack(false);
                    // MenuDialog();
                }
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            webView1.findAllAsync(findText.getText().toString());
            IMM.hideSoftInputFromWindow(findText.getWindowToken(), 0);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_F) {
            findbar.setVisibility(View.VISIBLE);
            findText.requestFocus();
            webView1.findAllAsync(findText.getText().toString());
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            findbar.setVisibility(View.GONE);
            // Toast.makeText(getApplicationContext(), "Esc Pressed",
            // Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    void loadPage(String url) {
        urlo = url;
        if (url.indexOf("://") == -1) {
            url = "http://" + url;
        }
        // editText1.setText(url);
        Log.e("webview", url);
        IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
        findText.setText("");
        webView1.loadUrl(url);
        // btnBack.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[] { result });
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[] {});
            }
            mUploadMessageForAndroid5 = null;
        }
        if (requestCode == 0) {
            if (intent != null)
                webView1.loadUrl(intent.getStringExtra("url"));
        }
    }

    private class SaveImage extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            String imgurl = HTRE;
            try {
                String sdcard = Environment.getExternalStorageDirectory().toString();
                File file = new File(sdcard + "/Download");
                if (!file.exists()) {
                    file.mkdirs();
                }
                int idx = imgurl.lastIndexOf("/");
                String filename = imgurl.substring(idx);
                String filepath = sdcard + "/Download/" + filename;
                file = new File(filepath);
                InputStream inputStream = null;
                URL url = new URL(imgurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(20000);
                if (conn.getResponseCode() == 200) {
                    inputStream = conn.getInputStream();
                }
                byte[] buffer = new byte[4096];
                int len = 0;
                FileOutputStream outStream = new FileOutputStream(file);
                while ((len = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                outStream.close();
                result = "图片已保存至：" + file.getAbsolutePath();
                MediaScannerConnection.scanFile(MainActivity.this, new String[] { filepath }, null, null);
            } catch (Exception e) {
                result = "保存失败！" + e.getLocalizedMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }
    }

    boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        // if (this.getResources().getConfiguration().orientation ==
        // Configuration.ORIENTATION_LANDSCAPE) {
        // Toast.makeText(getApplicationContext(), "横屏", Toast.LENGTH_SHORT)
        // .show();
        // } else if (this.getResources().getConfiguration().orientation ==
        // Configuration.ORIENTATION_PORTRAIT) {
        // Toast.makeText(getApplicationContext(), "竖屏", Toast.LENGTH_SHORT)
        // .show();
        // }
    }

    @Override
    protected void onPause() {
        Log.e("MainActivity", "onPause");
        pauseVideo();
        if (isFullScreen) {
            imageView1.setVisibility(View.VISIBLE);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    void pauseVideo() {
        String js = "javascript:var obj = document.getElementsByTagName('video');for (var i = 0; i < obj.length; i++) { obj[i].pause();}";
        webView1.loadUrl(js);
    }

    void playVideo() {
        String js = "javascript:var obj = document.getElementsByTagName('video');for (var i = 0; i < obj.length; i++) { obj[i].play();}";
        webView1.loadUrl(js);
    }

    void MenuDialog() {
        String[] items = { "收藏当前页", "收藏夹", "查找", "分享", "视频独立播放", "查看源码", "主页", "全屏", "广告过滤规则", "设置", "检查更新", "关于", "退出" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("菜单");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText ET_title = new EditText(MainActivity.this);
                        ET_title.setHint("标题");
                        ET_title.setText(ptitle);
                        layout.addView(ET_title);
                        final EditText ET_url = new EditText(MainActivity.this);
                        ET_title.setHint("网址");
                        ET_url.setText(urln);
                        layout.addView(ET_url);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(android.R.drawable.btn_star);
                        builder.setTitle("添加收藏");
                        builder.setView(layout);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String stitle = ET_title.getText().toString();
                                String surl = ET_url.getText().toString();
                                Field field = null;
                                try {
                                    //通过反射获取dialog中的私有属性mShowing
                                    field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);//设置该属性可以访问
                                } catch (Exception ex) {

                                }
                                if (!stitle.equals("") && surl.startsWith("http")) {
                                    DBHelper helper = new DBHelper(getApplicationContext());
                                    Cursor c = helper.query(surl);
                                    if (c.getCount() == 0) {
                                        ContentValues values = new ContentValues();
                                        values.put("website", surl);
                                        values.put("title", stitle);
                                        helper.insert(values);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "网址已存在", Toast.LENGTH_SHORT).show();
                                        IMM.hideSoftInputFromWindow(ET_title.getWindowToken(), 0);
                                    }
                                    try {
                                        //关闭
                                        field.set(dialog, true);
                                        dialog.dismiss();
                                    } catch (Exception ex) {
                                    }
                                } else {
                                    if (stitle.equals("")){
                                        ET_title.setError("标题不能为空！");
                                    }
                                    if (!surl.startsWith("http")){
                                        ET_url.setError("网址错误！");
                                    }
                                    try {
                                        //设置dialog不可关闭
                                        field.set(dialog, false);
                                        dialog.dismiss();
                                    } catch (Exception ex) {
                                    }
                                }
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IMM.hideSoftInputFromWindow(ET_title.getWindowToken(), 0);
                                Field field = null;
                                try {
                                    //通过反射获取dialog中的私有属性mShowing
                                    field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                    field.setAccessible(true);//设置该属性可以访问
                                } catch (Exception ex) {
                                }
                                try {
                                    field.set(dialog, true);
                                    dialog.dismiss();
                                } catch (Exception ex) {
                                }
                            }
                        });
                        builder.create().show();
                        break;
                    case 1:
                        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case 2:
                        findbar.setVisibility(View.VISIBLE);
                        findText.requestFocus();
                        webView1.findAllAsync(findText.getText().toString());
                        break;
                    case 3:
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, webView1.getUrl());
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "分享"));
                        break;
                    case 4:
                        String js = "javascript:var videos=document.getElementsByTagName('video'); if(videos.length!=0){ var url=videos[0].src; document.body.innerHTML=''; var video=document.createElement('video'); video.style.width='100%'; video.style.height='auto'; video.src=url; video.controls=true; document.body.appendChild(video); var a=document.createElement('a'); a.textContent=url; a.href=url; document.body.appendChild(a); } else { var iframes=document.getElementsByTagName('iframe'); if(iframes.length!=0) window.location.href=iframes[0].src; }";
                        webView1.loadUrl(js);
                        break;
                    case 5:
                        js = "javascript:var s='<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'; document.body.innerHTML=''; var pre=document.createElement('pre'); document.body.appendChild(pre); pre.textContent=s;";
                        webView1.loadUrl(js);
                        break;
                    case 6:
                        webView1.loadUrl(sharedPreferences.getString("homepage",""));
                        break;
                    case 7:
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        LinearLayout2.setVisibility(View.GONE);
                        pgb1.setVisibility(View.GONE);
                        isFullScreen = true;
                        break;
                    case 8:
                        DialogBlockList();
                        break;
                    case 9:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case 10:
                        isManualCheckUpdate = true;
                        new Thread(CU).start();
                        break;
                    case 11:
                        webView1.loadUrl("file:///android_asset/about.htm");
                        break;
                    case 12:
                        MainActivity.this.finish();
                        break;
                }
            }
        });
        builder.create().show();
    }

    void DialogBlock(String content){
        final EditText ETrule = new EditText(this);
        ETrule.setText(content);
        final AlertDialog dialogRule = new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("屏蔽规则").setView(ETrule).setCancelable(false).setPositiveButton("添加",null).setNeutralButton("删除",null).setNegativeButton("取消",null).create();
        dialogRule.show();
        dialogRule.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            boolean exist = false;
                                            String rule = ETrule.getText().toString();
                                            List<String> rulelist = new ArrayList<String>(Arrays.asList(ReadFile("blockrules").split("\n")));
                                            for(int i=0;i<rulelist.size();i++){
                                                if(rulelist.get(i).equals(rule)){
                                                    Toast.makeText(MainActivity.this, "此条规则已经存在！", Toast.LENGTH_SHORT).show();
                                                    exist = true;
                                                    break;
                                                }
                                            }
                                            if(!exist) {
                                                addRule(rule);
                                                dialogRule.dismiss();
                                            }
                                        }
                                    }
                );
        dialogRule.getButton(AlertDialog.BUTTON_NEUTRAL)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String rule = ETrule.getText().toString();
                                            List<String> rulelist = new ArrayList<String>(Arrays.asList(ReadFile("blockrules").split("\n")));
                                            for(int i=0;i<rulelist.size();i++){
                                                if(rulelist.get(i).equals(rule)){
                                                    rulelist.remove(i);
                                                    break;
                                                }
                                            }
                                            String content="";
                                            for(int i=0;i<rulelist.size();i++){
                                                content += rulelist.get(i)+"\n";
                                            }
                                            FileOutputStream outStream = null;
                                            try {
                                                outStream = MainActivity.this.openFileOutput("blockrules", Context.MODE_PRIVATE);
                                                outStream.write((content).getBytes());
                                                outStream.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            dialogRule.dismiss();
                                            ADBlock();
                                        }
                                    }
                );
        dialogRule.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialogRule.dismiss();
                                        }
                                    }
                );
    }

    void addRule(String content){
        FileOutputStream outStream = null;
        try {
            outStream = this.openFileOutput("blockrules", Context.MODE_APPEND);
            outStream.write((content+"\n").getBytes());
            outStream.close();
            ADBlock();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    void DialogBlockList(){
        //Log.e("readfile: ", ReadFile("blockrules"));
        final String[] datas = ReadFile("blockrules").split("\n");
        //Log.e("readfile: ", datas.toString());
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("屏蔽规则列表：");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DialogBlock(datas[which]);
                //Toast.makeText(MainActivity.this, "你点击了第" + which + "个item", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNeutralButton("清空", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("操作不可逆，确认清空吗？");
                builder1.setIcon(R.drawable.ic_launcher);
                builder1.setTitle("警告");
                builder1.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String filepath = getFilesDir().getAbsolutePath() + "/blockrules";
                        File file = new File(filepath);
                        //if(file.isFile() && file.exists()) {
                        Log.e("delete "+filepath, String.valueOf(file.delete()));
                        //}
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder1.create().show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    String ReadFile(String filename) {
        String s = "";
        FileInputStream istream;
        try {
            istream = MainActivity.this.openFileInput(filename);
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            int len;
            while ((len = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, len);
            }
            s = new String(ostream.toByteArray());
            istream.close();
            ostream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

    void ADBlock(){
        String s = ReadFile("blockrules");
        if(!s.equals("")) {
            String rules = s.replace("\n", ",");
            if (rules.endsWith(",")) {
                rules = rules.substring(0, rules.length() - 1);
            }
            String js = "javascript:var rules='" + rules + "';var rule=new Array();rule=rules.split(',');var img=document.getElementsByTagName('img');for(i=0;i<img.length;i++){for(j=0;j<rule.length;j++){if(img[i].src.indexOf(rule[j])!=-1){img[i].style.display='none';}}}";
            webView1.loadUrl(js);
        }
    }

    void iframeBlock(){
        String js = "javascript:var iframes=document.getElementsByTagName('iframe');for(i=0;i<iframes.length;i++){iframes[i].style.display='none';}document.getElementById('win-pop-foot').style.display='none';document.getElementById('win-pop-foot1').style.display='none';";
        webView1.loadUrl(js);
    }

    // 调用系统下载，https://www.jianshu.com/p/6e38e1ef203a
    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
        // request.setTitle("This is title");
        // 设置通知栏的描述
        // request.setDescription("This is description");
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(url, contentDisposition, mimeType);
        Log.e("fileName:", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // 自定义下载路径
        // request.setDestinationUri()
        // request.setDestinationInExternalFilesDir()
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        Log.e("downloadId:", downloadId+"");
        if(url == urlUpdate){
            downloadIdUpdate = downloadId;
        }
    }

    void checkUpdate() {
        try {
            String versionL = MainActivity.this.getPackageManager().getPackageInfo(MainActivity.this.getPackageName(), 0).versionName;
            URL url = new URL(urlVersion);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestMethod("GET");
            //if (conn.getResponseCode() == 200) {
            InputStream IS = conn.getInputStream();
            InputStreamReader ISR = new InputStreamReader(IS);
            BufferedReader bufferReader = new BufferedReader(ISR);
            String versionS = bufferReader.readLine();
            Log.e("Version", versionS + " > " + versionL + " ?");
            String[] AVersionS = versionS.split("\\.");
            String[] AVersionL = versionL.split("\\.");
            //Log.e("Version: ", AVersionS[0] + " > " + AVersionL[0] + " ? " + AVersionS[1] + " > " + AVersionL[1] + " ?");
            if ((Integer.parseInt(AVersionS[0]) > Integer.parseInt(AVersionL[0])) || (Integer.parseInt(AVersionS[1]) > Integer.parseInt(AVersionL[1]))) {
                Looper.prepare();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle("升级");
                builder.setMessage("发现新版本 " + versionS + " ，当前版本 " + versionL + " ，是否升级？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadBySystem(urlUpdate, "", "");
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                Looper.myLooper().loop();
            } else {
                if(isManualCheckUpdate) {
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), "当前版本 " + versionL + " 是最新的版本", Toast.LENGTH_SHORT).show();
                    Looper.myLooper().loop();
                }
                Log.e("检查更新: ", "当前版本是最新的版本");
            }
            //}
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("onReceive.intent", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.e("DownloadId", downloadId + "");
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    if(downloadId == downloadIdUpdate){
                        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                        Log.e("UriDownload", uri.toString());
                        Intent intentn = new Intent(Intent.ACTION_VIEW);
                        intentn.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intentn);
                    }
                }
            }
        }
    }

}