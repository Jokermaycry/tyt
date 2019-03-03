package com.clkj.micglmusicmixer;

import com.clkj.micglmusicmixer.hid.UsbHid;

import com.clkj.micglmusicmixer.util.Hidutil;
import com.clkj.micglmusicmixer.util.LogUtil;
import com.clkj.micglmusicmixer.util.SharedPrefsUtil;
import com.clkj.micglmusicmixer.util.SomeValues;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.graphics.PixelFormat;
import android.media.AudioManager;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Gravity;

import android.view.LayoutInflater;

import android.view.View;
import android.view.View.OnFocusChangeListener;

import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class VolumeService extends Service {
	WindowManager.LayoutParams wmTiaoyinParams;
	WindowManager mWindowTiaoyinManager;
	private static String TAG = "VolumeService";
	private Context mContext;
	private View view1,view2;
	private SeekBar mic_SeekBar, sys_SeekBar,hunxiang_SeekBar,diyin_SeekBar, gaoyin_SeekBar,yanchi_SeekBar;

	// 弹出的调音台界面
	private LinearLayout mFloatTiaoyinLayout;
	private LinearLayout micLinLayout, sysLinLayout,hunxiang_volumeLayout, diyin_volumeLayout,gaoyin_volumeLayout,yanchi_volumeLayout;
	private Button saveModelBtn;// 保存调音台效果
	private SeekBar mypro;
	private Spinner sp1,sp2,sp3;
	private Switch sw1,sw2,sw3;
	private boolean isINIT=false;
	private EffectInVisiableHandler mtimeHandler;
	// private final int MOBILE_QUERY = 1;

	// 主界面布局
	LinearLayout recordLinearLayout, cameraLinearLayout, volumeLinearLayout,
			tiaoyintaiLinearLayout, soundEffectLinearLayout;
	LinearLayout tiaoyinLayout;
	LinearLayout mSoundEffectLayout;
	LinearLayout AIcontrol;
	private Button ktvBtn, studioBtn, /*nanBtn, nvBtn, juchangBtn,*/ yanchanghuiBtn, shanguBtn, yuanshengBtn;
	private Button first,second;
	LinearLayout effectBtnLayout ;
	private UsbHid myUsbHid;
	private ControlScrollViewPager viewPager;
	private PagerAdapter viewAdapter;
	private List<View> myViewList=new ArrayList<View>();

	private int TID=0;
	public static String weatherOnView = "0";
	private AudioManager mAudioManager = null;
	// private Button yuanshengBtn, nanshengBtn, nvshengBtn;



	// private LocalSocketClient mLocalSocketClient;
	private static int sys_Progress = 0;
	private static int mic_Progress = 0;
	private static int hunxiang_progress = 0;
	private static int gaoyin_progress = 4;
	private static int yanchi_progress=8;
	private static int diyin_progress = 0;

	private static String micState = "0";
	private static String mChgVoiceState = "0";
	private static int soundEffectMode =0;
	private static int biansheng_progress = 0;
	private static int biansheng_progress_max = 20;



	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "oncreat");

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onStartCommand");
		mContext = this;
		registerUpdateBoradcastReceiver();
		initdata();
		myUsbHid = UsbHid.getInstance(mContext);
		if (myUsbHid.OpenDevice(GlmusicApplication.VendorID,
				GlmusicApplication.ProductID)) {
			 Log.d(TAG, "能检测到USB HID设备，打开调音台界面");
		     createTiaoyinView();

		}else{
			Log.d(TAG, "不能检测到USB HID设备，不打开调音台界面");
		}
			
		return super.onStartCommand(intent, flags, startId);
	}
	/**
	 * 监听ai助手发过来的数据并处理
	 * 1:人声+1
	 * 11:人声+n
	 * 2:伴唱+1
	 * 22:伴奏+n
	 * 3:K歌开
	 * 4:K歌关
	 * 5:ktv模式
	 * 6:录音棚模式
	 * 7:演唱会模式
	 * 8:山谷模式
	 * 9:原声模式
	 * 10:进入会议模式
	 * 11:退出会议模式
	 */
	private void AIlistener(Intent intent)
	{
		String action=intent.getAction();
		if(action.equals("AI_control"))
		{
			String tag=intent.getStringExtra("tag");
			switch (tag)
			{
				case "1":

					break;
				case "2":

					break;
				case "3":

					break;
				case "4":

					break;
			}
		}
	}
	private  void initdata()
	{
		//上次保存的模式
		soundEffectMode= SharedPrefsUtil.getValue(mContext,
				"whichEffect",
				GlmusicApplication.whichmode);
		//麦克风
		mic_Progress = SharedPrefsUtil.getValue(mContext,
				SharedPrefsUtil.MIC_PROGRESS,
				GlmusicApplication.mic_def_volumeProgress);
		//混响
		hunxiang_progress = SharedPrefsUtil.getValue(mContext,
				SharedPrefsUtil.HUNXIANG_PROGRESS,
				GlmusicApplication.hunxiang_def_progress);
		//高音
		gaoyin_progress = SharedPrefsUtil.getValue(mContext,
				SharedPrefsUtil.GAOYIN_PROGRESS,
				GlmusicApplication.gaoyin_def_progress);
		//低音
		diyin_progress = SharedPrefsUtil.getValue(mContext,
				SharedPrefsUtil.DIYIN_PROGRESS,
				GlmusicApplication.diyin_def_progress);
		//延迟
		yanchi_progress = SharedPrefsUtil.getValue(mContext,
				SharedPrefsUtil.YANCHI_PROGRESS,
				GlmusicApplication.yanchi_def_progress);
		LogUtil.d(TAG, "mic_Progress=" + mic_Progress);
		LogUtil.d(TAG, "hunxiang_progress=" + hunxiang_progress);
		LogUtil.d(TAG, "gaoyin_progress=" + gaoyin_progress);
		LogUtil.d(TAG, "diyin_progress=" + diyin_progress);
		LogUtil.d(TAG, "yanchi_progress=" + yanchi_progress);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private void createTiaoyinView() {
		wmTiaoyinParams = new WindowManager.LayoutParams();

		mWindowTiaoyinManager = (WindowManager) getApplication()
				.getSystemService(getApplication().WINDOW_SERVICE);
		// 设置window type
		wmTiaoyinParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmTiaoyinParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		//wmTiaoyinParams.flags = LayoutParams.FLAG_NOT_TOUCHABLE;

		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmTiaoyinParams.gravity = Gravity.CENTER;
		// 以屏幕左上角为原点，设置x、y初始值
		/*
		 * wmFunctionParams.x = 50; wmFunctionParams.y = 50;
		 */
		// 设置悬浮窗口长宽数据
		wmTiaoyinParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmTiaoyinParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatTiaoyinLayout = (LinearLayout) inflater.inflate(R.layout.volume_tiaoyintai_view, null);
		// 添加mFloatLayout
		mWindowTiaoyinManager.addView(mFloatTiaoyinLayout, wmTiaoyinParams);
		viewPager=(ControlScrollViewPager) mFloatTiaoyinLayout.findViewById(R.id.viewpager);


		//layout
		tiaoyinLayout = (LinearLayout) mFloatTiaoyinLayout.findViewById(R.id.tiaoyintaiLayout);
		effectBtnLayout = (LinearLayout) mFloatTiaoyinLayout.findViewById(R.id.tiaoyintaiBtnLayout);



		 view1=inflater.inflate(R.layout.layout_test,null);
		 view2=inflater.inflate(R.layout.layout_test2,null);

	 	sp1=(Spinner)view2.findViewById(R.id.spiner_first);
		sp2=(Spinner)view2.findViewById(R.id.spiner_second);
		sp3=(Spinner)view2.findViewById(R.id.spiner_third);

		sw1=(Switch) view2.findViewById(R.id.first_state);
		sw2=(Switch)view2.findViewById(R.id.second_state);
		sw3=(Switch)view2.findViewById(R.id.third_state);

		sp1.setOnFocusChangeListener(mOnFocusChangeListener);
		sp2.setOnFocusChangeListener(mOnFocusChangeListener);
		sp3.setOnFocusChangeListener(mOnFocusChangeListener);
		sw1.setOnFocusChangeListener(mOnFocusChangeListener);
		sw2.setOnFocusChangeListener(mOnFocusChangeListener);
		sw3.setOnFocusChangeListener(mOnFocusChangeListener);


		/********************** seekbar的背景 *****************/

		micLinLayout = (LinearLayout) view1
				.findViewById(R.id.mic_volumeLayout);
		sysLinLayout = (LinearLayout) view1
				.findViewById(R.id.sys_volumeLayout);
		hunxiang_volumeLayout = (LinearLayout) view1
				.findViewById(R.id.hunxiang_volumeLayout);
		diyin_volumeLayout = (LinearLayout) view1
				.findViewById(R.id.diyin_volumeLayout);
		gaoyin_volumeLayout= (LinearLayout) view1
				.findViewById(R.id.gaoyin_volumeLayout);
		yanchi_volumeLayout= (LinearLayout) view1
				.findViewById(R.id.yanchi_volumeLayout);

		myViewList.add(view1);
		myViewList.add(view2);
		viewPager.setAdapter(new ViewAdapter(myViewList));
		/********************** 按钮*****************/

		first=(Button)mFloatTiaoyinLayout.findViewById(R.id.first);
		second=(Button)mFloatTiaoyinLayout.findViewById(R.id.second);
		saveModelBtn=(Button)mFloatTiaoyinLayout.findViewById(R.id.saveModelBtn);

		first.setOnFocusChangeListener(mOnFocusChangeListener);
		second.setOnFocusChangeListener(mOnFocusChangeListener);
		saveModelBtn.setOnFocusChangeListener(mOnFocusChangeListener);

		first.setOnClickListener(mOnClickListener);
		second.setOnClickListener(mOnClickListener);
		saveModelBtn.setOnClickListener(mOnClickListener);
		AIcontrol=(LinearLayout)mFloatTiaoyinLayout.findViewById(R.id.aicontrol);

		ktvBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.effectKtvBtn);
		studioBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.effectStudioBtn);
		yanchanghuiBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.effectYanchanghuiBtn);
		shanguBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.effectShanguBtn);
		yuanshengBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.effectYuanshengBtn);


		ktvBtn.setOnFocusChangeListener(mOnFocusChangeListener);
		studioBtn.setOnFocusChangeListener(mOnFocusChangeListener);
		yanchanghuiBtn.setOnFocusChangeListener(mOnFocusChangeListener);
		shanguBtn.setOnFocusChangeListener(mOnFocusChangeListener);
		yuanshengBtn.setOnFocusChangeListener(mOnFocusChangeListener);


		ktvBtn.setOnClickListener(mOnClickListener);
		studioBtn.setOnClickListener(mOnClickListener);
		yanchanghuiBtn.setOnClickListener(mOnClickListener);
		shanguBtn.setOnClickListener(mOnClickListener);
		yuanshengBtn.setOnClickListener(mOnClickListener);





		/********************** seekbar *****************/

		mic_SeekBar = (SeekBar) view1
				.findViewById(R.id.mic_myView_ProgressBar);
		sys_SeekBar = (SeekBar) view1
				.findViewById(R.id.sys_myView_ProgressBar);
		hunxiang_SeekBar= (SeekBar) view1
				.findViewById(R.id.hunxiang_myView_ProgressBar);
		diyin_SeekBar= (SeekBar) view1
				.findViewById(R.id.diyin_myView_ProgressBar);
		gaoyin_SeekBar= (SeekBar) view1
				.findViewById(R.id.gaoyin_myView_ProgressBar);
		yanchi_SeekBar= (SeekBar) view1
				.findViewById(R.id.yanchi_myView_ProgressBar);






		/********************** 获取音量 *****************/

		this.mAudioManager = (AudioManager) super.getSystemService(Context.AUDIO_SERVICE);

		// 获取当前系统音量
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		Log.d(TAG, "获取音量：" + currentVolume);
		sys_Progress = currentVolume;
		GlmusicApplication.sys_progress_max=mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);//系统最大音量
		
		
		/*
		 * if (currentVolume < 2) { sys_Progress = 0; } else {
		 * sys_Progress = currentVolume - 1; }
		 */
		switch (soundEffectMode)
		{
			case 0:
				ktvBtn.setText("☑KTV");
				break;
			case 1:
				studioBtn.setText("☑录音棚");
				break;
			case 6:
				yanchanghuiBtn.setText("☑演唱会");
				break;
			case 7:
				shanguBtn.setText("☑山谷");
				break;
			case 8:
				yuanshengBtn.setText("☑原声");
				break;
			default:
				ktvBtn.setText("KTV");
				studioBtn.setText("录音棚");
				yanchanghuiBtn.setText("演唱会");
				shanguBtn.setText("山谷");
				yuanshengBtn.setText("原声");
				break;

		}


		yanchi_SeekBar.setMax(GlmusicApplication.yanchi_progress_max);
		diyin_SeekBar.setMax(GlmusicApplication.diyin_progress_max);
		gaoyin_SeekBar.setMax(GlmusicApplication.gaoyin_progress_max);
		hunxiang_SeekBar.setMax(GlmusicApplication.hunxiang_progress_max);
		mic_SeekBar.setMax(GlmusicApplication.mic_volumeProgress_max);
		sys_SeekBar.setMax(GlmusicApplication.sys_progress_max);


		sys_SeekBar.setProgress(sys_Progress);
		mic_SeekBar.setProgress(mic_Progress);
		hunxiang_SeekBar.setProgress(hunxiang_progress);
		gaoyin_SeekBar.setProgress(gaoyin_progress);
		diyin_SeekBar.setProgress(diyin_progress);
		yanchi_SeekBar.setProgress(yanchi_progress);

		/********************** 音量功能 *****************/
		/********************** 调音功能 *****************/
		mHandler.sendEmptyMessage(32);// 读取当前低音音量值
		mHandler.sendEmptyMessage(30);// 读取当前混响音量值
		mHandler.sendEmptyMessage(31);// 读取当前高音量值
		mHandler.sendEmptyMessage(16);// 读取当前mic音量值



		saveModelBtn = (Button) mFloatTiaoyinLayout.findViewById(R.id.saveModelBtn);
		saveModelBtn.setOnFocusChangeListener(mOnFocusChangeListener);


		yanchi_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		yanchi_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		diyin_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		diyin_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		gaoyin_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		gaoyin_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		hunxiang_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		hunxiang_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		sys_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		sys_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
		mic_SeekBar.setOnFocusChangeListener(mOnFocusChangeListener);
		mic_SeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);


		SomeValues.childrenWeatherOnView = "4";
		SomeValues.volumeWeatherOnView = "1";// 对话框弹出，赋值1
		mtimeHandler = new EffectInVisiableHandler();
		Message msg = mtimeHandler.obtainMessage(4);
		mtimeHandler.sendMessageDelayed(msg, GlmusicApplication.displayTime);
		Log.d(TAG, "----------------4");
	}


	View.OnClickListener mOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			switch (v.getId()) {




				case R.id.saveModelBtn:
					resetTime(4);// 计时15秒
					isINIT=!isINIT;
					if(isINIT)
					{
						saveModelBtn.setText("☑默认音效");
						mHandler.sendEmptyMessage(INITVOLUME);
					}
					else
					{
						saveModelBtn.setText("默认音效");
					}
					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));

					break;
				case R.id.effectKtvBtn:
					resetTime(4);// 计时15秒
					setEffect(0);
					isINIT=false;
					ktvBtn.setText("☑KTV");
					studioBtn.setText("录音棚");
					yanchanghuiBtn.setText("演唱会");
					shanguBtn.setText("山谷");
					yuanshengBtn.setText("原声");
					ktvBtn.setBackground(mContext.getResources().getDrawable(R.drawable.savebtn_focus_shape_0));
					SharedPrefsUtil.putValue(mContext, "whichEffect", 0);
					studioBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yanchanghuiBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					shanguBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yuanshengBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.effectStudioBtn:


					resetTime(4);// 计时15秒
					setEffect(1);
					isINIT=false;
					ktvBtn.setText("KTV");
					studioBtn.setText("☑录音棚");
					yanchanghuiBtn.setText("演唱会");
					shanguBtn.setText("山谷");
					yuanshengBtn.setText("原声");
					studioBtn.setBackground(mContext.getResources().getDrawable(R.drawable.savebtn_focus_shape_0));
					SharedPrefsUtil.putValue(mContext, "whichEffect", 1);
					ktvBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yanchanghuiBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					shanguBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yuanshengBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));

					break;


				case R.id.effectYanchanghuiBtn:

					resetTime(4);// 计时15秒
					setEffect(6);
					isINIT=false;
					ktvBtn.setText("KTV");
					studioBtn.setText("录音棚");
					yanchanghuiBtn.setText("☑演唱会");
					shanguBtn.setText("山谷");
					yuanshengBtn.setText("原声");
					yanchanghuiBtn.setBackground(mContext.getResources().getDrawable(R.drawable.savebtn_focus_shape_0));
					SharedPrefsUtil.putValue(mContext, "whichEffect", 6);
					ktvBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					studioBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					shanguBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yuanshengBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.effectShanguBtn:

					resetTime(4);// 计时15秒
					setEffect(7);
					isINIT=false;
					ktvBtn.setText("KTV");
					studioBtn.setText("录音棚");
					yanchanghuiBtn.setText("演唱会");
					shanguBtn.setText("☑山谷");
					yuanshengBtn.setText("原声");
					shanguBtn.setBackground(mContext.getResources().getDrawable(R.drawable.savebtn_focus_shape_0));
					SharedPrefsUtil.putValue(mContext, "whichEffect", 7);
					ktvBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					studioBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yanchanghuiBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yuanshengBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.effectYuanshengBtn:

					resetTime(4);// 计时15秒
					setEffect(8);
					isINIT=false;
					ktvBtn.setText("KTV");
					studioBtn.setText("录音棚");
					yanchanghuiBtn.setText("演唱会");
					shanguBtn.setText("山谷");
					yuanshengBtn.setText("☑原声");
					yuanshengBtn.setBackground(mContext.getResources().getDrawable(R.drawable.savebtn_focus_shape_0));
					SharedPrefsUtil.putValue(mContext, "whichEffect", 8);
					ktvBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					studioBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					yanchanghuiBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					shanguBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				default:
					break;
			}

		}
	};
	private void withoutmode()
	{
		ktvBtn.setText("KTV");
		studioBtn.setText("录音棚");
		yanchanghuiBtn.setText("演唱会");
		shanguBtn.setText("山谷");
		yuanshengBtn.setText("原声");
	}
	private OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			resetTime(4);// 计时15秒
			withoutmode();
			isINIT=false;
			SharedPrefsUtil.putValue(mContext, "whichEffect", 99);
			if (seekBar.getId() == R.id.sys_myView_ProgressBar) {// 系统音量
				sysLinLayout.setBackground(mContext.getResources().getDrawable(R.drawable.gaoyin_focus_shape_0));
                if (fromUser == true) {
                	LogUtil.d(TAG, "fromUser == true");
                	if (sys_Progress > progress)
                	{// 说明向左滑动

    					mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);

    				}
    				else if (sys_Progress < progress)
    				{
    					mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
    				}
    				else if (sys_Progress == progress)
    				{

    				}

                	sys_Progress = progress;

				}else
				{
					LogUtil.d(TAG, "fromUser == false");
					sys_Progress = progress;

					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sys_Progress,// 这里控制音量的直接大小
					0);

				}

				LogUtil.d(TAG, "系统音量：" + sys_Progress);

			}
			//麦克风
			else if (seekBar.getId() == R.id.mic_myView_ProgressBar) {
				Hidutil ht=new Hidutil();
				byte[] send= ht.getbytearray(0,"40",progress*2);
				byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
				SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.MIC_PROGRESS, progress);// mic音量持久化

				LogUtil.d(TAG, "mic_Progress=" + progress);


			}
			//混响
			else if (seekBar.getId() == R.id.hunxiang_myView_ProgressBar) {
				Hidutil ht=new Hidutil();
				byte[] send= ht.getbytearray(TID,"54",progress*10);
				byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
				SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.HUNXIANG_PROGRESS, progress);// 混响音量持久化
				LogUtil.d(TAG, "hunxiang_myView_ProgressBar=" + progress);
			}
			//高音
			else if (seekBar.getId() == R.id.gaoyin_myView_ProgressBar) {
				Hidutil ht=new Hidutil();
				byte[] send= ht.getbytearray(0,"6A",progress*10);
				byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
				SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.GAOYIN_PROGRESS, progress);// 混响音量持久化
				LogUtil.d(TAG, "gaoyin_myView_ProgressBar=" + progress);
			}
			//低音
			else if (seekBar.getId() == R.id.diyin_myView_ProgressBar) {
				Hidutil ht=new Hidutil();
				byte[] send= ht.getbytearray(0,"62",progress*10);
				byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
				SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.DIYIN_PROGRESS, progress);// 混响音量持久化
				LogUtil.d(TAG, "diyin_myView_ProgressBar=" + progress);
			}
			//延迟音
			else if (seekBar.getId() == R.id.yanchi_myView_ProgressBar) {
				Hidutil ht=new Hidutil();
				byte[] send= ht.getbytearray(0,"58",progress*10);
				byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
				SharedPrefsUtil.putValue(mContext, SharedPrefsUtil.YANCHI_PROGRESS, progress);// 混响音量持久化
				LogUtil.d(TAG, "yanchi_myView_ProgressBar=" + progress);
			}


		}
	};

	private static final int INITVOLUME = 0;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case INITVOLUME:
				int sys_progress_def=GlmusicApplication.sys_progress_max/2;

				sys_SeekBar.setProgress(sys_progress_def);
				mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sys_progress_def,// 这里控制音量的直接大小
						0);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mic_SeekBar
						.setProgress(GlmusicApplication.mic_def_volumeProgress);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gaoyin_SeekBar
						.setProgress(GlmusicApplication.gaoyin_def_progress);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				diyin_SeekBar
						.setProgress(GlmusicApplication.diyin_def_progress);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				hunxiang_SeekBar
						.setProgress(GlmusicApplication.hunxiang_def_progress);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				yanchi_SeekBar
						.setProgress(GlmusicApplication.yanchi_def_progress);

				break;

			case 14:

				if (mFloatTiaoyinLayout != null) {

					Log.d(TAG, "   调音台对话框准备消失");

					SomeValues.volumeWeatherOnView = "0";// 对话框关闭，赋值0
					mWindowTiaoyinManager.removeView(mFloatTiaoyinLayout);
					myViewList.remove(view1);
					myViewList.remove(view2);
					unregisterReceiver(clientBroadcastReceiver);//取消注册音量改变广播
				}
				break;

			case 2:

				sys_SeekBar.setProgress(sys_Progress);

				break;

			default:
				break;

			}

		};
	};

	/**
	 * 监听广播消息更新调音台数据广播
	 */
	public static final String SERVICE_UPDATE_ACTION = "com.clkj.action.update_volume";

	/**
	 * 监听音量改变广播
	 */
	public static final String VOLUME_UPDATE_ACTION = "android.media.VOLUME_CHANGED_ACTION";

	/**
	 * 自定义监听音量改变广播
	 */
	// public static final String VOLUME_UPDATE_ACTION =
	// "com.clkj.VOLUME_UPDATE_ACTION";

	BroadcastReceiver clientBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();
			if (action.equals(VOLUME_UPDATE_ACTION)) {

				if (SomeValues.volumeWeatherOnView.equals("1")) {

					int currentVolume = mAudioManager
							.getStreamVolume(AudioManager.STREAM_MUSIC);
					LogUtil.i(TAG, "这个是调音台对话框同步系统音量：" + currentVolume);
					sys_Progress = currentVolume;
					/*
					 * if (currentVolume < 2) { sys_Progress = 0; } else {
					 * sys_Progress = currentVolume - 1; }
					 */
					mHandler.sendEmptyMessage(2);
				} else {
					LogUtil.i(TAG, "但调音台对话框不存在");
				}

			}

		}
	};

	
	

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		LogUtil.d(TAG, "onDestroy()...");
		unregisterReceiver(clientBroadcastReceiver);

		forceTimeOut(4);
	/*	// mHandler.sendEmptyMessage(14);
		*//*
		 * if (mFloatTiaoyinLayout != null) {
		 *
		 * SomeValues.volumeWeatherOnView = "0";// 对话框弹出，赋值0
		 * mWindowManager.removeView(mFloatTiaoyinLayout); }
		 */
	}

	private void registerUpdateBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction(SERVICE_UPDATE_ACTION);
		myIntentFilter.addAction(VOLUME_UPDATE_ACTION);
		// 注册广播
		registerReceiver(clientBroadcastReceiver, myIntentFilter);
	}

	private class EffectInVisiableHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "msg.what=" + msg.what);
			switch (msg.what) {

			case 4:
				Log.i(TAG, "关闭**********************");
				mHandler.sendEmptyMessage(14); // 当若干秒到达后，隐藏。
				Log.i(TAG, "关闭 调音台调节页面**********************");
				break;

			default:

				break;
			}

		}
	}

	/**
	 * 开始计时
	 */
	public void resetTime(int mobile_query) {
		mtimeHandler.removeMessages(mobile_query);
		Message msg = mtimeHandler.obtainMessage(mobile_query);
		mtimeHandler.sendMessageDelayed(msg, GlmusicApplication.displayTime);
	}

	/**
	 * 强制时间到
	 */
	public void forceTimeOut(int mobile_query) {

		Log.d(TAG, "forceTimeOut mobile_query=" + mobile_query);
		if (mtimeHandler == null) {
			Log.d(TAG, "forceTimeOut mtimeHandler==null,new yi ge");
			mtimeHandler = new EffectInVisiableHandler();
			Message msg = mtimeHandler.obtainMessage(mobile_query);
			mtimeHandler.sendMessageDelayed(msg, 10);
		} else {
			mtimeHandler.removeMessages(mobile_query);
			Message msg = mtimeHandler.obtainMessage(mobile_query);
			mtimeHandler.sendMessageDelayed(msg, 10);
		}

	}

	OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@SuppressLint("NewApi")
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
				case R.id.spiner_first:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.spiner_second:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.spiner_third:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.first_state:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.second_state:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
				case R.id.third_state:
					resetTime(4);// 计时3秒

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					break;
			case R.id.mic_myView_ProgressBar:// mic音量进度

					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					second.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
					saveModelBtn.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
					resetTime(4);// 计时3秒

					micLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_0));
					sysLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					hunxiang_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					gaoyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					diyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					yanchi_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));

				break;

			case R.id.sys_myView_ProgressBar:// 系统音量进度
				first.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_0));
				second.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
				saveModelBtn.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
				resetTime(4);// 计时3秒

				micLinLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				sysLinLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_0));
				hunxiang_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				gaoyin_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				diyin_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				yanchi_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));

				break;

			case R.id.hunxiang_myView_ProgressBar:// 混响
				first.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_0));
				second.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
				saveModelBtn.setBackground(mContext.getResources()
						.getDrawable(R.drawable.savebtn_focus_shape_1));
				resetTime(4);// 计时3秒

				micLinLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				sysLinLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				hunxiang_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_0));
				gaoyin_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				diyin_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));
				yanchi_volumeLayout.setBackground(mContext.getResources()
						.getDrawable(R.drawable.gaoyin_focus_shape_1));

				break;
				case R.id.gaoyin_myView_ProgressBar:// 高音
					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					resetTime(4);// 计时3秒

					micLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					sysLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					hunxiang_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					gaoyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_0));
					diyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					yanchi_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					break;
				case R.id.diyin_myView_ProgressBar:// 低音
					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					resetTime(4);// 计时3秒

					micLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					sysLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					hunxiang_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					gaoyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					diyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_0));
					yanchi_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					break;
				case R.id.yanchi_myView_ProgressBar:// 延迟
					first.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_0));
					second.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					saveModelBtn.setBackground(mContext.getResources()
							.getDrawable(R.drawable.savebtn_focus_shape_1));
					resetTime(4);// 计时3秒

					micLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					sysLinLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					hunxiang_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					gaoyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					diyin_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_1));
					yanchi_volumeLayout.setBackground(mContext.getResources()
							.getDrawable(R.drawable.gaoyin_focus_shape_0));

					break;
				case R.id.first:
					if (hasFocus) {
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						resetTime(4);// 计时15秒
						viewPager.setCurrentItem(0);


						micLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						sysLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						hunxiang_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						gaoyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						diyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						yanchi_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));

					}
					break;
				case R.id.second:
					if (hasFocus) {
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						resetTime(4);// 计时15秒
						viewPager.setCurrentItem(1);

						micLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						sysLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						hunxiang_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						gaoyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						diyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						yanchi_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));

					}
					break;
				case R.id.saveModelBtn:
					if (hasFocus) {
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						resetTime(4);// 计时15秒
						viewPager.setCurrentItem(0);


						micLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						sysLinLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						hunxiang_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						gaoyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						diyin_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
						yanchi_volumeLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
					}
					break;

				case R.id.effectKtvBtn:
					if (hasFocus) {
						resetTime(4);// 计时15秒
						//setEffect(0);
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						ktvBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_0));
						//micDisplayText.setFocusable(false);
						//sysDisplayText.setFocusable(false);

						//SharedPreferenceUtil.putValue(mContext, "whichEffect", 0);

						studioBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));

						yanchanghuiBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						shanguBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						yuanshengBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));

					} else {
						ktvBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
					}
					break;
				case R.id.effectStudioBtn:
					if (hasFocus) {
						resetTime(4);// 计时15秒
						//setEffect(1);
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						studioBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_0));

						//micDisplayText.setFocusable(false);
						//sysDisplayText.setFocusable(false);
						//SharedPreferenceUtil.putValue(mContext, "whichEffect", 1);
					} else {
						studioBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));

					}
					break;


				case R.id.effectYanchanghuiBtn:
					if (hasFocus) {
						resetTime(4);// 计时15秒
						//setEffect(6);
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						yanchanghuiBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_0));
						//micDisplayText.setFocusable(false);
						//sysDisplayText.setFocusable(false);
						//SharedPreferenceUtil.putValue(mContext, "whichEffect", 5);
					} else {
						yanchanghuiBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
					}
					break;
				case R.id.effectShanguBtn:
					if (hasFocus) {
						resetTime(4);// 计时15秒
						//setEffect(7);
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						shanguBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_0));
						//micDisplayText.setFocusable(false);
						//sysDisplayText.setFocusable(false);
						//SharedPreferenceUtil.putValue(mContext, "whichEffect", 7);
					} else {
						shanguBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
					}
					break;
				case R.id.effectYuanshengBtn:
					if (hasFocus) {
						first.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						second.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						saveModelBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						resetTime(4);// 计时15秒
						//setEffect(8);
						yuanshengBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_0));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_0));
						//micDisplayText.setFocusable(false);
						//sysDisplayText.setFocusable(false);
						//SharedPreferenceUtil.putValue(mContext, "whichEffect", 8);
					} else {
						yuanshengBtn.setBackground(mContext.getResources()
								.getDrawable(R.drawable.savebtn_focus_shape_1));
						effectBtnLayout.setBackground(mContext.getResources()
								.getDrawable(R.drawable.gaoyin_focus_shape_1));
					}
					break;



			default:
				break;

			}

		}
	};

	/**
	 * 接收Mixer_Server消息，并处理
	 */
	String receiveData;
	private Handler clientHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.arg1 == 0x12) {

				receiveData = (String) msg.obj;
				LogUtil.d(TAG, "从服务获取的信息：" + receiveData);

			}

			return false;
		}
	});

	/**
	 * 发送数据到HID设备
	 */
	private void sendDataToHidDevice(int progress) {

		String progressData = Integer.toHexString(progress);

	}
	/**
	 * 设置模式
	 0x00:   切换至KTV音效模式
	 0x01:   切换至录音棚音效模式
	 0x05:   切换至酒吧音效模式
	 0x06:   切换至演唱会音效模式
	 0x07:   切换至山谷音效模式
	 0x08:   切换至原声音效模式
	 */
	private  void setEffect(int i)
	{
		Hidutil ht=new Hidutil();
		byte[] send= ht.getbytearray(0,"20",i);
		byte[] recv = myUsbHid.UsbHidSendCommand(send, send.length);
	}
}
