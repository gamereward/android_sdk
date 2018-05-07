package io.gamereward.sample;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.GrdSessionData;
import io.gamereward.grd.IGrdSessionDataCallBack;

public class HistoryFragment extends Fragment {
    public interface IFormatSessionData {
        String format(GrdSessionData data);
    }
    SessionDataAdapter adapter;
    RecyclerView recyclerView ;
    public String store;
    public String dataKey[];
    public IFormatSessionData formatData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HistoryFragment() {
    }

    public void loadData() {
        GrdManager.getUserSessionData(store, dataKey, 0, 10, new IGrdSessionDataCallBack() {
            @Override
            public void OnFinished(int error, String message, GrdSessionData[] data) {
                if(error==0) {
                    adapter = new SessionDataAdapter(data);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
    }
    public void addHistory(GrdSessionData data){
        if(adapter!=null) {
            adapter.sessions.add(0, data);
            adapter.notifyDataSetChanged();
        }
    }
    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final DiffUtil.ItemCallback<GrdSessionData> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GrdSessionData>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull GrdSessionData oldSession, @NonNull GrdSessionData newSession) {
                    // Session properties may have changed if reloaded from the DB, but ID is fixed
                    return oldSession.sessionid == newSession.sessionid;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull GrdSessionData oldSession, @NonNull GrdSessionData newSession) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldSession.equals(newSession);
                }
            };

    private class SessionDataAdapter extends ListAdapter<GrdSessionData, ViewHolder> {
        ArrayList<GrdSessionData> sessions = new ArrayList<>();

        public SessionDataAdapter(GrdSessionData[] sessionData) {
            super(DIFF_CALLBACK);
            for (GrdSessionData data : sessionData) {
                sessions.add(data);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(getActivity()).inflate(R.layout.history_item,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GrdSessionData data = sessions.get(position);
            String text="";
            if (formatData != null) {
               text= (formatData.format(data));
            } else {
                String s = data.getTime().toString() + "";
                for (String key : data.values.keySet()) {
                    s += "-" + key + ":" + data.values.get(key);
                }
                text=s;
            }
            String[]lines=text.split("\n");
            TextView titleView= holder.itemView.findViewById(R.id.history_title);
            titleView.setText(lines[0]);
            TextView dataView= holder.itemView.findViewById(R.id.history_data);
            if(lines.length>1){
                dataView.setVisibility(View.VISIBLE);
                text="";
                for(int i=1;i<lines.length;i++){
                    text+="\n"+lines[i];
                }
                text=text.substring(1);
                dataView.setText(text);
            }
            else{
                dataView.setVisibility(View.GONE);
            }
        }
        public int getItemCount() {
            return sessions.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_history,container,false);
        recyclerView=view.findViewById(R.id.listView);
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(inflater.getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
