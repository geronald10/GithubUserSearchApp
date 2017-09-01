package goronald.web.id.githubusersearchapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParse {

    public static String[] imgUrls;
    public static String[] userNames;

    private JSONArray users = null;
    private List<Data> Users;

    private String json;

    public JSONParse(String json) {
        this.json = json;
    }

    public void parseJSON() {
        try {
            JSONObject jsonObject = new JSONObject(json);
//            imgUrls = new String[jsonObject.getInt("total_count")];
//            userNames = new String[jsonObject.getInt("total_count")];
//            Log.d("response", json);
            users = jsonObject.getJSONArray("items");
//            Log.d("items length", String.valueOf(users.length()));
            imgUrls = new String[users.length()];
            userNames = new String[users.length()];
//            Log.d("Items", String.valueOf(users));
            Users = new ArrayList<>();

            for (int i = 0; i < users.length(); i++) {
                Data data = new Data();
                JSONObject itemJsonObject = users.getJSONObject(i);

                userNames[i] = itemJsonObject.getString("login");
                imgUrls[i] = itemJsonObject.getString("avatar_url");

                data.setUserName(userNames[i]);
                data.setUserImgUrl(imgUrls[i]);
                Users.add(data);
                Log.d("data Url", String.valueOf(data.getUserImgUrl()) + data.getUserName());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    List<Data> getUsers() {
        return Users;
    }
}
