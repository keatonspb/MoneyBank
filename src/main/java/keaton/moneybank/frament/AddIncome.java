package keaton.moneybank.frament;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import keaton.moneybank.adapter.ReasonAdapter;
import keaton.moneybank.db.DatabaseHelper;
import keaton.moneybank.db.dao.ReasonDao;
import keaton.moneybank.entity.ReasonItem;


public class AddIncome extends Fragment {

    private Activity ctx;
    private EditText sum_edit;
    private Spinner reason_spinner;
    private EditText caption;
    private Button sendbutton;

    private ArrayAdapter adapter;


    public AddIncome() {
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
        return inflater.inflate(R.layout.fragment_add_income, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ctx = getActivity();

        sum_edit = (EditText) getView().findViewById(R.id.add_summ);
        caption = (EditText) getView().findViewById(R.id.add_caption);
        reason_spinner = (Spinner) getView().findViewById(R.id.spinner_reasons);

        sendbutton = (Button) getView().findViewById(R.id.send_button);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToServer();
            }
        });

        getReasonsFromDb();
    }

    private void sendToServer() {

        final String sum = sum_edit.getText().toString();
        if(sum.equals("")) {
            makeToast("Введиту сумму");
            return;
        }
        final String captionText = caption.getText().toString();
        final int reason = ((ReasonItem) reason_spinner.getSelectedItem()).getId();

        Log.d("MONEYLOG", "Sum: "+sum+ " reasonId: "+reason+" caption: "+captionText);

        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] params) {
                HttpClient client = new DefaultHttpClient();

                try {
                    StringBuilder url = new StringBuilder("http://money.discode.ru/phone/set.php?action=income&sum="+sum+"&reason="+String.valueOf(reason)+"&caption="+ URLEncoder.encode(captionText, "UTF-8"));

                    HttpGet get = new HttpGet(url.toString());
                    HttpResponse r = client.execute(get);
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
                    builder.where().eq(ReasonItem.TYPE_FIELD, "income");
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
