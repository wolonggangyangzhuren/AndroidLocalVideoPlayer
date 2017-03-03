package com.yanzhenjie.andserver.sample;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.AndServerBuild;
import com.yanzhenjie.andserver.sample.response.AndServerDownloadHandler;
import com.yanzhenjie.andserver.sample.response.AndServerPingHandler;
import com.yanzhenjie.andserver.sample.response.AndServerTestHandler;
import com.yanzhenjie.andserver.sample.response.AndServerUploadHandler;

/**
 * Created by Administrator on 2017-2-27.
 */

public class WebService extends Service{

    private AndServer mAndServer;


    @Override
    public void onCreate() {
        super.onCreate();
        startAndServer();
    }

    private void startAndServer() {
        if (mAndServer == null || !mAndServer.isRunning()) {

            AndServerBuild andServerBuild = AndServerBuild.create();
            andServerBuild.setPort(2456);// 指定端口号。

            // 添加普通接口。
            andServerBuild.add("ping", new AndServerPingHandler());// 到时候在浏览器访问是：http://localhost:4477/ping
            andServerBuild.add("test", new AndServerTestHandler());// 到时候在浏览器访问是：http://localhost:4477/test

            // 添加接受客户端上传文件的接口。
            andServerBuild.add("upload", new AndServerUploadHandler());// 到时候在浏览器访问是：http://localhost:4477/upload
            andServerBuild.add("download",new AndServerDownloadHandler());
            mAndServer = andServerBuild.build();

            // 启动服务器。
            mAndServer.launch();
            Toast.makeText(this, "AndServer已经成功启动", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mAndServer!=null && mAndServer.isRunning())
        {
            mAndServer.close();
        }
    }
}
