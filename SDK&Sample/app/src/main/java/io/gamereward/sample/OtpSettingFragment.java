package io.gamereward.sample;


import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import io.gamereward.sample.R;
import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdImageCallBack;
import io.gamereward.grd.IGrdStringCallBack;


public class OtpSettingFragment extends Fragment {
    private MainMenuActivity activity;
    private Switch aSwitch;
    private EditText otpView;
    private TextView messageView;
    private LinearLayout instructionView;
    private TextView otpSecretView;
    private ImageView otpQrCodeView;
    public OtpSettingFragment() {
        // Required empty public constructor
    }
    public void setActivity(MainMenuActivity activity){
        this.activity=activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_otp_setting, container, false);
        aSwitch=(Switch) root.findViewById(R.id.switch_Otp);
        otpView=(EditText) root.findViewById(R.id.otp);
        messageView=root.findViewById(R.id.message_error);
        otpQrCodeView=root.findViewById(R.id.qrcode_OtpApp);
        otpSecretView=root.findViewById(R.id.secret_OtpApp);
        otpSecretView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("SECRET", otpSecretView.getText());
                clipboard.setPrimaryClip(clip);
            }
        });
        instructionView=root.findViewById(R.id.otp_instruction);
        instructionView.setVisibility(View.GONE);
        aSwitch.setChecked(GrdManager.getUser().otp);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked&&!GrdManager.getUser().otp){
                    activity.showProgress(true);
                    GrdManager.requestEnableOtp(new IGrdImageCallBack() {
                        @Override
                        public void OnFinished(int error, String data, Bitmap bitmap) {
                            if(error==0){
                                activity.showProgress(false);
                                instructionView.setVisibility(View.VISIBLE);
                                otpSecretView.setText(data);
                                otpQrCodeView.setImageBitmap(bitmap);
                                ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(activity.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("SECRET", data);
                                clipboard.setPrimaryClip(clip);
                            }
                            else{
                                messageView.setText("Error:"+error+",message:"+data);
                            }
                        }
                    });
                }
                else{
                    instructionView.setVisibility(View.GONE);
                }
            }
        });
        root.findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showMenu();
            }
        });
        root.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isenabled=aSwitch.isChecked();
                String otp=otpView.getText().toString();
                if(otp==null){
                    otp="";
                }
                activity.showProgress(true);
                GrdManager.enableOtp(isenabled,otp, new IGrdStringCallBack() {
                    @Override
                    public void OnFinished(int error, String data) {
                        activity.showProgress(false);
                        if(error==0){
                            messageView.setText(isenabled?"Otp has been enabled successfully!":"Otp has been disabled successfully!");
                        }
                        else{
                            messageView.setText("Error:"+error+",message:"+data);
                        }
                    }
                });
            }
        });
        root.findViewById(R.id.download_appstore_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://itunes.apple.com/vn/app/google-authenticator/id388497605?mt=8"));
                startActivity(browserIntent);
            }
        });
        root.findViewById(R.id.download_google_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.authenticator2"));
                startActivity(browserIntent);
            }
        });
        return  root;
    }

}
