package goronald.web.id.githubusersearchapp.activity;

import android.content.Context;
import android.os.CountDownTimer;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import goronald.web.id.githubusersearchapp.R;
import goronald.web.id.githubusersearchapp.adapter.UserDataAdapter;
import goronald.web.id.githubusersearchapp.helper.VolleySingleton;
import goronald.web.id.githubusersearchapp.model.UserData;
import goronald.web.id.githubusersearchapp.utility.DateTimeUtils;
import goronald.web.id.githubusersearchapp.utility.EndlessRecyclerViewScrollListener;
import goronald.web.id.githubusersearchapp.utility.JSONParse;
import goronald.web.id.githubusersearchapp.utility.NetworkUtils;

public class MainActivity extends AppCompatActivity {

    private EndlessRecyclerViewScrollListener scrollListener;
    private ConstraintLayout clEmptyView;
    private ConstraintLayout clLimitRateView;
    private TextView tvLimitTime;
    private EditText edtSearch;
    private ProgressBar pbLoading;
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

        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        clEmptyView = (ConstraintLayout) findViewById(R.id.cl_empty_view);
        clLimitRateView = (ConstraintLayout) findViewById(R.id.cl_limit_rate);
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
                if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        Log.d("action", "masuk");
                        keyword = edtSearch.getText().toString();
                        if (keyword.trim().length() == 0) {
                            Toast.makeText(getApplicationContext(), "Keyword can't be null", Toast.LENGTH_SHORT).show();
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

                }
                return false;
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(final int page, int totalItemsCount, RecyclerView view) {
                Log.d("fungsiOnLoadMore", "call");
                loadNextDataFromApi(page);
            }

        };
        mRecyclerView.addOnScrollListener(scrollListener);
    }

    private void sendSearchRequest(final String keyword) {
        Log.d("keyword", keyword);
        pbLoading.setVisibility(View.VISIBLE);
        if (NetworkUtils.isConnected(mContext)) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    String.valueOf(NetworkUtils.buildUrl(keyword, 1)),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONParse jsonParse = new JSONParse(response);
                            jsonParse.parseJSON();

                            mDataList.clear();
                            mNextDataList.clear();
                            scrollListener.resetState();

                            mDataList = jsonParse.getUsers();
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
                            pbLoading.setVisibility(View.GONE);
                            handleVolleyError(error);
                        }
                    }
            );
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest, "Search Request");
        } else {
            pbLoading.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
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
                            handleVolleyError(error);
                        }
                    }
            );
            VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest, "Load More Data");
        } else {
            pbLoading.setVisibility(View.GONE);
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
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
        pbLoading.setVisibility(View.GONE);
        TextView tvNotFoundMessage = (TextView) findViewById(R.id.tv_not_found_content);
        tvNotFoundMessage.setText("user " + "'" + edtSearch.getText().toString() + "'" + " tidak ditemukan");
    }

    private void showDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        clEmptyView.setVisibility(View.GONE);
        pbLoading.setVisibility(View.GONE);
    }

    private void handleVolleyError(VolleyError error) {
        if (error instanceof TimeoutError) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.generic_server_down, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else if (error instanceof ServerError || error instanceof AuthFailureError) {
            handleServerError(error);
        } else if (error instanceof NetworkError) {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.no_internet, Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.generic_error, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void handleServerError(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            switch (response.statusCode) {
                case 403:
                    try {
                        Log.d("networkResponseBody", new String(response.data));
                        Log.d("networkResponseHeader", String.valueOf(new JSONObject(response.headers)));
                        JSONObject responseHeader = new JSONObject(response.headers);
                        long dateTimeInMillis = DateTimeUtils
                                .convertFullDateToMillis(responseHeader.getString("Date"));
                        Log.d("beginTime", String.valueOf(dateTimeInMillis));
                        long rateLimitResetTimeInMillis = responseHeader.getLong("X-RateLimit-Reset") * 1000;
                        Log.d("resetDate", String.valueOf(rateLimitResetTimeInMillis));

                        tvLimitTime = (TextView) findViewById(R.id.tv_rate_limit_time);
                        new CountDownTimer(rateLimitResetTimeInMillis - dateTimeInMillis, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                clLimitRateView.setVisibility(View.VISIBLE);
                                tvLimitTime.setText(DateTimeUtils.convertMillisToTimeFormat(millisUntilFinished));
                                edtSearch.setEnabled(false);
                            }

                            @Override
                            public void onFinish() {
                                clLimitRateView.setVisibility(View.GONE);
                                edtSearch.setEnabled(true);

                            }
                        }.start();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                            R.string.generic_server_down, Snackbar.LENGTH_SHORT);
                    snackbar.show();
            }
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.myMainLayout),
                    R.string.generic_error, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }
}