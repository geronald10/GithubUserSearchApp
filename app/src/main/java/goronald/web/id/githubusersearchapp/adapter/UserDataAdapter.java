package goronald.web.id.githubusersearchapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import goronald.web.id.githubusersearchapp.model.UserData;
import goronald.web.id.githubusersearchapp.R;

public class UserDataAdapter extends RecyclerView.Adapter<UserDataAdapter.ViewHolder>{

    private List<UserData> mDataList;

    public UserDataAdapter(List<UserData> mDataList) {
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
        Glide.with(holder.ivUserPicture.getContext()).load(mDataList.get(position).getUserImgUrl())
                .into(holder.ivUserPicture);
        holder.tvUserName.setText(mDataList.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivUserPicture;
        public TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            ivUserPicture = (ImageView) itemView.findViewById(R.id.iv_user_picture);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
        }
    }
}
