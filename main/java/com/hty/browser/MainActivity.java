package com.hty.browser;

import java.io.BufferedOutputStream;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
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
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.FindListener;
import android.webkit.WebView.HitTestResult;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    Button button_title, button_page;
    TextView textView_searchCount, textView_filesize;
    EditText editText1, editText_search, editText_download_path;
    ImageButton imageButton_go, imageButton_back, imageButton_forward, imageButton_menu, imageButton_searchPrev, imageButton_searchNext, imageButton_searchClose, imageButton_info;
    // RelativeLayout RelativeLayout1;
    LinearLayout LinearLayout1, LinearLayout2;
    FrameLayout webViewLayout, video, searchBar;
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
    List<WebView> list_webView = new ArrayList<>();
    int currentPage;
    int FILECHOOSER_DOWNLOAD_PATH = 3;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        LinearLayout1 = (LinearLayout) findViewById(R.id.LinearLayout1);
        LinearLayout2 = (LinearLayout) findViewById(R.id.LinearLayout2);
        // RelativeLayout1 = (RelativeLayout) findViewById(R.id.RelativeLayout1);
        webViewLayout = (FrameLayout) findViewById(R.id.webViewLayout);
        video = (FrameLayout) findViewById(R.id.video);
        searchBar = (FrameLayout) findViewById(R.id.searchBar);
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
        imageButton_go = (ImageButton) findViewById(R.id.imageButton_go);
        imageButton_back = (ImageButton) findViewById(R.id.imageButton_back);
        imageButton_forward = (ImageButton) findViewById(R.id.imageButton_forward);
        imageButton_menu = (ImageButton) findViewById(R.id.imageButton_menu);
        imageView1 = (ImageView) findViewById(R.id.imageView1);
        editText_search = (EditText) findViewById(R.id.editText_search);
        textView_searchCount = (TextView) findViewById(R.id.textView_searchCount);
        imageButton_searchPrev = (ImageButton) findViewById(R.id.imageButton_searchPrev);
        imageButton_searchNext = (ImageButton) findViewById(R.id.imageButton_searchNext);
        imageButton_searchClose = (ImageButton) findViewById(R.id.imageButton_searchClose);
        imageButton_info = (ImageButton) findViewById(R.id.imageButton_info);
        button_title = (Button) findViewById(R.id.button_title);
        button_page = (Button) findViewById(R.id.button_page);
        button_title.setOnClickListener(new ButtonListener());
        button_page.setOnClickListener(new ButtonListener());
        imageButton_go.setOnClickListener(new ButtonListener());
        imageButton_back.setOnClickListener(new ButtonListener());
        imageButton_forward.setOnClickListener(new ButtonListener());
        imageButton_menu.setOnClickListener(new ButtonListener());
        imageView1.setOnClickListener(new ButtonListener());
        imageButton_searchPrev.setOnClickListener(new ButtonListener());
        imageButton_searchNext.setOnClickListener(new ButtonListener());
        imageButton_searchClose.setOnClickListener(new ButtonListener());
        editText1 = (EditText) findViewById(R.id.EditText1);
        editText1.setVisibility(View.GONE);
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());    //获取图标数据库路径
        getDataFromIntent(getIntent());

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

        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    list_webView.get(currentPage).findAllAsync(editText_search.getText().toString());
                    IMM.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        editText_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                // text 输入框中改变后的字符串信息
                // start 输入框中改变后的字符串的起始位置
                // before 输入框中改变前的字符串的位置 默认为0
                // count 输入框中改变后的一共输入字符串的数量
                list_webView.get(currentPage).findAllAsync(text.toString());
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
                case R.id.button_title:
                    button_title.setVisibility(View.GONE);
                    editText1.setVisibility(View.VISIBLE);
                    break;
                case R.id.button_page:
                    List<String> list_title = new ArrayList<>();
                    for (int i = 0 ; i < list_webView.size() ; i++) {
                        list_title.add(i + 1 + "." + list_webView.get(i).getTitle());
                    }
                    String[] titles = list_title.toArray(new String[list_title.size()]);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("窗口");
                    builder.setIcon(android.R.drawable.ic_menu_slideshow);
                    builder.setItems(titles, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if(which != currentPage) {
                                webViewLayout.removeAllViews();
                                WebView webView = list_webView.get(which);
                                webViewLayout.addView(webView);
                                button_page.setText(which + 1 + "");
                                button_title.setText(webView.getTitle());
                                editText1.setText(webView.getUrl());
                                imageButton_info.setImageBitmap(webView.getFavicon());
                                currentPage = which;
                            }
                        }
                    });
                    builder.create().show();
                    break;
                case R.id.imageButton_go:
                    //loadPage(editText1.getText().toString());
                    list_webView.get(currentPage).loadUrl(editText1.getText().toString());
                    break;
                case R.id.imageButton_back:
                    if (list_webView.get(currentPage).canGoBack()) {
                        list_webView.get(currentPage).goBack();
                        imageButton_forward.setEnabled(true);
                    } else {
                        imageButton_back.setEnabled(false);
                    }
                    break;
                case R.id.imageButton_forward:
                    if (list_webView.get(currentPage).canGoForward()) {
                        list_webView.get(currentPage).goForward();
                        imageButton_back.setEnabled(true);
                    } else {
                        imageButton_forward.setEnabled(false);
                    }
                    break;
                case R.id.imageButton_menu:
                    MenuDialog();
                    break;
                case R.id.imageView1:
                    playVideo();
                    imageView1.setVisibility(View.GONE);
                    break;
                case R.id.imageButton_searchPrev:
                    list_webView.get(currentPage).findNext(false);
                    break;
                case R.id.imageButton_searchNext:
                    list_webView.get(currentPage).findNext(true);
                    break;
                case R.id.imageButton_searchClose:
                    editText_search.setText("");
                    IMM.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
                    searchBar.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Log.e(Thread.currentThread().getStackTrace()[2] + ": ", url);
            //downloadBySystem(url, "", "");
            dialog_new_download(url, "", "");
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        WebView w = (WebView) v;
        HitTestResult result = w.getHitTestResult();
        HTRE = result.getExtra();
        menu.setHeaderIcon(android.R.drawable.ic_menu_report_image);
        menu.setHeaderTitle(HTRE);
        if (result.getType() == HitTestResult.IMAGE_TYPE || result.getType() == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            menu.setHeaderIcon(android.R.drawable.ic_menu_gallery); // Context menu items do not support icons
            menu.add(0, 0, 0, "查看图片");
            menu.add(0, 1, 1, "复制图片");
            menu.add(0, 2, 2, "保存图片").setIcon(android.R.drawable.ic_menu_save); // Context menu items do not support icons
            menu.add(0, 3, 3, "复制链接");
            menu.add(0, 4, 4, "屏蔽图片");
            menu.add(0, 5, 5, "隐藏图片");
        } else if (result.getType() == HitTestResult.SRC_ANCHOR_TYPE) {
            menu.setHeaderIcon(android.R.drawable.ic_menu_sort_alphabetically);
            menu.add(0, 2, 2, "下载");
            menu.add(0, 3, 3, "复制链接");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                list_webView.get(currentPage).loadUrl(HTRE);
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
                //downloadBySystem(HTRE, "", "");
                dialog_new_download(HTRE, "", "");
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
                list_webView.get(currentPage).loadUrl(js);
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(Thread.currentThread().getStackTrace()[2] + "", keyCode + ", " + event);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isFullScreen) {
                imageView1.setVisibility(View.GONE);
                quitFullScreen();
            } else {
                if (list_webView.get(currentPage).canGoBack()) {
                    list_webView.get(currentPage).goBack();
                } else {
                    // moveTaskToBack(false);
                    // MenuDialog();
                }
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            list_webView.get(currentPage).findAllAsync(editText_search.getText().toString());
            IMM.hideSoftInputFromWindow(editText_search.getWindowToken(), 0);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_F) {
            searchBar.setVisibility(View.VISIBLE);
            editText_search.requestFocus();
            list_webView.get(currentPage).findAllAsync(editText_search.getText().toString());
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
            searchBar.setVisibility(View.GONE);
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
        Log.e(Thread.currentThread().getStackTrace()[2] + "", url);
        IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
        editText_search.setText("");
        list_webView.get(currentPage).loadUrl(url);
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
                list_webView.get(currentPage).loadUrl(intent.getStringExtra("url"));
        }
        if (requestCode == FILECHOOSER_DOWNLOAD_PATH) {
            if (resultCode == Activity.RESULT_OK) { //是否选择，没选择就不会继续
                Uri uri = intent.getData();   // 得到uri，后面就是将uri转化成file的过程。
                //String scheme = uri.getScheme();
                Log.e("uri", uri.toString());
                String[] projection = { "_data" };
                Cursor cursor  = getContentResolver().query(uri, projection, null, null, null);
                if(cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    cursor.moveToFirst();
                    String filepath = cursor.getString(column_index);
                    Log.e(Thread.currentThread().getStackTrace()[2] + "", filepath);
                    int endIndex = filepath.lastIndexOf("/");
                    if (endIndex != -1) {
                        String path = filepath.substring(0, endIndex);
                        Log.e(Thread.currentThread().getStackTrace()[2] + "", path);
                        //Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                        editText_download_path.setText(path);
                    }

                }
            }
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
        //Log.e(Thread.currentThread().getStackTrace()[2] + "", "onPause");
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
        list_webView.get(currentPage).loadUrl(js);
    }

    void playVideo() {
        String js = "javascript:var obj = document.getElementsByTagName('video');for (var i = 0; i < obj.length; i++) { obj[i].play();}";
        list_webView.get(currentPage).loadUrl(js);
    }

    void MenuDialog() {
        String[] items = { "新建窗口", "关闭当前窗口", "收藏当前页", "收藏夹", "查找", "分享", "视频独立播放", "视频截图", "视频在播放器中打开", "查看源码", "主页", "全屏", "广告过滤规则", "设置", "检查更新", "关于", "退出" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("菜单");
        builder.setIcon(R.drawable.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0:
                        newWindow(sharedPreferences.getString("homepage","http://www.baidu.com"));
                        break;
                    case 1:
                        list_webView.remove(currentPage);
                        if(list_webView.size() == 0){
                            newWindow(sharedPreferences.getString("homepage","http://www.baidu.com"));
                        }else{
                            currentPage--;
                            if (currentPage < 0) currentPage = 0;
                            button_page.setText(currentPage + 1 + "");
                            webViewLayout.removeAllViews();
                            WebView webView = list_webView.get(currentPage);
                            webViewLayout.addView(webView);
                            button_title.setText(webView.getTitle());
                            editText1.setText(webView.getUrl());
                            imageButton_info.setImageBitmap(webView.getFavicon());
                        }
                        break;
                    case 2:
                        LinearLayout layout = new LinearLayout(MainActivity.this);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final EditText ET_title = new EditText(MainActivity.this);
                        ET_title.setHint("标题");
                        ET_title.setText(list_webView.get(currentPage).getTitle());
                        layout.addView(ET_title);
                        final EditText ET_url = new EditText(MainActivity.this);
                        ET_title.setHint("网址");
                        ET_url.setText(list_webView.get(currentPage).getUrl());
                        layout.addView(ET_url);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        Bitmap icon = list_webView.get(currentPage).getFavicon();
                        if(icon == null) icon = BitmapFactory.decodeResource(getResources(), R.drawable.network);
                        Matrix matrix = new Matrix();
                        matrix.postScale((float)100/icon.getWidth(), (float)100/icon.getHeight());
                        Bitmap bitmap = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true);
                        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
                        builder.setIcon(drawable);
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
                                if (!stitle.equals("") && (surl.startsWith("http") || surl.startsWith("file:///"))) {
                                    DBHelper helper = new DBHelper(getApplicationContext());
                                    Cursor c = helper.query(surl);
                                    if (c.getCount() == 0) {
                                        ContentValues values = new ContentValues();
                                        values.put("website", surl);
                                        values.put("title", stitle);
                                        helper.insert(values);
                                        IMM.hideSoftInputFromWindow(ET_title.getWindowToken(), 0);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "网址已存在", Toast.LENGTH_SHORT).show();
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
                                    if (!surl.startsWith("http") || !surl.startsWith("file:///")){
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
                    case 3:
                        Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
                        startActivityForResult(intent, 0);
                        break;
                    case 4:
                        searchBar.setVisibility(View.VISIBLE);
                        editText_search.requestFocus();
                        list_webView.get(currentPage).findAllAsync(editText_search.getText().toString());
                        break;
                    case 5:
                        intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, list_webView.get(currentPage).getUrl());
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "分享"));
                        break;
                    case 6:
                        String js = "javascript:var videos=document.getElementsByTagName('video'); if(videos.length!=0){ var url=videos[0].src;window.location.href=url; } else { var iframes=document.getElementsByTagName('iframe'); if(iframes.length!=0) window.location.href=iframes[0].src; }";
                        list_webView.get(currentPage).loadUrl(js);
                        break;
                    case 7:
                        js = "javascript:function capture(){var videos=document.getElementsByTagName('video');if(videos.length==0){var iframe=document.getElementsByTagName('iframe');videos=iframe[0].contentWindow.document.getElementsByTagName('video');}var canvas=document.createElement('canvas');videos[0].crossOrigin='*';canvas.width=videos[0].videoWidth;canvas.height=videos[0].videoHeight;canvas.getContext('2d').drawImage(videos[0],0,0,canvas.width,canvas.height);var s = canvas.toDataURL('image/jpeg',1.0); return s;}";
                        list_webView.get(currentPage).loadUrl(js);
                        list_webView.get(currentPage).evaluateJavascript("javascript:capture()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.e(Thread.currentThread().getStackTrace()[2] + "", value);
                                if(!"null".equals(value)){
                                    //字符串转Bitmap，https://www.jianshu.com/p/c9a18050a249
                                    byte[] bitmapArray = Base64.decode(value.split(",")[1], Base64.DEFAULT);
                                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
                                    LinearLayout layout = new LinearLayout(MainActivity.this);
                                    layout.setOrientation(LinearLayout.VERTICAL);
                                    ImageView imageView = new ImageView(MainActivity.this);
                                    imageView.setImageBitmap(bitmap);
                                    layout.addView(imageView);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setTitle("视频截图(" + bitmap.getWidth() + "X" + bitmap.getHeight() + ")");
                                    builder.setView(layout);
                                    builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String dir = Environment.getExternalStorageDirectory().getPath() + "/Pictures/Screenshots/";
                                            File temp = new File(dir);  // 如果文件夹不存在则创建
                                            if (!temp.exists()) {
                                                temp.mkdir();
                                            }
                                            Date date = new Date();
                                            SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");
                                            String stime = SDF.format(date);
                                            String path = dir + stime + ".jpg";
                                            File file = new File(path);
                                            BufferedOutputStream BOS = null;
                                            try {
                                                BOS = new BufferedOutputStream(new FileOutputStream(file));
                                            }catch (FileNotFoundException e){
                                                Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + e);
                                            }
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, BOS);
                                            try {
                                                BOS.flush();
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            try {
                                                BOS.close();
                                            }catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            Toast.makeText(getApplicationContext(), "保存到："+path, Toast.LENGTH_SHORT).show();
                                            MediaScannerConnection.scanFile(MainActivity.this, new String[] { path }, null, null);
                                        }
                                    });
                                    builder.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    builder.create().show();
                                }else{
                                    Toast.makeText(getApplicationContext(), "请先播放视频或把视频独立播放", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 8:
                        //网址正确，运行崩溃，本地播放器没有注册网络视频类型？
                        js = "javascript:function getVideoUrl(){var video_url='';var videos=document.getElementsByTagName('video');if(videos.length==0){var iframe=document.getElementsByTagName('iframe');videos=iframe[0].contentWindow.document.getElementsByTagName('video');}else{video_url=videos[0].src;}return video_url;}";
                        list_webView.get(currentPage).loadUrl(js);
                        list_webView.get(currentPage).evaluateJavascript("javascript:getVideoUrl()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + value);
                                if(!"null".equals(value)) {
                                    try {
                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        String type = "video/*";
                                        Uri uri = Uri.parse(value);
                                        intent1.setDataAndType(uri, type);
                                        startActivity(intent1);
                                    }catch (ActivityNotFoundException e){
                                        Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + e);
                                        Toast.makeText(getApplicationContext(), "系统默认播放器不能打开视频", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        break;
                    case 9:
                        js = "javascript:var s='<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'; document.body.innerHTML=''; var pre=document.createElement('pre'); document.body.appendChild(pre); pre.textContent=s;";
                        list_webView.get(currentPage).loadUrl(js);
                        break;
                    case 10:
                        list_webView.get(currentPage).loadUrl(sharedPreferences.getString("homepage",""));
                        break;
                    case 11:
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        LinearLayout2.setVisibility(View.GONE);
                        pgb1.setVisibility(View.GONE);
                        isFullScreen = true;
                        break;
                    case 12:
                        DialogBlockList();
                        break;
                    case 13:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                    case 14:
                        isManualCheckUpdate = true;
                        new Thread(CU).start();
                        break;
                    case 15:
                        list_webView.get(currentPage).loadUrl("file:///android_asset/about.htm");
                        break;
                    case 16:
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
        final String[] datas = ReadFile("blockrules").split("\n");
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
                        Log.e(Thread.currentThread().getStackTrace()[2] + "", String.valueOf(file.delete()));
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
            return s;
        } catch (FileNotFoundException e) {
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + e);
            String path = getFilesDir().getAbsolutePath() + "/blockrules";
            File file = new File(path);
            try {
                file.createNewFile();
            } catch (IOException e1) {
                Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + e1);
            }
            return s;
        } catch (IOException e) {
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + e);
            return s;
        }
    }

    void ADBlock(){
        String s = ReadFile("blockrules");
        Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + s);
        if(!"".equals(s)) {
            String rules = s.replace("\n", ",");
            if (rules.endsWith(",")) {
                rules = rules.substring(0, rules.length() - 1);
            }
            String js = "javascript:var rules='" + rules + "';var rule=new Array();rule=rules.split(',');var img=document.getElementsByTagName('img');for(i=0;i<img.length;i++){for(j=0;j<rule.length;j++){if(img[i].src.indexOf(rule[j])!=-1){img[i].style.display='none';}}}";
            list_webView.get(currentPage).loadUrl(js);
        }
    }

    void iframeBlock(){
        String js = "javascript:var iframes=document.getElementsByTagName('iframe');for(i=0;i<iframes.length;i++){iframes[i].style.display='none';}document.getElementById('win-pop-foot').style.display='none';document.getElementById('win-pop-foot1').style.display='none';";
        list_webView.get(currentPage).loadUrl(js);
    }

    // 调用系统下载，https://www.jianshu.com/p/6e38e1ef203a
    private void downloadBySystem(String surl, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(surl));
        // 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 设置通知栏的标题，如果不设置，默认使用文件名
        // request.setTitle("This is title");
        // 设置通知栏的描述
        request.setDescription(surl);
        // 允许在计费流量下下载
        request.setAllowedOverMetered(false);
        // 允许该记录在下载管理界面可见
        //request.setVisibleInDownloadsUi(true);
        // 允许漫游时下载
        request.setAllowedOverRoaming(true);
        // 允许下载的网路类型
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // 设置下载文件保存的路径和文件名
        String fileName  = URLUtil.guessFileName(surl, contentDisposition, mimeType);
        Log.e(Thread.currentThread().getStackTrace()[2] + "", fileName);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // 自定义下载路径
        //request.setDestinationUri();
        //request.setDestinationInExternalFilesDir();
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);
        Log.e(Thread.currentThread().getStackTrace()[2] + "", downloadId+"");
        if(surl == urlUpdate){
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
            Log.e(Thread.currentThread().getStackTrace()[2] + "", versionS + " > " + versionL + " ?");
            String[] AVersionS = versionS.split("\\.");
            String[] AVersionL = versionL.split("\\.");
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "(" + AVersionS[0] + " > " + AVersionL[0] + ") && (" + AVersionS[1] + " > " + AVersionL[1] + ")");
            if ((Integer.parseInt(AVersionS[0]) > Integer.parseInt(AVersionL[0])) || (Integer.parseInt(AVersionS[0]) == Integer.parseInt(AVersionL[0]) && Integer.parseInt(AVersionS[1]) > Integer.parseInt(AVersionL[1]))) {
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
                    Toast.makeText(getApplicationContext(), "当前版本 " + versionL + " >= 服务器版本 "+ versionS + "，是最新的版本", Toast.LENGTH_SHORT).show();
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
            Log.e(Thread.currentThread().getStackTrace()[2] + "", intent != null ? intent.toUri(0) : null);
            if (intent != null) {
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                    Log.e(Thread.currentThread().getStackTrace()[2] + "", downloadId + "");
                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
                    if(downloadId == downloadIdUpdate){
                        Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
                        Log.e(Thread.currentThread().getStackTrace()[2] + "", uri.toString());
                        Intent intentn = new Intent(Intent.ACTION_VIEW);
                        intentn.setDataAndType(uri, "application/vnd.android.package-archive");
                        startActivity(intentn);
                    }
                }
            }
        }
    }

    void newWindow(String surl){
        WebView webView = new WebView(MainActivity.this);
        settingWebView(webView);
        webView.loadUrl(surl);
        webViewLayout.removeAllViews();
        webViewLayout.addView(webView);
        list_webView.add(webView);
        currentPage = list_webView.size() - 1;
        button_page.setText(currentPage + 1 + "");
    }

    void settingWebView(final WebView webView){
        // 菜单
        registerForContextMenu(webView);
        // 支持获取手势焦点
        webView.requestFocusFromTouch();
        // 允许调试
        if(Build.VERSION.SDK_INT >= 19) {
            webView.setWebContentsDebuggingEnabled(true);
        }
        WebSettings webSettings = webView.getSettings();
        // 开启JS
        webSettings.setJavaScriptEnabled(true);
        // 开启JS能打开窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启缓存
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        // 支持缩放
        webSettings.setSupportZoom(true);
        // 启用内置缩放控件
        webSettings.setBuiltInZoomControls(true);
        // 隐藏缩放控件
        webSettings.setDisplayZoomControls(false);
        // 开启访问文件
        webSettings.setAllowFileAccess(true);
        // 开启数据库
        webSettings.setDatabaseEnabled(true);
        // 开启localStorage
        webSettings.setDomStorageEnabled(true);
        // 开启定位
        webSettings.setGeolocationEnabled(true);
        // 支持多窗口
        webSettings.setSupportMultipleWindows(true);
        // 允许跨域
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e(Thread.currentThread().getStackTrace()[2] + "", url);
                // 协议过滤
                if (url.startsWith("http") && !url.startsWith("https://cdn-haokanapk.baidu.com/")) {
                    view.loadUrl(url);
                    return false;
                } else if (url.startsWith("tbopen://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true; // 拦截原链接
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //super.onPageStarted(view, url, favicon);
                //Log.e(Thread.currentThread().getStackTrace()[2] + "", url);
                button_title.setText(url);
                button_title.setVisibility(View.VISIBLE);
                editText1.setText(url);
                editText1.setVisibility(View.GONE);
                urln = url;
                imageButton_back.setEnabled(true);
                IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
                pgb1.setVisibility(View.VISIBLE);
                imageButton_info.setImageResource(android.R.drawable.ic_menu_info_details);
                // if(favicon != null) {
                //   imageButton_info.setImageBitmap(favicon);
                // }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pgb1.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(getApplicationContext(), "ReceivedError:" + errorCode, Toast.LENGTH_SHORT).show();
                if (isNetworkConnected()) {
                    switch(errorCode){
                        case WebViewClient.ERROR_HOST_LOOKUP:   // 找不到主机，跳转百度搜索
                            Log.e(Thread.currentThread().getStackTrace()[2] + "", failingUrl);
                            String url = "http://m.baidu.com/s?word=" + urlo;
                            editText1.setText(url);
                            webView.loadUrl(url);
                            urln = url;
                            break;
                        case WebViewClient.ERROR_UNSUPPORTED_SCHEME:
                            Log.e(Thread.currentThread().getStackTrace()[2] + "",failingUrl);
                    }
                } else {
                    webView.loadDataWithBaseURL(
                            "",
                            "<html><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/><style>h1{font-size:10vw;margin:40vh auto;text-align:center;}</style><h1>网络未连接</h1></html>",
                            "text/html", "utf-8", "");
                }
            }

        });

        webView.setFindListener(new FindListener() {
            @Override
            public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                if(numberOfMatches == 0){
                    textView_searchCount.setText(activeMatchOrdinal + "/" + numberOfMatches);
                }else{
                    textView_searchCount.setText(activeMatchOrdinal + 1 + "/" + numberOfMatches);
                }
            }
        });

        webView.setDownloadListener(new MyWebViewDownLoadListener());

        webView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                button_title.setVisibility(View.VISIBLE);
                editText1.setVisibility(View.GONE);
                webView.requestFocus();
                IMM.hideSoftInputFromWindow(editText1.getWindowToken(), 0);
                return false;
            }
        });

        webView.setWebChromeClient(new MyWebChromeClient());
    }


    class MyWebChromeClient extends WebChromeClient {

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
            if(sharedPreferences.getBoolean("switch_adBlock",false)){
                ADBlock();
            }
            if(sharedPreferences.getBoolean("switch_iframeBlock",false)) {
                if (view.getUrl() != null) {
                    if (!view.getUrl().contains("baidu.com")) {
                        iframeBlock();
                    }
                }
            }
            // 链接关键字屏蔽
            if(sharedPreferences.getBoolean("switch_filter",false)){
                String sf = sharedPreferences.getString("filter","");
                Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + sf);
                if(!sf.equals("")) {
                    String js = "javascript:var s='"+sf+"';var sl=s.split(';');var a=document.getElementsByTagName('a');for(var i=0;i<a.length;i++){for(var j=0;j<sl.length;j++){if(a[i].textContent.indexOf(sl[j])!=-1){a[i].textContent='';}}}";
                    view.loadUrl(js);
                }
            }
            // 链接关键字高亮
            if(sharedPreferences.getBoolean("switch_highlight",false)){
                String shl = sharedPreferences.getString("highlight","");
                Log.e(Thread.currentThread().getStackTrace()[2] + "", "" + shl);
                if(!shl.equals("")) {
                    String js = "javascript:var s='"+shl+"';var sl=s.split(';');var a=document.getElementsByTagName('a');for(var i=0;i<a.length;i++){for(var j=0;j<sl.length;j++){if(a[i].textContent.indexOf(sl[j])!=-1){a[i].style.color='white';a[i].style.backgroundColor='#DA3434';}}}";
                    view.loadUrl(js);
                }
            }
            //图片宽度超出父元素适应父元素
            if(sharedPreferences.getBoolean("switch_shrink",false)) {
                String js = "javascript:var imgs=document.getElementsByTagName('img');for(var i=0;i<imgs.length;i++){if(imgs[i].parentNode.clientWidth > 0){if(imgs[i].clientWidth>imgs[i].parentNode.clientWidth){imgs[i].width=imgs[i].parentNode.clientWidth;}}}";
                view.loadUrl(js);
            }
        }

        // 获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            button_title.setText(title);
            ptitle = title;
        }

        // 接收网站图标(favicon)
        public void onReceivedIcon(WebView view, Bitmap icon) {
            imageButton_info.setImageBitmap(icon);
        }

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "onShowCustomView");
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
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "onHideCustomView");
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

        // target="_blank" 处理
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView webView = new WebView(MainActivity.this);
            settingWebView(webView);
            webViewLayout.removeAllViews();
            webViewLayout.addView(webView);
            list_webView.add(webView);
            currentPage = list_webView.size() - 1;
            button_page.setText(currentPage + 1 + "");
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webView);
            resultMsg.sendToTarget();
            return true;
        }

    }

    void getDataFromIntent(Intent intent) {
        Log.e(Thread.currentThread().getStackTrace()[2] + "", "intent(" + intent + ")");
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            urln = intent.getDataString();
            newWindow(urln);
        }else{
            newWindow(sharedPreferences.getString("homepage","http://www.baidu.com"));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Log.e(Thread.currentThread().getStackTrace()[2] + "", "onNewIntent(" + getIntent() + ")");
        super.onNewIntent(intent);
        setIntent(intent);
        if(getIntent().getDataString() != null) {
            urln = getIntent().getDataString();
            Log.e(Thread.currentThread().getStackTrace()[2] + "", "onNewIntent(" + urln + ")");
            newWindow(urln);
        }
    }

    void dialog_new_download(final String surl, String contentDisposition, String mimeType){
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_download, null, false);
        final EditText editText_download_url = (EditText) view.findViewById(R.id.editText_download_url);
        editText_download_url.setText(surl);
        EditText editText_download_filename = (EditText) view.findViewById(R.id.editText_download_filename);
        editText_download_filename.setText(surl.substring(surl.lastIndexOf("/")+1));
        editText_download_path = (EditText) view.findViewById(R.id.editText_download_path);
        //String path = Environment.getExternalStorageDirectory().getPath() + "/download";
        String path = Environment.DIRECTORY_DOWNLOADS;
        editText_download_path.setText(path);
        ImageButton imageButton_path = (ImageButton) view.findViewById(R.id.imageButton_path);

        imageButton_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILECHOOSER_DOWNLOAD_PATH);
            }
        });

        textView_filesize = (TextView) view.findViewById(R.id.textView_filesize);
        if(!surl.startsWith("data:")) {
            GetFileLengthThread getFileLengthThread = new GetFileLengthThread();
            getFileLengthThread.surl = editText_download_url.getText().toString();
            getFileLengthThread.start();
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("新建下载")
                .setIcon(android.R.drawable.stat_sys_download)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,	int which) {
                        //Toast.makeText(getApplicationContext(), "开始下载", Toast.LENGTH_SHORT).show();
                        String url = editText_download_url.getText().toString();
                        if(!url.equals("")) {
                            downloadBySystem(url, "", "");
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,	int which) {
                    }
                })
                .create();
        dialog.show();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what){
                case 0:
                    //textView_filesize.setText(formatFileSize(msg.arg1));
                    textView_filesize.setText(Formatter.formatFileSize(MainActivity.this, msg.arg1));
                    break;
            }

        };
    };

    class GetFileLengthThread extends Thread{
        String surl;
        public void run(){
            int fileLength = 0;
            URL url = null;
            try {
                url = new URL(surl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlcon;
            try {
                urlcon = (HttpURLConnection) url.openConnection();
                fileLength = urlcon.getContentLength();

            } catch (IOException e) {
                e.printStackTrace();
            }
            Message message = Message.obtain();
            message.what = 0;
            message.arg1 = fileLength;
            handler.sendMessage(message);
        }
    }

}