package io.gamereward.sample;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.GrdLeaderBoard;
import io.gamereward.grd.IGrdLeaderBoardCallBack;

public class LeaderBoardFragment extends Fragment {
    LeaderBoardAdapter adapter;
    RecyclerView recyclerView ;
    public String scoreType;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LeaderBoardFragment() {
    }

    public void loadData() {
        GrdManager.getLeaderBoard(scoreType, 0, 10, new IGrdLeaderBoardCallBack() {
            @Override
            public void OnFinished(int error, String message, GrdLeaderBoard[] data) {
                if(error==0) {
                    adapter = new LeaderBoardAdapter(data);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
                }
            }
        });
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static final DiffUtil.ItemCallback<GrdLeaderBoard> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GrdLeaderBoard>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull GrdLeaderBoard oldLeaderBoard, @NonNull GrdLeaderBoard newLeaderBoard) {
                    // LeaderBoard properties may have changed if reloaded from the DB, but ID is fixed
                    return oldLeaderBoard.username == newLeaderBoard.username;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull GrdLeaderBoard oldLeaderBoard, @NonNull GrdLeaderBoard newLeaderBoard) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldLeaderBoard.equals(newLeaderBoard);
                }
            };

    private class LeaderBoardAdapter extends ListAdapter<GrdLeaderBoard, ViewHolder> {
        ArrayList<GrdLeaderBoard> LeaderBoards = new ArrayList<>();

        public LeaderBoardAdapter(GrdLeaderBoard[] LeaderBoard) {
            super(DIFF_CALLBACK);
            for (GrdLeaderBoard data : LeaderBoard) {
                LeaderBoards.add(data);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
             TextView textView = new TextView(getActivity());
            return new ViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GrdLeaderBoard data = LeaderBoards.get(position);
            String s =  "#"+data.rank+":"+data.username+",Score:"+data.score;
            ((TextView) holder.itemView).setText(s);
        }
        public int getItemCount() {
            return LeaderBoards.size();
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
        return  view;
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
