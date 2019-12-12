package com.musheng.android.common.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.musheng.android.common.log.MSLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/11/29 10:59
 * Description :
 */
public class MoneyPlayer {

    private static MoneyPlayer moneyPlayer = new MoneyPlayer();

    private MoneyPlayer() {
    }

    public static MoneyPlayer getInstance(){
        return moneyPlayer;
    }

    public void play(final Context context, final int money){
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> strings = new ArrayList<>();
                strings.add("voice/money_lianhe.mp3");
                strings.addAll(readIntPart(String.valueOf(money), true));
                strings.add("voice/money_cny.mp3");
                play(context, strings);
            }
        }.start();
    }

    public void play(final Context context, final float money){
        MSLog.d("prepare " + money);
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<String> strings = new ArrayList<>();
                strings.add("voice/money_lianhe.mp3");
                String str = String.valueOf(money);
                String[] split = str.split("\\.");
                if(split.length > 0){
                    strings.addAll(readIntPart(split[0], true));
                }
                if(split.length > 1){
                    strings.add("voice/money_pot.mp3");
                    strings.addAll(readIntPart(split[1], false));
                }
                strings.add("voice/money_cny.mp3");

                MSLog.d("start " + money);
                play(context, strings);
                MSLog.d("stop " + money);
            }
        }.start();
    }

    private void play(final Context context, final List<String> voicePlay){
        synchronized (MoneyPlayer.this) {

            final MediaPlayer mMediaPlayer = new MediaPlayer();
            final CountDownLatch mCountDownLatch = new CountDownLatch(1);
            AssetFileDescriptor assetFileDescription = null;

            try {
                final int[] counter = {0};

                assetFileDescription = context.getAssets().openFd(voicePlay.get(counter[0]));

                mMediaPlayer.setDataSource(
                        assetFileDescription.getFileDescriptor(),
                        assetFileDescription.getStartOffset(),
                        assetFileDescription.getLength());

                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.reset();
                        counter[0]++;

                        if (counter[0] < voicePlay.size()) {
                            try {
                                AssetFileDescriptor fileDescription2 = context.getAssets().openFd(voicePlay.get(counter[0]));
                                mediaPlayer.setDataSource(
                                        fileDescription2.getFileDescriptor(),
                                        fileDescription2.getStartOffset(),
                                        fileDescription2.getLength());
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                                mCountDownLatch.countDown();
                            }
                        } else {
                            mediaPlayer.release();
                            mCountDownLatch.countDown();
                        }
                    }
                });

                mMediaPlayer.prepareAsync();

            } catch (Exception e) {
                e.printStackTrace();
                mCountDownLatch.countDown();
            } finally {
                if (assetFileDescription != null) {
                    try {
                        assetFileDescription.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                mCountDownLatch.await();
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] CHINESE_UNIT = {'元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟'};

    /**
     * 返回关于钱的中文式大写数字,支仅持到亿
     */
    private String readInt(int moneyNum) {
        String res = "";
        int i = 0;
        if (moneyNum == 0) {
            return "0";
        }

        if (moneyNum == 10) {
            return "拾";
        }

        if (moneyNum > 10 && moneyNum < 20) {
            return "拾" + moneyNum % 10;
        }

        while (moneyNum > 0) {
            res = CHINESE_UNIT[i++] + res;
            res = NUM[moneyNum % 10] + res;
            moneyNum /= 10;
        }

        return res.replaceAll("0[拾佰仟]", "0")
                .replaceAll("0+亿", "亿")
                .replaceAll("0+万", "万")
                .replaceAll("0+元", "元")
                .replaceAll("0+", "0")
                .replace("元", "");
    }


    /**
     * 返回数字对应的音频
     *
     * @param integerPart
     * @return
     */
    private List<String> readIntPart(String integerPart, boolean isSplit) {
        List<String> result = new ArrayList<>();
        String intString = readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i = 0; i < len; i++) {
            char current = intString.charAt(i);
            if (current == '拾') {
                if(isSplit){
                    result.add("voice/money_10.mp3");
                }
            } else if (current == '佰') {
                if(isSplit) {
                    result.add("voice/money_100.mp3");
                }
            } else if (current == '仟') {
                if(isSplit) {
                    result.add("voice/money_1000.mp3");
                }
            } else if (current == '万') {
                if(isSplit) {
                    result.add("voice/money_10000.mp3");
                }
            } else if (current == '亿') {
                if(isSplit) {
                    result.add("voice/money_100000000.mp3");
                }
            } else {
                result.add("voice/money_" + current + ".mp3");
            }
        }
        return result;
    }

}

