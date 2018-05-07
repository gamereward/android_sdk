package io.gamereward.sample;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdStringCallBack;
import io.gamereward.sample.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransferFragment extends Fragment {

    private MainMenuActivity activity;
    private EditText toAddressView;
    private EditText valueView;
    private EditText otpView;
    private TextView messageView;
    public void setActivity(MainMenuActivity activity) {
        this.activity = activity;
    }

    public TransferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_transfer, container, false);
        messageView=root.findViewById(R.id.message_error);
        toAddressView=root.findViewById(R.id.transfer_toAddress);
        valueView=root.findViewById(R.id.transfer_amount);
        otpView=root.findViewById(R.id.otp);
        if(!GrdManager.getUser().otp){
            otpView.setVisibility(View.GONE);
        }
        root.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showMenu();
            }
        });
        root.findViewById(R.id.transfer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address=toAddressView.getText().toString();
                BigDecimal value=new BigDecimal(valueView.getText().toString());
                String otp=otpView.getText().toString();
                activity.showProgress(true);
                GrdManager.transfer(address, value, new IGrdStringCallBack() {
                    @Override
                    public void OnFinished(int error, String data) {
                        activity.showProgress(false);
                        if(error==0){
                            messageView.setText(getString(R.string.transfer_success));
                        }
                        else {
                            messageView.setText("Error:"+error+",message:"+data);
                        }
                    }
                });
            }
        });
        return  root;
    }

}
