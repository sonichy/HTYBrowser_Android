package com.hty.browser;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
