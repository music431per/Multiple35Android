package net.freeeeedom.multiple35;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    private static final int SWIPE_MIN_DISTANCE = 180;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static final int ANIMATION_VELOCITY = 300;

    private TextView tvNumber;
    private TextView tvScore;

    private GestureDetector mGestureDetector;
    private TranslateAnimation mTranslateTop;
    private TranslateAnimation mTranslateLeft;
    private TranslateAnimation mTranslateRight;
    private TranslateAnimation mTranslateButtom;

    private Timer mTimer;
    private Handler mHandler;

    private Random mRandom;
    private Context context;

    private int number;
    private int point;
    private float time;
    private float mLaptime;
    private int baseTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvScore = (TextView) findViewById(R.id.tv_score);

        mGestureDetector = new GestureDetector(this, mOnGestureListener);

        mTranslateLeft = new TranslateAnimation(0, -1000, 0, 0);
        mTranslateLeft.setDuration(ANIMATION_VELOCITY);
        mTranslateTop = new TranslateAnimation(0, 0, 0, -1000);
        mTranslateTop.setDuration(ANIMATION_VELOCITY);
        mTranslateRight = new TranslateAnimation(0, 1000, 0, 0);
        mTranslateRight.setDuration(ANIMATION_VELOCITY);
        mTranslateButtom = new TranslateAnimation(0, 0, 0, 1000);
        mTranslateButtom.setDuration(ANIMATION_VELOCITY);

        mTimer = new Timer(true);
        mHandler = new Handler();

        mRandom = new Random();
        context = this;

        reset();

        nextTurn();


    }

    @Override
    protected void onStart() {
        super.onStart();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // mHandlerを通じてUI Threadへ処理をキューイング
                mHandler.post(new Runnable() {
                    public void run() {

                        //実行間隔分を加算処理
                        mLaptime += 0.1d;


                        time = baseTime - mLaptime;

                        //計算にゆらぎがあるので小数点第1位で丸める
                        BigDecimal bi = new BigDecimal(time);
                        time = bi.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();

                        tvScore.setText("time : " + time + "\npoint : " + point);

                        Log.d("time" + mLaptime, "baseTime" + baseTime);
                        if (mLaptime >= baseTime) {
                            mTimer.cancel();
                            Intent intent = new Intent(context, EndActivity.class);
                            intent.putExtra("score", point);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
            }
        }, 100, 100);

    }

    // これがないとGestureDetectorが動かない
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private final GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {

            try {

                // 開始位置から終了位置の移動距離が指定値より大きい
                // 軸の移動速度が指定値より大きいの判定

                if (event1.getY() - event2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    // 下から上
                    decisionPoint(1);
                    tvNumber.startAnimation(mTranslateTop); // アニメーション適用


                } else if (event2.getY() - event1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    // 上から下
                    decisionPoint(2);
                    tvNumber.startAnimation(mTranslateButtom); // アニメーション適用

                } else if (event1.getX() - event2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // 右から左
                    decisionPoint(3);
                    tvNumber.startAnimation(mTranslateLeft); // アニメーション適用

                } else if (event2.getX() - event1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    // 左から右
                    decisionPoint(4);
                    tvNumber.startAnimation(mTranslateRight); // アニメーション適用

                }


            } catch (Exception e) {
                // nothing
            }
            return false;
        }
    };

    private void reset() {
        number = 0;
        point = 0;
        time = 0;
        baseTime = 60;
    }

    private void createNumber() {
        number = mRandom.nextInt(100);
    }

    private void nextTurn() {
        createNumber();
        tvNumber.setText(String.valueOf(number));
    }

    private void decisionPoint(int i) {
        switch (i) {
            case 1:
                // other の処理
                if (!check5() && !check3()) {
                    point++;
                } else {
                    baseTime = baseTime - 3;
                }
                break;
            case 2:
                // x3 and x5 の処理
                if (check5() && check3()) {
                    point++;
                } else {
                    baseTime = baseTime - 3;
                }
                break;
            case 3:
                // x5 の処理
                if (check5() && !check3()) {
                    point++;
                } else {
                    baseTime = baseTime - 3;
                }
                break;
            case 4:
                // x3 の処理
                if (!check5() && check3()) {
                    point++;
                } else {
                    baseTime = baseTime - 3;
                }
                break;
            default:
                return;
        }
        nextTurn();

    }

    // 5で割り切れたらtrue
    private boolean check5() {
        if (number % 5 == 0) {
            return true;
        }
        return false;
    }

    // 3で割り切れたらtrue
    private boolean check3() {
        if (number % 3 == 0) {
            return true;
        }
        return false;
    }

}
