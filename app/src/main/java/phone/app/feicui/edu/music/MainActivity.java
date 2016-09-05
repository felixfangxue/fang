package phone.app.feicui.edu.music;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button btnPause, btnPlayUrl, btnStop,btnReplay;
    private SeekBar skbProgress;
    private Player player;
    private EditText file_name_text;
    private TextView tipsView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("在线音乐播放---ouyangpeng编写");

        btnPlayUrl = (Button) this.findViewById(R.id.btnPlayUrl);
        btnPlayUrl.setOnClickListener(new ClickEvent());

        btnPause = (Button) this.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new ClickEvent());

        btnStop = (Button) this.findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new ClickEvent());

        btnReplay = (Button) this.findViewById(R.id.btnReplay);
        btnReplay.setOnClickListener(new ClickEvent());

        file_name_text=(EditText) this.findViewById(R.id.file_name);
        tipsView=(TextView) this.findViewById(R.id.tips);

        skbProgress = (SeekBar) this.findViewById(R.id.skbProgress);
        skbProgress.setOnSeekBarChangeListener(new SeekBarChangeEvent());

        String url=file_name_text.getText().toString();
        player = new Player(url,skbProgress);

        TelephonyManager telephonyManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * 只有电话来了之后才暂停音乐的播放
     */
    private final class MyPhoneListener extends android.telephony.PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING://电话来了
                    player.callIsComing();
                    break;
                case TelephonyManager.CALL_STATE_IDLE: //通话结束
                    player.callIsDown();
                    break;
            }
        }
    }

    class ClickEvent implements OnClickListener {
        @Override
        public void onClick(View arg0) {
            if (arg0 == btnPause) {
                boolean pause=player.pause();
                if (pause) {
                    btnPause.setText("继续");
                    tipsView.setText("音乐暂停播放...");
                }else{
                    btnPause.setText("暂停");
                    tipsView.setText("音乐继续播放...");
                }
            } else if (arg0 == btnPlayUrl) {
                player.play();
                tipsView.setText("音乐开始播放...");
            } else if (arg0 == btnStop) {
                player.stop();
                tipsView.setText("音乐停止播放...");
            } else if (arg0==btnReplay) {
                player.replay();
                tipsView.setText("音乐重新播放...");
            }
        }
    }

    class SeekBarChangeEvent implements SeekBar.OnSeekBarChangeListener {
        int progress;
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
            this.progress = progress * player.mediaPlayer.getDuration()
                    / seekBar.getMax();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
            player.mediaPlayer.seekTo(progress);
        }
    }

}