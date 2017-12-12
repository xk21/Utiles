package com.jjyh.it.utiles.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ivvi.moassistant.model.WeekBean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class SPSaveList {
    private static SharedPreferences sp;
    private final SharedPreferences.Editor editor;
    
    public SPSaveList(Context context, String preferenceName) {
        sp = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = sp.edit();
    }
    public void putWeekBean(String key, List<WeekBean> bookList) {

        Gson gson = new Gson();
        String json = gson.toJson(bookList);
        editor.putString(key, json);
        editor.commit();
    }

    public List<WeekBean> getWeekBean(String key) {
        Gson gson = new Gson();
        String json = sp.getString(key, null);
        Type type = new TypeToken<List<WeekBean>>() {
        }.getType();
        List<WeekBean> arrayList = gson.fromJson(json, type);
        return arrayList;
    }

    /**
     * 存储
     */
    public void putValue(String key, Object object) {
        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        editor.commit();
    }

    /**
     * 获取保存的数据
     */
    public Object getValue(String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        } else {
            return sp.getString(key, null);
        }
    }

    /**
     * 移除某个key值已经对应的值
     */
    public void remove(String key) {
        editor.remove(key);
        editor.commit();
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        editor.clear();
        editor.commit();
    }

    /**
     * 查询某个key是否存在
     */
    public Boolean contain(String key) {
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public Map<String, ?> getAll() {
        return sp.getAll();
    }
}
