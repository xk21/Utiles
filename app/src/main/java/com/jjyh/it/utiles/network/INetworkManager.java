package com.jjyh.it.utiles.network;


import com.ivvi.moassistant.model.reqtype.BaseReqType;
import com.ivvi.moassistant.network.httpinterface.IProtocol;

import java.io.File;

/**
 * Created by suxinwei on 2017/2/23.
 */

public interface INetworkManager {

    /**
     * 取消请求
     */
    void cancel(IProtocol.Callback<BaseReqType> callback);

    /**
     * 提交意见反馈
     */
    void commitFeedBack(IProtocol.Callback<BaseReqType> callback, String devid, String checksign,
                        String contactWay, String content, String devId, int reqCode);
    /**
     * 检测更新
     */
    void upgrade(String version, IProtocol.Callback<BaseReqType> callback, int reqCode, Object... objects);
    
    /**
     * 下载
     */
    void download(File file, String downloadUrl, IProtocol.FileCallback fileCallback, int reqCode);
    
}
