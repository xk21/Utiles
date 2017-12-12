package com.jjyh.it.utiles.network.httpinterface;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.Map;

/**
 * 网络请求接口
 * Created by suxinwei on 2017/2/21.
 */

public interface IProtocol<REQTYPE> {

    /**
     * 通过Get发起请求
     *
     * @param callback 回调接口
     * @param reqCode  请求标识符
     * @param objects  传递对象(只需传一个参数)
     */
    void loadDataByGet(Callback callback, int reqCode, Object... objects);

    /**
     * 通过Post发起请求
     *
     * @param callback 回调接口
     * @param reqCode  请求标识符
     * @param objects  传递对象(只需传一个参数)
     */
    void loadDataByPost(Callback callback, int reqCode, Object... objects);

    /**
     * 文件下载
     *
     * @param fileCallback 回调接口
     * @param reqCode      请求标识符(只需传一个参数)
     */
    void download(FileCallback fileCallback, int... reqCode);


    /**
     * 决定请求的url地址
     *
     * @return
     */
    @NonNull
    String getUrl();

    /**
     * 添加url请求参数
     *
     * @param urlParamsMap url请求参数信息
     */
    void getUrlParamsMap(Map<String, String> urlParamsMap);

    /**
     * 添加请求头
     *
     * @param headersMap 请求头信息
     */
    void getHeadersMap(Map<String, String> headersMap);

    /**
     * 添加post请求body参数
     *
     * @param bodyParamsMap post请求body参数信息
     */
    void getBodyParamsMap(Map<String, String> bodyParamsMap);

    /**
     * 添加需要上传的文件
     *
     * @param filesMap 上传文件信息
     */
    void getFilesMap(Map<String, File> filesMap);

    /**
     * 添加需要下载的文件
     *
     * @return 文件信息
     */
    File getFile();

    interface Callback<REQTYPE> {

        /**
         * 请求失败回调接口
         *
         * @param e       异常信息
         * @param reqtype 返回数据
         * @param reqCode 请求标识符
         */
        void onError(Exception e, REQTYPE reqtype, int reqCode);

        /**
         * 请求成功回调接口
         *
         * @param reqType 返回数据
         * @param reqCode 请求标识符
         */
        void onResponse(REQTYPE reqType, int reqCode);

    }

    interface FileCallback {

        /**
         * 文件下载，进度监听接口
         *
         * @param progress 进度
         * @param total    总大小
         * @param reqCode  请求标识符
         */
        void inProgress(float progress, long total, int reqCode);

        /**
         * 请求失败回调接口
         *
         * @param e       异常信息
         * @param reqCode 请求标识符
         */
        void onFail(Exception e, int reqCode);

        /**
         * 请求成功回调接口
         *
         * @param file    返回数据
         * @param reqCode 请求标识符
         */
        void onSuccess(File file, int reqCode);
    }
}
