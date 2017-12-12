package com.jjyh.it.utiles.network;

import android.support.annotation.NonNull;

import com.ivvi.moassistant.MoApplication;
import com.ivvi.moassistant.global.Constant;
import com.ivvi.moassistant.model.FeedBackBean;
import com.ivvi.moassistant.model.reqtype.BaseReqType;
import com.ivvi.moassistant.model.reqtype.UpgradeReqType;
import com.ivvi.moassistant.network.httpinterface.BaseProtocol;
import com.ivvi.moassistant.network.httpinterface.IProtocol;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by suxinwei on 2017/2/23.
 */

public class NetworkManager implements INetworkManager {

    private MoApplication mAppApplication;

    public NetworkManager(MoApplication appApplication) {
        mAppApplication = appApplication;
    }

    @Override
    public void cancel(IProtocol.Callback<BaseReqType> callback) {
        OkHttpUtils.getInstance().cancelTag(callback);
    }

    @Override
    public void commitFeedBack(IProtocol.Callback<BaseReqType> callback, final String devid,final String checksign,
                               final String contactWay, final String content, final String devId, final int reqCode) {
        IProtocol<FeedBackBean> feedBackProtocol = new BaseProtocol<FeedBackBean>() {
            @NonNull
            @Override
            public String getUrl() {
                return Constant.FEEDBACK_URL + "?devid=" + devid + "&checksign=" + checksign;
            }

            @Override
            public void getBodyParamsMap(Map<String, String> bodyParamsMap) {
                bodyParamsMap.put("contactWay", contactWay);
                bodyParamsMap.put("content", content);
                bodyParamsMap.put("devId", devId);
            }
        };
        feedBackProtocol.loadDataByPost(callback, reqCode);
    }
    
    @Override
    public void upgrade(final String version, IProtocol.Callback<BaseReqType> callback, int reqCode, Object... objects) {
        IProtocol<UpgradeReqType> upgradeProtocol = new BaseProtocol<UpgradeReqType>() {
            @NonNull
            @Override
            public String getUrl() {
                return Constant.HTTP.BASE_DOWNLOAD_URL + "/upgrade/getupgrade";
            }
            
            @Override
            public void getUrlParamsMap(Map<String, String> paramsMap) {
                paramsMap.put("clientid", Constant.HTTP.UPGRADE_CLINT_ID + "");
                paramsMap.put("version", version);
            }
        };
        upgradeProtocol.loadDataByGet(callback, reqCode, objects);
    }
    
    @Override
    public void download(final File file, final String downloadUrl, IProtocol.FileCallback fileCallback, int reqCode) {
        IProtocol<File> downloadProtocol = new BaseProtocol<File>() {
            @NonNull
            @Override
            public String getUrl() {
                return downloadUrl;
            }
            
            @Override
            public File getFile() {
                return file;
            }
        };
        downloadProtocol.download(fileCallback, reqCode);
    }
}
