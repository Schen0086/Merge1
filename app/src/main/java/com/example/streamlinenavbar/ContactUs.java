package com.example.streamlinenavbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContactUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavView_Bar);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        TextView websiteTextView = findViewById(R.id.website);
        String websiteText = getString(R.string.website);
        Spanned spannedText = Html.fromHtml(websiteText, Html.FROM_HTML_MODE_LEGACY);
        websiteTextView.setText(spannedText);
        websiteTextView.setMovementMethod(LinkMovementMethod.getInstance());


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                onBackPressed();
                if (itemId == R.id.ic_arrow) {
                    onBackPressed();
                } else if (itemId == R.id.ic_team) {
                    Intent intent1 = new Intent(ContactUs.this, team.class);
                    intent1.putExtra("name", getIntent().getStringExtra("name"));
                    intent1.putExtra("email", getIntent().getStringExtra("email"));
                    intent1.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(team.class);
                    startActivity(intent1);
                    finish();
                } else if (itemId == R.id.ic_home) {
                    Intent intent2 = new Intent(ContactUs.this, HomePage.class);
                    intent2.putExtra("name", getIntent().getStringExtra("name"));
                    intent2.putExtra("email", getIntent().getStringExtra("email"));
                    intent2.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(HomePage.class);
                    startActivity(intent2);
                    finish();
                } else if (itemId == R.id.ic_more) {
                    Intent intent3 = new Intent(ContactUs.this, More.class);
                    intent3.putExtra("name", getIntent().getStringExtra("name"));
                    intent3.putExtra("email", getIntent().getStringExtra("email"));
                    intent3.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(More.class);
                    startActivity(intent3);
                    finish();
                } else if (itemId == R.id.ic_profile) {
                    Intent intent4 = new Intent(ContactUs.this, ProfilePage.class);
                    intent4.putExtra("name", getIntent().getStringExtra("name"));
                    intent4.putExtra("email", getIntent().getStringExtra("email"));
                    intent4.putExtra("age", getIntent().getStringExtra("age"));
                    navigateToActivity(ProfilePage.class);
                    startActivityForResult(intent4, 1);
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        String previousActivity = MyApp.getPreviousActivity();
        if (previousActivity != null) {
            try {
                Class<?> previousActivityClass = Class.forName(previousActivity);
                Intent intent = new Intent(ContactUs.this, previousActivityClass);
                intent.putExtra("name", getIntent().getStringExtra("name"));
                intent.putExtra("email", getIntent().getStringExtra("email"));
                intent.putExtra("age", getIntent().getStringExtra("age"));
                startActivity(intent);
                finish();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        String previousActivity = getClass().getName();
        MyApp.setPreviousActivity(previousActivity);
        Intent intent = new Intent(ContactUs.this, activityClass);
        intent.putExtra("name", getIntent().getStringExtra("name"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("age", getIntent().getStringExtra("age"));
        startActivity(intent);
        finish();
    }
}