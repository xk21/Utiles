package com.jjyh.it.utiles.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class URLUtils {
    /**
     * 解析出url请求的路径，包括页面
     *
     * @param strURL url地址
     * @return url路径
     */
    public static String parseAddr(String strURL) {
        String strPage = null;
        String[] arrSplit = null;

        strURL = strURL.trim().toLowerCase();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 0) {
            if (arrSplit.length > 1) {
                if (arrSplit[0] != null) {
                    strPage = arrSplit[0];
                }
            }
        }

        return strPage;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param url url地址
     * @return url请求参数部分
     */
    private static String removeAddr(String url) {
        String allParam = null;
        String[] slipts = null;

        url = url.trim().toLowerCase();

        slipts = url.split("[?]");
        if (url.length() > 1) {
            if (slipts.length > 1) {
                if (slipts[1] != null) {
                    allParam = slipts[1];
                }
            }
        }

        return allParam;
    }

    /**
     * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> urlKeys(String URL) {
        Log.d("URLUtils", "urlKeys: url="+URL);
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = removeAddr(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        // 每个键值为一组 www.ivvi.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

}
