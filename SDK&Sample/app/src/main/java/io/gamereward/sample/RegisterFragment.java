package io.gamereward.sample;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.gamereward.sample.R;
import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdStringCallBack;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private EditText mNickNameView;
    private EditText mPasswordView;
    private EditText mEmailView;
    private TextView mMesssageView;
    MainMenuActivity activity;
    public RegisterFragment() {
        // Required empty public constructor
    }
    public void setActivity(MainMenuActivity activity){
        this.activity=activity;
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_register, container, false);
        mNickNameView = (EditText) root.findViewById(R.id.nickname);
        mPasswordView = (EditText) root.findViewById(R.id.password);
        mEmailView=(EditText)root.findViewById(R.id.email);
        Button btnRegister=(Button)root.findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username=mNickNameView.getText().toString().trim();
                String password=mPasswordView.getText().toString();
                String email=mEmailView.getText().toString().trim();
                if(username==""){
                    mNickNameView.setError(getString(R.string.error_field_required));
                    return ;
                }
                if(email==""){
                    mNickNameView.setError(getString(R.string.error_field_required));
                    return ;
                }
                else  if(!isEmailValid(email)){
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    return ;
                }
                if(!isPasswordValid(password)){
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    return;
                }
                activity.showProgress(true);
                GrdManager.register(username, password, email, "", new IGrdStringCallBack() {
                    @Override
                    public void OnFinished(int error, String data) {
                        activity.showProgress(false);
                        if(error==0){
                            mMesssageView.setText(getString(R.string.register_success));
                        }
                        else{
                            mMesssageView.setText("Error:"+error+",message:"+data);
                        }
                    }
                });
            }
        });
        Button btnBack=(Button)root.findViewById(R.id.cancel_button);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showLogin();
            }
        });
        mMesssageView=root.findViewById(R.id.message_error);
        return root;
    }

}
