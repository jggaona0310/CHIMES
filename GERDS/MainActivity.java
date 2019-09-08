package com.example.chimes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    private TextToSpeech mTTS;
    private Button mAudibleMode;
    private Button mSilentMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //keeps screen on

        mAudibleMode = findViewById(R.id.button_audiblemode);
        mSilentMode = findViewById(R.id.button_silentmode);

        //TTS for Location button
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.US);

                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language not supported");
                    } else {
                        mAudibleMode.setEnabled(true);
                        mSilentMode.setEnabled(true);
                    }
                } else {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

       /* //speak when CHIMES app is opened
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTTS.speak("Welcome", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 100);*/

    }//end of onCreate

    //Called activity Silent Mode Button is selected
    public void silentMode(View view) { //(onClick)
        Intent silentMode = new Intent(this, SilentModeActivity.class);
        //add settings as an extra for the intent - next page could use key for data
        startActivity(silentMode);
    }

    //Called activity Audible Mode Button is selected
    public void audibleMode(View view) {
        Intent audibleMode = new Intent(this, AudibleModeActivity.class);
        //add settings as an extra for the intent - next page could use key for data
        startActivity(audibleMode);
    }

    //Called activity when map is supposed to show
    public void viewMap(View view){
        Intent viewMap = new Intent(this, ViewMap.class);
        startActivity(viewMap);
    }

    @Override //override this method and destroy mTTS when done w it
    protected void onDestroy() {
        if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume(); //always call super first

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTTS.speak("Welcome", TextToSpeech.QUEUE_FLUSH, null);
            }
        }, 100);
    }
}
