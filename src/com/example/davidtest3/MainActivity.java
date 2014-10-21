package com.example.davidtest3;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.*;
import android.view.SurfaceHolder.Callback;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 
 * 仿优酷播放，没有bug.
 * 
 * @author hongdawei
 *
 */
 
public class MainActivity extends Activity implements OnClickListener ,MediaPlayer.OnBufferingUpdateListener {
	private MediaPlayer mediaPlayer;
	private String path;
	private SurfaceView surfaceView;
	private Boolean iStart = true;
	private Boolean pause = true;
	private Button issrt;
	private int position;
	private SeekBar seekbar;
	private upDateSeekBar update; // 更新进度条用
	private boolean flag = true; // 用于判断视频是否在播放中
	private RelativeLayout relativeLayout;
	private Button quanping;
	private Button xiaoping;
	private final static int MEDIATHREAD = 0x11;

	private String localUrl;

	private long mediaLength = 0;
	private long readSize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 应用运行时，保持屏幕高亮，不锁屏
		setContentView(R.layout.main);
		update = new upDateSeekBar(); // 创建更新进度条对象
		mediaPlayer = new MediaPlayer();
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			String localpath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/VideoCache/" + "tangbohu" + ".mp4";
			File f = new File(localpath);
            path = "http://211.144.85.251/tangbohu.mp4";
//			if (!f.exists()) {
//				path = "http://211.144.85.251/tangbohu.mp4";
//			} else {
//				path = localpath;
//			}
		}else{
			Toast.makeText(this, "SD卡不存在！", Toast.LENGTH_SHORT).show();
		}
		seekbar = (SeekBar) findViewById(R.id.seekbar);
		issrt = (Button) findViewById(R.id.issrt);
		issrt.setOnClickListener(this);
		surfaceView = (SurfaceView) findViewById(R.id.surface);
		relativeLayout = (RelativeLayout) findViewById(R.id.btm);
		quanping = (Button) findViewById(R.id.quanping);
		xiaoping = (Button) findViewById(R.id.xiaoping);
		quanping.setOnClickListener(this);
		xiaoping.setOnClickListener(this);
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
		surfaceView.getHolder().addCallback(new surfaceCallBack());
		surfaceView.setOnClickListener(this);

		seekbar.setOnSeekBarChangeListener(new surfaceSeekBar());
        mediaPlayer.setOnErrorListener(new MyOnErrorListener());
        mediaPlayer.setOnBufferingUpdateListener(this);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				issrt.setText("开始");
				pause = true;
				mediaPlayer.seekTo(0);
				seekbar.setProgress(0);
				mediaPlayer.pause();
			}
		});
	}

	private final class surfaceSeekBar implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub

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

    private final class MyOnErrorListener implements MediaPlayer.OnErrorListener{

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
            mediaPlayer.reset();
            play(position);
            return false;
        }
    }

	private final class surfaceCallBack implements Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			if (position > 0) {
				play(position);
				position = 0;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (mediaPlayer.isPlaying()) {
				position = mediaPlayer.getCurrentPosition();
				mediaPlayer.stop();
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.issrt:
			relativeLayout.setVisibility(View.GONE);
			if (iStart) {
				play(0);
				issrt.setText("暂停");
				iStart = false;
				new Thread(update).start();
			} else {
				if (mediaPlayer.isPlaying()) {
					mediaPlayer.pause();
					issrt.setText("开始");
					pause = true;
				} else {
					if (pause) {
						issrt.setText("暂停");
						mediaPlayer.start();
						pause = false;
					}
				}
			}
			break;
		case R.id.quanping:
			// 设置横屏
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			break;
		case R.id.xiaoping:
			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			break;
		case R.id.surface:
			if (relativeLayout.getVisibility() == View.VISIBLE) {
				relativeLayout.setVisibility(View.GONE);
			} else {
				relativeLayout.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	private void writeMedia() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				FileOutputStream out = null;
				InputStream is = null;

				try {
					URL url = new URL(path);
					HttpURLConnection httpConnection = (HttpURLConnection) url
							.openConnection();

					if (localUrl == null) {
						localUrl = Environment.getExternalStorageDirectory()
								.getAbsolutePath()
								+ "/VideoCache/"
								+ "tangbohu" + ".mp4";
					}

					File cacheFile = new File(localUrl);

					if (!cacheFile.exists()) {
						cacheFile.getParentFile().mkdirs();
						cacheFile.createNewFile();
					}

					readSize = cacheFile.length();
					out = new FileOutputStream(cacheFile, true);

					httpConnection.setRequestProperty("User-Agent", "NetFox");
					httpConnection.setRequestProperty("RANGE", "bytes="
							+ readSize + "-");

					is = httpConnection.getInputStream();

					mediaLength = httpConnection.getContentLength();
					if (mediaLength == -1) {
						return;
					}

					mediaLength += readSize;

					byte buf[] = new byte[4 * 1024];
					int size = 0;

					while ((size = is.read(buf)) != -1) {
						try {
							out.write(buf, 0, size);
							readSize += size;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							//
						}
					}

					if (is != null) {
						try {
							is.close();
						} catch (IOException e) {
							//
						}
					}
				}

			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
			iStart = true;
		}
		super.onDestroy();
	}

	private void play(int position) {
		mediaThread mediathread = new mediaThread();
		new Thread(mediathread).start();
	}

	private class mediaThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msgMessage = new Message();
			msgMessage.arg1 = MEDIATHREAD;
			handler.sendMessage(msgMessage);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case MEDIATHREAD:
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(path);
					mediaPlayer.setDisplay(surfaceView.getHolder());
					mediaPlayer.prepare();// 进行缓冲处理
					mediaPlayer.setOnPreparedListener(new PreparedListener(
							position));// 监听缓冲是否完成
				} catch (Exception e) {
				}
				break;
			}
		}
	};

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int bufferingProgress) {
        seekbar.setSecondaryProgress(bufferingProgress);
        int currentProgress=seekbar.getMax()*mediaPlayer.getCurrentPosition()/mediaPlayer.getDuration();

    }

	private final class PreparedListener implements OnPreparedListener {
		private int position;

		public PreparedListener(int position) {
			// TODO Auto-generated constructor stub
			this.position = position;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {

			mediaPlayer.start(); // 播放视频
			writeMedia();
			if (position > 0) {
				mediaPlayer.seekTo(position);
			}
		}


	}

	/**
	 * 更新进度条
	 */
	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mediaPlayer == null) {
				flag = false;
			} else if (mediaPlayer.isPlaying()) {
				flag = true;
				int position = mediaPlayer.getCurrentPosition();
				int mMax = mediaPlayer.getDuration();
				int sMax = seekbar.getMax();
				seekbar.setProgress(position * sMax / mMax);
			} else {
				return;
			}
		};
	};

	class upDateSeekBar implements Runnable {

		@Override
		public void run() {
			mHandler.sendMessage(Message.obtain());
			if (flag) {
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
			relativeLayout.setVisibility(View.GONE);
			quanping.setVisibility(View.GONE);
			xiaoping.setVisibility(View.VISIBLE);
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
			relativeLayout.setVisibility(View.GONE);
			xiaoping.setVisibility(View.GONE);
			quanping.setVisibility(View.VISIBLE);
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
