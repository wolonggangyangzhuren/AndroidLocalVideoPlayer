package com.yanzhenjie.andserver.sample;

import android.app.Activity;
import android.app.DownloadManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import org.apache.http.HttpResponse;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.R.attr.id;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener{

    private Button bt_download;
    private ProgressBar progress_dowload;
    public static List<String> resultList = new ArrayList<>();
    private Timer timer;
    private TimerTask task;
    private DownloadManager downloadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        initView();

        Call<String> call = HttpRequestHelper.getInstance().getApiServes().getTsUrl();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e("url",response.body().toString());
                parseString(response.body().toString());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle bundle = msg.getData();
            int pro = bundle.getInt("pro");
            String name  = bundle.getString("name");
            progress_dowload.setProgress(pro);
        }
    };

    private void initView() {
        bt_download = (Button) findViewById(R.id.bt_download);
        progress_dowload = (ProgressBar) findViewById(R.id.download_process);

        bt_download.setOnClickListener(this);
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
        switch (v.getId())
        {
            case R.id.bt_download:
//                Iterator<String> iterator = resultList.iterator();
//                while(iterator.hasNext())
//                for(int i=0;i<resultList.size();i++)
//                {
//                    String url = iterator.next();
                    String url = resultList.get(0);
                    System.out.println(url+"：要下载的链接");
//                    Call<ResponseBody> call = HttpRequestHelper.getInstance().getApiServes().downLoadFile(url);
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://47.93.114.18/"+url));
                    request.setTitle("测试视频下载");
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    request.setAllowedOverRoaming(false);
                    request.setMimeType("application/octet-stream");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdir() ;
//                    request.setDestinationInExternalPublicDir("DayDayUp",url);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,url);
                    request.setDescription(url+"：正在下载");
                    long downloadId = downloadManager.enqueue(request);
                final  DownloadManager.Query query = new DownloadManager.Query();
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Cursor cursor = downloadManager.query(query.setFilterById(id));
                        if (cursor != null && cursor.moveToFirst()) {
                            if (cursor.getInt(
                                    cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                progress_dowload.setProgress(100);
                                task.cancel();
                            }
                            String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                            String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                            int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                            int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                            int pro =  (bytes_downloaded * 100) / bytes_total;
                            Log.e("download",bytes_downloaded+"：已下载数"+"   "+bytes_total+"：总数");
                            Message msg =Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putInt("pro",pro);
                            bundle.putString("name",title);
                            msg.setData(bundle);
                            mHandler.sendMessage(msg);
                        }
                        cursor.close();
                    }
                };
                timer.schedule(task, 0,1000);
//                }
                break;
        }
    }
}
