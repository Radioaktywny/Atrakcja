package com.example.projekt_atrakcja;



import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class MainActivity extends TabActivity {
    
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost tabHost = getTabHost();
       
     // Tab for Search
        TabSpec searchspac = tabHost.newTabSpec("Search");
        
     // setting Title and Icon for the Tab
        searchspac.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.icon_search_tab, null));
        Intent searchIntent = new Intent(this, SearchActivity.class);
        searchspac.setContent(searchIntent);
         
        // Tab for Mark Place
       
        TabSpec mark_placespec = tabHost.newTabSpec("Mark Place");
        mark_placespec.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.icon_mark_place_tab, null));
        Intent mark_placeIntent = new Intent(this, Mark_placeActivity.class);
        mark_placespec.setContent(mark_placeIntent);
         
        // Tab for Profile
        TabSpec profilespec = tabHost.newTabSpec("Profile");
        profilespec.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.icon_profile_tab, null));
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        profilespec.setContent(profileIntent);
        
        // Tab for Challenge
        TabSpec challengespec = tabHost.newTabSpec("Challenge");
        challengespec.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.icon_challenge_tab, null));
        Intent challengeIntent = new Intent(this, ChallengeActivity.class);
        challengespec.setContent(challengeIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(searchspac); // Adding search tab
        tabHost.addTab(mark_placespec); // Adding mark_place tab
        tabHost.addTab(profilespec); // Adding profile tab
        tabHost.addTab(challengespec); // Adding challenge tab
    }
}