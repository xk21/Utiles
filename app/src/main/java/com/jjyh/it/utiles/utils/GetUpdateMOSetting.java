package com.jjyh.it.utiles.utils;

import android.text.TextUtils;

import com.ivvi.moassistant.global.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.ivvi.moassistant.utils.CommonUtil.ensureStringNotNull;

/**
 * Created by zhaowei on 2017/12/9.
 */

public class GetUpdateMOSetting {
    public GetUpdateMOSetting() {
    }

    public void getSettings(){
        new Thread(new Runnable() {
            @Override public void run () {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(Constant.persionMoSettingUrl).build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String persionMoSetting = response.body().string();
                        JSONObject obj = new JSONObject(persionMoSetting);
                        int code = obj.getInt("code");
                        if (code == 200) {
                            String result = ensureStringNotNull(obj.getString("result"));
                            if (!TextUtils.isEmpty(result)) {
                                JSONObject retObj = new JSONObject(result);
                                String usedBaiduVoice = retObj.getString("usedBaiduVoice");
                                JSONArray japersonas = retObj.getJSONArray("personas");
                                for (int i = 0;i < 3 ; i++){
                                    JSONObject personas = new JSONObject(japersonas.get(i).toString());
                                    String person_key = personas.getString("key");
                                    String person_name = personas.getString("name");
                                    String person_speaker = personas.getString("speeaker");
                                    String person_pitch = personas.getString("pitch");
                                    String person_speed = personas.getString("speed");
                                    String person_volume = personas.getString("volume");

                                    StringBuffer sb = new StringBuffer();
                                    sb.append(usedBaiduVoice);
                                    sb.append(",");
                                    sb.append(person_key);
                                    sb.append(",");
                                    sb.append(person_name);
                                    sb.append(",");
                                    sb.append(person_speaker);
                                    sb.append(",");
                                    sb.append(person_pitch);
                                    sb.append(",");
                                    sb.append(person_speed);
                                    sb.append(",");
                                    sb.append(person_volume);
                                    SharedPrefsUtils.putString("update_setting_person"+i,sb.toString());
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
