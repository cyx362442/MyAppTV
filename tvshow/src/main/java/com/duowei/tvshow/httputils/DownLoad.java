package com.duowei.tvshow.httputils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.duowei.tvshow.contact.FileDir;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by Administrator on 2017-04-06.
 */

public class DownLoad {
    private DownLoad(){}
    private static DownLoad load;
    public static DownLoad getInstance() {
        if(load==null){
            load=new DownLoad();
        }
        return load;
    }
    private long  mReference = 0 ;
    private DownloadManager downloadManager ;
    public void startLoad(Context context,String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //设置通知类型
        setNotification(request) ;
        //设置下载路径
        File dir = new File(FileDir.getDir());//路径视频
        if (!dir.exists()) {//路径不存在则创建
            dir.mkdir();
        }
        File fileZip = new File(FileDir.getZipVideo());//下载保存的位置
        Uri uri = Uri.fromFile(fileZip);
        request.setDestinationUri(uri);
        /*在默认的情况下，通过Download Manager下载的文件是不能被Media Scanner扫描到的 。
        进而这些下载的文件（音乐、视频等）就不会在Gallery 和  Music Player这样的应用中看到。
        为了让下载的音乐文件可以被其他应用扫描到，我们需要调用Request对象的
         */
        request.allowScanningByMediaScanner() ;
        /*如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
        我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true。*/
        request.setVisibleInDownloadsUi( true ) ;
        //设置请求的Mime
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        request.setMimeType(mimeTypeMap.getMimeTypeFromExtension(url));
        //开始下载
        downloadManager = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE) ;
        mReference = downloadManager.enqueue( request ) ;
    }
    /**
     * 设置状态栏中显示Notification
     */
    void setNotification(DownloadManager.Request request ) {
        //设置Notification的标题
        request.setTitle( "文件下载中……" ) ;
        //设置描述
        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE ) ;
//        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED ) ;
//        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION ) ;
//        request.setNotificationVisibility( DownloadManager.Request.VISIBILITY_HIDDEN ) ;
    }
}
