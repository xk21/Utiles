package com.jjyh.it.utiles;

import com.ivvi.moassistant.global.Constant;
import com.ivvi.mosdk.BaseApplication;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created by zhangjunjie on 2017/8/7.
 */

public class MoApplication extends BaseApplication {
    private static volatile MoApplication instance = null;
    //time litmit for 10 second
    private static final int TIME_LIMIT = 10;
    private static final String TAG = "MoApplication";
    private IModelsManager mModelsManager;

    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new ActivityCallbacks());
        instance = this;
//        initOkHttpClient();
    }

    private void initOkHttpClient() {
//        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor(Constant.SharePreference.GAL_PREFERENCE, true))
//                .cookieJar(cookieJar)
                .connectTimeout(TIME_LIMIT, TimeUnit.SECONDS)
                .readTimeout(TIME_LIMIT, TimeUnit.SECONDS)
                .writeTimeout(TIME_LIMIT, TimeUnit.SECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public IModelsManager getModelsManager() {
        if (null == mModelsManager) {
            synchronized (MoApplication.class) {
                if (null == mModelsManager) {
                    mModelsManager = new ModelsManager(this);
                }
            }
        }
        return mModelsManager;
    }

    public static MoApplication getInstance() {
        return instance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}

