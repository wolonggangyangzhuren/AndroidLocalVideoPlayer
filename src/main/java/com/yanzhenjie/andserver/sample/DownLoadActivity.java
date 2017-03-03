package com.yanzhenjie.andserver.sample;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownLoadActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView down;
    private TextView progress;
    private TextView file_name;
    private ProgressBar pb_update;
    private DownloadManager downloadManager;
    private DownloadManager.Request request;
    public static List<String>resultList = new ArrayList<>();
    private int current;

//    public static String downloadUrl = "http://ucdl.25pp.com/fs08/2017/01/20/2/2_87a290b5f041a8b512f0bc51595f839a.apk";
    private String downloadUrl = "http://47.93.114.18/test0.ts";
    Timer timer;
    long id;
    TimerTask task;
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pb_update.setProgress(mBinder.getProgress());
            progress.setText(String.valueOf(mBinder.getProgress())+"%");
            file_name.setText(mBinder.getName());
            if(resultList.get(resultList.size()-1).equals(mBinder.getName()))
            {

            }else
            {
                handler.sendEmptyMessageDelayed(1,1000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        down = (TextView) findViewById(R.id.down);
        progress = (TextView) findViewById(R.id.progress);
        file_name = (TextView) findViewById(R.id.file_name);
        pb_update = (ProgressBar) findViewById(R.id.pb_update);
        down.setOnClickListener(this);

        Call<String> call = HttpRequestHelper.getInstance().getApiServes().getTsUrl();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                parseString(response.body().toString());
                Log.e("url",response.body().toString());
                String videoStr = replaceString(response.body().toString());
                resultList.add("file.key");
                File files = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DayDayUp/1/");
                if(!files.exists())
                {
                    files.mkdirs();
                }
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DayDayUp/1/test1.m3u8");
                if(!file.exists())
                {
                    try {
                        file.createNewFile();
                        FileOutputStream outputStream = new FileOutputStream(file);
                        outputStream.write(videoStr.getBytes());
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(videoStr.getBytes());
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    public String replaceString(String str)
    {
        if(str!=null && !"".equals(str))
        {
           Pattern pattern = Pattern.compile("[a-zA-Z]{0,}[0-9]{0,}.ts");
           Matcher matcher = pattern.matcher(str);
           while(matcher.find())
           {
               String ts = matcher.group();
               str = str.replace(ts,"download?filename=DayDayUp/1/"+ts);
           }
        }
        str = str.replace("./file.key","download?filename=DayDayUp/1/file.key");
        Log.e("replaceStr",str);
        return str;
    }

    public List<String> parseString(String str)
    {
        if(str!=null && !"".equals(str))
        {
            Pattern pattern = Pattern.compile("[a-zA-Z]{0,}[0-9]{0,}.ts");
            Matcher matcher = pattern.matcher(str);
            while(matcher.find())
            {
                String ts = matcher.group();
                resultList.add(ts);
            }
        }
        return resultList;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,DownLoadService.class);
        this.bindService(intent,conn,BIND_AUTO_CREATE);
        startService(intent);
        down.setClickable(false);

    }

    private DownLoadService.MyBinder mBinder;
    private ServiceConnection conn = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DownLoadService.MyBinder) service;
            handler.sendEmptyMessage(1);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void install(String path) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + path), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//4.0以上系统弹出安装成功打开界面
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
