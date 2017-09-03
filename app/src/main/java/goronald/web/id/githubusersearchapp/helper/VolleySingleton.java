package goronald.web.id.githubusersearchapp.helper;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import goronald.web.id.githubusersearchapp.utility.LruBitmapCache;

public class VolleySingleton {

    public static final String TAG = VolleySingleton.class.getSimpleName();

    private static VolleySingleton singletonInstance;
    private Context context;
    private RequestQueue requestQueue;

    public VolleySingleton(Context context) {
        this.context = context;
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (singletonInstance == null) {
            singletonInstance = new VolleySingleton(context);
        }
        return singletonInstance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return new ImageLoader(getRequestQueue(),
                new LruBitmapCache());
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) {
        // set the default tag if tag is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        VolleyLog.d("Adding request to queue: %s", request.getUrl());
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> request) {
        // set the default tag if tag is empty
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
