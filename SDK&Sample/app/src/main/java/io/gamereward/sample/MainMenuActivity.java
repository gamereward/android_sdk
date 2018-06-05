package io.gamereward.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import io.gamereward.grd.GrdNet;
import io.gamereward.sample.R;
import io.gamereward.grd.GrdManager;

/**
 * A login screen that offers login via email/password.
 */
public class MainMenuActivity extends AppCompatActivity {


    private View mProgressView;
    LoginFragment loginFragment;
    ResetPasswordFragment resetPasswordFragment;
    MenuFragment menuFragment;
    RegisterFragment registerFragment;
    OtpSettingFragment otpSettingFragment;
    TransferFragment transferFragment;
    TransactionFragment transactionFragment;
    Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        // Set up the login form.
        mProgressView = findViewById(R.id.login_progress);
        //Initialize the application parameter
        String appid = "cc8b8744dbb1353393aac31d371af9a55a67df16";
        String secret = "1679091c5a880faf6fb5e6087eb1b2dc4daa3db355ef2b0e64b472968cb70f0df4be00279ee2e0a53eafdaa94a151e2ccbe3eb2dad4e422a7cba7b261d923784";
        GrdManager.init(appid, secret, GrdNet.TestNet);
        showLogin();
    }
    public void  hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
       View view= this.getCurrentFocus();
        if(inputMethodManager!=null&&view!=null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void showRegister() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (registerFragment == null)
            registerFragment = new RegisterFragment();
        registerFragment.setActivity(this);
        currentFragment = registerFragment;
        transaction.add(R.id.loginRoot, registerFragment);
        transaction.commit();
    }

    public void showResetPassword() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (resetPasswordFragment == null)
            resetPasswordFragment = new ResetPasswordFragment();
        resetPasswordFragment.setActivity(this);
        currentFragment = resetPasswordFragment;
        transaction.add(R.id.loginRoot, resetPasswordFragment);
        transaction.commit();

    }
    public void showLogin() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (loginFragment == null)
            loginFragment = new LoginFragment();
        loginFragment.setActivity(this);
        currentFragment = loginFragment;
        transaction.add(R.id.loginRoot, loginFragment);
        transaction.commit();

    }

    public void showMenu() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (menuFragment == null)
            menuFragment = new MenuFragment();
        currentFragment = menuFragment;
        menuFragment.setActivity(this);
        transaction.add(R.id.loginRoot, menuFragment);
        transaction.commit();
    }

    public void showOtpSetting() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (otpSettingFragment == null)
            otpSettingFragment = new OtpSettingFragment();
        otpSettingFragment.setActivity(this);
        currentFragment = otpSettingFragment;
        transaction.add(R.id.loginRoot, otpSettingFragment);
        transaction.commit();
    }

    public void showTransfer() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (transferFragment == null)
            transferFragment = new TransferFragment();
        transferFragment.setActivity(this);
        currentFragment = transferFragment;
        transaction.add(R.id.loginRoot, transferFragment);
        transaction.commit();
    }

    public void showTransaction() {
        FragmentManager manager = this.getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (currentFragment != null) {
            transaction.remove(currentFragment);
            currentFragment = null;
        }
        if (transactionFragment == null)
            transactionFragment = new TransactionFragment();
        transactionFragment.setActivity(this);
        currentFragment = transactionFragment;
        transaction.add(R.id.loginRoot, transactionFragment);
        transaction.commit();
    }
    public void showGame1(){
        Intent intent=new Intent();
        intent.setClass(this,Game2Activity.class);
        startActivity(intent);
    }
    public void showGame2(){

        Intent intent=new Intent();
        intent.setClass(this,Game1Activity.class);
        startActivity(intent);
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }


}

