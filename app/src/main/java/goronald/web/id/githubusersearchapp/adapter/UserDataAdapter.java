package goronald.web.id.githubusersearchapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import goronald.web.id.githubusersearchapp.helper.VolleySingleton;
import goronald.web.id.githubusersearchapp.model.UserData;
import goronald.web.id.githubusersearchapp.R;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder> {

    private Context context;
    private List<UserData> mDataList;

    public UserDataAdapter(Context context, List<UserData> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    @Override
    public UserDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_data_layout,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserDataAdapter.ViewHolder holder, int position) {
        ImageLoader imageLoader = VolleySingleton.getInstance(context).getImageLoader();
        holder.nivUserPicture.setImageUrl(mDataList.get(position).getUserImgUrl(), imageLoader);
        holder.tvUserName.setText(mDataList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public NetworkImageView nivUserPicture;
        public TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            nivUserPicture = (NetworkImageView) itemView.findViewById(R.id.user_picture);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
        }
    }
}
