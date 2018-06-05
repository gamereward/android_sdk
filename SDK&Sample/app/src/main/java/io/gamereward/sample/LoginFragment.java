package io.gamereward.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.gamereward.grd.GrdManager;
import io.gamereward.grd.IGrdStringCallBack;

public class LoginFragment extends Fragment {
    // UI references.
    private EditText mNickNameView;
    private EditText mPasswordView;
    private EditText mOtpView;
    private View mLoginFormView;
    private View mOtpFormView;
    private TextView mMessageView;

    public LoginFragment() {
        // Required empty public constructor
    }

    private MainMenuActivity activity;
    public void setActivity(MainMenuActivity activity) {
        this.activity = activity;
    }

    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        mNickNameView = (EditText) root.findViewById(R.id.nickname);
        mPasswordView = (EditText) root.findViewById(R.id.password);
        Button mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = root.findViewById(R.id.email_login_form);

        mOtpFormView = root.findViewById(R.id.sign_in_otpverify);

        mOtpView = (EditText) root.findViewById(R.id.otp);
        Button btnVerify = root.findViewById(R.id.sign_in_otpverify_button);
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button btnRegister = root.findViewById(R.id.register_button);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.showRegister();
            }
        });
        root.findViewById(R.id.reset_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailNickName=mNickNameView.getText().toString();
                if(emailNickName.trim().length()>0) {
                    activity.showProgress(true);
                    GrdManager.requestResetPassword(emailNickName, new IGrdStringCallBack() {
                        @Override
                        public void OnFinished(int error, String data) {
                            activity.showProgress(false);
                            if(error==0){
                                mMessageView.setText("An email has sent to your email. Please check email to reset password!");
                            }
                            else{
                                mMessageView.setText("Error:"+error+",message:"+data);
                            }
                        }
                    });
                }
                else{
                    mNickNameView.setError(getString(R.string.error_field_required));
                }
            }
        });
        mOtpFormView.setVisibility(View.GONE);
        mMessageView = (TextView) root.findViewById(R.id.login_message);
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        activity.hideSoftKeyboard();
        // Reset errors.
        mNickNameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String nickname = mNickNameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String otp = mOtpView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(nickname)) {
            mNickNameView.setError(getString(R.string.error_field_required));
            focusView = mNickNameView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            GrdManager.login(nickname, password, otp, new IGrdStringCallBack() {
                @Override
                public void OnFinished(int error, String data) {
                    showProgress(false);
                    if (error == 0) {
                        activity.showMenu();
                    } else if (error == 4) {
                        if (mLoginFormView.isShown()) {
                            mLoginFormView.setVisibility(View.GONE);
                            mOtpFormView.setVisibility(View.VISIBLE);
                        } else {
                            mOtpView.setError(getString(R.string.error_incorrect_otp));
                        }
                    } else if (error == 2) {
                        mNickNameView.setError(getString(R.string.error_incorrect_nickname));
                        mNickNameView.requestFocus();
                    } else if (error == 3) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else {
                        mMessageView.setText("error:" + error + ",message:" + data);
                    }
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        activity.showProgress(show);
    }

}
