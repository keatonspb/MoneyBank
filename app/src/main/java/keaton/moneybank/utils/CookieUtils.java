package keaton.moneybank.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.cookie.Cookie;

import java.util.Date;
import java.util.List;

/**
 * Created by default on 18.03.2017.
 */

public class CookieUtils {

    private static CookieUtils instance;
    private CookieStore cookieStore;


    public static CookieUtils getInstance() {
        if(instance == null) {
            instance = new CookieUtils();
        }
        return instance;

    }

    public CookieUtils() {

    }
    /**
     * Passing cookie from DefaultHttpClient to WebView
     */
    public void saveCookie(DefaultHttpClient httpClient)
    {
        cookieStore = httpClient.getCookieStore();
//        Cookie sessionCookie = null;
//        CookieManager cookieManager = CookieManager.getInstance();
//        for (int i = 0; i < cookies.size(); i++)
//        {
//            sessionCookie = cookies.get(i);
//            if (sessionCookie != null)
//            {
//                String cookieString = sessionCookie.getName() + "=" + sessionCookie.getValue() + "; domain="
//                        + sessionCookie.getDomain();
//                cookieManager.setCookie(sessionCookie.getDomain(), cookieString);
//                Log.d("DEBUG", cookieString);
//            }
//        }
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
