package com.jjyh.it.utiles.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.ivvi.moassistant.R;
import com.ivvi.moassistant.adapter.SceneCardAdapter;
import com.ivvi.moassistant.global.Constants;
import com.ivvi.moassistant.manager.LocationModule;
import com.ivvi.moassistant.manager.SceneManager;
import com.ivvi.moassistant.model.SceneNode;
import com.ivvi.moassistant.observer.SceneResultObserver;
import com.ivvi.moassistant.observer.SceneResultSubject;
import com.ivvi.moassistant.utils.GeekUtils;
import com.ivvi.moassistant.utils.NetWorkUtil;
import com.sharedream.geek.sdk.GeekSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SceneCard extends LinearLayout implements SceneResultObserver {
    private static final String TAG ="SceneCard";
    private Context context;
    private GridView gvSceneCard;
    private TextView tvSceneName;
    private TextView tvFootprints;
    private TextView tvNearbyScene;
    private TextView tvSplitLine;
    private ImageView ivSceneLogo;
    private ImageView ivSecondarySceneLogo;
    private LinearLayout llSceneCard;
    private RelativeLayout llNoSceneCard;
    private RelativeLayout rlShop;

    private SceneCardAdapter sceneCardAdapter;
    private List<SceneNode> newSceneNodeList;
    private JSONObject currentSceneData;
    private String sceneUrl;
    private String shopName;
    private boolean isShowSecondaryScene = false;
    private TextView geekFootprints;
    private TextView geekNearbyScene;

    public SceneCard(Context context) {
        super(context);
        init(context);
    }

    public SceneCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        initView();
        setListener();
    }

    private void setListener() {
        Log.d(TAG,"setListener");
        SceneResultSubject.getInstance().registerObserver(this);
        rlShop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GeekUtils.launchServiceActivity(context, sceneUrl, shopName);
            }
        });

        gvSceneCard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (newSceneNodeList == null || newSceneNodeList.size() == 0) {
                    return;
                }

                SceneNode sceneNode = newSceneNodeList.get(position);
                if (sceneNode != null && context != null) {
                    GeekUtils.launchServiceActivity(context, sceneNode.actionUrl, sceneNode.serviceName);
                }
            }
        });

        tvFootprints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    return;
                }

                GeekUtils.launchServiceActivity(context, Constants.RECENTLY_FOOTPRINTS_URL, context.getResources().getString(R.string.geek_view_recently_footprints));
            }
        });
        geekFootprints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context == null) {
                    return;
                }

                GeekUtils.launchServiceActivity(context, Constants.RECENTLY_FOOTPRINTS_URL, context.getResources().getString(R.string.geek_view_recently_footprints));
            }
        });
        tvNearbyScene.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!NetWorkUtil.isNetworkConnected(getContext())){
                    Toast.makeText(context,context.getString(R.string.network_connect_failed),Toast.LENGTH_SHORT).show();
                    return;
                }
                double lastLatitude = LocationModule.getInstance().getLastLatitude();
                double lastLongitude = LocationModule.getInstance().getLastLongitude();
                SceneManager.getInstance().getNearbySceneUrl();
                GeekSdk.queryNearbyPoiUrl(lastLongitude, lastLatitude);
            }
        });
        geekNearbyScene.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetWorkUtil.isNetworkConnected(getContext())){
                    Toast.makeText(context,context.getString(R.string.network_connect_failed),Toast.LENGTH_SHORT).show();
                    return;
                }
                double lastLatitude = LocationModule.getInstance().getLastLatitude();
                double lastLongitude = LocationModule.getInstance().getLastLongitude();
                Log.d(TAG, "geekNearbyScene onClick: "+lastLatitude+","+lastLongitude);
                SceneManager.getInstance().getNearbySceneUrl();
                GeekSdk.queryNearbyPoiUrl(lastLongitude, lastLatitude);
            }
        });

        ivSecondarySceneLogo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                isShowSecondaryScene = true;
                String secondaryScenePoiId = (String) view.getTag();
                JSONObject secondarySceneData = SceneManager.getInstance().getSceneDataByPoiId(secondaryScenePoiId);
                onSceneResultFound(secondarySceneData);
            }
        });
    }

    private void showSceneCard(JSONObject sceneData) {
        currentSceneData = sceneData;
        String poiId = sceneData.optString(Constants.JSON_KEY_POI_ID);
        SceneManager.getInstance().setCurrentScenePagePoiId(poiId);

        shopName = sceneData.optString(Constants.JSON_KEY_SHOP_NAME);
        sceneUrl = sceneData.optString(Constants.JSON_KEY_SCENE_URL);
        tvSceneName.setText(shopName);
        String logoUrl = sceneData.optString(Constants.JSON_KEY_LOGO_URL);
        if (!TextUtils.isEmpty(logoUrl)) {
            Glide.with(context).load(logoUrl).asBitmap().into(new SimpleTarget<Bitmap>(GeekUtils.dip2px(context,24)
                    ,GeekUtils.dip2px(context,24)) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Bitmap logoBitmap = GeekUtils.convert2BorderBitmap(resource, 0, context);
                    ivSceneLogo.setImageBitmap(logoBitmap);
                }
            });

            JSONArray serviceJsonArray = sceneData.optJSONArray(Constants.JSON_KEY_SERVICE);
            if (serviceJsonArray != null) {
                List<SceneNode> sceneNodeList = new ArrayList<>();
                int length = serviceJsonArray.length();
                for (int i = 0; i < length; i++) {
                    SceneNode sceneNode = new SceneNode();
                    JSONObject sceneObject = serviceJsonArray.optJSONObject(i);
                    String serviceName = sceneObject.optString(Constants.JSON_KEY_SERVICE_NAME);
                    String actionUrl = sceneObject.optString(Constants.JSON_KEY_ACTION_URL);
                    String iconHttpUrl = sceneObject.optString(Constants.JSON_KEY_ICON_HTTP_URL);
                    String iconFileUrl = sceneObject.optString(Constants.JSON_KEY_ICON_FILE_URL);

                    sceneNode.serviceName = serviceName;
                    sceneNode.actionUrl = actionUrl;
                    sceneNode.iconHttpUrl = iconHttpUrl;
                    sceneNode.iconFileUrl = iconFileUrl;
                    sceneNode.sceneUrl = sceneUrl;
                    sceneNodeList.add(sceneNode);
                }

                // 处理服务个数,最多展示6个
                newSceneNodeList = handleSceneNodeList(sceneNodeList);
                gvSceneCard.setNumColumns(4); // 每行最多显示3个服务
                if (sceneCardAdapter == null) {
                    sceneCardAdapter = new SceneCardAdapter(context, newSceneNodeList);
                    gvSceneCard.setAdapter(sceneCardAdapter);
                } else {
                    sceneCardAdapter.setData(newSceneNodeList);
                    sceneCardAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void saveScene2Sp(JSONObject sceneData) {
        String poiId = sceneData.optString(Constants.JSON_KEY_POI_ID);
        String footprintsInSp = GeekUtils.restoreFieldString(context, Constants.CONFIG_KEY_FOOT_PRINTS, "");
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArrayFinal = null;
        try {
            if (!TextUtils.isEmpty(footprintsInSp)) {
                JSONArray footprintsInSpArray = new JSONArray(footprintsInSp);
                if (footprintsInSpArray.length() > 0) {
                    int length = footprintsInSpArray.length();
                    for (int i = 0; i < length; i++) {
                        JSONObject jsonObject = footprintsInSpArray.optJSONObject(i);
                        String poiIdInSp = jsonObject.optString(Constants.JSON_KEY_POI_ID);
                        if (!poiId.equals("0") && !poiIdInSp.equals("0") && poiId.equals(poiIdInSp)) {
                            continue;
                        }

                        jsonArray.put(jsonObject);
                    }
                }
            }

            JSONObject currentSceneDataJSON = new JSONObject();
            currentSceneDataJSON.put(Constants.JSON_KEY_LOGO_URL, sceneData.optString(Constants.JSON_KEY_LOGO_URL));
            currentSceneDataJSON.put(Constants.JSON_KEY_SHOP_NAME, sceneData.optString(Constants.JSON_KEY_SHOP_NAME));
            currentSceneDataJSON.put(Constants.JSON_KEY_SCENE_URL, sceneData.optString(Constants.JSON_KEY_SCENE_URL));
            currentSceneDataJSON.put(Constants.JSON_KEY_POI_ID, sceneData.optString(Constants.JSON_KEY_POI_ID));
            jsonArray.put(currentSceneDataJSON);

            // 最多记录过去的5个场景
            int length = jsonArray.length();
            if (length > Constants.MAX_RECENTLY_FOOTPRINTS_SHOW_NUM) {
                jsonArrayFinal = new JSONArray();
                for (int i = length - Constants.MAX_RECENTLY_FOOTPRINTS_SHOW_NUM; i < length; i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    jsonArrayFinal.put(jsonObject);
                }
            } else {
                jsonArrayFinal = jsonArray;
            }

            GeekUtils.saveField(context, Constants.CONFIG_KEY_FOOT_PRINTS, jsonArrayFinal.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<SceneNode> handleSceneNodeList(List<SceneNode> nodeList) {
        if (nodeList == null) {
            return null;
        }

        List<SceneNode> sceneNodeList = new ArrayList<>();
        if (nodeList.size() > Constants.MAX_SERVICE_SHOW_NUM) {
            for (int i = 0; i < Constants.MAX_SERVICE_SHOW_NUM ; i++) {
                SceneNode newSceneNode = new SceneNode();
                SceneNode sceneNode = nodeList.get(i);
                newSceneNode.serviceName = sceneNode.serviceName;
                newSceneNode.sceneUrl = sceneNode.sceneUrl;
                newSceneNode.actionUrl = sceneNode.actionUrl;
                newSceneNode.iconFileUrl = sceneNode.iconFileUrl;
                newSceneNode.iconHttpUrl = sceneNode.iconHttpUrl;
                sceneNodeList.add(newSceneNode);
            }

            // 第6个服务设置成 "更多服务"
//            SceneNode sceneNode = new SceneNode();
//            sceneNode.sceneUrl = sceneUrl;
//            sceneNode.actionUrl = sceneUrl;
//            sceneNode.serviceName = context.getResources().getString(R.string.cardview_list_more);
//            sceneNode.iconFileUrl = Constants.MORE_SERVICE_ICON_URL;
//            sceneNodeList.add(sceneNode);
        } else {
            sceneNodeList = nodeList;
        }

        return sceneNodeList;
    }

    private void initView() {
        View view = View.inflate(context, R.layout.geek_scene_card, this);
        gvSceneCard = (GridView) view.findViewById(R.id.gv_scene_card);
        tvSceneName = (TextView) view.findViewById(R.id.tv_scene_name);
        ivSceneLogo = (ImageView) view.findViewById(R.id.iv_scene_logo);
        ivSecondarySceneLogo = (ImageView) view.findViewById(R.id.iv_secondary_scene);
        tvFootprints = (TextView) view.findViewById(R.id.tv_footprints);
        tvNearbyScene = (TextView) view.findViewById(R.id.tv_nearby_scene);
        geekFootprints = (TextView) view.findViewById(R.id.geek_footprints);
        geekNearbyScene = (TextView) view.findViewById(R.id.geek_nearby_scene);
        llSceneCard = (LinearLayout) view.findViewById(R.id.ll_scene_container);
        llNoSceneCard = (RelativeLayout) view.findViewById(R.id.mo_geek_noscene);
        rlShop = (RelativeLayout) view.findViewById(R.id.rl_scenes);
        tvSplitLine = (TextView) findViewById(R.id.tv_line_between_nearby_and_footprints);

        String footprints = GeekUtils.restoreFieldString(context, Constants.CONFIG_KEY_FOOT_PRINTS, "");
        if (!TextUtils.isEmpty(footprints)) {
            showFootprintsMenu();
        }
    }

    @Override
    public void onSceneResultFound(JSONObject sceneData) {

        SceneManager.getInstance().addSceneData(sceneData);
        String currentScenePoiId = SceneManager.getInstance().getCurrentSceneDataByPoiId();
        Log.d("ddas","jjyhgeek onSceneResultFound="+sceneData+" \ncurrentScenePoiId="+currentScenePoiId
        +" isShowSecondaryScene="+isShowSecondaryScene);
        if (currentScenePoiId == null) { // 当前没有场景
            llSceneCard.setVisibility(VISIBLE);
            llNoSceneCard.setVisibility(GONE);
            showSceneAndSaveScene(sceneData);
        } else { // 当前已经有场景
            JSONObject secondarySceneData = null;
            if (isShowSecondaryScene) { // 主次场景切换
                isShowSecondaryScene = false;
                secondarySceneData = currentSceneData;
                showSceneAndSaveScene(sceneData);
            } else {
                ivSecondarySceneLogo.setVisibility(VISIBLE);
                secondarySceneData = sceneData;
            }

            String logoUrl = secondarySceneData.optString(Constants.JSON_KEY_LOGO_URL);
            final String poiId = secondarySceneData.optString(Constants.JSON_KEY_POI_ID);
            if (!TextUtils.isEmpty(logoUrl)) {
                Glide.with(context).load(logoUrl).asBitmap().into(new SimpleTarget<Bitmap>(GeekUtils.dip2px(context,24)
                        ,GeekUtils.dip2px(context,24)) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Bitmap convert2RoundBitmap = GeekUtils.convert2BorderBitmap(resource, 0, context);
                        ivSecondarySceneLogo.setTag(poiId);
                        ivSecondarySceneLogo.setImageBitmap(convert2RoundBitmap);
                    }
                });
            }
        }
    }

    private void showSceneAndSaveScene(JSONObject sceneData) {
        saveScene2Sp(sceneData);
        showFootprintsMenu();
        showSceneCard(sceneData);
    }

    private void showFootprintsMenu() {
        tvFootprints.setVisibility(VISIBLE);
        tvSplitLine.setVisibility(VISIBLE);
    }

    @Override
    public void onSceneResultNotFound(JSONArray leaveShopJsonArray) {
        if (leaveShopJsonArray != null && leaveShopJsonArray.length() > 0) {
            int length = leaveShopJsonArray.length();
            if (length == 1) {
                JSONObject leaveShopData = leaveShopJsonArray.optJSONObject(0);

                String poiId = leaveShopData.optString(Constants.JSON_KEY_POI_ID);
                SceneManager.getInstance().removeSceneDataByPoiId(poiId);

                String currentScenePagePoiId = SceneManager.getInstance().getCurrentSceneDataByPoiId();
                int sceneNum = SceneManager.getInstance().getSceneNum();
                if (sceneNum == 2) {
                    // 当前显示的场景离店了
                    if (currentScenePagePoiId != null && currentScenePagePoiId.equals(poiId)) {
                        // 此时若有其它场景,就切回来
                        ivSecondarySceneLogo.setVisibility(View.GONE);
                        String secondaryScenePoiId = (String) ivSecondarySceneLogo.getTag();
                        JSONObject secondarySceneData = SceneManager.getInstance().getSceneDataByPoiId(secondaryScenePoiId);
                        SceneManager.getInstance().setCurrentScenePagePoiId(null);
                        onSceneResultFound(secondarySceneData);
                    } else { // 非当前显示的场景离店了
                        ivSecondarySceneLogo.setVisibility(View.GONE);
                    }
                } else if (sceneNum == 1) {
                    // 没有其他场景了,直接隐藏卡片
                    ivSecondarySceneLogo.setVisibility(View.GONE);
                    llSceneCard.setVisibility(GONE);
                    llNoSceneCard.setVisibility(VISIBLE);
                    SceneManager.getInstance().setCurrentScenePagePoiId(null);
                }

                SceneManager.getInstance().removeSceneDataByPoiId(poiId);
            } else if (length == 2) { // 两个场景都离店
                SceneManager.getInstance().setCurrentScenePagePoiId(null);
                SceneManager.getInstance().removeAllSceneData();
                ivSecondarySceneLogo.setVisibility(View.INVISIBLE);
                llSceneCard.setVisibility(GONE);
                llNoSceneCard.setVisibility(GONE);
            }
        } else { // 处理异常离店
            SceneManager.getInstance().setCurrentScenePagePoiId(null);
            SceneManager.getInstance().removeAllSceneData();
            ivSecondarySceneLogo.setVisibility(View.GONE);
            llSceneCard.setVisibility(GONE);
            llNoSceneCard.setVisibility(VISIBLE);
        }
    }

    @Override
    public void onSceneResultNotFound() {
        ivSecondarySceneLogo.setVisibility(View.GONE);
        llSceneCard.setVisibility(GONE);
        llNoSceneCard.setVisibility(VISIBLE);
    }

    @Override
    public void onRelease() {
        llSceneCard.setVisibility(GONE);
        ivSecondarySceneLogo.setVisibility(GONE);
        if (newSceneNodeList != null) {
            newSceneNodeList.clear();
            newSceneNodeList = null;
        }
        isShowSecondaryScene = false;
        currentSceneData = null;
    }
}
