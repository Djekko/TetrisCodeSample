package com.example.tetris;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {

    Button B_AboutReturn;

    TextView TV_AboutInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        setUpAboutScreen();
        displayText();
    }

    public void setUpAboutScreen(){

        B_AboutReturn = (Button)findViewById(R.id.B_AboutReturn);

        B_AboutReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void displayText(){
        TV_AboutInfo = (TextView)findViewById(R.id.TV_AboutInfo);

        String AppInfo;

        AppInfo = "This app is a remake of the original made by Nintendo.\n " +
                "This app was made by Karl Spurgin";

        TV_AboutInfo.setText(AppInfo);
    }
}
