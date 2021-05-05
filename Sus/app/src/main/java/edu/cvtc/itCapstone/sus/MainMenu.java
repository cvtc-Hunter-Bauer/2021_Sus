package edu.cvtc.itCapstone.sus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.action_upcoming_payments);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_upcoming_payments:

                    break;
                case R.id.action_subscriptions:
                    Intent intent = new Intent(MainMenu.this, MainActivity.class);
                    MainMenu.this.startActivity(intent);
                    break;
                case R.id.action_graph:
                    //TODO:Have a Intent to the graph activity and remove Toast
                    //Intent intent = new Intent(MainActivity.this, Graph.class);
                    //MainActivity.this.startActivity(intent);
                    Toast.makeText(MainMenu.this, "Graph", Toast.LENGTH_SHORT).show();
                    break;                }
            return true;
        });


    }
}