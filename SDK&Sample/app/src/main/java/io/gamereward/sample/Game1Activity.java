package io.gamereward.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.gamereward.grd.GrdCustomCallBack;
import io.gamereward.grd.GrdManager;
import io.gamereward.grd.GrdSessionData;
import io.gamereward.grd.GrdUser;

public class Game1Activity extends AppCompatActivity {
    HistoryFragment historyFragment;
    LeaderBoardFragment leaderBoardFragment;
    private View mProgressView;
    private TextView accountBalanceView,gameMessageView;
    private EditText betView;
    Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game1);
        accountBalanceView = findViewById(R.id.account_balance);
        gameMessageView=findViewById(R.id.game_message);
        betView=findViewById(R.id.bet);
        accountBalanceView.setText("Balance:" + GrdManager.getUser().balance.toString());
        mProgressView = findViewById(R.id.progressBar);
        mProgressView.setVisibility(View.GONE);
        historyFragment = (HistoryFragment) getFragmentManager().findFragmentById(R.id.history);
        leaderBoardFragment = (LeaderBoardFragment) getFragmentManager().findFragmentById(R.id.leaderboard);
        createGame1();
    }

    private void showProgress(boolean show){
        mProgressView.setVisibility(show?View.VISIBLE:View.GONE);
    }
    private void createGame1() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        GridLayout layout = findViewById(R.id.rootView);
        int w = width / 3;
        w = w - w / 4;
        buttons = new Button[9];
        for (int index = 0; index < 9; index++) {
            Button btn = new Button(this);
            btn.setHeight(w);
            btn.setWidth(w);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btn.setBackground(getDrawable(R.drawable.square_normal));
            }
            buttons[index] = btn;
            btn.setText((index + 1) + "");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int number = Integer.parseInt(((Button) v).getText().toString());
                    double bet = Double.parseDouble(betView.getText().toString());
                    Object[] params = new Object[2];
                    params[0] = number;
                    params[1] = bet;
                    for (int k = 0; k < buttons.length; k++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            buttons[k].setBackground(getDrawable(R.drawable.square_normal));
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        buttons[number - 1].setBackground(getDrawable(R.drawable.square_selected));
                    }
                    //showProgress(true);
                    GrdManager.callServerScript("testscript", "random9", params, new GrdCustomCallBack() {

                        @Override
                        public void OnFinished(int error, String message, Object jsonObject) {
                            //showProgress(false);
                            if (error == 0) {
                                JSONArray jsonArray = (JSONArray) jsonObject;
                                int result = 0;
                                try {
                                    result = (int) jsonArray.get(0);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (result == 0) {
                                    int yourNumber = 0;
                                    try {
                                        yourNumber = jsonArray.getInt(2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    int randNumber = 0;
                                    try {
                                        randNumber = jsonArray.getInt(1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    double money = 0;
                                    try {
                                        money = jsonArray.getDouble(3);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (randNumber == yourNumber) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            buttons[randNumber - 1].setBackground(getDrawable(R.drawable.square_win));
                                        }
                                    } else {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            buttons[randNumber - 1].setBackground(getDrawable(R.drawable.square_win));
                                            buttons[yourNumber - 1].setBackground(getDrawable(R.drawable.square_lose));
                                        }

                                    }
                                    GrdSessionData session = new GrdSessionData();
                                    session.sessionstart = (new Date()).getTime();
                                    session.values.put("rand", yourNumber + "," + randNumber + "," + money);
                                    historyFragment.addHistory(session);
                                    GrdUser user = GrdManager.getUser();
                                    user.balance = user.balance.add(new BigDecimal(money));
                                    accountBalanceView.setText("Balance:" + user.balance.toString());
                                    if(money>0){
                                        gameMessageView.setText("CONGRATULATIONS! YOU WIN:"+money);
                                    }
                                    else if(money<0){
                                        gameMessageView.setText("NOT LUCKY YET! LOSE:"+money);

                                    }
                                    else {
                                        gameMessageView.setText("DRAW");

                                    }
                                }
                                else{
                                    try {
                                        gameMessageView.setText(jsonArray.getString(1));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            else{
                                gameMessageView.setText("Error:"+error+",message:"+message);
                            }
                        }
                    });
                }
            });
            layout.addView(btn);
        }
        historyFragment.store = "GAME-9";
        historyFragment.dataKey = new String[]{"rand"};
        historyFragment.formatData = new HistoryFragment.IFormatSessionData() {
            @Override
            public String format(GrdSessionData data) {
                DateFormat dateFormat =new SimpleDateFormat("dd-MM HH:mm:ss");
                String st =dateFormat.format(data.getTime())+ "\n";
                if (data.values.containsKey("rand")) {
                    String[] arr = data.values.get("rand").split(",");
                    Double money = Double.parseDouble(arr[2]);
                    st += "SELECT:" + arr[0] + ",RESULT:" + arr[1] + "\n";
                    if (money > 0) {
                        st += "WIN:" + money.toString();
                    } else if (money < 0) {
                        st += "LOSE:" + money.toString();
                    } else {
                        st += "DRAW";
                    }
                }
                return st;
            }
        };
        historyFragment.loadData();
        leaderBoardFragment.scoreType = "random9_score";
        leaderBoardFragment.loadData();
    }
}
