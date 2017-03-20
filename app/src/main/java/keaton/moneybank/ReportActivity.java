package keaton.moneybank;

import android.content.Intent;
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
import com.j256.ormlite.table.TableUtils;

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
import keaton.moneybank.db.dao.PopularItemDao;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.DataItem;
import keaton.moneybank.entity.PopularItem;
import keaton.moneybank.entity.ReasonItem;
import keaton.moneybank.utils.CookieUtils;


public class ReportActivity extends ActionBarActivity {
    public static String ACTION_SHOW_INCOME = "ShowIncome";
    public static String ACTION_REQUEST_SUM = "ActionRequestSum";
    public static String ACTION_REQUEST_REASON = "ActionRequestReason";
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

        Intent intent = getIntent();

        flipper = (ViewFlipper) findViewById(R.id.flipper);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new AddAdapter(getSupportFragmentManager()));



        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    getSupportActionBar().setTitle("Потратить");

                } else {
                    getSupportActionBar().setTitle("Пополнить");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if(intent.getAction() == ACTION_SHOW_INCOME) {
            viewPager.setCurrentItem(1);
        }




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

                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(CookieUtils.getInstance().getCookieStore());
                StringBuilder url = new StringBuilder("http://m.discode.ru/api/reasons?with_popular=1");
                HttpGet get = new HttpGet(url.toString());
                try {
                    HttpResponse r = client.execute(get);
                    CookieUtils.getInstance().saveCookie(client);
                    HttpEntity e = r.getEntity();

                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    object = object.getJSONObject("data");
                    Log.d("MONEYLOG", data);
                    JSONArray rows = object.getJSONArray("list");
                    if(rows == null) {
                        throw new Exception("Нет данных");
                    }
                    ArrayList<ReasonItem> result = new ArrayList<>();
                    DatabaseHelper helper = OpenHelperManager.getHelper(ReportActivity.this, DatabaseHelper.class);
                    ReasonDao reasonDao = helper.getReasonDao();
                    TableUtils.clearTable(helper.getConnectionSource(), ReasonItem.class);
                    for(int i = 0; i < rows.length(); i++) {
                        JSONObject json = rows.optJSONObject(i);
                        ReasonItem reason = new ReasonItem();
                        reason.setId(json.getInt("id"));
                        reason.setName(json.getString("name"));
                        reason.setType(json.getString("type"));
                        reason.setRating(json.optInt("rating"));
                        Log.d("MONEYLOG", "Add reason "+reason.getName()+", "+reason.getType());
                        reasonDao.createOrUpdate(reason);
                    }

                    //Частые затраты
                    JSONArray expenses = object.getJSONArray("popular_expenses");
                    PopularItemDao popularItemDao = helper.getPopularItemDao();
                    TableUtils.clearTable(helper.getConnectionSource(), PopularItem.class);
                    if(expenses != null) {
                        for(int i = 0; i < expenses.length(); i++) {
                            JSONObject json = expenses.optJSONObject(i);
                            PopularItem popularItem = new PopularItem();
                            popularItem.setIdReason(json.getInt("reason_id"));
                            popularItem.setReasonName(json.getString("name"));
                            popularItem.setSum(json.getInt("value"));
                            popularItemDao.createOrUpdate(popularItem);
                        }
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
