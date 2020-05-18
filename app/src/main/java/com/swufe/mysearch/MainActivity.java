package com.swufe.mysearch;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable, OnItemClickListener {
    private String TAG = "MySearch";
    Handler handler;
    private List<String> list;
    private SimpleAdapter ListItemAdapter;
    EditText MySearch;
    List<String> list2 = new ArrayList<String>();
    ListView listView;
    TextView tv;
    ArrayAdapter arrayAdapter;
    String updateDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySearch = findViewById(R.id.Search_Text);
        SharedPreferences sharedPreferences = getSharedPreferences("MyFind2", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        updateDate = sharedPreferences.getString("update_date","");
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        final String todayStr = simpleDateFormat.format(today);
        Log.i(TAG,"当前时间"+todayStr);
        if (!today.equals(updateDate)) {
            Log.i(TAG, "onCreate: need updates");
            Thread t = new Thread(this);
            t.start();
        } else {
            Log.i(TAG, "onCreate:don't need updates");
        }
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                   list = (List<String>) msg.obj;
                   Log.i(TAG,"get_find"+list);
                    Log.i("TAG","得到更新");
//                    updateDate = bd1.getString("update_date");
                    Toast.makeText(MainActivity.this, "信息更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);

            }
        };
        //保存更新的日期
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("update_date", todayStr);
        editor.commit();
    }


    public void btnSearch(View btn){
        MySearch = findViewById(R.id.Search_Text);
        tv = findViewById(R.id.itemTitle1);
        listView = findViewById(R.id.myfind);
        tv.setText("wait....");
        String key = String.valueOf(MySearch.getText());
        if (key.length()>0) {
            for (int i = 0; i < list.size(); i++) {
                String find = list.get(i);
                if (find.contains(key)) {
                    Log.i(TAG, find + "isContain" + key);
                    list2.add(find);
                } else {
                    Log.i(TAG, find + "isNotContain" + key);
                }
            }
            Log.i(TAG, "getAns" + list2);
            if (list2.size() > 0) {
                tv.setText("找到以下信息");
                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list2);
                listView.setAdapter(arrayAdapter);
                listView.setOnItemClickListener(this);
            } else {
                tv.setText("没有关于" + key + "的信息");
            }
        }else {
            Toast.makeText(MainActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
        }

    }



    @Override
    public void run() {
        Log.i(TAG, "RUN...RUN...");
        for (int i = 1; i < 3; i++) {
            Log.i(TAG, "i:" + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Document doc = null;
        try {
            doc = Jsoup.connect("https://it.swufe.edu.cn/index/tzgg.htm").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements uls = doc.getElementsByTag("ul");
        Element ul18 = uls.get(17);
        Log.i(TAG, ul18.text());
        Elements lis = ul18.getElementsByTag("li");
        List<String> str2 = new ArrayList<>();
        List<String> herf = new ArrayList<>();
        for (int i = 0; i < lis.size(); i++) {
            Element a = lis.get(i);
            str2.add(a.text());
        }
        Log.i(TAG,"find"+str2);
        Message message = handler.obtainMessage(5);
        message.obj = str2;
        handler.sendMessage(message);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG,"position:"+position);
        Uri uri = Uri.parse("https://it.swufe.edu.cn/index/tzgg.htm");
        Intent it = new Intent();
        it.setAction(Intent.ACTION_VIEW);
        it.setData(uri);
        startActivity(it);
    }
}