//package com.example.davidtest3;
//
//import android.app.Activity;
//import android.content.pm.ActivityInfo;
//import android.content.res.Configuration;
//import android.media.MediaPlayer;
//import android.media.MediaPlayer.OnCompletionListener;
//import android.media.MediaPlayer.OnPreparedListener;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.util.DisplayMetrics;
//import android.view.*;
//import android.view.SurfaceHolder.Callback;
//import android.view.View.OnClickListener;
//import android.widget.*;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//
//import java.io.File;
//
///**
// * 仿优酷播放，没有bug.
// *
// * @author hongdawei
// */
//
//public class MainActivity1 extends Activity implements OnClickListener, MediaPlayer.OnInfoListener {
//
//    private MediaPlayer mediaPlayer;
//    private SurfaceView surfaceView;
//    private String path;
//    private Boolean iStart = true;
//    private Boolean pause = true;
//    private ImageView issrt;
//    private int position;
//    private SeekBar seekbar;
//    private upDateSeekBar update; // 更新进度条用
//    private boolean flag = true; // 用于判断视频是否在播放中
//    private RelativeLayout relativeLayout;
//    private ImageView fullScreen;
//    private ImageView smallScreen;
//    private ImageView centerPlay;
//    private final static int MEDIATHREAD = 0x11;
//    private ProgressBar progressBar;
//
//    private String localUrl;
//
//    private long mediaLength = 0;
//    private long readSize = 0;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
//        setContentView(R.layout.main);
//        centerPlay = (ImageView) findViewById(R.id.centerPlay);
//        update = new upDateSeekBar(); // 创建更新进度条对象
//        mediaPlayer = new MediaPlayer();
//        initView();
//    }
//
//    private void initView() {
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            String localpath = Environment.getExternalStorageDirectory()
//                    .getAbsolutePath() + "/VideoCache/" + "tangbohu" + ".mp4";
//            File f = new File(localpath);
//            path = "http://211.144.85.251/tangbohu.mp4";
//        } else {
//            Toast.makeText(this, "SD卡不存在！", Toast.LENGTH_SHORT).show();
//        }
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        seekbar = (SeekBar) findViewById(R.id.seekbar);
//        issrt = (ImageView) findViewById(R.id.issrt);
//        issrt.setOnClickListener(this);
//        surfaceView = (SurfaceView) findViewById(R.id.surface);
//        relativeLayout = (RelativeLayout) findViewById(R.id.btm);
//        fullScreen = (ImageView) findViewById(R.id.quanping);
//        smallScreen = (ImageView) findViewById(R.id.xiaoping);
//        fullScreen.setOnClickListener(this);
//        smallScreen.setOnClickListener(this);
//        centerPlay.setOnClickListener(this);
//
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int mSurfaceViewWidth = dm.widthPixels;
//        int mSurfaceViewHeight = dm.heightPixels;
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//        lp.width = mSurfaceViewWidth;
//        lp.height = mSurfaceViewHeight * 1 / 3;
//        surfaceView.setLayoutParams(lp);
//        surfaceView.getHolder().setFixedSize(lp.width, lp.height);
//        surfaceView.getHolder().setKeepScreenOn(true);
//        surfaceView.getHolder().addCallback(new surfaceCallBack());
//        surfaceView.setOnClickListener(this);
//        mediaPlayer.setOnInfoListener(this);
//        seekbar.setOnSeekBarChangeListener(new surfaceSeekBar());
//        mediaPlayer.setOnErrorListener(new MyOnErrorListener());
//
//        /**
//         * 播放完毕
//         */
//        mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
////				issrt.setText("开始");
//                pause = true;
//                mediaPlayer.seekTo(0);
//                seekbar.setProgress(0);
//                mediaPlayer.pause();
//            }
//        });
//    }
//
//    @Override
//    public boolean onInfo(MediaPlayer mediaPlayer,int what, int extra) {
//        switch (what) {
//            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                progressBar.setVisibility(View.VISIBLE);
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                    progressBar.setVisibility(View.VISIBLE);
//                }
//                break;
//            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                mediaPlayer.start();
//                progressBar.setVisibility(View.GONE);
//                break;
//        }
//        return true;
//    }
//
//    private final class surfaceSeekBar implements OnSeekBarChangeListener {
//
//        @Override
//        public void onProgressChanged(SeekBar seekBar, int progress,
//                                      boolean fromUser) {
//            seekBar.setProgress(progress);
//        }
//
//        @Override
//        public void onStartTrackingTouch(SeekBar seekBar) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onStopTrackingTouch(SeekBar seekBar) {
//            // TODO Auto-generated method stub
//            int value = seekbar.getProgress() * mediaPlayer.getDuration() // 计算进度条需要前进的位置数据大小
//                    / seekbar.getMax();
//            mediaPlayer.seekTo(value);
//        }
//
//    }
//
//    private final class MyOnErrorListener implements MediaPlayer.OnErrorListener {
//
//        @Override
//        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
//            mediaPlayer.reset();
//            play(position);
//            return false;
//        }
//    }
//
//    private final class surfaceCallBack implements Callback {
//
//        @Override
//        public void surfaceChanged(SurfaceHolder holder, int format, int width,
//                                   int height) {
//        }
//
//        @Override
//        public void surfaceCreated(SurfaceHolder holder) {
//            if (position > 0) {
//                play(position);
//                position = 0;
//            }
//        }
//
//        @Override
//        public void surfaceDestroyed(SurfaceHolder holder) {
//            if (mediaPlayer.isPlaying()) {
//                position = mediaPlayer.getCurrentPosition();
//                mediaPlayer.stop();
//            }
//        }
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.issrt:
//                relativeLayout.setVisibility(View.GONE);
//                if (iStart) {
//                    play(0);
//                    issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
//                    iStart = false;
//                    new Thread(update).start();
//                    progressBar.setVisibility(View.VISIBLE);
//                } else {
//                            mediaPlayer.start();
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        issrt.setImageDrawable(getResources().getDrawable(R.drawable.player));
//                        pause = true;
//                    } else {
//                        if (pause) {
//                            progressBar.setVisibility(View.VISIBLE);
//                            issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
//                            pause = false;
//                        }
//                    }
//                }
//                break;
//            case R.id.quanping:
//                // 设置横屏
//                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                }
//                break;
//            case R.id.xiaoping:
//                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                }
//                break;
//            case R.id.centerPlay:
//                if (centerPlay.getVisibility() == View.VISIBLE) {
//                    centerPlay.setVisibility(View.INVISIBLE);
//                    issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
//                    mediaPlayer.start();
//                }
//                break;
//
//            case R.id.surface:
//                if (centerPlay.getVisibility() == View.VISIBLE) {
//                    centerPlay.setVisibility(View.INVISIBLE);
//                    issrt.setImageDrawable(getResources().getDrawable(R.drawable.pause));
//                    mediaPlayer.start();
//                    break;
//                }
//                if (relativeLayout.getVisibility() == View.VISIBLE) {
//                    relativeLayout.setVisibility(View.GONE);
//                } else {
//                    relativeLayout.setVisibility(View.VISIBLE);
//                }
//                break;
//        }
//    }
//
////	private void writeMedia() {
////		// TODO Auto-generated method stub
////		new Thread(new Runnable() {
////
////			@Override
////			public void run() {
////				FileOutputStream out = null;
////				InputStream is = null;
////
////				try {
////					URL url = new URL(path);
////					HttpURLConnection httpConnection = (HttpURLConnection) url
////							.openConnection();
////
////					if (localUrl == null) {
////						localUrl = Environment.getExternalStorageDirectory()
////								.getAbsolutePath()
////								+ "/VideoCache/"
////								+ "tangbohu" + ".mp4";
////					}
////
////					File cacheFile = new File(localUrl);
////
////					if (!cacheFile.exists()) {
////						cacheFile.getParentFile().mkdirs();
////						cacheFile.createNewFile();
////					}
////
////					readSize = cacheFile.length();
////					out = new FileOutputStream(cacheFile, true);
////
////					httpConnection.setRequestProperty("User-Agent", "NetFox");
////					httpConnection.setRequestProperty("RANGE", "bytes="
////							+ readSize + "-");
////
////					is = httpConnection.getInputStream();
////
////					mediaLength = httpConnection.getContentLength();
////					if (mediaLength == -1) {
////						return;
////					}
////
////					mediaLength += readSize;
////
////					byte buf[] = new byte[4 * 1024];
////					int size = 0;
////
////					while ((size = is.read(buf)) != -1) {
////						try {
////							out.write(buf, 0, size);
////							readSize += size;
////						} catch (Exception e) {
////							e.printStackTrace();
////						}
////					}
////
////				} catch (Exception e) {
////					e.printStackTrace();
////				} finally {
////					if (out != null) {
////						try {
////							out.close();
////						} catch (IOException e) {
////							//
////						}
////					}
////
////					if (is != null) {
////						try {
////							is.close();
////						} catch (IOException e) {
////							//
////						}
////					}
////				}
////
////			}
////		}).start();
////	}
//
//    @Override
//    protected void onDestroy() {
//        if (mediaPlayer != null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//            iStart = true;
//        }
//        super.onDestroy();
//    }
//
//    private void play(int position) {
//        mediaThread mediathread = new mediaThread();
//        new Thread(mediathread).start();
//    }
//
//    private class mediaThread implements Runnable {
//
//        @Override
//        public void run() {
//            Message msgMessage = new Message();
//            msgMessage.arg1 = MEDIATHREAD;
//            handler.sendMessage(msgMessage);
//        }
//    }
//
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.arg1) {
//                case MEDIATHREAD:
//                    try {
//                        mediaPlayer.reset();
//                        mediaPlayer.setDataSource(path);
//                        mediaPlayer.setDisplay(surfaceView.getHolder());
//                        mediaPlayer.prepare();// 进行缓冲处理
//                        mediaPlayer.setOnPreparedListener(new PreparedListener(
//                                position));// 监听缓冲是否完成
//                    } catch (Exception e) {
//                    }
//                    break;
//            }
//        }
//    };
//
//
//    private final class PreparedListener implements OnPreparedListener {
//        private int position;
//
//        public PreparedListener(int position) {
//            this.position = position;
//        }
//
//        @Override
//        public void onPrepared(MediaPlayer mp) {
//            progressBar.setVisibility(View.GONE);
//            mediaPlayer.start(); // 播放视频
//            if (position > 0) {
//                mediaPlayer.seekTo(position);
//            }
//        }
//
//    }
//
//    /**
//     * 更新进度条
//     */
//    Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (mediaPlayer == null) {
//                flag = false;
//            } else if (mediaPlayer.isPlaying()) {
//                flag = true;
//
//
//                int position = mediaPlayer.getCurrentPosition();
//                int mMax = mediaPlayer.getDuration();
//                int sMax = seekbar.getMax();
//                seekbar.setProgress(position * sMax / mMax);
//            } else {
//                return;
//            }
//        }
//
//        ;
//    };
//
//    class upDateSeekBar implements Runnable {
//
//        @Override
//        public void run() {
//            mHandler.sendMessage(Message.obtain());
//            if (flag) {
//                mHandler.postDelayed(update, 1000);
//            }
//        }
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//        // 检测屏幕的方向：纵向或横向
//        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            // 当前为横屏， 在此处添加额外的处理代码
//            relativeLayout.setVisibility(View.GONE);
//            fullScreen.setVisibility(View.INVISIBLE);
//            smallScreen.setVisibility(View.VISIBLE);
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int mSurfaceViewWidth = dm.widthPixels;
//            int mSurfaceViewHeight = dm.heightPixels;
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            lp.width = mSurfaceViewWidth;
//            lp.height = mSurfaceViewHeight;
//            surfaceView.setLayoutParams(lp);
//            surfaceView.getHolder().setFixedSize(mSurfaceViewWidth,
//                    mSurfaceViewHeight);
//        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            // 当前为竖屏， 在此处添加额外的处理代码
//            relativeLayout.setVisibility(View.GONE);
//            fullScreen.setVisibility(View.INVISIBLE);
//            smallScreen.setVisibility(View.VISIBLE);
//            DisplayMetrics dm = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(dm);
//            int mSurfaceViewWidth = dm.widthPixels;
//            int mSurfaceViewHeight = dm.heightPixels;
//            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                    RelativeLayout.LayoutParams.MATCH_PARENT,
//                    RelativeLayout.LayoutParams.MATCH_PARENT);
//            lp.width = mSurfaceViewWidth;
//            lp.height = mSurfaceViewHeight * 1 / 3;
//            surfaceView.setLayoutParams(lp);
//            surfaceView.getHolder().setFixedSize(lp.width, lp.height);
//        }
//        super.onConfigurationChanged(newConfig);
//    }
//}
