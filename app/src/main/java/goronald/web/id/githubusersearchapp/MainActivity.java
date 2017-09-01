package goronald.web.id.githubusersearchapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String SEARCH_USER_JSON_URL = "https://api.github.com/search/users?q=";

    private EditText edtSearch;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Data> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtSearch = (EditText) findViewById(R.id.edt_search_user);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_github_user_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    sendSearchRequest(SEARCH_USER_JSON_URL + edtSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void sendSearchRequest(String urlRequest) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONParse jsonParse = new JSONParse(response);
                        jsonParse.parseJSON();
                        mDataList = new ArrayList<>();

                        mDataList = jsonParse.getUsers();
                        mAdapter = new DataAdapter(mDataList);
                        mRecyclerView.setAdapter(mAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
