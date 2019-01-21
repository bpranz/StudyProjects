package at.fhooe.mc.android.findbuddy.Helper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import at.fhooe.mc.android.findbuddy.Entities.MyActivity;
import at.fhooe.mc.android.findbuddy.Interfaces.CustomItemClickListener;
import at.fhooe.mc.android.findbuddy.R;

/**
 * Helper for Recycle Views
 */
public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    CustomItemClickListener listener;

    private ArrayList<MyActivity> activityList;

    public static class ActivityViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName, activityCategory, activityDate, activityMaxParticipants, activityLocation;

        public ActivityViewHolder(View view) {
            super(view);
            activityName = view.findViewById(R.id.activityItemName);
            activityCategory = view.findViewById(R.id.activityItemCategory);
            activityDate = view.findViewById(R.id.activityItemDate);
            activityMaxParticipants = view.findViewById(R.id.activityItemMaxParticipants);
            activityLocation = view.findViewById(R.id.activityItemLocation);
        }
    }

    public ActivityAdapter(ArrayList<MyActivity> activityList, CustomItemClickListener listener) {
        this.activityList = activityList;
        this.listener = listener;
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_recycler_view_item, parent, false);

        //set layout parameters here if needed

        final ActivityViewHolder vh = new ActivityViewHolder(v);

        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                listener.onItemClick(v, vh.getAdapterPosition());
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        MyActivity activity = activityList.get(position);
        MyCalendar cal = new MyCalendar();
        holder.activityName.setText(activity.getName());
        holder.activityCategory.setText(activity.getCategory());
        holder.activityDate.setText(cal.convertDatesToString(activity.getStartDate(), activity.getEndDate()));
        holder.activityMaxParticipants.setText("Max. Teilnehmeranzahl: " + activity.getMaxParticipants());
        holder.activityLocation.setText(activity.getInformations());
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }


}
