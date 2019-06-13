package uk.ac.le.cityTourPlanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class ScheduledTripRecyclerViewAdapter extends RecyclerView.Adapter<ScheduledTripRecyclerViewAdapter.ScheduledTripViewHolder> {

    private Context mContext;
    private List<GeneratedTrip> mTripList;

    public ScheduledTripRecyclerViewAdapter(Context context, List<GeneratedTrip> tripList) {
        mContext = context;
        mTripList = tripList;
    }

    @NonNull
    @Override
    public ScheduledTripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.scheduled_trip_item,viewGroup,false);
        return new ScheduledTripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduledTripViewHolder viewHolder, int i) {
        GeneratedTrip generatedTrip = mTripList.get(i);
        viewHolder.ScheduledTripNameTextView.setText(generatedTrip.getTripName());
        viewHolder.ScheduledTripDateTextView.setText(generatedTrip.getTripDate());

    }

    @Override
    public int getItemCount() {
        return mTripList.size();
    }

    public class ScheduledTripViewHolder extends RecyclerView.ViewHolder{
        public TextView ScheduledTripNameTextView;
        public TextView ScheduledTripDateTextView;
        public TextView ScheduledTripOptionsMenuTextView;

        public ScheduledTripViewHolder(@NonNull View itemView) {
            super(itemView);

            ScheduledTripNameTextView = (TextView)itemView.findViewById(R.id.scheduledTripNameTextView);
            ScheduledTripDateTextView = (TextView)itemView.findViewById(R.id.scheduledTripDateTextView);
            ScheduledTripOptionsMenuTextView = (TextView)itemView.findViewById(R.id.scheduledTripOptionsTextView);
        }
    }
}
