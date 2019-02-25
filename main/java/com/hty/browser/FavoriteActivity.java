package com.hty.browser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebIconDatabase;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FavoriteActivity extends Activity {
    SimpleCursorAdapter adapter;
    ListView listView;
    EditText editText;
    InputMethodManager IMM;
    ImageButton imageButton_clear;
    WebIconDatabase WID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new EditChangedListener());
        listView = (ListView) findViewById(R.id.listView1);
        imageButton_clear = (ImageButton) findViewById(R.id.imageButton_clear);
        imageButton_clear.setOnClickListener(new ButtonListener());
        imageButton_clear.setVisibility(View.GONE);
        search(editText.getText().toString());
        IMM = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        WID = WebIconDatabase.getInstance();
        WID.open(getDir("icons", MODE_PRIVATE).getPath());
    }

    class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.imageButton_clear:
                    editText.setText("");
                    break;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String url = ((TextView) menuInfo.targetView.findViewById(R.id.website)).getText().toString();
                cm.setPrimaryClip(ClipData.newPlainText("link", url));
                Toast.makeText(getApplicationContext(), "链接已复制", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                int id = Integer.parseInt(((TextView) menuInfo.targetView.findViewById(R.id.id)).getText().toString());
                DBHelper helper = new DBHelper(getApplicationContext());
                helper.del(id);
                search(editText.getText().toString());
                break;
            case 2:
                final String sid = ((TextView) menuInfo.targetView.findViewById(R.id.id)).getText().toString();
                final String stitle = ((TextView) menuInfo.targetView.findViewById(R.id.title)).getText().toString();
                final String surl = ((TextView) menuInfo.targetView.findViewById(R.id.website)).getText().toString();
                LinearLayout layout = new LinearLayout(FavoriteActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText ET_title = new EditText(FavoriteActivity.this);
                ET_title.setHint("标题");
                ET_title.setText(stitle);
                layout.addView(ET_title);
                final EditText ET_url = new EditText(FavoriteActivity.this);
                ET_title.setHint("网址");
                ET_url.setText(surl);
                layout.addView(ET_url);
                AlertDialog.Builder builder = new AlertDialog.Builder(FavoriteActivity.this);
                builder.setIcon(android.R.drawable.btn_star_big_on);
                builder.setTitle("修改收藏");
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
                        if (!stitle.equals("") && (surl.startsWith("http") || !surl.startsWith("file:///"))) {
                            DBHelper dbHelper = new DBHelper(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("website", surl);
                            values.put("title", stitle);
                            db.update(DBHelper.TableName, values, "_id = " + sid, null);
                            IMM.hideSoftInputFromWindow(ET_title.getWindowToken(), 0);
                            search(editText.getText().toString());
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
        }
        return true;
    }

    public void favback(View v) {
        IMM.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        finish();
    }

    void search(String s) {
        DBHelper helper = new DBHelper(this);
        Cursor c = helper.query(s);
        String[] from = { "_id", "title", "website", "website" };
        int[] to = { R.id.id, R.id.title, R.id.website, R.id.imageView_favicon };
        adapter = new SimpleCursorAdapter(this, R.layout.favorite_row, c, from, to, 0);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder(){
            public boolean setViewValue(View view, Cursor cursor, int columnIndex){
                //Log.e("L192", view.toString() + columnIndex);
                if(view.getId() == R.id.imageView_favicon){
                    String website = cursor.getString(columnIndex);
                    //                    if (website.startsWith("https://")) {
                    //                        //((ImageView) view).setImageResource(android.R.drawable.ic_secure);
                    //                        Runner1 r1 = new Runner1();
                    //                        r1.setUrl("https://static.easyicon.net/preview/52/523893.gif");
                    //                        r1.setView(view);
                    //                        Thread thread1 = new Thread(r1);
                    //                        thread1.start();
                    //                        return true;
                    //                    } else if (website.startsWith("http://")) {
                    //                        // ((ImageView) view).setImageResource(android.R.drawable.ic_partial_secure);
                    //                        Runner1 r1 = new Runner1();
                    //                        r1.setUrl("https://static.easyicon.net/preview/1/11847.gif");
                    //                        r1.setView(view);
                    //                        Thread thread1 = new Thread(r1);
                    //                        thread1.start();
                    //                        return true;
                    //                    } else {
                    //                        ((ImageView) view).setImageResource(R.drawable.network);
                    //                        return true;
                    //                    }
                    Runner1 r1 = new Runner1();
                    r1.setUrl(website);
                    r1.setView(view);
                    Thread thread1 = new Thread(r1);
                    thread1.start();
                    return true;
                }
                return false;
            }
        });
        listView.setAdapter(adapter);

        //        WebIconDatabase WID = WebIconDatabase.getInstance();
        //        WID.open(getDir("icons", MODE_PRIVATE).getPath());
        //        for (int i=0; i<adapter.getCount(); i++) {
        //            LinearLayout layout = (LinearLayout) listView.getAdapter().getView(i, null, null);
        //            String website = ((TextView) layout.findViewById(R.id.website)).getText().toString();
        //            if (website.startsWith("https://")) {
        //                Log.e("star_big_on", website);
        //                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_favicon);
        //                imageView.setImageResource(android.R.drawable.star_big_on);
        //            } else if (website.startsWith("http://")){
        //                ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_favicon);
        //                imageView.setImageResource(android.R.drawable.star_big_off);
        //                Log.e("star_big_off", website);
        //            } else {
        //                Log.e("earth", website);
        //            }
        //            WID.requestIconForPageUrl(website, new WebIconDatabase.IconListener() {
        //                public void onReceivedIcon(String url, Bitmap icon) {
        //                    Log.e("onReceivedIcon", url);
        //                    ImageView imageView = (ImageView) layout.findViewById(R.id.imageView_favicon);
        //                    imageView.setImageBitmap(icon);
        //                }
        //            });
        //        }
        //        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String url = ((TextView) arg1.findViewById(R.id.website)).getText().toString();
                Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                intent.putExtra("url", url);
                setResult(RESULT_OK, intent);
                IMM.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                finish();
            }
        });

        listView.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
                String title = ((TextView) info.targetView.findViewById(R.id.title)).getText().toString();
                menu.setHeaderTitle(title);
                menu.add(0, 0, 0, "复制链接");
                menu.add(0, 1, 1, "删除");
                menu.add(0, 2, 2, "修改");
            }
        });

    }

    class EditChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.toString().equals("")){
                imageButton_clear.setVisibility(View.GONE);
            }else{
                imageButton_clear.setVisibility(View.VISIBLE);
            }
            search(s.toString());
        }
    }

    public Bitmap webBitMap(String surl) {
        URL url = null;
        Bitmap bitmap = null;
        try {
            url = new URL(surl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream IS = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(IS);
            IS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    class Runner1 implements Runnable {
        private String surl;
        private View view;

        private void setUrl(String surl1) {
            surl = surl1;
        }

        private void setView(View view1) {
            view = view1;
        }

        public void run() {
            // Bitmap bitmap = webBitMap(surl);
            // Message msg = new Message();
            // Bundle bundle = new Bundle();
            // bundle.putParcelable("bitmap", bitmap);
            // msg.setData(bundle);
            // msg.obj = view;
            // handler.sendMessage(msg);
            Log.e("L350", surl);
            WID.requestIconForPageUrl(surl, new WebIconDatabase.IconListener() {
                public void onReceivedIcon(String url, Bitmap icon) {
                    Log.e("L353", url);
                    Message msg = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("bitmap", icon);
                    msg.setData(bundle);
                    msg.obj = view;
                    handler.sendMessage(msg);
                }
            });
        }
    }

    static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            Bitmap bitmap = bundle.getParcelable("bitmap");
            View view = (View) msg.obj;
            Log.e("L372", "setIcon");
            ((ImageView) view).setImageBitmap(bitmap);
        }
    };

}