package goronald.web.id.githubusersearchapp.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private final static String GITHUB_BASE_URL = "https://api.github.com/search/users";
    private final static String PARAM_QUERY = "q";
    private final static String PARAM_PAGE = "page";

    public static URL buildUrl(String githubSearchQuery, int page) {
        Uri builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_PAGE, String.valueOf(page))
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static boolean isConnected(Context context) {
        Log.d("cek koneksi", "masuk sini");
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
