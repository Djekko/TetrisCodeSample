package com.example.tetris;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends Activity {

    Button B_Return;
    Button B_ResetHS;
    TextView TV_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        setupButtons();
        displayText();

    }

    public void setupButtons(){

        B_ResetHS = (Button)findViewById(R.id.B_ResetHS);
        B_Return = (Button)findViewById(R.id.B_Return);

        B_ResetHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteSharedPreferences();
            }
        });

        B_Return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

            }
        });
    }

    public void displayText(){

        TV_info = (TextView)findViewById(R.id.TV_info);

        String TouchControls = "";
        String ArrowControls = "";
        String AppInfo;

        String tvText;

        TouchControls = "TOUCH CONTROLS\n" +
                "Swipe across the screen to move your blocks.\n" +
                "(SWIPE UP) = Rotate Blocks\n" +
                "(SWIPE LEFT) = Move Blocks Left\n" +
                "(SWIPE RIGHT) = Move Blocks Right\n" +
                "(SWIPE DOWN) = Blocks Fall Faster\n";

        ArrowControls = "ARROW CONTROLS\n" +
                "Use the arrow keys to move your blocks.\n" +
                "(UP) = Rotate Blocks\n" +
                "(LEFT) = Move Blocks Left\n" +
                "(RIGHT) = Move Blocks Right\n" +
                "(DOWN) = Blocks Fall Faster\n";


        AppInfo = "This app is a remake of the original made by Nintendo.\n " +
                "This app was made by Karl Spurgin";

        tvText = ArrowControls + "\n" + TouchControls;

        TV_info.setText(tvText);

    }

    public void deleteSharedPreferences(){
        SharedPreferences sp = getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        sp.edit().remove("HighScore").apply();

        Toast.makeText(this, "High Score has been reset to 0", Toast.LENGTH_SHORT).show();

    }
}
