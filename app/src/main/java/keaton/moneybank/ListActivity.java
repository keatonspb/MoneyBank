package keaton.moneybank;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import keaton.moneybank.adapter.RowsListAdapterWithSwipe;
import keaton.moneybank.db.DatabaseHelper;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.DataItem;
import keaton.moneybank.entity.ReasonItem;
import keaton.moneybank.utils.CookieUtils;
import keaton.moneybank.utils.Tools;


public class ListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int REQUEST_SPEECH_COMPLETE_CODE = 1;
    private Long sum;
    private ArrayList allPosts;
    private ImageButton add_button;
    private ImageButton voice_button;
    private ViewFlipper flipper;
    private RecyclerView list;
    private RowsListAdapterWithSwipe listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeLayout;
    private android.support.v7.app.ActionBar actionBar;

    private static int SHOW_LOADING = 0;
    private static int SHOW_LIST = 1;
    private static int SHOW_ERROR = 2;

    private String regex ="^[0-9]+$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_list);
        flipper = (ViewFlipper) findViewById(R.id.flipper);
        actionBar = getSupportActionBar();
        allPosts = new ArrayList();
        list = (RecyclerView) this.findViewById(R.id.content_list);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        listAdapter = new RowsListAdapterWithSwipe(allPosts, new DeleteCallBack());
        list.setAdapter(listAdapter);
        list.setHasFixedSize(true);
        list.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(mLayoutManager);

        add_button = (ImageButton) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListActivity.this, ReportActivity.class);
                ListActivity.this.startActivity(intent);
            }
        });
        voice_button = (ImageButton) findViewById(R.id.voice_button);
        voice_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isConnected()) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_SPEECH_COMPLETE_CODE);
                } else {
                    Toast.makeText(getApplicationContext(), "Plese Connect to Internet", Toast.LENGTH_LONG).show();
                }
            }
        });
        getData();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_SPEECH_COMPLETE_CODE) {
            ArrayList<String> matches_text = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String regex = "(\\d+).+на\\s(.+)";
            String sum = "";
            ArrayList<String> reasons = new ArrayList<String>();
            for (String phrase : matches_text) {
                Log.d("MONEYLOG PGRASE", phrase);
                Matcher matcher = Pattern.compile(regex).matcher(phrase);
                if (matcher.matches()) {
                    sum = matcher.group(1);
                    String reason = matcher.group(2);
                    reasons.add(reason.trim());
                    Log.d("MONEYLOG SUM", sum);
                    Log.d("MONEYLOG REASON", reason);
                    Log.d("MONEYLOG ", "   ");
                }
            }
            if (reasons.size() != 0) {
                findReasonFromDb(reasons, sum);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public class DeleteCallBack {
        public void deleteItemCallback(int pos) {
            DataItem deleteItem = (DataItem) allPosts.get(pos);
            allPosts.remove(pos);
            listAdapter.notifyItemRemoved(pos);
            deleteItem(deleteItem);
        }
    }

    private void deleteItem(DataItem item) {
        final int id = item.id;

        Log.d("deleteItem", "position: " + id);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                HttpContext ctx = new BasicHttpContext();
                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(CookieUtils.getInstance().getCookieStore());
                StringBuilder url = new StringBuilder("http://m.discode.ru/api/delete_bill?id=" + id);
                HttpGet get = new HttpGet(url.toString());
                try {
                    client.execute(get);
                    CookieUtils.getInstance().saveCookie(client);
                } catch (IOException e) {
                    Log.e("Delete", e.getMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                getData();
            }
        }.execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        allPosts.clear();

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(CookieUtils.getInstance().getCookieStore());
                StringBuilder url = new StringBuilder("http://m.discode.ru/api/bills");
                HttpGet get = new HttpGet(url.toString());
                try {
                    HttpResponse r = client.execute(get);
                    HttpEntity e = r.getEntity();
                    Log.d("MONEYLOG", String.valueOf(r.getStatusLine().getStatusCode()));

                    CookieUtils.getInstance().saveCookie(client);

                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    Log.d("MONEYLOG", data);
                    JSONObject odata = object.getJSONObject("data");
                    sum = odata.getLong("debit");
                    JSONArray rows = odata.getJSONArray("list");
                    if (rows == null) {
                        throw new Exception("Нет данных");
                    }
                    ArrayList<DataItem> result = new ArrayList<DataItem>();

                    for (int i = 0; i < rows.length(); i++) {
                        JSONObject json = rows.optJSONObject(i);
                        DataItem row = new DataItem();
                        row.id = json.getInt("id");
                        row.sum = json.getLong("value");
                        row.caption = json.getString("description");
                        row.reason = json.getString("reason_name");
                        row.date = json.getString("created_at");
                        if (json.getString("type").equals("income")) {
                            row.type = DataItem.TYPE_INCOME;
                        } else {
                            row.type = DataItem.TYPE_EXPENSE;
                            if (!json.getString("credit").equals("0")) {
                                row.credit = true;
                            }
                        }

                        result.add(row);
                    }
                    return result;

                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                if (o instanceof Exception) {
                    flipper.setDisplayedChild(SHOW_ERROR);
                    Toast.makeText(ListActivity.this, ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (sum != null) {
                        actionBar.setTitle(getString(R.string.app_name) + " - " + formatSum(sum));
                    }
                    allPosts.addAll((ArrayList) o);
                    listAdapter.notifyDataSetChanged();

                    flipper.setDisplayedChild(SHOW_LIST);
                    swipeLayout.setRefreshing(false);
                }
            }
        }.execute();
    }

    private void findReasonFromDb(final ArrayList<String> reasons, final String sum) {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                List<ReasonItem> result = new ArrayList<>();
                DatabaseHelper helper = OpenHelperManager.getHelper(ListActivity.this, DatabaseHelper.class);
                try {
                    ReasonDao reasonDao = helper.getReasonDao();
                    QueryBuilder<ReasonItem, Integer> builder = reasonDao.queryBuilder();
                    builder.where().eq(ReasonItem.TYPE_FIELD, "expense");
                    builder.where().in(ReasonItem.NAME_FIELD, reasons);

                    result.addAll(reasonDao.query(builder.prepare()));
                    return result;
                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if(o instanceof List) {
                    Log.d("MONEYLOG", "Reasonsize: "+((List) o).size());
                    if(((List) o).size() == 1) {
                        ReasonItem reason = (ReasonItem) ((List) o).get(0);
                        Log.d("MONEYLOG", "Reason: " + reason.getName());
                        Intent intent = new Intent(ListActivity.this, ReportActivity.class);
                        intent.putExtra(ReportActivity.ACTION_REQUEST_SUM, sum);
                        intent.putExtra(ReportActivity.ACTION_REQUEST_REASON, reason.getId());
                        ListActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(ListActivity.this,  "Затрата не найдена", Toast.LENGTH_LONG).show();
                    }
                } else if(o instanceof Exception) {
                    Toast.makeText(ListActivity.this,  ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }


    public static String formatSum(Long sum) {
        return Tools.numberFormat(sum) + " " + Tools.nget_text(sum.intValue(), "рубль", "рубля", "рублей");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        getData();
    }
}
