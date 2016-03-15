package keaton.moneybank;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.crashlytics.android.Crashlytics;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import keaton.moneybank.adapter.RowsListAdapterWithSwipe;
import keaton.moneybank.entity.DataItem;
import keaton.moneybank.utils.Tools;


public class ListActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private String sum;
    private ArrayList allPosts;
    private ImageButton add_button;
    private ViewFlipper flipper;
    private RecyclerView list;
    private RowsListAdapterWithSwipe listAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeLayout;
    private android.support.v7.app.ActionBar actionBar;

    private static int SHOW_LOADING = 0;
    private static int SHOW_LIST = 1;
    private static int SHOW_ERROR = 2;

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

        getData();

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
        final String type = item.getReasonAsString();

        Log.d("deleteItem", "position: "+id);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                HttpClient client = new DefaultHttpClient();
                Log.d("deleteItem", "http://money.discode.ru/phone/delete.php?id="+id+"&type="+type);
                StringBuilder url = new StringBuilder("http://money.discode.ru/phone/delete.php?id="+id+"&type="+type);
                HttpGet get = new HttpGet(url.toString());
                try {
                    client.execute(get);
                } catch (IOException e) {
                    e.printStackTrace();
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
                HttpClient client = new DefaultHttpClient();
                StringBuilder url = new StringBuilder("http://money.discode.ru/phone/list.php");
                HttpGet get = new HttpGet(url.toString());
                try {
                    HttpResponse r = client.execute(get);
                    HttpEntity e = r.getEntity();

                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    Log.d("MONEYLOG", data);
                    sum = object.getString("money");
                    JSONArray rows = object.getJSONArray("data");
                    if(rows == null) {
                        throw new Exception("Нет данных");
                    }
                    ArrayList<DataItem> result = new ArrayList<DataItem>();

                    for(int i = 0; i < rows.length(); i++) {
                        JSONObject json = rows.optJSONObject(i);
                        DataItem row = new DataItem();
                        row.id = json.getInt("id");
                        row.sum = json.getString("sum");
                        row.caption = json.getString("caption");
                        row.reason  = json.getString("reason");
                        row.date  = json.getString("date");
                        if(json.getString("type").equals("income")) {
                            row.type = DataItem.TYPE_INCOME;
                        } else {
                            row.type = DataItem.TYPE_EXPENSE;
                            if(!json.getString("credit").equals("0")) {
                                row.credit = true;
                            }
                        }

                        result.add(row);
                        Log.d("MONEYLOG", json.getString("sum"));
                    }
                    return result;

                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if(o instanceof Exception) {
                    flipper.setDisplayedChild(SHOW_ERROR);
                    Toast.makeText(ListActivity.this, ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if(sum != null) {
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


    public static String formatSum(String sum) {
         return Tools.numberFormat(Long.valueOf(sum))+" "+Tools.nget_text(Integer.valueOf(sum),"рубль", "рубля", "рублей");
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
