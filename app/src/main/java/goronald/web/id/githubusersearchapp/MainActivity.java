package goronald.web.id.githubusersearchapp;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

    private ConstraintLayout clEmptyView;
    private EditText edtSearch;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Data> mDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataList = new ArrayList<>();

        clEmptyView = (ConstraintLayout) findViewById(R.id.cl_empty_view);
        edtSearch = (EditText) findViewById(R.id.edt_search_user);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_github_user_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String keyword = edtSearch.getText().toString();
                    if (keyword.trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "keyword can't be null", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        keyword = keyword.replaceAll(" ", "%20");
                        sendSearchRequest(SEARCH_USER_JSON_URL + keyword);
                        // handle soft keyboard visibility
                        InputMethodManager imm = (InputMethodManager) getApplicationContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
                        return true;
                    }
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
                        mDataList = jsonParse.getUsers();
                        mAdapter = new DataAdapter(mDataList);
                        mRecyclerView.setAdapter(mAdapter);

                        if (mDataList.size() == 0) {
                            showEmptyView();
                        } else {
                            showDataView();
                        }
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

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        clEmptyView.setVisibility(View.VISIBLE);
        TextView tvNotFoundMessage = (TextView) findViewById(R.id.tv_not_found_content);
        tvNotFoundMessage.setText("user " + "'" + edtSearch.getText().toString() + "'" + " tidak ditemukan");
    }

    private void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        clEmptyView.setVisibility(View.GONE);
    }
}
