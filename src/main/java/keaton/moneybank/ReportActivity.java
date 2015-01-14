package keaton.moneybank;

import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import keaton.moneybank.adapter.AddAdapter;
import keaton.moneybank.db.DatabaseHelper;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.ReasonItem;


public class ReportActivity extends ActionBarActivity {
    private static int SHOW_LOADING = 0;
    private static int SHOW_FORM = 1;
    private ViewPager viewPager;
    private ViewFlipper flipper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setTitle("Потратить");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        flipper = (ViewFlipper) findViewById(R.id.flipper);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new AddAdapter(getSupportFragmentManager()));
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    getSupportActionBar().setTitle("Потратить");

                } else {
                    getSupportActionBar().setTitle("Пополнить");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        updateReasons();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void updateReasons() {
        Log.d("MONEYLOG", "updateReasons");
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                HttpClient client = new DefaultHttpClient();
                StringBuilder url = new StringBuilder("http://money.discode.ru/phone/info.php");
                HttpGet get = new HttpGet(url.toString());
                try {
                    HttpResponse r = client.execute(get);
                    HttpEntity e = r.getEntity();

                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    Log.d("MONEYLOG", data);
                    JSONArray rows = object.getJSONArray("reasons");
                    if(rows == null) {
                        throw new Exception("Нет данных");
                    }
                    ArrayList<ReasonItem> result = new ArrayList<>();
                    DatabaseHelper helper = OpenHelperManager.getHelper(ReportActivity.this, DatabaseHelper.class);
                    ReasonDao reasonDao = helper.getReasonDao();
                    DeleteBuilder deleteBuilder = reasonDao.deleteBuilder();
                    deleteBuilder.delete();
                    for(int i = 0; i < rows.length(); i++) {
                        JSONObject json = rows.optJSONObject(i);
                        ReasonItem reason = new ReasonItem();
                        reason.setId(json.getInt("id"));
                        reason.setName(json.getString("name"));
                        reason.setType(json.getString("source"));
                        Log.d("MONEYLOG", "Add reason "+reason.getName()+", "+reason.getType());
                        reasonDao.createOrUpdate(reason);
                    }
                    return result;

                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                flipper.setDisplayedChild(SHOW_FORM);
            }
        }.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();//finish your activity
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}