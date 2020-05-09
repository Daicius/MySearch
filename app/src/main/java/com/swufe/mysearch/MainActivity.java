package com.swufe.mysearch;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
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
import java.nio.channels.ServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements Runnable {
    private String TAG = "MySearch";
    Handler handler;
    private ArrayAdapter<String> arr_aAdapter;
    private SimpleAdapter ListItemAdapter;
    EditText MySearch;
    ListView listView;
    ArrayList<String> str1;
    ArrayList<String> ans1;
    String str[] = new String[20];
    String ans[] = new String[20];
    String updateDate;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MySearch = findViewById(R.id.Search_Text);
        SharedPreferences sharedPreferences = getSharedPreferences("MyFind", Activity.MODE_PRIVATE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
//        str = Double.parseDouble(sharedPreferences.getStringArry("MyFind", "0"));

//        获取当前时间
//        判断日期是否相同
            Log.i(TAG, "onCreate: need updates");
//            开启子线程
            Thread t = new Thread(this);
            t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == 5) {
                    Bundle bd1 = (Bundle) msg.obj;
//                    str = bd1.getStringArray("MyFind");
                    str1 = bd1.getStringArrayList("MyFind2");
                    Log.i("TAG","得到更新");
                    Log.i(TAG,"get:"+str1);
//                    updateDate = bd1.getString("update_date");
                    Toast.makeText(MainActivity.this, "信息更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);

            }
        };
        //保存更新的日期
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.commit();
    }
    public void btnSearch(View btn){
        MySearch = findViewById(R.id.Search_Text);
        tv = findViewById(R.id.itemTitle1);
        listView = findViewById(R.id.myfind);
        tv.setText("wait....");
        String key = String.valueOf(MySearch.getText());
        for(int i = 0,j = 0;i<str1.size();i++){
            if (str1.get(i).contains(key)){
                ans[j] = str[i];
                j++;
            }
            if (ans.length == 0){
                tv.setText("没有找到相应信息");
            }else {
                tv.setText("找到以下信息");
                arr_aAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ans);
                listView.setAdapter(arr_aAdapter);
            }
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
        Bundle bundle = new Bundle();
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
        ArrayList<String> str2 = new ArrayList<String>();
        String str1[] = new String[lis.size()];
        for (int i = 0; i < lis.size(); i++) {
            Element a = lis.get(i);
            str2.add(a.text());
            str1[i] = a.text();
            Log.i(TAG, "msgfind:" + str1[i]);
        }
        bundle.putStringArray("MyFind",str1);
        bundle.putStringArrayList("MyFind2",str2);
        Message message = handler.obtainMessage(5);
        message.obj = bundle;
        handler.sendMessage(message);
    }
}