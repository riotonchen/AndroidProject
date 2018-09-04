package com.example.admin.project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class HttpUtils {
    /**
     * 發送POST請求到伺服器並返回伺服器訊息
     *
     * @param path   POST的路徑
     * @param params 請求體內容
     *               //@param encode 編碼格式
     * @return 伺服器返回訊息
     */
    public static JSONObject GetToken(String path, Map<String, String> params) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);    //路徑
            //byte[] data = GetRequestData(params, encode).toString().getBytes();
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);  // 設置連接超時時間
            httpURLConnection.setDoInput(true);         // 打開輸入流，以便從伺服器獲取數據
            httpURLConnection.setDoOutput(true);        // 打開輸出流，以便向伺服器提交數據
            httpURLConnection.setRequestMethod("POST"); // 設置以POST方式提交數據
            httpURLConnection.setUseCaches(false);      // 使用POST方式不能使用緩存
            // 設置請求體的類型是文本類型
            //httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 設置請求體的類型是JSON
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            // 設置請求體的長度
            //httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            // 獲得輸入流，向伺服器寫入數據
            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            DataOutputStream writer = new DataOutputStream(outputStream);
            writer.writeBytes(MapToJSONString(params));
            writer.flush();
            writer.close();
            //outputStream.write(data);
            //outputStream.flush();                       // 重要！flush()之後才會寫入

            int responseCode = httpURLConnection.getResponseCode();     // 獲得伺服器回應碼
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK: //HTTP Status Code=200
                    inputStream = httpURLConnection.getInputStream();   // 獲得伺服器回傳的資料
                    return DealResponseResultToJSON(inputStream, responseCode); // 處理伺服器回應結果
                default:    //其他訊息(錯誤)
                    inputStream = httpURLConnection.getErrorStream();   //注意如果連結(傳送)失敗但伺服器仍然發送了有用數據，則回傳錯誤流
                    return DealResponseResultToJSON(inputStream, responseCode);
            }
            /*
             * 200 OK 用於請求成功 。GET 檔案成功，PUT， PATCH 更新成功
             * 201 Created 用於請求 POST 成功建立資料。
             * 204 No Content 用於請求 DELETE 成功。
             * 400 Bad Request 用於請求 API 參數不正確的情況，例如傳入的 JSON 格式錯誤。
             * 401 Unauthorized 用於表示請求的 API 缺少身份驗證資訊。
             * 403 Forbidden 用於表示該資源不允許特定用戶訪問。
             * 404 Not Found 用於表示請求一個不存在的資源。
             * 500 Internal Server Error
             * */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;  //只有程式錯誤時才會執行到這行
    }

    public static JSONObject Register(String path, Map<String, String> params) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);    //路徑
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);  // 設置連接超時時間
            httpURLConnection.setDoInput(true);         // 打開輸入流，以便從伺服器獲取數據
            httpURLConnection.setDoOutput(true);        // 打開輸出流，以便向伺服器提交數據
            httpURLConnection.setRequestMethod("POST"); // 設置以POST方式提交數據
            httpURLConnection.setUseCaches(false);      // 使用POST方式不能使用緩存
            // 設置請求體的類型是JSON
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            // 獲得輸入流，向伺服器寫入數據
            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            DataOutputStream writer = new DataOutputStream(outputStream);
            writer.writeBytes(MapToJSONString(params));
            writer.flush();
            writer.close();

            int responseCode = httpURLConnection.getResponseCode();     // 獲得伺服器回應碼
            switch (responseCode) {
                case HttpURLConnection.HTTP_CREATED:   //HTTP Status Code=201
                    inputStream = httpURLConnection.getInputStream();
                    return DealResponseResultToJSON(inputStream, responseCode);
                default:    //其他訊息(錯誤)
                    inputStream = httpURLConnection.getErrorStream();   //注意如果連結(傳送)失敗但伺服器仍然發送了有用數據，則回傳錯誤流
                    return DealResponseResultToJSON(inputStream, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    public static JSONObject RefreshToken(String path, Map<String, String> params) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);    //路徑
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);  // 設置連接超時時間
            httpURLConnection.setDoInput(true);         // 打開輸入流，以便從伺服器獲取數據
            httpURLConnection.setDoOutput(true);        // 打開輸出流，以便向伺服器提交數據
            httpURLConnection.setRequestMethod("POST"); // 設置以POST方式提交數據
            httpURLConnection.setUseCaches(false);      // 使用POST方式不能使用緩存
            // 設置請求體的類型是JSON
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            // 獲得輸入流，向伺服器寫入數據
            OutputStream outputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            DataOutputStream writer = new DataOutputStream(outputStream);
            writer.writeBytes(MapToJSONString(params));
            writer.flush();
            writer.close();

            int responseCode = httpURLConnection.getResponseCode();     // 獲得伺服器回應碼
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK: //HTTP Status Code=200
                    inputStream = httpURLConnection.getInputStream();   // 獲得伺服器回傳的資料
                    return DealResponseResultToJSON(inputStream, responseCode); // 處理伺服器回應結果
                default:    //其他訊息(錯誤)
                    inputStream = httpURLConnection.getErrorStream();   //注意如果連結(傳送)失敗但伺服器仍然發送了有用數據，則回傳錯誤流
                    return DealResponseResultToJSON(inputStream, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    public static JSONArray Get(String path, String token) { //注意回傳JSONArray
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(path);    //路徑
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);  // 設置連接超時時間
            httpURLConnection.setDoInput(true);         // 打開輸入流，以便從伺服器獲取數據
            httpURLConnection.setDoOutput(false);        // 注意，必須關閉輸出流，否則伺服器會誤判
            httpURLConnection.setRequestMethod("GET"); // 設置以GET方式提交數據
            // 設置請求體的類型是JSON
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Authorization", "JWT " + token);  //注意空格

            int responseCode = httpURLConnection.getResponseCode();     // 獲得伺服器回應碼
            switch (responseCode) {
                case HttpURLConnection.HTTP_OK: //HTTP Status Code=200
                    inputStream = httpURLConnection.getInputStream();   // 獲得伺服器回傳的資料
                    return DealResponseResultToJSONArray(inputStream, responseCode); // 處理伺服器回應結果(JSONArray)
                default:    //其他訊息(錯誤)
                    inputStream = httpURLConnection.getErrorStream();   //注意如果連結(傳送)失敗但伺服器仍然發送了有用數據，則回傳錯誤流
                    return DealResponseResultToJSONArray(inputStream, responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    /**
     * 轉成JSON格式
     * https://dotblogs.com.tw/newmonkey48/2017/09/21/152425
     *
     * @param params 來源資料
     * @return 回傳資料
     */
    private static String MapToJSONString(Map<String, String> params) {
        JSONObject json = new JSONObject();
        for (String key : params.keySet()) {
            try {
                json.put(key, params.get(key));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return json.toString();
    }

    /**
     * 處理伺服器的回應結果（將輸入流轉換成字串)
     *
     * @param inputStream  伺服器的回應輸入流
     * @param responseCode 伺服器的回應碼
     * @return 伺服器回應結果轉成JSONObject
     */
    //這方法設計將所有的inputStream都轉成JSON加上responseCode回給其他程式處理
    private static JSONObject DealResponseResultToJSON(InputStream inputStream, int responseCode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        JSONObject jsonObj;
        if (inputStream == null) {
            try {
                jsonObj = new JSONObject();
                jsonObj.put("responseCode", responseCode);
                return jsonObj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);
                }
                String jsonStr = new String(byteArrayOutputStream.toByteArray());
                Object obj = new JSONTokener(jsonStr).nextValue();
                if (obj instanceof JSONObject) {  //再檢查一次
                    jsonObj = new JSONObject(jsonStr);
                    jsonObj.put("responseCode", responseCode);   //加入回應碼，不需要可刪除
                } else {  //其他格式
                    jsonObj = null;
                }
                return jsonObj;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 處理伺服器的回應結果（將輸入流轉換成字串)
     *
     * @param inputStream  伺服器的回應輸入流
     * @param responseCode 伺服器的回應碼
     * @return 伺服器回應結果轉成JSONArray
     */
    //回傳是JSONArray時
    private static JSONArray DealResponseResultToJSONArray(InputStream inputStream, int responseCode) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len;
        JSONObject jsonObj;
        JSONArray jsonArray = null;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
            String jsonStr = new String(byteArrayOutputStream.toByteArray());
            Object obj = new JSONTokener(jsonStr).nextValue();
            if (obj instanceof JSONArray) { //正確訊息為JSONArray
                jsonObj = new JSONObject();
                jsonObj.put("responseCode", responseCode);  //加入回應碼，不需要可刪除

                jsonArray = new JSONArray(jsonStr); //放入訊息
                jsonArray.put(jsonObj); //放入回應碼
            } else if (obj instanceof JSONObject) {   //其他格式(錯誤訊息為JSON)
                jsonObj = new JSONObject(jsonStr);
                jsonObj.put("responseCode", responseCode);  //加入回應碼

                jsonArray.put(jsonObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;

    }

    public static String[] SendGetRequest(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add("發票"+i);
                    arrayList.add("總金額: "+new JSONObject(jsonStr).getString("amount"));
                    arrayList.add("日期: "+year[i]+"/"+month[i]+"/"+date[i]);
                    arrayList.add("發票號碼: "+invoice[i]);
                    arrayList.add("商家: "+new JSONObject(jsonStr).getString("sellerName"));
                    arrayList.add("--------------------");
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]






        /*ArrayList<String> arrayList = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼
                for (String elem : colName) {
                    arrayList.add(new JSONObject(jsonStr).getString(elem));

                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]*/
    }

    public static String[] SendGetRequest2(String path, String colName) {//獲得發票號碼
        ArrayList<String> arrayList = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());
                }

                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestDate(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("invDate"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestAmount(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("amount"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }

    public static String[] SendGetRequestNumber(String path, String colName) {
        ArrayList<String> arrayList1 = new ArrayList<>();ArrayList<String> arrayList2 = new ArrayList<>();ArrayList<String> arrayList3 = new ArrayList<>();ArrayList<String> arrayList4 = new ArrayList<>();
        HttpGet httpGet = new HttpGet(path);                           //創建一个GET方式的HttpRequest對象
        //DefaultHttpClient httpClient = new DefaultHttpClient();        //創建一个默認的HTTP客户端
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);               //執行GET方式的HTTP请求
            int responseCode = httpResponse.getStatusLine().getStatusCode();        //獲得伺服器的回應碼
            if (responseCode == HttpStatus.SC_OK) {
                String jsonStr = EntityUtils.toString(httpResponse.getEntity()); //注意編碼    // String details = new JSONObject(JsonText).getJSONArray("details").getJSONObject(1).get("invNum").toString()
                JSONArray myJSONArray=new JSONObject(jsonStr).getJSONArray("details");

                for(int i=0;i<myJSONArray.length();i++) {
                    arrayList1.add(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).get(colName).toString());////發票明細URL需要的參數(發票號碼)
                    String sYear=(new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("year"));//發票明細URL需要的參數(發票日期-年)
                    int iYear =Integer.valueOf(sYear);
                    iYear=1911+iYear;//民國轉西元
                    sYear=Integer.toString(iYear);
                    arrayList2.add(sYear);
                    arrayList3.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("month")));//發票明細URL需要的參數(發票日期-月)
                    arrayList4.add((new JSONObject(jsonStr).getJSONArray("details").getJSONObject(i).getJSONObject("invDate").getString("date")));//發票明細URL需要的參數(發票日期-日)
                }
                Log.i("GET_RESULT", String.valueOf(responseCode));     //輸出回傳狀態
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] invoice =arrayList1.toArray(new String[arrayList1.size()]);
        String[] year =arrayList2.toArray(new String[arrayList1.size()]);
        String[] month =arrayList3.toArray(new String[arrayList1.size()]);
        String[] date =arrayList4.toArray(new String[arrayList1.size()]);
        ArrayList<String> arrayList = new ArrayList<>();
        Long tsLong = System.currentTimeMillis()/1000+25;
        final String ts = tsLong.toString();
        for(int i=0;i<invoice.length;i++) {
            httpGet = new HttpGet("https://api.einvoice.nat.gov.tw/PB2CAPIVAN/invServ/InvServ?version=0.3&cardType=3J0002&cardNo=/1P685U2&expTimeStamp=" + ts + "&action=carrierInvDetail&timeStamp=" + ts + "&invNum="+invoice[i]+"&invDate="+year[i]+"/"+month[i]+"/"+date[i]+"&uuid=003&appID=EINV4201804318254&cardEncrypt=0130");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                if (responseCode == HttpStatus.SC_OK) {
                    String jsonStr = EntityUtils.toString(httpResponse.getEntity());
                    arrayList.add(new JSONObject(jsonStr).getString("invNum"));
                }


            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return arrayList.toArray(new String[arrayList.size()]);//ArrayList轉成String[]
    }
}
