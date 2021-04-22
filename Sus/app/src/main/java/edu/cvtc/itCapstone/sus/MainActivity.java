package edu.cvtc.itCapstone.sus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final String MY_PREFS = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSub.class);
                MainActivity.this.startActivity(intent);
                //Toast.makeText(MainActivity.this, "Fab", Toast.LENGTH_SHORT).show();
            }
        });


        SharedPreferences sp = getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        if (sp.getBoolean("firstStart", true)) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();

            View overlayPageOne = findViewById(R.id.OverlayPageOne);
            overlayPageOne.setVisibility(View.VISIBLE);
        }
    }

    public void closeTutorial(View view) {
        View overlayPageOne = findViewById(R.id.OverlayPageOne);
        overlayPageOne.setVisibility(View.INVISIBLE);
    }

}