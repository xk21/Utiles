package com.jjyh.it.utiles.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by suxinwei on 2017/8/22.
 */

public class BeanUtils {

    private BeanUtils() {
    }

    public static <T> T copyProperties(Object orig, Class<T> dest) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(orig), dest);
    }

    public static <T> T copyProperties(Object orig, TypeToken<T> typeToken) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(orig), typeToken.getType());
    }
}
