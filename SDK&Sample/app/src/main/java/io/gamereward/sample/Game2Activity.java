package io.gamereward.sample;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import io.gamereward.grd.GrdCustomCallBack;
import io.gamereward.grd.GrdManager;
import io.gamereward.grd.GrdSessionData;
import io.gamereward.grd.GrdUser;

public class Game2Activity extends AppCompatActivity {
    HistoryFragment historyFragment;
    LeaderBoardFragment leaderBoardFragment;
    private View mProgressView;
    private TextView accountBalanceView,gameMessageView;
    private EditText betView;
    private ImageView randomCardView;
    private ImageView resultCardView;
    private Card randomCard=new Card();
    private Card resultCard;
    HashMap<String, Bitmap> cards = new HashMap<>();
    public static class Card {
        public int suit;
        public int symbol;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2);
        accountBalanceView = findViewById(R.id.account_balance);
        gameMessageView=findViewById(R.id.game_message);
        betView = findViewById(R.id.bet);
        accountBalanceView.setText("Balance:" + GrdManager.getUser().balance.toString());
        mProgressView = findViewById(R.id.progressBar);
        mProgressView.setVisibility(View.GONE);
        historyFragment = (HistoryFragment) getFragmentManager().findFragmentById(R.id.history);
        leaderBoardFragment = (LeaderBoardFragment) getFragmentManager().findFragmentById(R.id.leaderboard);
        randomCardView = findViewById(R.id.randomcard);
        resultCardView = findViewById(R.id.resultcard);
        findViewById(R.id.random_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomNextCard();
            }
        });

        Button lowButton= findViewById(R.id.low_button);
        lowButton.setTag("1");
        lowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame(v.getTag()=="1");
            }
        });
        Button highButton= findViewById(R.id.highbutton);
        highButton.setTag("0");
        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame(v.getTag()=="1");
            }
        });
        showProgress(true);
        hideCard(resultCardView);
        loadGame2();
        randomNextCard();

        showProgress(false);
    }
    private void playGame(boolean islow){
        hideCard(resultCardView);
        final int low=islow?1:0;
        final int cardSymbol=randomCard.symbol;
        double bet=Double.parseDouble(betView.getText().toString());
        GrdManager.callServerScript("testscript", "lowhighgame", new Object[]{low,cardSymbol, bet}, new GrdCustomCallBack() {
            @Override
            public void OnFinished(int error, String message, Object jsonObject) {
                if(error==0){
                    JSONArray jsonArray=(JSONArray)jsonObject;
                    try {
                        int result=jsonArray.getInt(0);
                        if(result==0){
                            resultCard=GrdManager.getObject(jsonArray.get(1),Card.class);
                            showCard(resultCardView,resultCard);
                            double money=jsonArray.getDouble(2);
                            GrdUser user=  GrdManager.getUser();
                            user.balance=user.balance.add(new BigDecimal(money));
                            accountBalanceView.setText(user.balance.toString());
                            GrdSessionData sessionData=new GrdSessionData();
                            sessionData.sessionstart=(new Date()).getTime();
                            sessionData.values.put("result","["+low+","+cardSymbol+","+resultCard.symbol+","+money+"]");
                            historyFragment.addHistory(sessionData);
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
                            gameMessageView.setText(jsonArray.getString(1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    gameMessageView.setText("Error:"+error+",message:"+message);
                }
            }
        });
    }
    private void randomNextCard(){
        Random rd=new Random();
        randomCard.suit= rd.nextInt(4);
        randomCard.symbol= rd.nextInt(12)+2;
        showCard(randomCardView,randomCard);
        hideCard(resultCardView);
    }
    private void showCard(ImageView imageView, Card card) {
        imageView.setImageBitmap(cards.get(card.symbol + "_" + card.suit));
    }

    private void hideCard(ImageView imageView) {
        imageView.setImageBitmap(cards.get("cardback"));
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadGame2() {
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open("cards/cardback.png");
            bitmap = BitmapFactory.decodeStream(istr);
            cards.put("cardback", bitmap);
        } catch (IOException e) {
            // handle exception
        }
        String[] suits = new String[]{"hearts", "diamonds", "spades", "clubs"};
        for (int i = 2; i <= 14; i++) {
            for (int j = 0; j < 4; j++) {
                try {
                    istr = assetManager.open("cards/" + i + "_of_" + suits[j] + ".png");
                    bitmap = BitmapFactory.decodeStream(istr);
                    cards.put(i + "_" + j, bitmap);
                } catch (IOException e) {
                    // handle exception
                }
            }
        }
        historyFragment.store = "LOWHIGHGAME";
        historyFragment.dataKey = new String[]{"result"};
        historyFragment.formatData = new HistoryFragment.IFormatSessionData() {
            @Override
            public String format(GrdSessionData data) {
                DateFormat dateFormat = new SimpleDateFormat("dd-MM HH:mm:ss");
                String st = dateFormat.format(data.getTime()) + "\n";
                if (data.values.containsKey("result")) {
                    try {
                        JSONArray array = new JSONArray(data.values.get("result"));
                        Double money = Double.parseDouble(array.get(3).toString());
                        boolean islow = array.getInt(0) == 1;
                        int randomSymbol = array.getInt(1);
                        int resultSymbol = array.getInt(2);
                        st += "CARD:" + randomSymbol + "SELECT:" + (islow ? "LOW" : "HIGH") + ",RESULT:" + resultSymbol + "\n";
                        if (money > 0) {
                            st += "WIN:" + money.toString();
                        } else if (money < 0) {
                            st += "LOSE:" + money.toString();
                        } else {
                            st += "DRAW";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
