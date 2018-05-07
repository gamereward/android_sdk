package io.gamereward.grd;

public interface IGrdLeaderBoardCallBack {
    void OnFinished(int error, String message,GrdLeaderBoard[] leaderBoards);
}
