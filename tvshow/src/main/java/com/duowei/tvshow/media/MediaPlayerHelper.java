package com.duowei.tvshow.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import java.util.ArrayList;


/**
 * MediaPlayer帮助类
 */
public class MediaPlayerHelper implements OnPreparedListener, 
                OnCompletionListener,
                OnErrorListener,
                OnSeekCompleteListener,
                OnBufferingUpdateListener,
                SurfaceHolder.Callback {
   
    /*
     * 播放器状态
     */
    public static enum MediaPlayerState {
        IDEL        ("空闲"),
        PREPARING   ("准备中"),
        PREPARED    ("已准备"),
        PLAYING     ("播放中"),
        PAUSED      ("已暂停"),
        STOPED      ("已停止"),
        COMPLETION  ("播放结束"),
        ERROR       ("发生错误"),
        ;
        String name;
        private MediaPlayerState(String name){
            this.name = name;
        }
        public String getName() {
            return name;
        }
    }
    public static interface OnMediaPlayerStateChangeListener {
        public void onMediaPlayerStateChange(MediaPlayer mediaPlayer, MediaPlayerState mediaState);
    }
    private OnMediaPlayerStateChangeListener mOnMediaPlayerStateChangeListener;
    
    /*
     * 播放器显示类型
     */
    public static enum MediaPlayerDisplayType {
        FULLSCREEN("全屏"),
        SCALE("缩放"),
        ;
        private String name;
        private MediaPlayerDisplayType(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
    }
    private MediaPlayerDisplayType mMediaPlayerDisplayType = MediaPlayerDisplayType.SCALE; // 默认全屏显示
    
    
    public static final String TAG = MediaPlayerHelper.class.getSimpleName();
    
    /*
     * 日志包装类
     */
    public static class LogWrapper {
        private String name;
        private LogWrapper appendLogger;
        private boolean debugMode;
        public LogWrapper() {
            this("", false);
        }
        public LogWrapper(String name, boolean debugMode) {
            this.name = name;
            this.debugMode = debugMode;
        }
        public static LogWrapper getLogger(String name, boolean debugMode){
            return new LogWrapper(name, debugMode);
        }
        public void setAppendLogger(LogWrapper appendLogger) {
            this.appendLogger = appendLogger;
        }
        public LogWrapper getAppendLogger() {
            return appendLogger;
        }
        public int i(String msg) {
            if(appendLogger != null) 
                appendLogger.i(msg);
            if(debugMode)
                return Log.i(name, msg);
            return -1;
        }
        public int e(String msg) {
            if(appendLogger != null) 
                appendLogger.e(msg);
            if(debugMode)
                return Log.e(name, msg);
            return -1;
        }
    }
    private LogWrapper logger = LogWrapper.getLogger(TAG, false);
    
    /*
     * UI控件
     */
    private SurfaceView mSurfaceView;
    
    private MediaPlayer mMediaPlayer; // 当前播放器（总是指向当前使用状态的播放器对象）
    private MediaPlayerState mMediaPlayerState = MediaPlayerState.IDEL; // 当前播放器状态（总是指向当前使用状态的播放器对象），默认为空闲状态
    
    private MediaPlayer mNextMediaPlayer; // 第二播放器
    private MediaPlayerState mNextMediaPlayerState = MediaPlayerState.IDEL; // 第二播放器状态，默认为空闲状态
    
    private int mSurfaceViewWidth; // 同SurfaceView父类控件的宽高保持一致
    private int mSurfaceViewHeight; // 在surfaceChange中判断到与此处不一致时，触发配置视频的显示类型
                                                // 注：两处触发configMediaPlayerDisplayType，一是视频切换，而是控件尺寸改变
    
    /*
     * 事件Handler
     */
    public final int EVENT_PLAY = 0;
    public final int EVENT_NEXT_PLAY = 1;
    public final int EVENT_PAUSE = 2;
    public final int EVENT_START = 3;
    public final int EVENT_STOP = 4;
    public final int EVENT_SEEK_TO = 5;
    public final int EVENT_CONFIG_DISPLAY_TYPE = 6;
    
    private class EventHandler extends Handler {
        public EventHandler(Looper looper) {
            super(looper);
        }
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_PLAY:
                {
                    try {
                        String path = (String) msg.obj;
                        
                        if(TextUtils.isEmpty(path)){
                            logger.i("数据源不可为空");
                            return;
                        }
                        /*
                         * 重置MediaPlayer
                         * 
                         * 无论是处在任何状态，调用reset方法重置MediaPlayer对象
                         * 如果是重复播放同一视频，可配合使用seekTo、start等方法来替代，而不是每次都执行play方法
                         * 由于重置是异步操作，且重置过程中无法设置数据源，此处采用sleep来进行延时
                         */
                        if(getMediaPlayerState(mMediaPlayer) != MediaPlayerState.IDEL){
                            mMediaPlayer.reset(); // 切换数据源
                            SystemClock.sleep(1000); // 等待重置完成
                            setMediaPlayerState(mMediaPlayer, MediaPlayerState.IDEL);
                        }
                        
                        // 重置NextMediaPlayer，如果处在等待播放的状态
                        if(getMediaPlayerState(mNextMediaPlayer) == MediaPlayerState.PREPARED){
                            mNextMediaPlayer.reset(); // 从Error中恢复
                            SystemClock.sleep(1000); // 等待重置完成
                            setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.IDEL);
                        }
                        mMediaPlayer.setDataSource(path);
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.PREPARING);
                        mMediaPlayer.prepare(); // 阻塞当前线程
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.PREPARED);
                        
                        mMediaPlayer.start();
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.PLAYING);
                        
                        configMediaPlayerDisplayType(); // 配置视频的显示类型
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.ERROR);
                        
                        mMediaPlayer.reset(); // 从Error中恢复
                        SystemClock.sleep(1000); // 等待重置完成
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.IDEL);
                    }
                    
                    break;
                }
                case EVENT_NEXT_PLAY:
                {
                    try {
                        String path = (String) msg.obj;
                        
                        // 重置NextMediaPlayer，如果不是处在空闲状态的话
                        if(getMediaPlayerState(mNextMediaPlayer) != MediaPlayerState.IDEL){
                            mNextMediaPlayer.reset(); // 从Error中恢复
                            SystemClock.sleep(1000); // 等待重置完成
                            setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.IDEL);
                        }
                        
                        if(TextUtils.isEmpty(path)){
                            logger.i("清除可能存在的待播放音视频");
                            mMediaPlayer.setNextMediaPlayer(null);
                            return;
                        }
                        if(getMediaPlayerState(mMediaPlayer) == MediaPlayerState.IDEL
                                || getMediaPlayerState(mMediaPlayer) == MediaPlayerState.STOPED 
                                || getMediaPlayerState(mMediaPlayer) == MediaPlayerState.COMPLETION){ // 空闲或结束
                            play(path);
                            return;
                        }
                        
                        mNextMediaPlayer.setDataSource(path);
                        setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.PREPARING);
                        mNextMediaPlayer.prepare(); // 执行准备，等待前一段音视频播放结束
                        // SystemClock.sleep(10 * 1000); // 测试代码：跳过后面的自动开始播放
                        setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.PREPARED);
                        
                        if(getMediaPlayerState(mMediaPlayer) == MediaPlayerState.PLAYING){ 
                            logger.i("第二播放器 - 稍后自动开始播放");
                            mMediaPlayer.setNextMediaPlayer(mNextMediaPlayer); // prepared之后，否则：next player is not prepared
                        }else{ 
                            logger.i("第二播放器 - 手动开始播放");
                            mMediaPlayer.start(); // 准备完成时，主播放器已播放完成，手动开始播放
                            setMediaPlayerState(mMediaPlayer, MediaPlayerState.PLAYING);
                            
                            configMediaPlayerDisplayType(); // 配置视频的显示类型
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.ERROR);

                        mNextMediaPlayer.reset(); // 从Error中恢复
                        SystemClock.sleep(1000); // 等待重置完成
                        setMediaPlayerState(mNextMediaPlayer, MediaPlayerState.IDEL);
                    }
                    
                    break;
                }
                case EVENT_PAUSE:
                {
                    if(getMediaPlayerState(mMediaPlayer) != MediaPlayerState.PLAYING){
                        logger.i("忽略暂停操作");
                        return;
                    }
                    
                    mMediaPlayer.pause();
                    while(true){
                        if(!mMediaPlayer.isPlaying()){
                            setMediaPlayerState(mMediaPlayer, MediaPlayerState.PAUSED);
                            
                            break;
                        }
                        SystemClock.sleep(200); // 轮询间隔时间
                    }
                    break;
                }
                case EVENT_START:
                {
                    if(getMediaPlayerState(mMediaPlayer) == MediaPlayerState.IDEL
                            || getMediaPlayerState(mMediaPlayer) == MediaPlayerState.STOPED
                            || getMediaPlayerState(mMediaPlayer) == MediaPlayerState.ERROR){
                        logger.i("忽略继续操作");
                        return;
                    }
                    
                    mMediaPlayer.start();
                    while(true){
                        if(mMediaPlayer.isPlaying()){
                            setMediaPlayerState(mMediaPlayer, MediaPlayerState.PLAYING);
                            
                            break;
                        }
                        SystemClock.sleep(200); // 轮询间隔时间
                    }
                    break;
                }
                case EVENT_STOP:
                {
                    mMediaPlayer.stop();
                    setMediaPlayerState(mMediaPlayer, MediaPlayerState.STOPED);
                    break;
                }
                case EVENT_SEEK_TO:
                {
                    if(getMediaPlayerState(mMediaPlayer) != MediaPlayerState.PLAYING){
                        logger.i("忽略定位操作");
                        return;
                    }
                    
                    try {
                        int msec = msg.arg1;
                        
                        /*
                         * 重置MediaPlayer
                         */
                        mMediaPlayer.stop(); // 停止
                        SystemClock.sleep(1000); // 等待停止完成（可能不需要？）
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.IDEL);
                        
                        mMediaPlayer.prepare(); // 阻塞当前线程
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.PREPARED);
                        
                        mMediaPlayer.seekTo(msec); // 定位完成后，自动开始播放
                        logger.i("定位完成后，自动开始播放");
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.ERROR);
                        
                        mMediaPlayer.reset(); // 从Error中恢复
                        SystemClock.sleep(1000); // 等待重置完成
                        setMediaPlayerState(mMediaPlayer, MediaPlayerState.IDEL);
                    }
                    
                    break;
                }
                case EVENT_CONFIG_DISPLAY_TYPE:
                {
                    try{
                        double vw = -1;
                        double vh = -1;
                        double sw = mSurfaceViewWidth; // 使用父类控件的宽高
                        double sh = mSurfaceViewHeight;
                        int max = 20; // 限制轮询次数，尤其是对于音频文件
                        while(--max != 0){
                            if(getMediaPlayerState(mMediaPlayer) != MediaPlayerState.PLAYING){
                                break;
                            }
                            if(mMediaPlayer.getVideoWidth() != 0 && mMediaPlayer.getVideoHeight() != 0){
                                vw = mMediaPlayer.getVideoWidth();
                                vh = mMediaPlayer.getVideoHeight();
                                break;
                            }
                            SystemClock.sleep(200); // 轮询间隔时间
                        }
                        
                        logger.i("视频分辨率：" + vw + "," + vh + "; 容器分辨率：" + sw + "," + sh);
                        if(vw == -1 || vh == -1)
                            return;
                        
                        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT);
                        
                        switch (mMediaPlayerDisplayType) {
                            case FULLSCREEN:
                            {
                                lp.setMargins(0, 0, 0, 0);
                            }
                            break;
                            case SCALE:
                            {
                                double sc = sw / sh; // 宽高比
                                double vc = vw / vh;
                                int ml, mt, mr, mb;
                                
                                if(vc > sc){ // 视频比容器更胖
                                    ml = mr = 0;
                                    mt = mb = (int)((sh - sw / vc) / 2);
                                }else{
                                    ml = mr = (int)((sw - sh / vc) / 2);
                                    mt = mb = 0;
                                }
                                lp.setMargins(ml, mt, mr, mb);
                            }
                            break;
                        }
                        
                        mUIHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mSurfaceView.setLayoutParams(lp);
                            }
                        });
                        
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    };
    private HandlerThread mHandlerThread;
    private EventHandler mEventHandler;
    
    private Handler mUIHandler;
    private ArrayList<String>listPath;
    private int position=0;

    public MediaPlayerHelper(SurfaceView surfaceView, ArrayList<String>listPath){
        this.mSurfaceView = surfaceView; 
        this.listPath=listPath;

        mMediaPlayer = new MediaPlayer();
        mNextMediaPlayer = new MediaPlayer();
        
        mMediaPlayer.setOnPreparedListener(this);
        mNextMediaPlayer.setOnPreparedListener(this);
        
        mMediaPlayer.setOnCompletionListener(this);
        mNextMediaPlayer.setOnCompletionListener(this);
        
        mMediaPlayer.setOnErrorListener(this);
        mNextMediaPlayer.setOnErrorListener(this);
        
        mMediaPlayer.setOnSeekCompleteListener(this);
        mNextMediaPlayer.setOnSeekCompleteListener(this);
        
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mNextMediaPlayer.setOnBufferingUpdateListener(this);
        
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // AudioManager音频管理器，分通话、系统、音乐等多个实例
        mNextMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        
        if(surfaceView != null){
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            // 配置...
            // surfaceHolder.setFixedSize(300, 300); // 指定固定的控件大小
            // surfaceHolder.setSizeFromLayout();// 自适应容器大小 (this is the default).
            // surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            // surfaceHolder.setFormat(PixelFormat.TRANSPARENT); // 背景透明
            surfaceHolder.setKeepScreenOn(true);
            surfaceHolder.addCallback(this);
        }
        
        mHandlerThread = new HandlerThread("event handler thread", Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mEventHandler = new EventHandler(mHandlerThread.getLooper());
        
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * 判断父类控件的宽高，来触发configMediaPlayerDisplayType方法
         */
        int w = ((RelativeLayout)mSurfaceView.getParent()).getWidth();
        int h = ((RelativeLayout)mSurfaceView.getParent()).getHeight();
        if(w != 0 &&  h != 0 && (mSurfaceViewWidth != w || mSurfaceViewHeight != h)){
            mSurfaceViewWidth = w;
            mSurfaceViewHeight = h;
            configMediaPlayerDisplayType();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHandlerThread.quit();
        mMediaPlayer.release();
        mNextMediaPlayer.release();
    }

    public void play(String path) {
        Message msg = mEventHandler.obtainMessage(EVENT_PLAY, path);
        mEventHandler.sendMessage(msg);
    }

    public void nextPlay(String path) {
        Message msg = mEventHandler.obtainMessage(EVENT_NEXT_PLAY, path);
        mEventHandler.sendMessage(msg);
    }

    /**
     * @see MediaPlayer#pause()
     */
    public void pause() {
        mEventHandler.sendEmptyMessage(EVENT_PAUSE);
    }

    /**
     * @see MediaPlayer#start()
     */
    public void start(){
        mEventHandler.sendEmptyMessage(EVENT_START);
    }

    /**
     * @see MediaPlayer#stop()
     */
    public void stop() {
        mEventHandler.sendEmptyMessage(EVENT_STOP);
    }

    /**
     * @see MediaPlayer#seekTo(int)
     */
    public void seekTo(int msec){
        Message msg = mEventHandler.obtainMessage(EVENT_SEEK_TO, msec, 0);
        mEventHandler.sendMessage(msg);
    }

    /**
     * @see MediaPlayer#setLooging(boolean)
     */
    public void setLooging(boolean looping){
        mMediaPlayer.setLooping(looping);
    }

    /**
     * @see MediaPlayer#isLooping()
     */
    public boolean isLooping(){
        return mMediaPlayer.isLooping();
    }
    
    /**
     * 通过轮询来获取播放进度？
     * 
     * getDuration() 总进度
     * getCurrentPosition() 当前进度
     * 
     * @see MediaPlayer#getCurrentPosition()
     */
    public int getCurrentPosition(){
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * @see MediaPlayer#getDuration()
     */
    public int getDuration(){
        return mMediaPlayer.getDuration();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if(getMediaPlayerState(mp) == MediaPlayerState.PREPARED){
            logger.i("定位完成 - 开始播放");
            mMediaPlayer.start();
            setMediaPlayerState(mp, MediaPlayerState.PLAYING);
        }else{
            logger.i("定位异常");
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        logger.i("当前播放器" + (mp == mMediaPlayer ? "*" : "") + " - 缓冲进度 -> " + percent + "%"
                + "； 第二播放器" + (mp == mMediaPlayer ? "" : "*") + " - 缓冲进度 -> " + percent + "%");
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        setMediaPlayerState(mp, MediaPlayerState.ERROR);
        return false;
    }

    @Override
        public void onCompletion(MediaPlayer mp) {
//        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT); // 可注释掉其余内容，测试该属性
//        mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        position++;
        position=position%listPath.size();
        play(listPath.get(position));
//        if(getMediaPlayerState(mp) == MediaPlayerState.ERROR)
//            return; // 确保错误状态不会被覆盖，便于状态的管理
//
//        setMediaPlayerState(mp, MediaPlayerState.COMPLETION);
//        if(mMediaPlayer.isLooping()){
//            logger.i("继续循环");
//            setMediaPlayerState(mp, MediaPlayerState.PLAYING);
//
//            return;
//        }
//
//        if(getMediaPlayerState(mNextMediaPlayer) == MediaPlayerState.PREPARING
//                || getMediaPlayerState(mNextMediaPlayer) == MediaPlayerState.PREPARED){ // 准备中或已准备
//            logger.i("播放器切换");
//
//            if(mSurfaceView != null){
//                mMediaPlayer.setDisplay(null);
//                mNextMediaPlayer.setDisplay(mSurfaceView.getHolder());
//            }
//
//            MediaPlayer t = mMediaPlayer;
//            mMediaPlayer = mNextMediaPlayer;
//            mNextMediaPlayer = t;
//
//            MediaPlayerState m = mMediaPlayerState;
//            mMediaPlayerState = mNextMediaPlayerState;
//            mNextMediaPlayerState = m;
//
//            if(getMediaPlayerState(mMediaPlayer) == MediaPlayerState.PREPARED){
//                setMediaPlayerState(mMediaPlayer, MediaPlayerState.PLAYING);
//
//                configMediaPlayerDisplayType(); // 配置视频的显示类型
//            }
//        }
    }
    public MediaPlayerState getMediaPlayerState(MediaPlayer mediaPlayer){
        return mediaPlayer == mMediaPlayer ? mMediaPlayerState : mNextMediaPlayerState;
    }
    private void setMediaPlayerState(MediaPlayer mediaPlayer, MediaPlayerState mediaPlayerState){
        if(mediaPlayer == mMediaPlayer)
            mMediaPlayerState = mediaPlayerState;
        else if(mediaPlayer == mNextMediaPlayer)
            mNextMediaPlayerState = mediaPlayerState;

        logger.i("当前播放器" + (mediaPlayer == mMediaPlayer ? "*" : "") + " -> " + mMediaPlayerState.getName() 
                + "； 第二播放器" + (mediaPlayer == mMediaPlayer ? "" : "*") + " -> " + mNextMediaPlayerState.getName());
        
        if(mediaPlayer == mMediaPlayer && mOnMediaPlayerStateChangeListener != null)
            mOnMediaPlayerStateChangeListener.onMediaPlayerStateChange(mediaPlayer, mediaPlayerState);
    }
    public MediaPlayer getMediaPlayer(){
        return this.mMediaPlayer;
    }
    
    public MediaPlayerDisplayType getMediaPlayerDisplayType() {
        return mMediaPlayerDisplayType;
    }

    public void setMediaPlayerDisplayType(MediaPlayerDisplayType displayType) {
    	if(this.mMediaPlayerDisplayType == displayType)
    		return;
    	
        this.mMediaPlayerDisplayType = displayType;
        
        configMediaPlayerDisplayType();
    }

    public void configMediaPlayerDisplayType() {
        if (mSurfaceView != null) {
            mEventHandler.sendEmptyMessageDelayed(EVENT_CONFIG_DISPLAY_TYPE, 200);
        }
    }

    public void toggleMediaPlayerDisplayType(){
    	setMediaPlayerDisplayType(this.mMediaPlayerDisplayType == MediaPlayerDisplayType.FULLSCREEN ? 
    			MediaPlayerDisplayType.SCALE : MediaPlayerDisplayType.FULLSCREEN);
    }

    public void setOnMediaStateChangeListener(OnMediaPlayerStateChangeListener listener) {
        this.mOnMediaPlayerStateChangeListener = listener;
    }
    public OnMediaPlayerStateChangeListener getOnMediaStateChangeListener() {
        return this.mOnMediaPlayerStateChangeListener;
    }

    public void setAppendLogger(LogWrapper appendLogger) {
        this.logger.setAppendLogger(appendLogger);
    }
    public LogWrapper getAppendLogger() {
        return this.logger.getAppendLogger();
    }
    public LogWrapper getLogger() {
        return this.logger;
    }
}

