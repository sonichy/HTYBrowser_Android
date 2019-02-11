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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;

public class FavoriteActivity extends Activity {
	SimpleCursorAdapter adapter;
	ListView listView;
	EditText editText;
    InputMethodManager IMM;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		editText = (EditText) findViewById(R.id.editText);
        editText.addTextChangedListener(new EditChangedListener());
        listView = (ListView) findViewById(R.id.listView1);
        search(editText.getText().toString());
        IMM = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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
			Cursor c = helper.query(editText.getText().toString());
			String[] from = { "_id", "title", "website" };
			int[] to = { R.id.id, R.id.title, R.id.website };
			adapter = new SimpleCursorAdapter(this, R.layout.favorite_row, c, from, to, 0);
			listView.setAdapter(adapter);
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
                    if (!stitle.equals("") && surl.startsWith("http")) {
                        DBHelper dbHelper = new DBHelper(getApplicationContext());
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("website", surl);
                        values.put("title", stitle);
                        db.update(DBHelper.TableName, values, "_id = " + sid, null);
                        IMM.hideSoftInputFromWindow(ET_title.getWindowToken(), 0);
                        Cursor c = dbHelper.query(editText.getText().toString());
                        String[] from = { "_id", "title", "website" };
                        int[] to = { R.id.id, R.id.title, R.id.website };
                        adapter = new SimpleCursorAdapter(FavoriteActivity.this, R.layout.favorite_row, c, from, to, 0);
                        listView.setAdapter(adapter);
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
        String[] from = { "_id", "title", "website" };
        int[] to = { R.id.id, R.id.title, R.id.website };
        adapter = new SimpleCursorAdapter(this, R.layout.favorite_row, c, from, to, 0);
        listView.setAdapter(adapter);
        listView.setDivider(new ColorDrawable(Color.GREEN));
        listView.setDividerHeight(2);
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
           search(s.toString());
        }
    }

}
