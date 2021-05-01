package com.example.spotibot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.PlayerApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "2b1adf5ad6cd4c7aafe0acadea34ab1f";
    private static final String REDIRECT_URI = "https://spotibot.com/callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    Button start , stop;
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{permission.RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);

        textView = findViewById(R.id.text);
        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                textView.setText("Error!! Try Again!");
            }

            @Override
            public void onResults(Bundle results) {

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String string="";
                if(matches!=null)
                {
                    string = matches.get(0);
                    textView.setText(string);

                    if(string.contains("playback")) {
                        mSpotifyAppRemote.getPlayerApi().skipPrevious();
                    }
                    else if(string.contains("pause")){
                        mSpotifyAppRemote.getPlayerApi().pause();
                    }
                    else if(string.contains("resume")){
                        mSpotifyAppRemote.getPlayerApi().resume();
                    }
                    else if(string.contains("next")){
                        mSpotifyAppRemote.getPlayerApi().skipNext();
                    }
                    else{
                        textView.setText("Didn't get that! Try Again!");
                    }
//                    else if(string.contains("shuffle")){
//                        mSpotifyAppRemote.getPlayerApi().toggleShuffle();
//                    }
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });
    }

    public void StartButton(View view){
        textView.setText("Listening...");
        speechRecognizer.startListening(intentRecognizer);
    }

//    public void StopButton(View view){
//    speechRecognizer.stopListening();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        // We will start writing our code here.
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");
                        Toast.makeText(MainActivity.this, "Connected!!" , Toast.LENGTH_LONG);

                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);
                        Toast.makeText(MainActivity.this, "Not able to connect!!" , Toast.LENGTH_LONG);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // And we will finish off here.
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

}