package com.duowei.tvshow.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017-03-02.
 */

public class MediaPlayerServeice extends Service{
    private MediaPlayer mPlayer;
//    private SongJson songJson;

//    private ArrayList<SongJson> mPlayList = new ArrayList<SongJson>();//播放列表
//    private ArrayList<SongJson> mRadomPlayList = new ArrayList<SongJson>();//随机播放列表
//    private SongJson mPlayingSong;//当前播放歌曲
    private ArrayList<String> mPlayList = new ArrayList<>();//播放列表
    private ArrayList<String> mRadomPlayList = new ArrayList<>();//随机播放列表
    private String mPlayingSong;//当前播放歌曲
    private int mPlayMode;//播放模式
    private int mSongPosition;//当前播放歌曲在列表中的位置

    public MediaPlayerServeice()
    {

    }

    @Override
    public void onCreate()
    {
        super.onCreate();
//        mPlayingSong = new SongJson();
        mPlayer = new MediaPlayer();
//        mBinder = new PlayBinder();

        //出现状态错误时，此方法规避fc
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra)
            {
                mPlayer.reset();
                return false;
            }
        });

        //监听音乐是否播放完毕
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                playNext();
            }
        });
    }



    private IBinder mBinder;
    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    //快进快退
    private void seekTo(int msec)
    {
        mPlayer.getDuration();//得到歌曲总时间
        mPlayer.seekTo(msec);
    }

    //得到当前播放位置
    public int getCurrentPosition()
    {
        return mPlayer.getCurrentPosition();
    }

    //播放下一首
    private void playNext()
    {

    }

    //停止播放
    private void stop()
    {
        mPlayer.stop();
    }
    //得到播放循环模式
    private int setPlayMode(int mode)
    {
        return mPlayMode = mode;
    }

//    //添加歌曲至播放列表
    private void setPlayList(ArrayList<String> list)
    {
        this.mPlayList=list;
    }

    // 得到当前播放进度
    private int getCurrTime()
    {
        return mPlayer.getCurrentPosition();
    }

    //继续播放
    private void continuePlay()
    {
        mPlayer.start();
    }

    //是否正在播放
    private boolean isplaying()
    {
        return mPlayer.isPlaying();
    }

    //暂停
    private void pause()
    {
        mPlayer.pause();
    }

    // 播放音乐
    private void play(final String url)
    {
        mPlayer.reset();
        try
        {
            mPlayer.setDataSource(url);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
            {
                @Override
                public void onPrepared(MediaPlayer mp)
                {
                    mPlayer.start();
                }
            });
            mPlayer.prepareAsync();

        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    // 自定义Binder
    public class PlayBinder extends Binder
    {
        public void play(String url)
        {
            MediaPlayerServeice.this.play(url);
        }

        public boolean isplaying()
        {
            return MediaPlayerServeice.this.isplaying();
        }

        public void pause()
        {
            MediaPlayerServeice.this.pause();
        }

        public void continuePlay()
        {
            MediaPlayerServeice.this.continuePlay();
        }

        public int getCurrTime()
        {
            return MediaPlayerServeice.this.getCurrTime();
        }

        public void setPlayList(ArrayList<String> list)
        {
            MediaPlayerServeice.this.setPlayList(list);
        }

        public int setPlayMode(int mode)
        {

            return  MediaPlayerServeice.this.setPlayMode(mode);
        }

        public void playNext()
        {
            MediaPlayerServeice.this.playNext();
        }

        public void stop()
        {
            MediaPlayerServeice.this.stop();
        }
        public int getCurrentPosition()
        {
            return MediaPlayerServeice.this.getCurrentPosition();
        }
    }
}
