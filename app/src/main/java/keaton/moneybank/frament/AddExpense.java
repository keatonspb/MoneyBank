package keaton.moneybank.frament;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import keaton.moneybank.R;
import keaton.moneybank.ReportActivity;
import keaton.moneybank.adapter.PopularAdapter;
import keaton.moneybank.adapter.ReasonAdapter;
import keaton.moneybank.db.DatabaseHelper;
import keaton.moneybank.db.dao.PopularItemDao;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.PopularItem;
import keaton.moneybank.entity.ReasonItem;
import keaton.moneybank.utils.CookieUtils;


public class AddExpense extends Fragment {

    private Activity ctx;
    private EditText sum_edit;
    private Spinner reason_spinner;
    private EditText caption;
    private CheckBox credit;
    private Button sendbutton;


    private ReasonAdapter adapter;
    private ArrayAdapter popularAdapter;
    private GridView popularGridView;


    public AddExpense() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = getActivity();



        sum_edit = (EditText) ctx.findViewById(R.id.add_summ);



        caption = (EditText) ctx.findViewById(R.id.add_caption);
        reason_spinner = (Spinner) ctx.findViewById(R.id.spinner_reasons);
        credit = (CheckBox) ctx.findViewById(R.id.credit_checkbox);
        sendbutton = (Button) ctx.findViewById(R.id.send_button);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
            }
        });

        popularGridView = (GridView) ctx.findViewById(R.id.popular_expense_grid);
        popularGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("MONEYLOG", "fav clicked");
                PopularItem popularItem = (PopularItem) popularAdapter.getItem(position);
                sum_edit.setText(popularItem.getSum().toString());
                ReasonItem item = new ReasonItem();
                item.setId(popularItem.getIdReason());
                int pos = adapter.getPosition(item);
                if(pos != 0) {
                    reason_spinner.setSelection(pos);
                }



            }
        });
        //Возможно прищли данные с предустановкой
        Intent intent = ctx.getIntent();
        Integer reasonId = intent.getIntExtra(ReportActivity.ACTION_REQUEST_REASON, 0);
        if(reasonId != 0) {
        }
        String sum = intent.getStringExtra(ReportActivity.ACTION_REQUEST_SUM);
        if(sum != null) {
            sum_edit.setText(sum);
        }
        getReasonsFromDb();
        getPopularFromDb();

    }



    private void sendToServer() {

        final String sum = sum_edit.getText().toString();
        if(sum.equals("")) {
            makeToast("Введиту сумму");
            return;
        }
        final String captionText = caption.getText().toString();
        final int reason = ((ReasonItem) reason_spinner.getSelectedItem()).getId();

        Log.d("MONEYLOG", "Sum: " + sum + " reasonId: " + reason + " caption: " + captionText);
        final String credit_part = credit.isChecked() ? "&credit=1" : "";
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                DefaultHttpClient client = new DefaultHttpClient();
                client.setCookieStore(CookieUtils.getInstance().getCookieStore());

                try {
                    StringBuilder url = new StringBuilder("http://m.discode.ru/api/bill?type=expense&sum="+sum+"&reason_id="+String.valueOf(reason)+"&description="+ URLEncoder.encode(captionText, "UTF-8")+credit_part);
                    HttpGet get = new HttpGet(url.toString());
                    HttpResponse r = client.execute(get);
                    CookieUtils.getInstance().saveCookie(client);
                    HttpEntity e = r.getEntity();

                    String data = EntityUtils.toString(e);
                    JSONObject object = new JSONObject(data);
                    Log.d("MONEYLOG", data);
                    if(!object.getBoolean("success")) {
                        throw new Exception(object.getString("message"));
                    }
                    return object;

                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }

            }
            @Override
            protected void onPostExecute(Object o) {
                if(o instanceof Exception) {
                    makeToast(((Exception) o).getMessage());
                    return;
                }
                getActivity().finish();
            }
        }.execute();
    }

    private void makeToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void getReasonsFromDb() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                List<ReasonItem> result = new ArrayList<>();
                DatabaseHelper helper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
                try {
                ReasonDao reasonDao = helper.getReasonDao();
                QueryBuilder<ReasonItem, Integer> builder = reasonDao.queryBuilder();
                    builder.where().eq(ReasonItem.TYPE_FIELD, "expense");
                    builder.orderBy(ReasonItem.RATING_FIELD, false);
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
                    adapter = new ReasonAdapter(getActivity(), (List) o);
                    reason_spinner.setAdapter(adapter);

                    //Проверим, можен уже есть причина затрат
                    Intent intent = ctx.getIntent();
                    Integer reasonId = intent.getIntExtra(ReportActivity.ACTION_REQUEST_REASON, 0);
                    if(reasonId != 0) {
                        ReasonItem reasonItem = new ReasonItem();
                        reasonItem.setId(reasonId);
                        int pos = adapter.getPosition(reasonItem);
                        if(pos != 0) {
                            reason_spinner.setSelection(pos);
                        }

                    }

                } else if(o instanceof Exception) {
                    Toast.makeText(getActivity(),  ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    private void getPopularFromDb() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                List<PopularItem> result = new ArrayList<>();
                DatabaseHelper helper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
                try {
                    PopularItemDao popularItemDao = helper.getPopularItemDao();
                    QueryBuilder<PopularItem, Integer> builder = popularItemDao.queryBuilder();
                    builder.orderBy(PopularItem.SUM_FIELD, true);
                    result.addAll(popularItemDao.query(builder.prepare()));
                    return result;
                } catch (Exception e) {
                    Log.d("MONEYLOG", e.getMessage());
                    return e;
                }
            }
            @Override
            protected void onPostExecute(Object o) {
                if(o instanceof List) {
                    Log.d("MONEYLOG", "Popularsize: "+((List) o).size());
                    popularAdapter = new PopularAdapter(getActivity(), (List) o);
                    popularGridView.setAdapter(popularAdapter);



                } else if(o instanceof Exception) {
                    Toast.makeText(getActivity(),  ((Exception) o).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }



}
