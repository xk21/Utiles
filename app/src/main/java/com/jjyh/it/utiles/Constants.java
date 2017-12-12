package com.jjyh.it.utiles;

public class Constants {
    public static final int JS_EVENT_INIT = 0x01;
    public static final String DEFAULT_ANDROID_ID= "9574d56d682e324c";

    public static final int DELAY_UNIT_TIME = 1000; // 1s
    public static final int VALID_EXIT_TIME = 2 * DELAY_UNIT_TIME; // 2s

    public static final int LOCATION_DELAY_TIME_UNIT = 60 * DELAY_UNIT_TIME; // 60s

    public static final int MAX_SERVICE_SHOW_NUM = 4;
    public static final int MAX_RECENTLY_FOOTPRINTS_SHOW_NUM = 5;

    public static final String ACTION_URL = "actionUrl";

    public static final String MX_JS_OBJECT = "mx";

    public static final String CONFIG_KEY_FOOT_PRINTS = "footprints";

    public static final String JSON_KEY_CONTEXT = "Context";
    public static final String JSON_KEY_UID = "Uid";

    public static final String JSON_KEY_URL = "url";
    public static final String JSON_KEY_RESULT = "result";
    public static final String JSON_KEY_DATA = "data";
    public static final String JSON_KEY_POI_ID = "poiId";
    public static final String JSON_KEY_SERVICE = "service";
    public static final String JSON_KEY_SHOP_NAME = "shopName";
    public static final String JSON_KEY_LOGO_URL = "logoUrl";
    public static final String JSON_KEY_SCENE_URL = "sceneUrl";
    public static final String JSON_KEY_ICON_HTTP_URL = "iconHttpUrl";
    public static final String JSON_KEY_ICON_FILE_URL = "iconFileUrl";
    public static final String JSON_KEY_SERVICE_NAME = "serviceName";
    public static final String JSON_KEY_ACTION_URL = ACTION_URL;

    // 过滤规则
    public static final String JSON_KEY_LIST = "list";
    public static final String JSON_KEY_SCRIPT_CODE = "scriptCode";
    public static final String JSON_KEY_VERSION = "version";

    public static final String SP_KEY_FILTER_RULE_VERSION = "filterRuleVersion";

    public static final String BUNDLE_KEY_ACTION_URL = ACTION_URL;
    public static final String BUNDLE_KEY_ACTION_NAME = "actionName";

    public static final String RECENTLY_FOOTPRINTS_URL = "http://poi.igeekee.cn/scene/history";
    public static final String MORE_SERVICE_ICON_URL = "file:///android_asset/icon_more_service.png";
    public static final int MO_GEEK_RESIDENT_SHOP = 100;
    public static final int MO_GEEK_NETWORK_STATUS = 101;
    public static final int MO_GEEK_RESIDENT_SHOP_MAIN= 102;
    public static final int MO_GEEK_IS_WINDOW_CHANGE= 103;
    public static final int MO_GEEK_SAME_SHOP_NAME= 104;
    public static final String MO_GEEK_FLOAT_INTENT_KEY = "geekshop";
    public static final String MO_GEEK_FLOAT_SP_GEEK = "geeksp";
    public static final String MO_GEEK_FLOAT_SP_X = "geekspX";
    public static final String MO_GEEK_FLOAT_SP_Y = "geekspY";
    public static final String MO_GEEK_SP_SHOP_NAME = "geekshopName";
    public static final String MO_GEEK_SP_SAME_SHOP_NAME = "sameShopName";
    public static final String MO_GEEK_SP_IS_OPEN_WINDOW = "isOpenWindow";
    public static final String MO_GEEK_SP_IS_OPEN_SERVICE = "isOpenService";
    public static final String MO_SETTING_INTENT_OPEN = "settingOpenIntent";
    public static final String MO_SETTING_SP_WEATHER_FESTIVAL = "weatherFestivalSP"; //天气节日文件名 (SPSaveList)
    //天气
    public static final String MO_SETTING_SP_WEATHER_WEEK_KEY = "weatherWeekData";//选着星期key list集合 查看工具类
    public static final String MO_SETTING_SP_WEATHER_TIME_KEY = "weatherTimeData";//选着时间key 8:00等
    public static final String MO_SETTING_WEATHER_DEFAULT_TIME = "8:00";//默认时间
    public static final String MO_SETTING_SP_WEATHER_BROADCAST_KEY = "weatherBroadcastSwitch";//播报开关
    public static final String MO_SETTING_SP_WEATHER_REMIND_KEY = "weatherRemindSwitch";//提醒开关总开关
    //节日
    public static final String MO_SETTING_SP_FESTIVAL_TIME_KEY = "festivalTimeData";//选着时间key 8:00等
    public static final String MO_SETTING_SP_FESTIVAL_INTERVAL_KEY = "festivalInterval";//设置提醒间隔
    public static final String MO_SETTING_FESTIVAL_DEFAULT_TIME = "20:00";//默认时间
    public static final String MO_SETTING_SP_FESTIVAL_BROADCAST_KEY = "festivalBroadcastSwitch";//播报开关
    public static final String MO_SETTING_SP_FESTIVAL_REMIND_KEY = "festivalRemindSwitch";//提醒开关总开关

    public static final String MO_GUIDE_SP = "moGuideSP";
    public static final String MO_GUIDE_KEY = "moGuideKey";

    public static final int MO_GEEK_ACTIVITY_NORMAL = 0;
    public static final int MO_GEEK_FLOAT_TO_ACTIVITY = 1;

}
