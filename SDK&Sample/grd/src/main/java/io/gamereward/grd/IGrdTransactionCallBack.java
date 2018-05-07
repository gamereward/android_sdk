package io.gamereward.grd;


public interface IGrdTransactionCallBack {
    void OnFinished(int error, String message,GrdTransaction[] transactions);
}
