package io.gamereward.sample;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.GrdTransaction;
import io.gamereward.grd.IGrdTransactionCallBack;
import io.gamereward.sample.R;

public class TransactionFragment extends Fragment {
    private MainMenuActivity activity;
    private int start = 0;
    private int count = 20;

    public void setActivity(MainMenuActivity activity) {
        this.activity = activity;
    }

    public TransactionFragment() {
        // Required empty public constructor
    }

    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_transaction, container, false);
        root.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showMenu();
            }
        });
        mRecyclerView = root.findViewById(R.id.transaction_list);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(mLayoutManager);
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }
    public static final DiffUtil.ItemCallback<GrdTransaction> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<GrdTransaction>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull GrdTransaction oldTrans, @NonNull GrdTransaction newTrans) {
                    // Trans properties may have changed if reloaded from the DB, but ID is fixed
                    return oldTrans.transid == newTrans.transid;
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull GrdTransaction oldTrans, @NonNull GrdTransaction newTrans) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldTrans.equals(newTrans);
                }
            };
    private class TransactionListAdapter extends ListAdapter<GrdTransaction, ViewHolder> {
        GrdTransaction[] transactions;

        public TransactionListAdapter(GrdTransaction[] transactions) {
            super(DIFF_CALLBACK);
            this.transactions = transactions;
        }



        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
            ViewHolder vh = new ViewHolder(mView);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GrdTransaction item = transactions[position];
            ((TextView) holder.itemView.findViewById(R.id.transaction_tx)).setText("tx:"+item.tx);
            ((TextView) holder.itemView.findViewById(R.id.transaction_from)).setText("from:"+item.from);
            ((TextView) holder.itemView.findViewById(R.id.transaction_to)).setText("to:"+item.to);
            ((TextView) holder.itemView.findViewById(R.id.transaction_amount)).setText("amount:"+item.amount.toString());
            ((TextView) holder.itemView.findViewById(R.id.transaction_date)).setText(item.getTime().toString());
            if (item.status == GrdManager.PENDING_TRANSSTATUS) {
                ((TextView) holder.itemView.findViewById(R.id.transaction_status)).setText("status:Pending");
            } else if (item.status == GrdManager.SUCCESS_TRANSSTATUS) {
                ((TextView) holder.itemView.findViewById(R.id.transaction_status)).setText("status:Success");
            } else {
                ((TextView) holder.itemView.findViewById(R.id.transaction_status)).setText("status:Error");
            }
            if (item.transtype == GrdManager.BASE_TRANSTYPE) {
                ((TextView) holder.itemView.findViewById(R.id.transaction_type)).setText("transtype:Base");
            } else if (item.status == GrdManager.INTERNAL_TRANSTYPE) {
                ((TextView) holder.itemView.findViewById(R.id.transaction_status)).setText("transtype:Internal");
            } else {
                ((TextView) holder.itemView.findViewById(R.id.transaction_status)).setText("transtype:External");
            }
        }
        @Override
        public int getItemCount() {
            return transactions.length;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        activity.showProgress(true);
        GrdManager.getTransactions(start, count, new IGrdTransactionCallBack() {
            @Override
            public void OnFinished(int error, String message, GrdTransaction[] transactions) {
                activity.showProgress(false);
                if(error==0) {
                    TransactionListAdapter adapter = new TransactionListAdapter(transactions);
                    mRecyclerView.setAdapter(adapter);
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
