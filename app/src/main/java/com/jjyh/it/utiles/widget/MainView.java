package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;

import com.baidu.duer.dcs.duerossdk.devicemodule.screen.message.RenderCardPayload;
import com.ivvi.moassistant.IModelsManager;
import com.ivvi.moassistant.MoApplication;
import com.ivvi.moassistant.R;
import com.ivvi.moassistant.action.DownloadAction;
import com.ivvi.moassistant.activity.MainActivity;
import com.ivvi.moassistant.adapter.ChatAdapter;
import com.ivvi.moassistant.calendar.MoCalendarBean;
import com.ivvi.moassistant.calendar.MoCalendarGlobal;
import com.ivvi.moassistant.calendar.MoCalendarManager;
import com.ivvi.moassistant.contactmodule.ContactMessage;
import com.ivvi.moassistant.global.Constant;
import com.ivvi.moassistant.global.Constants;
import com.ivvi.moassistant.helper.overscroll2.IOverScrollDecor;
import com.ivvi.moassistant.helper.overscroll2.IOverScrollStateListener;
import com.ivvi.moassistant.helper.overscroll2.IOverScrollUpdateListener;
import com.ivvi.moassistant.helper.overscroll2.VerticalOverScrollBounceEffectDecorator;
import com.ivvi.moassistant.helper.overscroll2.adapters.RecyclerViewOverScrollDecorAdapter;
import com.ivvi.moassistant.input.InputSpeechView;
import com.ivvi.moassistant.model.ChatMessage;
import com.ivvi.moassistant.model.MainEvent;
import com.ivvi.moassistant.model.reqtype.BaseReqType;
import com.ivvi.moassistant.model.reqtype.UpgradeReqType;
import com.ivvi.moassistant.music.MoMusicBean;
import com.ivvi.moassistant.music.MoMusicGlobal;
import com.ivvi.moassistant.music.MoMusicManager;
import com.ivvi.moassistant.network.NetReqCodeList;
import com.ivvi.moassistant.network.httpinterface.IProtocol;
import com.ivvi.moassistant.service.FloatWindowService;
import com.ivvi.moassistant.tts.TtsImpl;
import com.ivvi.moassistant.utils.CommonUtil;
import com.ivvi.moassistant.utils.LogUtils;
import com.ivvi.moassistant.xtime.AlarmTextToTimer;
import com.ivvi.moassistant.xtime.MoClockBean;
import com.ivvi.moassistant.xtime.MoClockGlobal;
import com.zhy.base.fileprovider.FileProvider7;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;

import static com.ivvi.moassistant.global.Constant.RECEIVE_UPGRADE_CARD;
import static com.ivvi.moassistant.utils.UiUtils.getString;

/**
 * Created by zhangjunjie on 2017/9/22.
 */

public class MainView extends LinearLayout implements View.OnClickListener,IProtocol.Callback<BaseReqType> ,IProtocol.FileCallback {
    private static final String TAG = "MainView";

    //刷新view Event 定义
    private static final int EVENT_MAINVIEW_START = 120;
    public static final int LOAD_FLOAT_SINGLE_DOWNLOADAPP = EVENT_MAINVIEW_START + 1;//加载单个动态添加的APP下载浮层视图
    public static final int FLOAT_LAYER_EXPAND = LOAD_FLOAT_SINGLE_DOWNLOADAPP + 1;//浮层视图展开
    public static final int FLOAT_LAYER_FOLD = FLOAT_LAYER_EXPAND + 1;//浮层视图收起
    public static final int CHAT_LIST_SMOOTH_BOTTOM = FLOAT_LAYER_FOLD + 1;//对话列表滚动
    private static final int EVENT_MAINVIEW_END = 199;

    private RecyclerView mChatRecyclerView;
    private LinearLayout moFloatView;
    private FloatRecyclerView mFloatRecyclerView;
    private Button mFloatFooterBtn;

    private DynaminFloat mDynaminFloat;

    private ChatAdapter mChatAdapter;
    private List<ChatMessage> chatMessages;

    private ChatMessage mSpeakMsg = null;
    private List<ChatMessage> mFloatMessages;
    private ChatAdapter mFloatAdapter;

    private IModelsManager mModelsManager;
    private String mIntroduction;
    private String mUpgradeUrl;
    private String mNewVersion;
    private long mFileSize;
    private String mMd5;

    /**
     * 文字语音输入变量定义
     */
    private InputSpeechView messageInputView;
    private Context mContext;
    private MyHandler mHandler;
    private String mCachePath;
    private String mFileName;
    private File mApkFile;

    public MainView(Context context) {
        this(context, null);
        mContext = context;
    }
    public MainView(Context context,AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }
    public MainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private void initView() {
        mChatRecyclerView = (RecyclerView) findViewById(R.id.mo_chat_list_view);

        Button sideMenuBtn = (Button) findViewById(R.id.sideMenuBtn);
        if(sideMenuBtn != null) {
            sideMenuBtn.setOnClickListener(this);
            sideMenuBtn.setVisibility(View.GONE);
        }
        moFloatView = (LinearLayout) findViewById(R.id.mo_float_layer_main);

        mFloatRecyclerView = (FloatRecyclerView) findViewById(R.id.mo_float_layer_rcview);
        mFloatFooterBtn = (Button) findViewById(R.id.mo_float_layer_footer);
        mFloatFooterBtn.setVisibility(View.GONE);
        mFloatFooterBtn.setOnClickListener(this);
        mDynaminFloat = (DynaminFloat) findViewById(R.id.mo_float_layer_single);
        /**
         * 语音文字输入
         */
        messageInputView = (InputSpeechView)findViewById(R.id.message_input_view);

        //获取此会话的所有消息
        chatMessages = new ArrayList<ChatMessage>();
        //MoDBHelper.getInstance(mContext).updateChatRecord(chatMessages);
        mChatAdapter = new ChatAdapter(chatMessages, (MainActivity) mContext);
        initChatRecyclerView();
        mChatAdapter.notifyDataSetChanged();

        initFloatList();
        initMessageInputView();
        mHandler = new MyHandler(this);
        mModelsManager = MoApplication.getInstance().getModelsManager();
        final String version = CommonUtil.getVersionCode();
        Log.d(TAG,"jjyh update= "+version);
        mModelsManager.getNetworkManger().upgrade(version,this , NetReqCodeList.sUpgradeUpdate);
    }

    private void initFloatList() {
        mFloatMessages = new ArrayList<ChatMessage>();
        mFloatAdapter = new ChatAdapter(mFloatMessages, (MainActivity) mContext);
        mFloatRecyclerView.setAdapter(mFloatAdapter);
        mFloatRecyclerView.smoothScrollToPosition(0);
    }

    public boolean isDownloading(String pkg) {
        for (ChatMessage mess : mFloatMessages) {
            if (mess.getType() == Constant.RECEIVE_DOWNLOADAPP_CARD) {
                if (mess.getBody().toString().split(DownloadAction.APP_SPLIT)[0].equals(pkg))
                    return true;
            }
        }
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initChatRecyclerView() {
        //mChatRecyclerView.setLayoutManager(new OverScrollLinearLayoutManager(mChatRecyclerView));
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        // Set-up of recycler-view's native item swiping.
        ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return 0;//makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                Log.i("jay","WebViewHolder onSwiped:"+direction);
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return super.isItemViewSwipeEnabled();
            }
        };

        // Apply over-scroll in 'advanced form' - i.e. create an instance manually.
        VerticalOverScrollBounceEffectDecorator mVertOverScrollEffect = new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(mChatRecyclerView, itemTouchHelperCallback));

        // Over-scroll listeners are applied here via the mVertOverScrollEffect explicitly.
        mVertOverScrollEffect.setOverScrollUpdateListener(new IOverScrollUpdateListener() {
            @Override
            public void onOverScrollUpdate(IOverScrollDecor decor, int state, float offset) {
            }
        });
        mVertOverScrollEffect.setOverScrollStateListener(new IOverScrollStateListener() {
            @Override
            public void onOverScrollStateChange(IOverScrollDecor decor, int oldState, int newState) {
            }
        });

        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.smoothScrollToPosition(0);
    }

    private void smoothChatListToLastRecord() {
        if (mChatRecyclerView != null && mChatRecyclerView.canScrollVertically(1)) {
            mChatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }
    }

    private void initMessageInputView() {
        InputSpeechView.MessageInputClickListener messageInputClickListener = new InputSpeechView.MessageInputClickListener() {
            @Override
            public void letterSendListener(String letter) {
                //TODO: 2017/8/11 文字输入回调
                addOneChat(new ChatMessage(ChatMessage.SEND, letter, Constant.SEND_TXT), false);
                Bundle bundle = new Bundle();
                bundle.putString("value",letter);
                EventBus.getDefault().post(new MainEvent(Constant.EVENT_SDK_TEXT_INPUT,bundle));

                ((MainActivity)mContext).parseIntention(letter);
            }

            @Override
            public void speechNotActive() {
                // TODO: 2017/8/11 语音未激活状态回调
                EventBus.getDefault().post(new MainEvent(Constant.SET_SPEECH_NOT_ACTIVE));
            }

            @Override
            public void speechActive() {
                // TODO: 2017/8/11 语音激活状态回调
                LogUtils.i(TAG, "speechActive call moStartRecord");
                EventBus.getDefault().post(new MainEvent(Constant.SET_SPEECH_ACTIVE));
            }
            @Override
            public void speechInputing() {
                // TODO: 2017/8/11 语音正在输入状态回调

            }
            @Override
            public void letterInputing() {
                //// TODO: 2017/8/17 文字输入时，让列表的最后一条数据可见
            }
        };

        InputSpeechView.KeyBoardHeightListener keyBoardHeightListener = new InputSpeechView.KeyBoardHeightListener() {
            @Override
            public void keyBoardHeight() {
                // TODO: 2017/8/11  键盘高度回调
                if (mChatRecyclerView != null) {
                    mChatRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
                }
            }
        };
        messageInputView.setMessageInputClickListener(messageInputClickListener);
        messageInputView.setKeyBoardHeightListener(keyBoardHeightListener);

    }

    public void volumeChanged(int volume) {
        messageInputView.volumeChanged(volume);
    }
    public void finishRecordUI() {
        messageInputView.finishRecordUI();
        mSpeakMsg = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sideMenuBtn:
                if (moFloatView.getVisibility() == View.VISIBLE || mDynaminFloat.getVisibility() == View.VISIBLE) {
                    EventBus.getDefault().post(new MainEvent(FLOAT_LAYER_FOLD));
                } else {
                    EventBus.getDefault().post(new MainEvent(FLOAT_LAYER_EXPAND));
                }
                break;
            case R.id.mo_float_layer_footer:
                if (moFloatView.getVisibility() == View.VISIBLE || mDynaminFloat.getVisibility() == View.VISIBLE) {
                    EventBus.getDefault().post(new MainEvent(FLOAT_LAYER_FOLD));
                    //mHandler.sendEmptyMessage(Constant.FLOAT_LAYER_FOLD);
                    setSideMenuBtnStyle(false);
                }
                break;
            default:
                break;
        }
    }

    public void onEventMainThread(MainEvent event) {
        Log.d(TAG,"onEventMainThread收到了消息：" + event.getType());
        switch (event.getType()) {
            case CHAT_LIST_SMOOTH_BOTTOM:
                //smoothChatListToLastRecord();
                mHandler.sendEmptyMessageDelayed(0,100);
                break;
            case MoClockGlobal.EVENT_CLOCK_SINGLE_FLOAT:
                Bundle bundle = event.getBundle();
                String clockInfo = bundle.getString(MoClockGlobal.CLOCK_PARAM_CLOCK_INFO);
                if (TextUtils.isEmpty(clockInfo)) {
                    addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
                    break;
                }

                if (mDynaminFloat != null)
                    mDynaminFloat.loadAlarmBar(clockInfo);
                break;
            case MoClockGlobal.EVENT_CLOCK_REMOVE_FLOAT_ITEM:
                long clock_id = event.getBundle().getLong(MoClockGlobal.CLOCK_PARAM_CLOCK_ID);
                removeClockFloat(clock_id);
                break;
            case MoClockGlobal.EVENT_CLOCK_UPDATE_FLOAT_ITEM:
                updateClockFloat(event.getBundle());
                break;
            case MoClockGlobal.EVENT_CLOCK_ALARM_ALERT:
                ((MainActivity)mContext).stopDcsSpeaker();
                break;
            case MoClockGlobal.EVENT_CLOCK_NOTIFICATION_SWITCHED:
                Bundle notification_bundle = event.getBundle();
                if (null == notification_bundle) {
                    break;
                }
                int notification_id = notification_bundle.getInt(MoClockGlobal.CLOCK_PARAM_CLOCK_ID);
                boolean bNotification = notification_bundle.getBoolean(MoClockGlobal.CLOCK_PARAM_CLOCK_NOTIFICATION);
                updateClockNotificationStatus(notification_id, bNotification);
                break;
            case MoCalendarGlobal.EVENT_CALENDAR_SINGLE_FLOAT:
                Bundle CalBundle = event.getBundle();
                final MoCalendarBean calendar = CalBundle.getParcelable(MoCalendarGlobal.CALENDAR_PARAM_BEAN);
                if (null == calendar) {
                    addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
                    break;
                }

                if (mDynaminFloat != null)
                    mDynaminFloat.loadCalendarBar(calendar);
                break;
            case MoCalendarGlobal.EVENT_CALENDAR_REMOVE_FLOAT_ITEM:
                long remove_calendar_id = event.getBundle().getLong(MoCalendarGlobal.CALENDAR_PARAM_ID);
                removeCalendarFloat(remove_calendar_id);
                break;
            case MoCalendarGlobal.EVENT_CALENDAR_UPDATE_FLOAT_ITEM:
                long update_calendar_id = event.getBundle().getLong(MoCalendarGlobal.CALENDAR_PARAM_ID);
                updateCalendarFloat(update_calendar_id);
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_LOAD_SINGLE_FLOAT: {
                Bundle musicBundle = event.getBundle();
                final String musicMsg = musicBundle.getString(MoMusicGlobal.MO_MUSIC_PARAM_DISPLAY);
                if ( TextUtils.isEmpty(musicMsg) ) {
                    addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
                    break;
                }

                if (mDynaminFloat != null) {
                    mDynaminFloat.loadMusicBar(musicMsg);
                    setSideMenuBtnStyle(true);
                }
            }
                break;
            case LOAD_FLOAT_SINGLE_DOWNLOADAPP:
                Bundle appDownloadBundle = event.getBundle();
                preDownloadApp(appDownloadBundle);
                break;
            case Constant.SET_INPUT_NOT_ACTIVE:
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
                break;
            case FLOAT_LAYER_EXPAND:
                handleFloatView(true);
                setSideMenuBtnStyle(true);
                break;
            case FLOAT_LAYER_FOLD:
                handleFloatView(false);
                setSideMenuBtnStyle(false);
                break;
            case Constant.SET_HELP_ALARM:
                MoClockBean clockBean = new MoClockBean(0, "小魔闹钟", 8, 0, "true", 0);
                int clockId = ((MainActivity)mContext).getMoClockManager().insertClock(clockBean);
                if ( 0 != clockId ) {
                    clockBean.setClockID(clockId);
                    insertOneAlarmChat(clockBean);
                    setSideMenuBtnStyle(true);
                }
                break;
            case Constant.SET_HELP_CALENDAR:
                String CalendarInfo = getResources().getString(R.string.calendar_help_item).replace("”", "");
                CalendarInfo = CalendarInfo.replace("“", "");
                MoCalendarBean calendarBean = AlarmTextToTimer.getCalendarFromString(CalendarInfo);
                if (null == calendarBean) {
                    break;
                }

                int retID = ((MainActivity)mContext).getMoCalendarManager().insertCalendar(calendarBean);
                if ( 0 == retID ) {
                    break;
                }

                calendarBean.setId(retID);
                insertOneCalendarChat(calendarBean);
                setSideMenuBtnStyle(true);
                break;
            case Constant.SET_HELP_MUSIC: {
                MoMusicManager musicMgr = ((MainActivity) mContext).getMoMusicManager();
                musicMgr.setMusicName("最炫民族风");
                musicMgr.bindMyService();
            }
                break;
            case Constant.RECEIVE_UPGRADE_CARD:
                addUpdateChat(Constant.RECEIVE_UPGRADE_CARD,false);
                break;
            case Constant.RECEIVE_DOWNLOAD_CARD:
                Log.d(TAG,"jjyh DOWNLOAD=");
                mCachePath = mContext.getExternalCacheDir().getAbsolutePath() + "/ApkCache";
                mFileName = getString(R.string.app_name) + ".apk";
                File file = new File(mCachePath, mFileName);
                if (file != null && mMd5.equals(CommonUtil.getFileMD5(file))) {
                    apkInstall(file);
                    addUpdateChat(Constant.RECEIVE_DOWNLOAD_CARD);
                } else {
                    Log.d(TAG,"jjyh addUpdateChat");
                    addUpdateChat(Constant.RECEIVE_DOWNLOAD_CARD,true);
                    mModelsManager.getNetworkManger().download(file, mUpgradeUrl, this, NetReqCodeList.sUpgradeDownload);
                }
                break;
            case Constant.RECEIVE_INSTALL_CARD:
                Log.d(TAG,"jjyh onFail="+mApkFile);
                addUpdateChat(Constant.RECEIVE_INSTALL_CARD,false);
                apkInstall(mApkFile);
                break;
            case DownloadAction.APP_SEARCH_ERROR:
                Bundle searchInfo = event.getBundle();
                addSearchErrorChat(searchInfo);
                break;
            case Constant.SHOW_ROOT_LAYOUT:
                this.setVisibility(View.VISIBLE);
                playAnimate(this, R.anim.fade_in);
                if (((MainActivity)mContext).getPlayMusicHelper() != null)
                    ((MainActivity)mContext).getPlayMusicHelper().stopMusic();
                break;
            case DownloadAction.INT_APPDOWNLOADSTSTUS_REFRESH:
                Bundle info = event.getBundle();
                refreshDownloadAppInfo(info);
                break;
            case DownloadAction.UPDATE_APPDOWNLOADUI:
                mFloatAdapter.notifyDataSetChanged();
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_INIT_PLAYER: {
                Bundle musicBundle = event.getBundle();
                MoMusicBean musicBean = musicBundle.getParcelable(MoMusicGlobal.MO_MUSIC_PARAM_DISPLAY);
                if (null == musicBean) {
                    addOneChat(new ChatMessage(ChatMessage.RECEIVE, getResources().getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
                    break;
                }

                insertOneMusicChat(musicBean);
            }
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_SONG_CHANGE:
                switchMusic(event.getBundle());
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_STATUS_UPDATE:
                refreshMusicStatus(event.getBundle());
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_ELAPSED_UPDATE:
                refreshMusicElapsed(event.getBundle());
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_ERROR_INFO:
                Bundle errorBundle = event.getBundle();
                int errorCode = errorBundle.getInt(MoMusicGlobal.MO_MUSIC_PARAM_ERROR_CODE);
                if (MoMusicGlobal.MO_MUSIC_ERROR_SEARCH_NONE == errorCode) {
                    addMsgWithTTS(getResources().getString(R.string.music_error_msg_search_none));
                }
                break;
            case MoMusicGlobal.MO_MUSIC_CONTROL_UPDATE_TOP_LIST:
                if (null != ((MainActivity)mContext).getMoMusicManager()) {
                    ((MainActivity)mContext).getMoMusicManager().getTopMusicList();
                }
                break;
            case Constant.EVENT_SYNC_LAST_ITEM_TO_SINGLE_FLOAT:
                LastMultiFloatMsgToSingleFloat(event.getBundle());
                break;
            case Constants.MO_GEEK_RESIDENT_SHOP_MAIN:
                if (FloatWindowService.sGeekShopType==1) {
                    messageInputView.messageBtn.setImageResource(R.drawable.more_on);
                }else {
                    messageInputView.messageBtn.setImageResource(R.drawable.more_off);
                }
                break;
            case DownloadAction.APP_DOWNLOAD_LIST:
                Bundle appListBundle = event.getBundle();
                String appList = getResources().getString(R.string.app_download_search1)
                        + appListBundle.getString(DownloadAction.MARK_DOWNLOADBROADAPPLIST)
                        + getResources().getString(R.string.app_download_search2);
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, appList, Constant.RECEIVE_TXT), false);
                break;
            case DownloadAction.APP_DOWNLOAD_NO_RESULT:
                Bundle resultBundle = event.getBundle();
                addOneChat(new ChatMessage(ChatMessage.RECEIVE,resultBundle.getString(DownloadAction.MARK_SEARCHRESULT) , Constant.RECEIVE_TXT), false);
                break;
            default:
                break;
        }
    }

    private void apkInstall(File apkfile) {
        Log.d(TAG,"jjyh apkfile="+apkfile);
        if (!apkfile.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        FileProvider7.setIntentDataAndType(mContext, intent, "application/vnd.android.package-archive", apkfile, true);
        mContext.startActivity(intent);
    }

    private void refreshDownloadAppInfo(Bundle info) {
        String action = info.getString("action");
        String pkg = info.getString("pkg");
        String speed = info.getString("speed");
        int progress = info.getInt("progress");
        mDynaminFloat.refreshDynaminFloatOfDownloadApp(action,pkg,speed,progress);
        mFloatAdapter.updateDownloadProgress(action,pkg,speed,progress);
    }

    private void addSearchErrorChat(Bundle searchInfo) {
        final String error = searchInfo.getString(DownloadAction.MARK_ERROR).toString();
        if (error.contains("permission")){
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_error_msg), Constant.RECEIVE_TXT), false);
        } else {
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, mContext.getString(R.string.voice_app_seacherror), Constant.RECEIVE_TXT), false);
        }
    }

    private void preDownloadApp(Bundle appDownloadBundle) {
        final String appID = appDownloadBundle.get(DownloadAction.MARK_APPPACKAGENAME).toString();
        final String appName = appDownloadBundle.get(DownloadAction.MARK_APPNAME).toString();
        final String iconUrl = appDownloadBundle.get(DownloadAction.MARK_ICONURL).toString();
        final String appSize = appDownloadBundle.get(DownloadAction.MARK_APPSIZE).toString();
        if (mDynaminFloat != null) {
            mDynaminFloat.loadAppDownloadBar(appID, appName, iconUrl, appSize, true);
        }
        setSideMenuBtnStyle(true);
        addOneChat(new ChatMessage(ChatMessage.RECEIVE, appID + DownloadAction.APP_SPLIT +
                appName + DownloadAction.APP_SPLIT + iconUrl + DownloadAction.APP_SPLIT
                + appSize + DownloadAction.APP_SPLIT + "0k/s"
                + DownloadAction.APP_SPLIT + "0%",  Constant.RECEIVE_DOWNLOADAPP_CARD), true);
    }

    /**
     * expand:true 处理展开false，处理收起
     */
    private void handleFloatView(boolean expand) {
        if (expand) {
            if (mFloatMessages.size() == 1) {
                if (mDynaminFloat.getVisibility() != View.VISIBLE) {
                    mDynaminFloat.setVisibility(View.VISIBLE);
                    playAnimate(mDynaminFloat, R.anim.single_float_out);
                }
            } else {
                if (mChatRecyclerView.getVisibility() == View.VISIBLE) {
                    mChatRecyclerView.setVisibility(View.INVISIBLE);
                    playAnimate(mChatRecyclerView, R.anim.fade_out);
                }
                if (View.VISIBLE != moFloatView.getVisibility()) {
                    moFloatView.setVisibility(View.VISIBLE);
                    playAnimate(moFloatView, R.anim.scale_to_big);
                    moFloatView.setFocusable(true);
                    moFloatView.setFocusableInTouchMode(true);
                    moFloatView.requestFocus();
                }
            }
        } else {
            //单一浮层
            if (mDynaminFloat.getVisibility() == View.VISIBLE) {
                mDynaminFloat.playAnimation(false);
                mDynaminFloat.setVisibility(View.GONE);
                playAnimate(mDynaminFloat, R.anim.single_float_in);
            }

            //列表浮层
            if (moFloatView.getVisibility() == View.VISIBLE) {
                moFloatView.setVisibility(View.GONE);
                playAnimate(moFloatView, R.anim.scale_to_small);
            }
            //对话窗口
            if (mChatRecyclerView.getVisibility() != View.VISIBLE) {
                mChatRecyclerView.setVisibility(View.VISIBLE);
                playAnimate(mChatRecyclerView, R.anim.fade_in);
            }
        }
    }

    public void playAnimate(View v, int animateid) {
        Animation anim = AnimationUtils.loadAnimation(mContext, animateid);
        //anim = AnimationUtils.loadAnimation(this, R.anim.scale_to_big);
        //anim = AnimationUtils.loadAnimation(this,R.anim.scale_to_small);
        v.startAnimation(anim);
    }

    //isExpand:true 展开,显示收起 false，关闭显示展开
    public void setSideMenuBtnStyle(boolean isExpand) {
        Button btn = (Button) this.findViewById(R.id.sideMenuBtn);
        if (isExpand) btn.setBackgroundResource(R.drawable.mo_sidemenu_fold);
        else btn.setBackgroundResource(R.drawable.mo_sidemenu_expand);
        if (btn.getVisibility() != View.VISIBLE)
            btn.setVisibility(View.VISIBLE);
    }

    public void addOneCardView(RenderCardPayload cardPayload) {
        switch(cardPayload.type) {
            case TextCard:
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, cardPayload.content, Constant.RECEIVE_TXT), false);
                break;
            case StandardCard:
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, cardPayload, Constant.RECEIVE_STANDARD_CARD), false);
                break;
            case ListCard:
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, cardPayload, Constant.RECEIVE_LIST_CARD), false);
                break;
            case ImageListCard:
                addOneChat(new ChatMessage(ChatMessage.RECEIVE, cardPayload, Constant.RECEIVE_IMAGELIST_CARD), false);
                break;
            default:break;
        }
    }
    /**
     * direct:是send 还是 receive
     */
    //增加聊天信息demo
    public void addOneChat(ChatMessage msg, boolean isFloat) {
        if (isFloat) {
            mFloatAdapter.addData(msg);
        } else {
            mChatAdapter.addData(msg);
            //if (msg.getType() == Constant.RECEIVE_TXT || msg.getType() == Constant.SEND_TXT)
                //MoDBHelper.getInstance(mContext).insertChatRecord(msg.getBody().toString(),msg.getType());
            mHandler.sendEmptyMessageDelayed(0,100);
        }
    }
    public void addContactMessage(List<? extends Object> messages) {
        if (mChatAdapter != null) {
            ChatMessage chatMessage = new ContactMessage(ChatMessage.RECEIVE, null, Constant.RECEIVE_CONTACT);
            ((ContactMessage) chatMessage).setContactBody(messages);
            addOneChat(chatMessage, false);
        }
    }

    public void insertOneAlarmChat(MoClockBean bean) {
        if (null == bean) {
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, getResources().getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
            return;//错误
        }

        String clockInfo = bean.toString();
        Bundle bundle = new Bundle();
        bundle.putString(MoClockGlobal.CLOCK_PARAM_CLOCK_INFO, clockInfo);
        EventBus.getDefault().post(new MainEvent(MoClockGlobal.EVENT_CLOCK_SINGLE_FLOAT, bundle));

        addOneChat(new ChatMessage(ChatMessage.RECEIVE, clockInfo, Constant.RECEIVE_ALARM_CARD), true);
    }

    public void insertOneCalendarChat(MoCalendarBean calendarBean) {
        if ( null == calendarBean ) {
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, getResources().getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(MoCalendarGlobal.CALENDAR_PARAM_BEAN, calendarBean);
        EventBus.getDefault().post(new MainEvent(MoCalendarGlobal.EVENT_CALENDAR_SINGLE_FLOAT, bundle));

        String desc = MoCalendarManager.getDescOfTimer(calendarBean.getDateStartLong());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(calendarBean.getDateStartLong());
        String title = calendarBean.getTitle();
        if ( TextUtils.isEmpty(title) ) {
            title = getResources().getString(R.string.calendar_default_title);
        }

        addOneChat(new ChatMessage(ChatMessage.RECEIVE, calendarBean.toString(), Constant.RECEIVE_CALENDAR_CARD), true);
    }

    public void insertOneMusicChat(MoMusicBean bean) {
        if (null == bean) {
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, getResources().getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
            return;//错误
        }

        String msg = bean.toString();

        Bundle bundle = new Bundle();
        bundle.putString(MoMusicGlobal.MO_MUSIC_PARAM_DISPLAY, msg);
        EventBus.getDefault().post(new MainEvent(MoMusicGlobal.MO_MUSIC_CONTROL_LOAD_SINGLE_FLOAT, bundle));

        addOneChat(new ChatMessage(ChatMessage.RECEIVE, msg, Constant.RECEIVE_MUSIC_CARD), true);
        ((MainActivity)mContext).getMoMusicManager().switchMusicPlayingStatus();
    }

    public void switchMusic(Bundle bundle) {
        MoMusicBean musicBean = bundle.getParcelable(MoMusicGlobal.MO_MUSIC_PARAM_DISPLAY);
        if ( null == musicBean ) {
            addOneChat(new ChatMessage(ChatMessage.RECEIVE, getResources().getString(R.string.voice_no_msg), Constant.RECEIVE_TXT), false);
            return;//错误
        }

        String musicInfo = musicBean.toString();
        if (mFloatMessages.size() > 1) {
            mFloatAdapter.updateMusicPlayerStatus(musicInfo);
        }

        mDynaminFloat.refreshMusicBar(musicInfo, true);

        handleFloatView(true);
        setSideMenuBtnStyle(true);

        if ( MoMusicGlobal.MO_PLAY_STATUS_ALL_FINISHED != musicBean.getPlayerStatus() ) {
            ((MainActivity) mContext).getMoMusicManager().switchMusicPlayingStatus();
        }
    }

    private void refreshMusicStatus(Bundle bundle) {
        String musicInfo = bundle.getString(MoMusicGlobal.MO_MUSIC_PARAM_PLAYER_STATUS);
        mDynaminFloat.refreshMusicBar(musicInfo, false);
        mFloatAdapter.updateMusicPlayerStatus(musicInfo);

        if ( TextUtils.isEmpty(musicInfo) ) {
            return;
        }

        MoMusicBean bean = ((MainActivity)mContext).getMoMusicManager().MsgToBean(musicInfo);
        if (null == bean) {
            return;
        }

        switch ( bean.getPlayerStatus() ) {
            case MoMusicGlobal.MO_PLAY_STATUS_PAUSED:
            case MoMusicGlobal.MO_PLAY_STATUS_CURRENT_FINISHED:
            case MoMusicGlobal.MO_PLAY_STATUS_ALL_FINISHED:
                ((MainActivity)mContext).stopMo3DToIdle();
                break;
            case MoMusicGlobal.MO_PLAY_STATUS_PLAYING:
                ((MainActivity) mContext).stopDcsSpeaker();
                break;
            default:
                break;
        }
    }

    private void refreshMusicElapsed(Bundle bundle){
        String elapsed = bundle.getString(MoMusicGlobal.MO_MUSIC_PARAM_ELAPSED);
        mDynaminFloat.refreshMusicBar(elapsed);
        mFloatAdapter.updateMusicPlayerElapsed(elapsed);
        ((MainActivity) mContext).startMo3DSing();
    }

    public void updateClockFloat(Bundle bundle) {
        if ( null == bundle ) {
            return;
        }

        MoClockBean bean = bundle.getParcelable(MoClockGlobal.CLOCK_PARAM_CLOCK_BEAN);
        if (null == bean) {
            return;
        }

        mDynaminFloat.refreshClockBar(bean);
        mFloatAdapter.refreshClockBar(bean);
    }

    public void removeClockFloat(long clock_id) {
        if ( 0 == clock_id ) {
            return;
        }

        handleFloatView(false);
        if (null != mFloatAdapter) {
            mFloatAdapter.removeClockItem(clock_id);
        }

        // 单个浮层
        if ( 0 == mFloatMessages.size() ) {
            Button btn = (Button) this.findViewById(R.id.sideMenuBtn);
            btn.setBackgroundResource(R.drawable.mo_sidemenu_fold);
            btn.setVisibility(View.GONE);
            mDynaminFloat.setVisibility(View.GONE);
        }
    }

    public void updateClockNotificationStatus(int clock_id, boolean bNotification) {
        if ( 0 == mFloatMessages.size() ) {
            return;
        }

        if (null != mFloatAdapter) {
            mFloatAdapter.refreshClockNotificationMsg(clock_id, bNotification);
        }
    }

    public void updateCalendarFloat(long calendar_id) {
        if ( 0 == calendar_id ) {
            return;
        }

        MoCalendarBean bean = ((MainActivity) mContext).getMoCalendarManager().getCalendarInfo(calendar_id);
        if (null == bean) {
            return;
        }

        mDynaminFloat.refreshCalendarBar(bean);
        mFloatAdapter.refreshCalendarBar(bean);
    }

    public void removeCalendarFloat(long calendar_id) {
        if ( 0 == calendar_id ) {
            return;
        }

        handleFloatView(false);
        mFloatAdapter.removeCalendarItem(calendar_id);

        // 单个浮层
        if ( 0 == mFloatMessages.size() ) {
            Button btn = (Button) this.findViewById(R.id.sideMenuBtn);
            btn.setBackgroundResource(R.drawable.mo_sidemenu_fold);
            btn.setVisibility(View.GONE);
            mDynaminFloat.setVisibility(View.GONE);
        }
    }

    public void LastMultiFloatMsgToSingleFloat(Bundle bundle) {
        if (null == bundle || null == mDynaminFloat || 1 != mFloatAdapter.getItemCount()) {
            return;
        }

        int type_of_last_item = bundle.getInt(Constant.TYPE_OF_LAST_MULTI_FLOAT_ITEM);
        String msg_of_last_item = bundle.getString(Constant.MSG_OF_LAST_MULTI_FLOAT_ITEM);

        switch (type_of_last_item) {
            case Constant.RECEIVE_ALARM_CARD:
                mDynaminFloat.loadAlarmBar(msg_of_last_item);
                break;
            case Constant.RECEIVE_CALENDAR_CARD:
                mDynaminFloat.loadCalendarBar(MoCalendarManager.MoCalendarInfoToBean(msg_of_last_item));
                break;
            case Constant.RECEIVE_MUSIC_CARD:
                mDynaminFloat.loadMusicBar(msg_of_last_item);
                break;
            case Constant.RECEIVE_DOWNLOADAPP_CARD:
                final String[] rs = msg_of_last_item.split(DownloadAction.APP_SPLIT);
                if ( null == rs || 4 > rs.length ) {
                    return;
                }

                final String appID = rs[0];
                final String appName = rs[1];
                String iconUrl = rs[2];
                float appSize = (float) (Math.round(((float) (Integer.parseInt(rs[3])) / 1024) * 100)) / 100;
                mDynaminFloat.loadAppDownloadBar(appID, appName, iconUrl, rs[3], false);
                break;
            default:
                return;
        }

        setSideMenuBtnStyle(true);
    }

    public void updateScreenWhenRecord() {
        messageInputView.beginRecordUI();
        //add by jay 20170919
        if (moFloatView.getVisibility() == View.VISIBLE || mDynaminFloat.getVisibility() == View.VISIBLE) {
            handleFloatView(false);
            setSideMenuBtnStyle(false);
        }
    }

    public void doUpdateSpeakChatItem(String info) {
        if (mSpeakMsg == null) {
            mSpeakMsg = new ChatMessage(ChatMessage.SEND, info, Constant.SEND_TXT);
            mChatAdapter.addData(mSpeakMsg);
        } else {
            if (!info.equals(mSpeakMsg.getBody())) {
                mSpeakMsg.setBody(info);
                mChatAdapter.notifyItemChanged(chatMessages.size() - 1);
                smoothChatListToLastRecord();
            }
        }
    }

    public void addHelpItemInChat() {
        //判断最新一条对话是否为helplist
        if(chatMessages==null||chatMessages.size()==0){
            mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,"HelpList",Constant.RECEIVE_HELP_LIST));
            EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
        }else{
            if ( -1 != mChatAdapter.getHelpPosition() ) {
                mChatAdapter.removeHelp();
            }else {
                mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,"HelpList",Constant.RECEIVE_HELP_LIST));
                //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
                mHandler.sendEmptyMessageDelayed(0,100);
            }
        }
    }

    public void addUpdateChat(int viewType,boolean... next) {
        //判断最新一条对话是否为helplist
        Bundle bundle = new Bundle();
        bundle.putString("introduction",mIntroduction);
        bundle.putLong("size",mFileSize);
        if(chatMessages==null||chatMessages.size()==0){
            mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,bundle, RECEIVE_UPGRADE_CARD));
            //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
        }else {
            if ( -2 != mChatAdapter.getUpdatePosition() ) {
                mChatAdapter.removeUpdate();
                if (next.length!=0&&next[0]) {
                    mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE, bundle, viewType));
                    //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
                    mHandler.sendEmptyMessageDelayed(0, 100);
                }
            }else {
                mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,bundle, RECEIVE_UPGRADE_CARD));
                //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
                mHandler.sendEmptyMessageDelayed(0,100);
            }
        }
    }

    public void addGeekChat() {
        if(chatMessages==null||chatMessages.size()==0){
            mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,"geek",Constant.RECEIVE_INTELLIGENT_SCENE_CARD));
            EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
        }else{
            if ( -3 != mChatAdapter.getGeekPosition() ) {
                mChatAdapter.removeGeek();
            }else {
                mChatAdapter.addData(new ChatMessage(ChatMessage.RECEIVE,"geek",Constant.RECEIVE_INTELLIGENT_SCENE_CARD));
                //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
                mHandler.sendEmptyMessageDelayed(0,100);
            }
        }
    }

    public void handleDanceAction() {
        if(this == null) return;
        this.setVisibility(View.GONE);
        playAnimate(this, R.anim.fade_out);
    }

    @Override
    public void onError(Exception e, BaseReqType baseReqType, int reqCode) {
        Log.d(TAG,"jjyh onError up="+reqCode+" Exception="+e+" baseReqType="+baseReqType.rtn);
    }

    @Override
    public void onResponse(BaseReqType reqType, int reqCode) {
        if (reqCode==NetReqCodeList.sUpgradeUpdate&&reqType instanceof UpgradeReqType) {
            UpgradeReqType upgrade = (UpgradeReqType) reqType;
            mIntroduction = upgrade.introduction;
            mUpgradeUrl = upgrade.upgrade_url;
            mNewVersion = upgrade.new_version;
            mFileSize = upgrade.filesize;
            mMd5 = upgrade.md5;
            Log.d(TAG, "jjyh onResponse=" + mIntroduction + "\nmUpgradeUrl=" + mUpgradeUrl + "\nmNewVersion="
                    + mNewVersion + "\nreqType=" + reqType.rtn);

            addOneChat(new ChatMessage(ChatMessage.RECEIVE, getString(R.string.mo_update_discover_new_version), Constant.RECEIVE_TXT), false);
            addUpdateChat(Constant.RECEIVE_UPGRADE_CARD);
        }
    }

    @Override
    public void inProgress(float progress, long total, int reqCode) {
        if (reqCode==NetReqCodeList.sUpgradeDownload) {
            Log.d(TAG, "jjyh inProgress=" + progress + " total=" + total + " reqCode" + reqCode);
            mChatAdapter.progressUpdate(progress, total);
        }
    }

    @Override
    public void onFail(Exception e, int reqCode) {
        if (reqCode==NetReqCodeList.sUpgradeDownload) {
            Log.d(TAG, "jjyh onFail=" + e.getMessage() + " reqCode=" + reqCode);
            mChatAdapter.textUpdate(getString(R.string.mo_update_file_fail), mFileSize);
        }
    }

    @Override
    public void onSuccess(File file, int reqCode) {
        if (reqCode==NetReqCodeList.sUpgradeDownload) {
            mApkFile = file;
            addUpdateChat(Constant.RECEIVE_INSTALL_CARD, true);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainView> mMainViewWeakReference;
        public MyHandler(MainView mMainView){
            mMainViewWeakReference = new WeakReference<MainView>(mMainView);
        }

        @Override
        public void handleMessage(Message msg) {
            MainView view = mMainViewWeakReference.get();
            if (view != null) {
                view.smoothChatListToLastRecord();
                //EventBus.getDefault().post(new MainEvent(CHAT_LIST_SMOOTH_BOTTOM));
            }
        }
    }

    public ChatAdapter getFloatAdapter(){
        return mFloatAdapter;
    }

    public DynaminFloat getDynaminFloat(){
        return mDynaminFloat;
    }



    public boolean hideFloat(View focusedView, float x, float y) {
        if ( null == focusedView ) {
            return false;
        }

        if (null != mFloatFooterBtn) {
            int l[] = {0, 0};
            int left = l[0];
            int top = l[1];
            int right = left + mFloatFooterBtn.getWidth();
            int bottom = top + mFloatFooterBtn.getHeight();
            if ( x > left && x < right && y > top && y < bottom) {
                return false;
            }
        }

        if (null != mDynaminFloat && View.VISIBLE == mDynaminFloat.getVisibility()) {
            int l[] = {0, 0};
            mDynaminFloat.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + mDynaminFloat.getHeight(),
                    right = left + mDynaminFloat.getWidth();
            if ( ! (x > left && x < right && y > top && y < bottom) ) {
                handleFloatView(false);
                setSideMenuBtnStyle(false);
                return true;
            }
            return false;
        }

        if (null != moFloatView && View.VISIBLE == moFloatView.getVisibility()) {
            int l[] = {0, 0};
            moFloatView.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + moFloatView.getHeight(),
                    right = left + moFloatView.getWidth();
            if ( ! (x > left && x < right && y > top && y < bottom) ) {
                handleFloatView(false);
                setSideMenuBtnStyle(false);
                return true;
            }
        }
        return false;
    }

    /**
     * 添加带语音播报的提示信息
     * @param msg
     */
    private void addMsgWithTTS(String msg) {
        if ( TextUtils.isEmpty(msg) ) {
            return;
        }

        TtsImpl tts = TtsImpl.getInstance(mContext);
        if (null != tts && tts.canSpeak()) {
            tts.addTts(msg);
            tts.speak(msg, null);
        }
        addOneChat(new ChatMessage(ChatMessage.RECEIVE, msg, Constant.RECEIVE_TXT), false);
    }
}
