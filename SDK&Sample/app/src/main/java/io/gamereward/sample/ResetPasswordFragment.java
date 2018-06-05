package io.gamereward.sample;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdStringCallBack;

public class ResetPasswordFragment extends Fragment {
    private EditText passwordView;
    private EditText tokenView;
    private TextView messageView;
    private MainMenuActivity activity;
    public void setActivity(MainMenuActivity activity) {
        this.activity = activity;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_resetpassword, container, false);
        passwordView=root.findViewById(R.id.password);
        tokenView=root.findViewById(R.id.token);
        messageView=root.findViewById(R.id.message_error);
        root.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLogin();
            }
        });
        root.findViewById(R.id.reset_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tokenView.getText().toString().trim()==""){
                    messageView.setText("Token can not be empty!");
                    return;
                }
                if(passwordView.getText().toString().trim()==""){
                    messageView.setText("New password can not be empty!");
                    return;
                }
                activity.showProgress(true);
                GrdManager.resetPassword(tokenView.getText().toString(), passwordView.getText().toString(), new IGrdStringCallBack() {
                    @Override
                    public void OnFinished(int error, String data) {
                        activity.showProgress(false);
                        if(error==0){
                            messageView.setText("YOUR PASSWORD HAD BEEN RESETED!");
                        }
                        else{
                            messageView.setText(data);
                        }
                    }
                });
            }
        });
        return  root;
    }
}
