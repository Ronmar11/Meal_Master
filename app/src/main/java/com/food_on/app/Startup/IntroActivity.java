package com.food_on.app.Startup;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.food_on.app.ChefFoodPanel.Chef_PostDish;
import com.food_on.app.ChefFoodPanel_BottomNavigation;
import com.food_on.app.ChooseOne;
import com.food_on.app.CustomerFoodPanel_BottomNavigation;
import com.food_on.app.Delivery_FoodPanelBottomNavigation;
import com.food_on.app.MainActivity;
import com.food_on.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class IntroActivity extends AppCompatActivity {
    LottieAnimationView Anim1;
    TextView textView;
    FirebaseAuth Fauth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Anim1 = findViewById(R.id.lottieAnimationView);
        textView = findViewById(R.id.textView2);

        Anim1.animate().alpha(0f).setDuration(0);
        textView.animate().alpha(0f).setDuration(0);
        Anim1.animate().alpha(0f).setDuration(0);
        Anim1.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            /**
             * {@inheritDoc}
             *
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.animate().alpha(1f).setDuration(800);
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
                startActivity(intent);

            }
        },3000);

    }
}