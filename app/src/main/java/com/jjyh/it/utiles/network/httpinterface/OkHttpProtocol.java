package com.jjyh.it.utiles.network.httpinterface;


import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.jjyh.it.utiles.MoApplication;
import com.jjyh.it.utiles.utils.IOUtils;
import com.jjyh.it.utiles.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Response;

/**
 * 网络请求OkHttp实现类
 * Created by suxinwei on 2017/2/21.
 */
public abstract class OkHttpProtocol<REQTYPE> implements IProtocol<REQTYPE> {

    public void loadDataByGet(Callback callback, int reqCode, Object... objects) {
        if (forceNetwork()) {   // 是否强制更新数据
            // 在网络
            loadDataFromNetByGet(callback, reqCode, objects);
        } else {
            // 尝试本地缓存获取
            tryLoadDataFromLocal(callback, reqCode, objects);
        }
    }


    public void loadDataByPost(Callback callback, int reqCode, Object... objects) {
        // 在网络
        loadDataFromNetByPost(callback, reqCode, objects);
    }

    private void tryLoadDataFromLocal(Callback callback, int reqCode, Object objects) {
        BufferedReader reader = null;
        try {
            File cacheFile = getCacheFile();
            if (cacheFile.exists()) {   // 有缓存
                reader = new BufferedReader(new FileReader(cacheFile));
                String cacheResJson = reader.readLine(); // 读取缓存内容
                REQTYPE reqType = parseJson(cacheResJson);
                if (reqType == null) {
                    loadDataFromNetByGet(callback, reqCode, objects);
                } else {
                    rightResponse(cacheResJson, reqType, false, callback, reqCode, objects);
                }
            } else {
                loadDataFromNetByGet(callback, reqCode, objects);
            }
        } catch (IOException e) {
            e.printStackTrace();
            loadDataFromNetByGet(callback, reqCode, objects);
        } finally {
            IOUtils.close(reader);
        }
    }

    @Override
    public void download(FileCallback fileCallback, int... reqCode) {
        String url = getUrl();

        File file = getFile();

        OkHttpUtils
                .get()
                .url(url)
                .tag(fileCallback)
                .build()
                .execute(getFileCallback(file, fileCallback, reqCode));
    }

    /**
     * 从网络获取数据
     *
     * @param callback
     * @param reqCode
     * @param objects
     */
    private void loadDataFromNetByGet(final Callback callback, final int reqCode, Object... objects) {
        ArrayMap<String, String> urlParamsMap = new ArrayMap<>();
        ArrayMap<String, String> headersMap = new ArrayMap<>();
        getUrlParamsMap(urlParamsMap);
        getHeadersMap(headersMap);

        String url = appendUrlParams(getUrl(), urlParamsMap);

        OkHttpUtils
                .get()
                .url(url)
                .tag(callback)
                .headers(headersMap)
                .build()
                .execute(getStringCallback(callback, reqCode, objects));
    }

    protected boolean forceNetwork() {
        return true;  // 默认配置
    }


    /**
     * 发起post请求
     *
     * @param callback
     * @param reqCode
     * @param objects
     */
    private void loadDataFromNetByPost(final Callback callback, final int reqCode, Object... objects) {
        ArrayMap<String, String> urlParamsMap = new ArrayMap<>();
        ArrayMap<String, String> paramsMap = new ArrayMap<>();
        ArrayMap<String, String> headersMap = new ArrayMap<>();
        ArrayMap<String, File> filesMap = new ArrayMap<>();
        getUrlParamsMap(urlParamsMap);
        getBodyParamsMap(paramsMap);
        getHeadersMap(headersMap);
        getFilesMap(filesMap);

        String url = appendUrlParams(getUrl(), urlParamsMap);

        if (isPostJson()) {
            String paramsJsonString = new Gson().toJson(paramsMap).toString();
            OkHttpUtils
                    .postString()
                    .url(url)
                    .tag(callback)
                    .mediaType(MediaType.parse("application/json"))
                    .content(paramsJsonString)
                    .headers(headersMap)
                    .build()
                    .execute(getStringCallback(callback, reqCode, objects));
            return;

        }

        PostFormBuilder postFormBuilder = OkHttpUtils
                .post()
                .url(url)
                .tag(callback)
                .params(paramsMap)
                .headers(headersMap);

        //遍历集合fileMap,动态添加文件
        if (filesMap != null) {
            for (Map.Entry<String, File> info : filesMap.entrySet()) {
                String key = info.getKey();
                File value = info.getValue();
                postFormBuilder.addFile(key, value.getName(), value);
            }
        }

        postFormBuilder
                .build()
                .execute(getStringCallback(callback, reqCode, objects));
    }

    @NonNull
    private StringCallback getStringCallback(final Callback callback, final int reqCode, final Object... objects) {
        return new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (callback != null) {
                    BaseReqType baseReqType = new BaseReqType();
                    baseReqType.rtn = -88888888;
                    if (objects.length != 0) {
                        baseReqType.object = objects[0];
                    }
                    callback.onError(e, baseReqType, reqCode);
                }
            }

            @Override
            public void onResponse(String resJson, int id) {
                REQTYPE reqType = parseJson(resJson);
                if (reqType == null) {
                    if (callback != null) {
                        Exception e = new Exception("not correct json string return");
                        BaseReqType baseReqType = new BaseReqType();
                        baseReqType.rtn = -88888888;
                        if (objects.length != 0) {
                            baseReqType.object = objects[0];
                        }
                        callback.onError(e, baseReqType, reqCode);
                    }
                    return;
                }
                rightResponse(resJson, reqType, true, callback, reqCode, objects);
            }

            @Override
            public boolean validateReponse(Response response, int id) {
                return true;
            }
        };

    }

    private void rightResponse(String resJson, REQTYPE reqType, boolean isNetworkResponse, Callback callback, int reqCode, Object... objects) {
        BaseReqType baseReqType = (BaseReqType) reqType;
        int rtn = baseReqType.rtn;
        if (callback != null) {
            if (rtn == Constant.HTTP.RESULT_OK) {
                if (isNetworkResponse) {
                    write2Local(resJson);
                }
                if (objects.length != 0) {
                    baseReqType.object = objects[0];
                }
                callback.onResponse(reqType, reqCode);
            } else {
                Exception e = new Exception("not correct data return");
                if (objects.length != 0) {
                    baseReqType.object = objects[0];
                }
                callback.onError(e, baseReqType, reqCode);
            }
        }
    }

    @NonNull
    private FileCallBack getFileCallback(File srcFile, final FileCallback callback, final int[] reqCode) {
        return new FileCallBack(srcFile.getParent(), srcFile.getName()) {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (callback != null) {
                    if (reqCode.length == 0) {
                        callback.onFail(e, 0);
                    } else {
                        callback.onFail(e, reqCode[0]);
                    }
                }
            }

            @Override
            public void onResponse(File file, int id) {
                if (callback != null) {
                    if (file == null) {
                        if (reqCode.length == 0) {
                            callback.onFail(null, 0);
                        } else {
                            callback.onFail(null, reqCode[0]);
                        }
                    } else {
                        if (reqCode.length == 0) {
                            callback.onSuccess(file, 0);
                        } else {
                            callback.onSuccess(file, reqCode[0]);
                        }
                    }
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (callback != null) {
                    if (reqCode.length == 0) {
                        callback.inProgress(progress, total, 0);
                    } else {
                        callback.inProgress(progress, total, reqCode[0]);
                    }
                }
            }
        };
    }


    protected boolean isPostJson() {
        return true;
    }

    /**
     * 基类完成统一的泛型解析
     *
     * @param resJson
     * @return
     */
    private REQTYPE parseJson(String resJson) {
        Type type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            return new Gson().fromJson(resJson, type);
        } catch (Exception e) {
            LogUtils.e("parseJsonError", resJson);
            return null;
        }
    }

    private String appendUrlParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        sb.append("?");
        for (String key : params.keySet()) {
            if (!TextUtils.isEmpty(key)) {
                sb.append(key).append("=");
            }
            sb.append(params.get(key)).append("&");
        }
        return sb.substring(0, sb.lastIndexOf("&"));
    }

    @Override
    public void getUrlParamsMap(Map<String, String> urlParamsMap) {
    }

    @Override
    public void getBodyParamsMap(Map<String, String> bodyParamsMap) {
    }

    @Override
    public void getHeadersMap(Map<String, String> headersMap) {
    }

    @Override
    public void getFilesMap(Map<String, File> filesMap) {
    }

    @Override
    public File getFile() {
        return null;
    }

    /**
     * 缓存协议内容到本地
     *
     * @param resJson
     */
    private void write2Local(String resJson) {
        BufferedWriter writer = null;
        try {
            File cacheFile = getCacheFile();
            LogUtils.i("CacheToLocal", "缓存数据到本地-->" + cacheFile.getAbsolutePath());
            writer = new BufferedWriter(new FileWriter(cacheFile));
            writer.write(resJson);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 得到缓存文件
     */
    private File getCacheFile() {
        String dir = MoApplication.getInstance().getExternalCacheDir().getAbsolutePath() + "/OkHttpCache";
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        return new File(dirFile, getCacheFileName());
    }

    /**
     * @des 得到缓存的唯一索引
     */
    private String getCacheFileName() {
        ArrayMap<String, String> paramsMap = new ArrayMap<>();
        getUrlParamsMap(paramsMap);
        String paramsString = "";
        for (String key : paramsMap.keySet()) {
            paramsString += key + paramsMap.get(key);
        }
        return (getUrl().replace(Constant.HTTP.BASE_URL, "") + paramsString).replaceAll("[\\p{P}\\p{S}\\p{Z}]", "");
    }
}
