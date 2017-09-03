package goronald.web.id.githubusersearchapp.activity;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

import goronald.web.id.githubusersearchapp.R;
import goronald.web.id.githubusersearchapp.adapter.UserDataAdapter;
import goronald.web.id.githubusersearchapp.helper.VolleySingleton;
import goronald.web.id.githubusersearchapp.model.UserData;
import goronald.web.id.githubusersearchapp.utility.EndlessRecyclerViewScrollListener;
import goronald.web.id.githubusersearchapp.utility.JSONParse;
import goronald.web.id.githubusersearchapp.utility.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private EndlessRecyclerViewScrollListener scrollListener;
    private ConstraintLayout clEmptyView;
    private EditText edtSearch;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<UserData> mDataList;
    private List<UserData> mNextDataList;
    private Context mContext;

    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mDataList = new ArrayList<>();
        mNextDataList = new ArrayList<>();

        clEmptyView = (ConstraintLayout) findViewById(R.id.cl_empty_view);
        edtSearch = (EditText) findViewById(R.id.edt_search_user);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_github_user_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    keyword = edtSearch.getText().toString();
                    if (keyword.trim().length() == 0) {
                        Toast.makeText(getApplicationContext(), "keyword can't be null", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        sendSearchRequest(keyword);
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

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                loadNextDataFromApi(page);
            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void sendSearchRequest(final String keyword) {
        Log.d("keyword", keyword);
        if (NetworkUtils.isConnected(mContext)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    String.valueOf(NetworkUtils.buildUrl(keyword, 1)),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONParse jsonParse = new JSONParse(response);
                            jsonParse.parseJSON();
                            mDataList = jsonParse.getUsers();
                            mNextDataList.clear();
                            mAdapter = new UserDataAdapter(mContext, mDataList);
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
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest, "Search Request");
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSearchRequest(keyword);
                }
            });
            snackbar.show();
        }
    }

    private void loadNextDataFromApi(final int offset) {
        Log.d("offset value", String.valueOf(offset));
        if (NetworkUtils.isConnected(mContext)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    NetworkUtils.buildUrl(keyword, offset + 1).toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONParse jsonParse = new JSONParse(response);
                            jsonParse.parseJSON();
                            mNextDataList = jsonParse.getUsers();
                            if (!mNextDataList.isEmpty()) {
                                for (int i = 0; i < mNextDataList.size(); i++)
                                    mDataList.add(mNextDataList.get(i));
                                mAdapter.notifyItemRangeInserted(mDataList.size() - mNextDataList.size(),
                                        mNextDataList.size());
                            } else {
                                Toast.makeText(mContext, "End of result", Toast.LENGTH_SHORT).show();
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
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest, "Load More Data");
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    "No Internet Connection", Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction("RETRY", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadNextDataFromApi(offset);
                }
            });
            snackbar.show();
        }
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