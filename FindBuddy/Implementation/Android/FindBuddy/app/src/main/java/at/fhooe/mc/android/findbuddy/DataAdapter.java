package at.fhooe.mc.android.findbuddy;

/**
 * Created by Laurenz on 05.02.2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import at.fhooe.mc.android.findbuddy.Helper.CircleTransform;
import at.fhooe.mc.android.findbuddy.Interfaces.CustomItemClickListener;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<AndroidVersion> android_versions;
    private Context context;
    CustomItemClickListener listener;

    public DataAdapter(Context context,ArrayList<AndroidVersion> android_versions, CustomItemClickListener listener) {
        this.context = context;
        this.android_versions = android_versions;
        this.listener = listener;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        final ViewHolder vh =  new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onItemClick(v, vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.tv_android.setText(android_versions.get(i).getAndroid_version_name());
        Picasso.with(context).load(android_versions.get(i).getAndroid_image_url()).transform(new CircleTransform()).into(viewHolder.img_android);
    }

    @Override
    public int getItemCount() {
        return android_versions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_android;
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);

            tv_android = (TextView)view.findViewById(R.id.tv_android);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }
    }
}