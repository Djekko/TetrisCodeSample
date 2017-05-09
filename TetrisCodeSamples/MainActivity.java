/**
 * Created by Karl Spurgin on 18/12/2016.
 */

package com.example.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private GameView gameView;

    Button B_Help;
    Button B_About;

    Button B_playButton;
    TextView TV_HighScore;

    public static Activity game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupMainActivity();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        displayHighScore();
    }
    public void setupMainActivity() {

        TV_HighScore = (TextView) findViewById(R.id.TV_HighScore);
        B_playButton = (Button)findViewById(R.id.B_playButton);
        B_Help = (Button)findViewById(R.id.B_Help);
        B_About = (Button)findViewById(R.id.B_About);

        B_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( getApplicationContext(), GameActivity.class ));
            }
        });

        B_Help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( getApplicationContext(), HelpActivity.class ));
            }
        });

        B_About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent( getApplicationContext(), AboutActivity.class ));
            }
        });

    }

    public void displayHighScore(){
        String hsTEXT = "HIGH SCORE\n" + String.valueOf(loadHighScore());

        TV_HighScore.setText(hsTEXT);
    }

    public long loadHighScore(){
        SharedPreferences sp = getSharedPreferences("HighScore", Context.MODE_PRIVATE);

        return sp.getLong("HighScore", 0);
    }
}
