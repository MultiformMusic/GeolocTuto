package net.multiform_music.ifeelrun.helper;

import android.util.Log;

import net.multiform_music.ifeelrun.bean.RunningStepBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;

import javax.net.ssl.SSLException;

/**
 * Created by Michel on 01/05/2017.
 *
 */

public final class ElevationHelper {

    private static int GetHttpToServer(String urlLink, StringBuffer response) {

        try {

            URL obj = new URL(urlLink);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 2;
        } catch (NoRouteToHostException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 3;
        } catch (SocketTimeoutException ex){
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 4;
        } catch (SSLException ex){
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 5;

        } catch (IOException ex) {
            Log.e("GetHttp", Log.getStackTraceString(ex));
            return 6;
        } catch (Exception e){
            Log.e("GetHttp", Log.getStackTraceString(e));
            return 7;

        }
        return 0;
    }

    public static double getElevationFromLocation(RunningStepBean runningStepBean) {

        int ret;
        double elevation = 0;
        String ApiKey = "AIzaSyDsxpVlY2m0Fp8bS0dUhxZvKboxz7BFDXA";   //Your Api Key
        String url = "https://maps.googleapis.com/maps/api/elevation/json?locations=" + runningStepBean.getLatitude() + "," + runningStepBean.getLongitude() + "&key="+ApiKey;
        StringBuffer response = new StringBuffer();

        ret = GetHttpToServer(url, response);

        if (ret == 0)
        {
            try {
                JSONObject jsonObj = new JSONObject(response.toString());
                JSONArray resultEl = jsonObj.getJSONArray("results");
                JSONObject current = resultEl.getJSONObject(0);
                elevation = Double.parseDouble(current.getString("elevation"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return elevation;
    }
}
