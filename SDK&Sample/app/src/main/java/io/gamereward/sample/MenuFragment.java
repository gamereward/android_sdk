package io.gamereward.sample;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.gamereward.sample.R;
import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdImageCallBack;


public class MenuFragment extends Fragment {
    private MainMenuActivity activity;
   private ImageView qrcodeView;
   private TextView acountBalanceView;
    public MenuFragment() {
        // Required empty public constructor
    }

    public void setActivity(MainMenuActivity activity){
        this.activity=activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_menu, container, false);
        qrcodeView=(ImageView)root.findViewById(R.id.address_qrcode);
        ((TextView)root.findViewById(R.id.address_text)).setText(GrdManager.getUser().address);
        acountBalanceView=root.findViewById(R.id.account_balance);
        showAccountInfo();
        root.findViewById(R.id.otp_setting_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showOtpSetting();
            }
        });
        root.findViewById(R.id.transfer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showTransfer();
            }
        });
        root.findViewById(R.id.transaction_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showTransaction();
            }
        });
        root.findViewById(R.id.game2_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showGame1();
            }
        });
        root.findViewById(R.id.game1_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showGame2();
            }
        });
        return  root;
    }
    void showAccountInfo(){
        if(acountBalanceView!=null) {
            GrdManager.getAddressQRCode(GrdManager.getUser().address, new IGrdImageCallBack() {
                @Override
                public void OnFinished(int error, String data, Bitmap bitmap) {
                    qrcodeView.setImageBitmap(bitmap);
                }
            });
            acountBalanceView.setText(GrdManager.getUser().balance.toString());
        }
    }

    @Override
    public void onResume() {
        showAccountInfo();
        super.onResume();
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
