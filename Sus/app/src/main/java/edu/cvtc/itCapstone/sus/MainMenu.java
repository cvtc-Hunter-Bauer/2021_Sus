package edu.cvtc.itCapstone.sus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

=======
>>>>>>> b73a1fa266708f807a15cedfb2735a20979ee6d9
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);



        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
<<<<<<< HEAD
=======
        bottomNavigationView.setSelectedItemId(R.id.action_upcoming_payments);

>>>>>>> b73a1fa266708f807a15cedfb2735a20979ee6d9
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

<<<<<<< HEAD
=======

>>>>>>> b73a1fa266708f807a15cedfb2735a20979ee6d9
    }
}