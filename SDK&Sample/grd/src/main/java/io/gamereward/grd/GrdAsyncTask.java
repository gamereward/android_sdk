package io.gamereward.grd;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by Administrator on 4/16/2018.
 */

public class GrdAsyncTask extends AsyncTask<Map<String,String>,String,String> {
    private String url;
    private boolean isGet=false;
    private INetworkCallBack callBack;
    public GrdAsyncTask(String url,boolean isGet,String api_Id,String api_Key,String userToken,INetworkCallBack callBack){
        this.url=url;
        this.api_Id=api_Id;
        this.isGet=isGet;
        this.api_Key=api_Key;
        this.userToken=userToken;
        this.callBack=callBack;
    }
    @Override
    protected String doInBackground(Map<String, String>... params) {
        Map<String,String>data=null;
        if(params.length>0) {
            data=params[0];
        }
        if(isGet){
            return getData(url,data);
        }
        else{
            return postData(url,data);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        callBack.OnRespose(s);
    }

    private String api_Id = "";
    private String api_Key = "";
    private String userToken = "";


    private String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String getAppToken() {
        long t = System.currentTimeMillis() / 1000L;
        t = t / 15;
        int k = (int) (t % 20);
        int len = api_Key.length() / 20;
        String str = api_Key.substring(k * len, (k + 1) * len);
        str = GrdManager.md5(str+t);
        return str;
    }

    private String getSendData(Map<String, String> params) {
        String data = "api_id=" + api_Id + "&api_key=" + getAppToken();
        if (userToken.length() > 0) {
            data += "&token=" + userToken;
        }
        if (params != null) {
            for (String key : params.keySet()) {
                data += "&" + key + "=" + params.get(key);
            }
        }
        return data;
    }
    private HttpsURLConnection getConnection(String urlString){

// Create an SSLContext that uses our TrustManager
        SSLContext context = null;
        try {
            context = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            context.init(null, null, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        urlConnection.setSSLSocketFactory(context.getSocketFactory());
        return  urlConnection;
    }
    private String getData(String urlString, Map<String, String> params) {
        String data = getSendData(params);
        OutputStream out = null;
        try {
            HttpsURLConnection urlConnection=getConnection(urlString+"?"+data);
            urlConnection.setRequestMethod("GET");
            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String result = convertStreamToString(inputStream);
            return result;

        } catch (Exception e) {

        }
        return "";
    }
    private String postData(String urlString, Map<String, String> params) {
        String data = getSendData(params);
        OutputStream out = null;
        try {
            HttpsURLConnection urlConnection =getConnection(urlString);
            urlConnection.setRequestMethod("POST");
            out = new BufferedOutputStream(urlConnection.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();
            BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            String result = convertStreamToString(inputStream);
            return result;


        } catch (Exception e) {
            return "{\"error\":100,\"message\":\""+e.getMessage()+"\"}";
        }
    }
}
