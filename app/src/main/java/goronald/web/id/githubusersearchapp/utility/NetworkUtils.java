package goronald.web.id.githubusersearchapp.utility;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    final static String GITHUB_BASE_URL = "https://api.github.com/search/users";
    final static String PARAM_QUERY = "q";
    final static String PARAM_PAGE = "page";

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
}
