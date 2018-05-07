package io.gamereward.grd;

import org.json.JSONObject;

import java.lang.reflect.Type;

public abstract class GrdCustomCallBack {
    public Object jobject;
    public abstract void OnFinished(int error, String data, Object jsonObject);
    public <T> T getObject(Class<T>tClass){
        return GrdManager.getObject(jobject,tClass);
    }
}
