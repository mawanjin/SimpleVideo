package com.example.davidtest3;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.File;
import java.io.IOException;

/**
 * 仿优酷播放，没有bug.
 *
 * @author hongdawei
 */

public class MainActivity extends Activity implements OnClickListener, MediaPlayer.OnInfoListener {

    private int postion = 0;
    private MediaPlayer mediaPlayer;
    private SurfaceView surfaceView;
    private String path;
    private Boolean iStart = true;
    private Boolean pause = true;
    private ImageView issrt;
    private int position;
    private SeekBar seekbar;
    private upDateSeekBar update; // 更新进度条用
    private boolean isplayingFlag = true; // 用于判断视频是否在播放中
    private RelativeLayout btm;
    private ImageView fullScreen;
    //    private ImageView smallScreen;
    private ImageView centerPlay;
    private final static int MEDIATHREAD = 0x11;
    private ProgressBar progressBar;
    private RelativeLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
        setContentView(R.layout.main);
        centerPlay = (ImageView) findViewById(R.id.centerPlay);
        update = new upDateSeekBar(); // 创建更新进度条对象
        mediaPlayer = new MediaPlayer();
        initView();
    }

    private void initView() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            String localpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/VideoCache/" + "tangbohu" + ".mp4";
            File f = new File(localpath);
            path = "http://aia.fortune-net.cn/share/2014-9-17/1567340084.mp4";
//            path = "http://211.144.85.251/tangbohu.mp4";


        } else {
            Toast.makeText(this, "SD卡不存在！", Toast.LENGTH_SHORT).show();
        }
        container = (RelativeLayout) findViewById(R.id.container);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        issrt = (ImageView) findViewById(R.id.issrt);
        issrt.setOnClickListener(this);

        surfaceView = (SurfaceView) findViewById(R.id.surface);
        btm = (RelativeLayout) findViewById(R.id.btm);
        fullScreen = (ImageView) findViewById(R.id.quanping);
        container.setOnClickListener(this);
        fullScreen.setOnClickListener(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int mSurfaceViewWidth = dm.widthPixels;
        int mSurfaceViewHeight = dm.heightPixels;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.width = mSurfaceViewWidth;
        lp.height = mSurfaceViewHeight * 1 / 3;
        surfaceView.setLayoutParams(lp);
        surfaceView.getHolder().setFixedSize(lp.width, lp.height);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(new SurfaceViewLis());
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnInfoListener(this);
        seekbar.setOnSeekBarChangeListener(new surfaceSeekBar());
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();
            }
        });
//        mediaPlayer.setOnErrorListener(new MyOnErrorListener());

        mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());

    }

    private final class MyOnCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
            seekbar.setProgress(0);
            issrt.setImageDrawable(getResources().getDrawable(R.drawable.player));
        }
    }

    private class SurfaceViewLis implements SurfaceHolder.Callback {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (postion == 0) {
                try {
                    // 把视频输出到SurfaceView上
                    mediaPlayer.setDisplay(surfaceView.getHolder());
//                    play();
//                    mediaPlayer.seekTo(postion);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                progressBar.setVisibility(View.VISIBLE);
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                progressBar.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                progressBar.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    private final class surfaceSeekBar implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            seekBar.setProgress(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
            int value = seekbar.getProgress() * mediaPlayer.getDuration() // 计算进度条需要前进的位置数据大小
                    / seekbar.getMax();
            mediaPlayer.seekTo(value);
        }

    }

    private final class MyOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            mediaPlayer.reset();

            return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.container:
                if (centerPlay.getVisibility() == View.VISIBLE) {
                    try {
                        centerPlay.setVisibility(View.GONE);
                        play();
                        issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    if (btm.getVisibility() == View.VISIBLE)
                        btm.setVisibility(View.GONE);
                    else
                        btm.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.quanping:
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case R.id.issrt:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    issrt.setImageDrawable(getResources().getDrawable(R.drawable.player));
                } else {
                        mediaPlayer.start();
                        issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public void play() throws IllegalArgumentException, SecurityException,
            IllegalStateException, IOException {
//        mediaPlayer.reset();
        new Thread(update).start();
        progressBar.setVisibility(View.VISIBLE);
        mediaPlayer.prepareAsync();
//        mediaPlayer.start();
    }

    /**
     * 更新进度条
     */
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (mediaPlayer == null) {
                isplayingFlag = false;
            } else if (mediaPlayer.isPlaying()) {
                isplayingFlag = true;

                int position = mediaPlayer.getCurrentPosition();
                int mMax = mediaPlayer.getDuration();
                int sMax = seekbar.getMax();
                seekbar.setProgress(position * sMax / mMax);
            } else {
                return;
            }
        }

        ;
    };

    class upDateSeekBar implements Runnable {

        @Override
        public void run() {
            mHandler.sendMessage(Message.obtain());
            if (isplayingFlag) {
                mHandler.postDelayed(update, 1000);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            btm.setVisibility(View.GONE);
//            fullScreen.setVisibility(View.INVISIBLE);
//            smallScreen.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.width = mSurfaceViewWidth;
            lp.height = mSurfaceViewHeight;
            surfaceView.setLayoutParams(lp);
            surfaceView.getHolder().setFixedSize(mSurfaceViewWidth,
                    mSurfaceViewHeight);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            btm.setVisibility(View.GONE);
//            fullScreen.setVisibility(View.INVISIBLE);
//            smallScreen.setVisibility(View.VISIBLE);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int mSurfaceViewWidth = dm.widthPixels;
            int mSurfaceViewHeight = dm.heightPixels;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            lp.width = mSurfaceViewWidth;
            lp.height = mSurfaceViewHeight * 1 / 3;
            surfaceView.setLayoutParams(lp);
            surfaceView.getHolder().setFixedSize(lp.width, lp.height);
        }
        super.onConfigurationChanged(newConfig);
    }

}
