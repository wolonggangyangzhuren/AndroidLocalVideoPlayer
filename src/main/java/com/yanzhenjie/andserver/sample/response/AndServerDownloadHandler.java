/*
 * AUTHOR：Yan Zhenjie
 *
 * DESCRIPTION：create the File, and add the content.
 *
 * Copyright © ZhiMore. All Rights Reserved
 *
 */
package com.yanzhenjie.andserver.sample.response;

import com.yanzhenjie.andserver.AndServerRequestHandler;
import com.yanzhenjie.andserver.util.HttpRequestParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created on 2016/7/1.
 *
 * @author Yan Zhenjie: QQ: 757699476.
 */
public class AndServerDownloadHandler implements AndServerRequestHandler {

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        // 拿到客户端参数key-value。
        Map<String, String> params = HttpRequestParser.parse(request);

        // 假设客户端要下载的文件名是通过filename这个参数提交的。
        String filePath = params.get("filename");
        File file = new File("/storage/emulated/0/"+filePath);
        if(file.exists()) {
            System.out.println("文件存在");
            response.setStatusCode(200);// 文件存在，返回成功。
            long contentLength = file.length();
            response.setHeader("ContentLength", Long.toString(contentLength));
            InputStream inputStream = new FileInputStream(file);
            HttpEntity httpEntity = new InputStreamEntity(inputStream, contentLength);
            response.setEntity(httpEntity);
        } else {
            // 文件不存在。
            response.setStatusCode(404);
        }
    }

}
