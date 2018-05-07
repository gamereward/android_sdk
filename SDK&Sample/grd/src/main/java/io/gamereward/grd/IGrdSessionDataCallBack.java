package io.gamereward.grd;

public interface IGrdSessionDataCallBack
{
    void OnFinished(int error, String message,GrdSessionData[] data);

}
