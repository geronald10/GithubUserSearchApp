package goronald.web.id.githubusersearchapp.utility;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import goronald.web.id.githubusersearchapp.model.UserData;

public class JSONParse {

    public static String[] imgUrls;
    public static String[] userNames;

    private JSONArray users = null;
    private List<UserData> Users;

    private String json;

    public JSONParse(String json) {
        this.json = json;
    }

    public void parseJSON() {
        try {
            JSONObject jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray("items");
            imgUrls = new String[users.length()];
            userNames = new String[users.length()];
            Users = new ArrayList<>();

            if (users.length() > 0) {
                for (int i = 0; i < users.length(); i++) {
                    UserData data = new UserData();
                    JSONObject itemJsonObject = users.getJSONObject(i);

                    userNames[i] = itemJsonObject.getString("login");
                    imgUrls[i] = itemJsonObject.getString("avatar_url");

                    data.setUserName(userNames[i]);
                    data.setUserImgUrl(imgUrls[i]);
                    Users.add(data);
                    Log.d("data Url", String.valueOf(data.getUserImgUrl()) + data.getUserName());
                }
            } else {
                Users = Collections.emptyList();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public List<UserData> getUsers() {
        return Users;
    }
}
