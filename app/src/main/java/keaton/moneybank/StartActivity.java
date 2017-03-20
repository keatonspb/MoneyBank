package keaton.moneybank;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import keaton.moneybank.utils.CookieUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StartActivity extends Activity {

    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private View mControlsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_start);
        login();
    }


    private void login() {

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                HttpContext ctx = new BasicHttpContext();
                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(CookieUtils.getInstance().getCookieStore());
                StringBuilder url = new StringBuilder("http://m.discode.ru/api/login");
                HttpGet get = new HttpGet(url.toString());
                try {
                    HttpResponse r = client.execute(get, ctx);
                    CookieUtils.getInstance().saveCookie(client);
                    HttpEntity e = r.getEntity();


                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    Log.d("MONEYLOG", data);


                    return object.getBoolean("success");

                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) {
                    Toast.makeText(StartActivity.this, ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if((boolean)o) {
                        startNextActivity();
                    } else {
                        Toast.makeText(StartActivity.this, "Не удалось авторизоваться", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }.execute();
    }

    private void startNextActivity() {
        Intent intent = new Intent(StartActivity.this, ListActivity.class);
        startActivity(intent);
    }
}

