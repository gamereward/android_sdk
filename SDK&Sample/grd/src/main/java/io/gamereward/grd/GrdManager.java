package io.gamereward.grd;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GrdManager {
    public static final int EXTERNAL_TRANSTYPE = 3;
    public static final int INTERNAL_TRANSTYPE = 2;
    public static final int BASE_TRANSTYPE = 1;
    public static final int SUCCESS_TRANSSTATUS = 0;
    public static final int PENDING_TRANSSTATUS = 1;
    public static final int ERROR_TRANSSTATUS = 2;
    private final static String MAIN_NET_URL = "https://gamereward.io/appapi/";
    private final static String TEST_NET_URL = "https://test.gamereward.io/appapi/";
    private static   String baseUrl = "";
    private static String api_Id = "";
    private static String api_Secret = "";
    private static String userToken = "";
    private static GrdUser _user;

    public static void init(String appId, String secret,GrdNet net) {
        api_Id = appId;
        api_Secret = secret;
        if(net==GrdNet.MainNet){
            baseUrl=MAIN_NET_URL;
        }
        else{
            baseUrl=TEST_NET_URL;
        }
    }

    public static String md5(String s) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.update(s.getBytes(), 0, s.length());
        return new BigInteger(1, m.digest()).toString(16);
    }

    public static GrdUser getUser() {
        return _user;
    }

    public static boolean isLogged() {
        return _user != null;
    }

    private static void requestAsyncTask(String action, Map<String, String> params, boolean isGet, INetworkCallBack callBack) {
        GrdAsyncTask task = new GrdAsyncTask(baseUrl + action, isGet, api_Id, api_Secret, userToken, callBack);

        if (Build.VERSION.SDK_INT >= 11/*HONEYCOMB*/) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }

    }

    private static JSONObject getJsonObject(String json) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            try {
                jsonObject = new JSONObject("{\"error\":500,\"message\":\"Server error\"}");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private static String serializeJson(Object o) {
        String result = "";
        if (o == null) {
            result = "null";
        } else if (o.getClass().isPrimitive()
                || o.getClass().isAssignableFrom(Byte.class)
                || o.getClass().isAssignableFrom(Integer.class)
                || o.getClass().isAssignableFrom(Long.class)
                || o.getClass().isAssignableFrom(Double.class)
                || o.getClass().isAssignableFrom(Boolean.class)
                || o.getClass().isAssignableFrom(BigDecimal.class)) {
            result = o.toString();
        } else if (o.getClass().isAssignableFrom(String.class)) {
            result = "\"" + o.toString().replaceAll("\"", "\\\"") + "\"";
        } else if (o.getClass().isArray()) {
            result = "[";
            int len = Array.getLength(o);
            for (int i = 0; i < len; i++) {
                Object element = Array.get(o, i);
                result += serializeJson(element) + ",";
            }
            if (result.length() > 2) {
                result = result.substring(0, result.length() - 1);
            }
            result += "]";
        } else if (o instanceof List<?> || o instanceof ArrayList<?>) {
            Method size = getMethod("size", o.getClass());
            Method get = getMethod("get", o.getClass());
            int len = 0;
            try {
                len = (int) size.invoke(o);
                result = "[";
                for (int i = 0; i < len; i++) {
                    try {
                        Object element = get.invoke(o, i);
                        result += serializeJson(element) + ",";
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                if (result.length() > 2) {
                    result = result.substring(0, result.length() - 1);
                }
                result += "]";

            } catch (IllegalAccessException e) {
                result = "";
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                result = "";
                e.printStackTrace();
            }

        } else if (o instanceof Map<?, ?>) {
            try {
                Method keySet = o.getClass().getMethod("keySet");
                Method get = o.getClass().getMethod("get");
                try {
                    Object setOfKeys = keySet.invoke(o);
                    Method toArray=getMethod("toArray", setOfKeys.getClass());
                    Object array=toArray.invoke(setOfKeys);
                    int len= Array.getLength(array);
                    result = "{";
                    for (int i = 0; i <len; i++) {
                        Object key=Array.get(array,i);
                        try {
                            Object element = get.invoke(o, key);
                            result +="\""+ key.toString()+"\":"+serializeJson(element) + ",";
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    if (result.length() > 2) {
                        result = result.substring(0, result.length() - 1);
                    }
                    result += "}";

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                result = "";
            }
        } else {
            Field[] fields = o.getClass().getFields();
            result = "{";
            for (Field f : fields) {
                Object value = null;
                try {
                    value = f.get(o);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                result += "\"" + f.getName() + "\":" + serializeJson(value);
            }
            result = "}";
        }
        return result;
    }

    private static BigDecimal getBigDecimal(String st) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        try {
            BigDecimal bigDecimal = (BigDecimal) decimalFormat.parse(st);
            return bigDecimal;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new BigDecimal(0);
    }
    public static <T> T getObject(Object jObj, Class<T> tClass) {
        T t = null;
        if (tClass.isPrimitive()) {
            if (tClass.isAssignableFrom(byte.class)) {
                if (jObj.getClass() == tClass) {
                    t = (T) jObj;
                } else {
                    Object v = Byte.parseByte(jObj.toString());
                    t = (T) v;
                }
            } else if (tClass.isAssignableFrom(int.class)) {
                if (jObj.getClass() == tClass) {
                    t = (T) jObj;
                } else {
                    Object v = Integer.parseInt(jObj.toString());
                    t = (T) v;
                }
            } else if (tClass.isAssignableFrom(long.class)) {
                if (jObj.getClass() == tClass) {
                    t = (T) jObj;
                } else {
                    Object v = Long.parseLong(jObj.toString());
                    t = (T) v;
                }
            } else if (tClass.isAssignableFrom(double.class)) {
                if (jObj.getClass() == tClass) {
                    t = (T) jObj;
                } else {
                    Object v = Double.parseDouble(jObj.toString());
                    t = (T) v;
                }
            } else if (tClass.isAssignableFrom(boolean.class)) {
                Boolean value = !((jObj.toString() == "0" || jObj.toString() == "False" || jObj.toString() == "FALSE" || jObj.toString() == "false"));
                t = (T) value;
            }
        } else if (tClass.isAssignableFrom(Byte.class)) {
            if (jObj instanceof Byte) {
                t = (T) jObj;
            } else {
                Object v = Byte.parseByte(jObj.toString());
                t = (T) v;
            }
        } else if (tClass.isAssignableFrom(Integer.class)) {
            if (jObj instanceof Integer) {
                t = (T) jObj;
            } else {
                Object v = Integer.parseInt(jObj.toString());
                t = (T) v;
            }
        } else if (tClass.isAssignableFrom(Long.class)) {
            if (jObj instanceof Long) {
                t = (T) jObj;
            } else {
                Object v = Long.parseLong(jObj.toString());
                t = (T) v;
            }
        } else if (tClass.isAssignableFrom(Double.class)) {
            if (jObj instanceof Double) {
                t = (T) jObj;
            } else {
                Object v = Double.parseDouble(jObj.toString());
                t = (T) v;
            }
        } else if (tClass.isAssignableFrom(String.class)) {
            t = (T) jObj;
        } else if (tClass.isAssignableFrom(Boolean.class)) {
            Boolean value = !((jObj.toString() == "0" || jObj.toString() == "False" || jObj.toString() == "FALSE" || jObj.toString() == "false"));
            t = (T) value;
        } else if (tClass.isAssignableFrom(BigDecimal.class)) {
            t = (T) getBigDecimal(jObj.toString());
        } else if (tClass.isArray()) {
            JSONArray jarray = (JSONArray) jObj;
            Class elementType = tClass.getComponentType();
            Object array = Array.newInstance(elementType, jarray.length());
            for (int i = 0; i < jarray.length(); i++) {
                try {
                    Array.set(array, i, getObject(jarray.get(i), elementType));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            t = (T) array;
        } else {
            try {
                t = (T) tClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (t instanceof List<?> || t instanceof List<?>) {
                Method add = getMethod("add", tClass);
                if (add != null) {
                    JSONArray jarray = (JSONArray) jObj;
                    //Type elementType=  add.getParameterTypes()[0];
                    //Class elementClass=(Class) elementType;
                    for (int i = 0; i < jarray.length(); i++) {
                        try {
                            Object element = jarray.get(i);//getObject(jarray.get(i), elementClass);
                            try {
                                add.invoke(t, element);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else if (t instanceof Dictionary<?, ?> || t instanceof Map<?, ?>) {
                Method add = getMethod("put", tClass);
                if (add != null) {
                    JSONObject jsonObject = (JSONObject) jObj;
                    Iterator<String> keys = jsonObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        try {
                            Object element = jsonObject.get(key);
                            try {
                                add.invoke(t, key, element);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                JSONObject jsonObject = (JSONObject) jObj;
                Field[] fields = tClass.getFields();
                for (Field f : fields) {
                    if (jsonObject.has(f.getName())) {
                        try {
                            Object fvalue = getObject(jsonObject.get(f.getName()), f.getType());
                            try {
                                f.set(t, fvalue);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return t;
    }

    private static Method getMethod(String name, Class tClass) {
        Method[] methods = tClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName() == name) {
                return methods[i];
            }
        }
        return null;
    }

    public static void requestResetPassword(String usernameOrEmail, final IGrdStringCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        pars.put("email", usernameOrEmail);
        requestAsyncTask("requestresetpassword", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (jsonObject.has("message")) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message);
                }
            }
        });
    }

    public static void getAddressQRCode(String address, final IGrdImageCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("text", "gamereward:" + address);
        requestAsyncTask("qrcode", params, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                data = data.replace("data:image/image/png;base64,", "");
                int error = 0;
                String message = "";
                Bitmap result = null;
                if (data.length() > 0) {
                    try {
                        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        result = decodedByte;
                    } catch (Exception e) {
                        error = 1;
                        message = "";

                    }
                }
                if (callBack != null) {
                    callBack.OnFinished(error, message, result);
                }
            }
        });
    }

    public static void register(String username, String password, String email, String userdata, final IGrdStringCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", md5(password));
        params.put("userdata", userdata);
        params.put("email", email);
        requestAsyncTask("createaccount", params, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject obj = getJsonObject(data);
                int error = 100;
                String message = "";
                try {
                    error = obj.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    message = obj.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (callBack != null) {
                    callBack.OnFinished(error, message);
                }
            }
        });
    }

    public static void login(String username, String password, String otp, final IGrdStringCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", md5(password));
        params.put("otp", otp);
        requestAsyncTask("login", params, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject obj = getJsonObject(data);
                int error = 100;
                try {
                    error = obj.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String result = null;
                if (error == 0) {
                    _user = getObject(obj, GrdUser.class);
                    try {
                        _user.otp = obj.getInt("otpoptions") != 0;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        userToken = obj.getString("token");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result = "";
                } else {
                    try {
                        result = obj.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callBack != null) {
                    callBack.OnFinished(error, result);
                }
            }
        });
    }

    public static void enableOtp(final boolean enable, String otp, final IGrdStringCallBack callBack) {
        HashMap<String, String> params = new HashMap<>();
        params.put("otpoptions", enable ? "1" : "0");
        params.put("otp", otp);
        requestAsyncTask("enableotp", params, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject obj = getJsonObject(data);
                int error = 100;
                try {
                    error = obj.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String result = null;
                if (error == 0) {
                    _user.otp = enable;
                    result = "";
                } else {
                    try {
                        result = obj.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callBack != null) {
                    callBack.OnFinished(error, result);
                }
            }
        });
    }

    public static void requestEnableOtp(final IGrdImageCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        requestAsyncTask("requestotp", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                Bitmap bitmap = null;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error != 0) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        message = jsonObject.getString("secret");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String qrcode = "";
                    try {
                        qrcode = jsonObject.getString("qrcode");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (qrcode.length() > 0) {
                        try {
                            qrcode = qrcode.replace("data:image/image/png;base64,", "");
                            byte[] decodedString = Base64.decode(qrcode, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            bitmap = decodedByte;
                        } catch (Exception e) {

                        }
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, bitmap);
                }
            }
        });
    }

    public static void getTransactions(int start, int count, final IGrdTransactionCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        pars.put("start", start + "");
        pars.put("count", count + "");
        requestAsyncTask("transactions", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                GrdTransaction[] transactions = null;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error != 0) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        transactions = getObject(jsonObject.get("transactions"), GrdTransaction[].class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, transactions);
                }
            }
        });
    }

    public static void getTransactionCount(final IGrdIntCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        requestAsyncTask("counttransactions", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                int count = 0;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error != 0) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        count = jsonObject.getInt("count");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, count);
                }
            }
        });
    }

    public static void getUserSessionData(String store, String[] keys, int start, int count, final IGrdSessionDataCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        pars.put("store", store);
        String sKey = "";
        for (int i = 0; i < keys.length; i++) {
            sKey += "," + keys[i];
        }
        if (sKey.length() > 0) {
            sKey = sKey.substring(1);
        }
        pars.put("keys", sKey);
        pars.put("start", start + "");
        pars.put("count", count + "");
        requestAsyncTask("getusersessiondata", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                GrdSessionData[] list = null;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error != 0) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        list = getObject(jsonObject.get("data"), GrdSessionData[].class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, list);
                }
            }
        });
    }

    public static void getLeaderBoard(String scoreType, int start, int count, final IGrdLeaderBoardCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        pars.put("scoretype", scoreType);
        pars.put("start", start + "");
        pars.put("count", count + "");
        requestAsyncTask("getleaderboard", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                String message = "";
                GrdLeaderBoard[] list = null;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error != 0) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        list = getObject(jsonObject.get("leaderboard"), GrdLeaderBoard[].class);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, list);
                }
            }
        });
    }

    public static void callServerScript(String scriptName, String functionName, Object[] parameters, final GrdCustomCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        String vars = serializeJson(parameters);
        pars.put("fn", functionName);
        pars.put("script", scriptName);
        pars.put("vars", vars);
        requestAsyncTask("callserverscript", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject obj = getJsonObject(data);
                int error = 100;
                try {
                    error = obj.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Object rdata = null;
                String message = "";
                if (error != 0) {
                    try {
                        message = obj.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        rdata = obj.get("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message, rdata);
                }
            }
        });
    }

    public static void updateBalance(final IGrdStringCallBack callback) {
        requestAsyncTask("accountbalance", null, true, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject result = getJsonObject(data);
                String r = null;
                int error = 100;
                try {
                    error = result.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error == 0) {
                    if (result.has("balance")) {
                        try {
                            getUser().balance = getBigDecimal(result.getString("balance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    r = "";
                } else {
                    try {
                        r = result.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, r);
                }
            }
        });
    }

    public static void transfer(String address, final BigDecimal money, final IGrdStringCallBack callback) {
        HashMap<String, String> pars = new HashMap<String, String>();
        pars.put("to", address);
        pars.put("value", money.toPlainString());
        requestAsyncTask("transfer", pars, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject result = getJsonObject(data);
                int error = 100;
                try {
                    error = result.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (error == 0) {
                    if (!result.has("balance")) {
                        getUser().balance.subtract(money);
                    } else {
                        try {
                            getUser().balance = getBigDecimal(result.getString("balance"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (callback != null) {
                    String message = "";
                    if (error != 0) {
                        try {
                            message = result.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (callback != null) {
                        callback.OnFinished(error, message);
                    }
                }
            }
        });
    }

    /// <summary>
    /// LogOut user from system.
    /// </summary>
    /// <param name="callback"></param>
    public void logOut(final IGrdStringCallBack callback) {
        requestAsyncTask("logout", null, false, new INetworkCallBack() {
            @Override
            public void OnRespose(String data) {
                JSONObject jsonObject = getJsonObject(data);
                int error = 100;
                try {
                    error = jsonObject.getInt("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String message = "";
                if (jsonObject.has("message")) {
                    try {
                        message = jsonObject.getString("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (callback != null) {
                    callback.OnFinished(error, message);
                }
            }
        });
    }
}
