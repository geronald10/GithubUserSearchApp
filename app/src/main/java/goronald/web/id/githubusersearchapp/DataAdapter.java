package goronald.web.id.githubusersearchapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{

    private List<Data> mDataList;

    public DataAdapter(List<Data> mDataList) {
        this.mDataList = mDataList;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_data_layout,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
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

    public void add(int position, Data item) {
        mDataList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(Data item) {
        int position = mDataList.indexOf(item);
        mDataList.remove(position);
        notifyItemRemoved(position);
    }
}
