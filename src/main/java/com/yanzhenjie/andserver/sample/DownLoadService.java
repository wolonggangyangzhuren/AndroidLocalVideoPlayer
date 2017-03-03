package com.yanzhenjie.andserver.sample;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class DownLoadService extends Service {
    public DownLoadService() {
    }

    private DownloadManager downloadManager;
    private String downloadUrl;
    private DownloadManager.Request request;
    private Timer timer;
    private TimerTask task;
    private int current;
    private int pro;
    private String title;
    private long id;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        registerReceiver(new DownLoadReceiver(),new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        new Thread(new DownLoadThread()).start();

        final  DownloadManager.Query query = new DownloadManager.Query();
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Cursor cursor = downloadManager.query(query.setFilterById(id));
                if (cursor != null && cursor.moveToFirst()) {
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
//                        pb_update.setProgress(100);
//                        install(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/app-release.apk" );
//                        task.cancel();
                    }
                    title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                    String address = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    pro =  (bytes_downloaded * 100) / bytes_total;
//                        Message msg =Message.obtain();
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("pro",pro);
//                        bundle.putString("name",title);
//                        msg.setData(bundle);
//                        handler.sendMessage(msg);
                    System.out.println(title+"：任务名称"+"   "+pro+"%：任务进度");

                }
                cursor.close();
            }
        };
        timer.schedule(task, 0,1000);
        task.run();
        return super.onStartCommand(intent, flags, startId);
    }

    private class DownLoadReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
            if(downLoadId==id)
            {
                new Thread(new DownLoadThread()).start();
            }
        }
    }

    public  class MyBinder extends Binder
    {
        public int getProgress()
        {
            return pro;
        }
        public String getName()
        {
            return title;
        }
    }

    private class DownLoadThread implements Runnable
    {

        @Override
        public void run() {
            if(current<DownLoadActivity.resultList.size())
            {
                Log.e("downLoadId",current+"：任务id");
                String fileName = DownLoadActivity.resultList.get(current++);
                System.out.println("视频路径："+fileName);
                downloadUrl = "http://47.93.114.18/"+fileName;
                Log.e("下载链接",downloadUrl);
//                downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                request = new DownloadManager.Request(Uri.parse(downloadUrl));

                request.setTitle(fileName);
                //只在wifi下下载
                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                //移动情况下是否漫游
                request.setAllowedOverRoaming(false);
//        request.setMimeType("application/vnd.android.package-archive");
                //设置MimeType 否则可能导致下载的文件无法打开
                request.setMimeType("application/octet-stream");
                //不显示通知栏
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
                //设置文件存放路径
                request.setDestinationInExternalPublicDir("DayDayUp/1/" , fileName ) ;
//            pb_update.setMax(100);
                id = downloadManager.enqueue(request);

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new MyBinder();
    }
}
