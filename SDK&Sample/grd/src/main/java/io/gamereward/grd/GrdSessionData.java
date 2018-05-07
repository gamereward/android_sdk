package io.gamereward.grd;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GrdSessionData {
    public long sessionid;
    public long sessionstart;
    public HashMap<String,String>values=new HashMap<>();
    public Date getTime(){
        Date date = new java.util.Date(sessionstart*1000L);
        return date;
    }
}
