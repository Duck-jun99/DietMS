package com.mobilelec.dietms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobilelec.dietms.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<ItemList> users;

    public interface OnClickListener {
        void onClick(ItemList user);
    }

    public interface OnLongClickListener {
        void onLongClick(ItemList user);
    }

    OnClickListener onClickListener;
    OnLongClickListener onLongClickListener;

    public MyAdapter(OnClickListener onClickListener, OnLongClickListener onLongClickListener) {

        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvText;
        ImageView img;
        View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvText = itemView.findViewById(R.id.tvText);
            img = itemView.findViewById(R.id.img);
        }

        public void bind(final ItemList user) {
            if(Objects.equals(user.meal, "0")){
                tvTitle.setText("아침");
            }

            if(Objects.equals(user.meal, "1")){
                tvTitle.setText("점심");
            }

            if(Objects.equals(user.meal, "2")){
                tvTitle.setText("저녁");
            }

            tvText.setText("음식: "+user.text);
            //tvAge.setText(Integer.toString(user.age));
            Picasso.get().load(itemView.getContext().getString(R.string.BASE_URL)+user.image).into(img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(user);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onLongClickListener.onLongClick(user);
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.cardview_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        if (users != null && users.size() > 0) {
            return users.size();
        } else {
            return 0;
        }
    }

    public void setUsers(List<ItemList> users) {
        this.users= users;
        notifyDataSetChanged();
    }



}