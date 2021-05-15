/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.homepage;

import android.content.res.Resources;
import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.settings.SettingsEnums;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.preference.SwitchPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragmentCompat;

import com.android.internal.util.UserIcons;
import com.android.settings.DisplaySettings;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import android.widget.EditText;

import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.accounts.AvatarViewMixin;
import com.android.settings.core.HideNonSystemOverlayMixin;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;

import com.android.settingslib.drawable.CircleFramedDrawable;
import java.util.Calendar;
import java.util.Random;

public class SettingsHomepageActivity extends FragmentActivity {

    Context context;
    ImageView avatarView;
    UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_homepage_container);
        final View root = findViewById(R.id.settings_homepage_container);
        root.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        setHomepageContainerPaddingTop();

        Context context = getApplicationContext();

        mUserManager = context.getSystemService(UserManager.class);

//        getRandomName();
        goodVibesPlease();

        final Toolbar toolbar = findViewById(R.id.search_action_bar);
        FeatureFactory.getFactory(this).getSearchFeatureProvider()
                .initSearchToolbar(this /* activity */, toolbar, SettingsEnums.SETTINGS_HOMEPAGE);

      final TextView homepageUsernameTextView = root.findViewById(R.id.userNameTextView); 
      getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));

        avatarView = root.findViewById(R.id.account_avatar);
        //final AvatarViewMixin avatarViewMixin = new AvatarViewMixin(this, avatarView);
        avatarView.setImageDrawable(getCircularUserIcon(context));
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$UserSettingsActivity"));
                startActivity(intent);
            }
        });
	String name = mUserManager.getUserName();
	homepageUsernameTextView.setText(name!=null?name:"User");
        //getLifecycle().addObserver(avatarViewMixin);

        if (!getSystemService(ActivityManager.class).isLowRamDevice()) {
            // Only allow contextual feature on high ram devices.
            showFragment(new ContextualCardsFragment(), R.id.contextual_cards_content);
        }
        showFragment(new TopLevelSettings(), R.id.main_content);
        ((FrameLayout) findViewById(R.id.main_content))
                .getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void showFragment(Fragment fragment, int id) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment showFragment = fragmentManager.findFragmentById(id);

        if (showFragment == null) {
            fragmentTransaction.add(id, fragment);
        } else {
            fragmentTransaction.show(showFragment);
        }
        fragmentTransaction.commit();
    }

//    private void getRandomName(){
//    Resources res = getResources();
//    String[] array = res.getStringArray(R.array.random_user_names);
//    String randomName = array[new Random().nextInt(array.length)];
//    final EditText homepageUsernameTextView = root.findViewById(R.id.userNameTextView);
//    homepageUsernameTextView.setText(homepageUsernameTextView);

//    }

    private void goodVibesPlease(){

    int NewDashAnim = Settings.System.getInt(getApplicationContext().getContentResolver(),
        Settings.System.GREETING_MODE_ANIMATIONS, 1);
        Calendar c = Calendar.getInstance();
    int hours = c.get(Calendar.HOUR_OF_DAY);
    String greeting = null;
    TextView homePageGreetingTextView=(TextView) findViewById(R.id.greetingsTextView);
    View root = findViewById(R.id.settings_homepage_container);

    switch (NewDashAnim) {

    case 0:

    greeting = getGreetingMessage(hours);

    break;

    case 1:
    setFullscreen();
    setTextColorWhite();
    greeting = getGreetingMessage(hours);
    if(hours>=04 && hours<=11){

        root.setBackground(getResources().getDrawable(R.drawable.background_morning));
        setTextColorBlack();
    } else if(hours>=12 && hours<=15){

        root.setBackground(getResources().getDrawable(R.drawable.background_noon));
        setTextColorBlack();
    } else if(hours>=16 && hours<=20){

        root.setBackground(getResources().getDrawable(R.drawable.background_evening));
        setTextColorWhite();
    } else if((hours>=21 && hours<=24) || hours==0 ){

        root.setBackground(getResources().getDrawable(R.drawable.background_night));
        setTextColorWhite();
    } else {

        root.setBackground(getResources().getDrawable(R.drawable.background_ufo));
        setTextColorWhite();
    }

    break;

    case 2:
    setFullscreen();
    greeting = getGreetingMessage(hours);
    if(hours>=04 && hours<=11){

        root.setBackground(getResources().getDrawable(R.drawable.background_morning_liq));
        setTextColorBlack();

    } else if(hours>=12 && hours<=15){

        root.setBackground(getResources().getDrawable(R.drawable.background_noon_liq));
        setTextColorBlack();

    } else if(hours>=16 && hours<=20){

        root.setBackground(getResources().getDrawable(R.drawable.background_evening_liq));
        setTextColorWhite();

    } else if((hours>=21 && hours<=24) || hours==0 ){

        root.setBackground(getResources().getDrawable(R.drawable.background_night_liq));
        setTextColorWhite();

    } else {

        root.setBackground(getResources().getDrawable(R.drawable.background_ufo_liq));
        setTextColorWhite();

    }

    break;

    }

    homePageGreetingTextView.setText(greeting);

 }

    @VisibleForTesting
    void setHomepageContainerPaddingTop() {
        final View view = this.findViewById(R.id.homepage_container);

        // Prevent inner RecyclerView gets focus and invokes scrolling.
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    private Drawable getCircularUserIcon(Context context) {
        Bitmap bitmapUserIcon = mUserManager.getUserIcon(UserHandle.myUserId());

        if (bitmapUserIcon == null) {
            // get default user icon.
            final Drawable defaultUserIcon = UserIcons.getDefaultUserIcon(
                    context.getResources(), UserHandle.myUserId(), false);
            bitmapUserIcon = UserIcons.convertToBitmap(defaultUserIcon);
        }
        Drawable drawableUserIcon = new CircleFramedDrawable(bitmapUserIcon,
                (int) context.getResources().getDimension(R.dimen.circle_avatar_size));

        return drawableUserIcon;
    }

    @Override
    public void onResume() {
        super.onResume();
        avatarView.setImageDrawable(getCircularUserIcon(getApplicationContext()));
        goodVibesPlease();

    }
    
    private String getGreetingMessage(int hours){
        Resources res = getResources();
        String greeting_gm = res.getString(R.string.greeting_gm);
        String greeting_ga = res.getString(R.string.greeting_ga);
        String greeting_ge = res.getString(R.string.greeting_ge);
        String greeting_gn = res.getString(R.string.greeting_gn);
        String greeting_wte = res.getString(R.string.greeting_wte);
        if(hours>=04 && hours<=11){

            return greeting_gm;

        } else if(hours>=12 && hours<=15){

            return greeting_ga;

        } else if(hours>=16 && hours<=20){

            return greeting_ge;

        } else if((hours>=21 && hours<=24) || hours==0 ){

            return greeting_gn;

        } else {

            return greeting_wte;

        }
    }
    private void setFullscreen(){
        View root = findViewById(R.id.settings_homepage_container);
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN );
    }
    private void setTextColorBlack(){
        TextView homePageGreetingTextView=(TextView) findViewById(R.id.greetingsTextView);
        TextView homepageUsernameTextView=(TextView) findViewById(R.id.userNameTextView);
        TextView homepageSettingsTextView=(TextView) findViewById(R.id.settings_header);
        homepageSettingsTextView.setTextColor(0xff000000);
        homePageGreetingTextView.setTextColor(0xff000000);
        homepageUsernameTextView.setTextColor(0xff000000);
    }
    private void setTextColorWhite(){
        TextView homePageGreetingTextView=(TextView) findViewById(R.id.greetingsTextView);
        TextView homepageUsernameTextView=(TextView) findViewById(R.id.userNameTextView);
        TextView homepageSettingsTextView=(TextView) findViewById(R.id.settings_header);
        homepageSettingsTextView.setTextColor(0xffffffff);
        homePageGreetingTextView.setTextColor(0xffffffff);
        homepageUsernameTextView.setTextColor(0xffffffff);
    }
}
