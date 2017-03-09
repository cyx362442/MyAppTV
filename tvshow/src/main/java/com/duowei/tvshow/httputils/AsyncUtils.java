package com.duowei.tvshow.httputils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.duowei.tvshow.SecondActivity;
import com.duowei.tvshow.contact.FileDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Administrator on 2017-01-09.
 */

public class AsyncUtils extends AsyncTask<String, Integer, Integer> {
    Context context;
    private final ProgressDialog mProgressDialog;
    public AsyncUtils(Context context) {
        this.context = context;
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setTitle("提示");
        mProgressDialog.setMessage("文件下载中……");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mProgressDialog.show();
    }
    @Override
    protected Integer doInBackground(String... params) {
        int count;
        int result = 0;
        long total = 0;
        int lenghtOfFile = 0;
        File dir = new File(FileDir.getDir());//路径视频
        if (!dir.exists()) {//路径不存在则创建
            dir.mkdir();
        }
        File fileZip = new File(FileDir.getZipVideo());//下载保存的位置
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            URL url = new URL(params[0]);
            URLConnection urlConnection = url.openConnection();//开启连接
            urlConnection.connect();
            lenghtOfFile = urlConnection.getContentLength();//获取下载文件的总长度
            is = urlConnection.getInputStream();// 开启流
            fos = new FileOutputStream(fileZip);// 开启写的流
            byte[] bytes = new byte[1024];
            while ((count = is.read(bytes)) != -1) {
                total += count;
                fos.write(bytes, 0, count);
                publishProgress((int)(total*50/lenghtOfFile));
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lenghtOfFile == 0 || total < lenghtOfFile) {
            result = -1;
        } else {
            result = 1;
        }
        return result;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(progress[0]);
        mProgressDialog.incrementProgressBy(progress[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case 0:
                mProgressDialog.dismiss();
                break;
            case -1:
                mProgressDialog.dismiss();
                Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
                break;
            case 1:
                mProgressDialog.setMessage("下载成功，正在解压中……");
                mProgressDialog.dismiss();
//                ZipAsync zipAsync = new ZipAsync();
//                zipAsync.execute();
                ZipExtractorTask task = new ZipExtractorTask(FileDir.getZipVideo(), FileDir.getDir(), context, true);
                task.execute();
                break;
            default:
                break;
        }
    }
    class ZipAsync extends AsyncTask<Integer,Integer,Integer>{
        @Override
        protected Integer doInBackground(Integer... Integer) {
            int ch = 0;
            try {
                ZipInputStream in = new ZipInputStream(new FileInputStream(FileDir.getZipVideo()));//解压的文件
                ZipEntry z;
                String name = "";
                while ((z = in.getNextEntry()) != null) {
                    name = z.getName();
                    if (z.isDirectory()) {
                        // get the folder name of the widget
                        name = name.substring(0, name.length() - 1);
                        File folder = new File(FileDir.getDir() + File.separator + name);
                        folder.mkdirs();
                    } else {
                        File file = new File(FileDir.getDir() + File.separator + name);
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        while ((ch = in.read(buffer)) != -1) {
                            out.write(buffer, 0, ch);
                            out.flush();
                        }
                        out.close();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ch;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer==-1){
                Intent intent = new Intent(context, SecondActivity.class);
                context.startActivity(intent);
                Activity context = (Activity) AsyncUtils.this.context;
                context.finish();
                mProgressDialog.dismiss();
            }
            super.onPostExecute(integer);
        }
    }
}
