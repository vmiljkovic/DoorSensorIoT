package com.iot.doorsensor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeviceGroupActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    MenuItem actionSettings;
    MenuItem actionAdd;

    public static ArrayList<DeviceInfo> mDeviceList = new ArrayList<>();
    public static ArrayList<GroupInfo> mGroupList = new ArrayList<>();

    ActionBar mActionBar = null;

    //private View mProgressView;
    public TextView mNavUser;
    public TextView mActionBarTitle;
    private int mSectionNumber;

    Fragment mFragmentSettings;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    TabLayout mTabLayout;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        mNavUser = (TextView)hView.findViewById(R.id.textView);

        mActionBar = getSupportActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(true);

        mActionBarTitle = findViewById(R.id.toolbar_title);
        mActionBarTitle.setText(getString(R.string.app_name));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        /*
      The {@link android.support.v4.view.PagerAdapter} that will provide
      fragments for each of the sections. We use a
      {@link FragmentPagerAdapter} derivative, which will keep every
      loaded fragment in memory. If this becomes too memory intensive, it
      may be best to switch to a
      {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */ /**
         * The {@link android.support.v4.view.PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link FragmentPagerAdapter} derivative, which will keep every
         * loaded fragment in memory. If this becomes too memory intensive, it
         * may be best to switch to a
         * {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //mProgressView = findViewById(R.id.getall_progress);

        if (getIntent().hasExtra(PlaceholderFragment.ARG_SECTION_NUMBER)) {
            mSectionNumber = getIntent().getExtras().getInt(PlaceholderFragment.ARG_SECTION_NUMBER);

            mViewPager.setCurrentItem(mSectionNumber);
        }

        FragmentManager fm = getSupportFragmentManager();
        mFragmentSettings = fm.findFragmentById(R.id.fragment);
        fm.beginTransaction().hide(mFragmentSettings).commit();

        if(SaveSharedPreference.getUserName(DoorSensorIoT.getContext()).length() == 0) {
            goToLogin();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (DoorSensorIoT.mServiceCaller.mUserName != null)
            mNavUser.setText(DoorSensorIoT.mServiceCaller.mUserName);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//    private void showProgress(final boolean show) {
//        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//        // for very easy animations. If available, use these APIs to fade-in
//        // the progress spinner.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//            mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
//            mViewPager.animate().setDuration(shortAnimTime).alpha(
//                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
//                }
//            });
//
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mProgressView.animate().setDuration(shortAnimTime).alpha(
//                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//                }
//            });
//        } else {
//            // The ViewPropertyAnimator APIs are not available, so simply show
//            // and hide the relevant UI components.
//            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
//            mViewPager.setVisibility(show ? View.GONE : View.VISIBLE);
//        }
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_device_group, menu);
        actionAdd = menu.getItem(0);
        actionSettings = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            if (mViewPager.getCurrentItem() == 0) {
                AddDeviceActivity.parents.push(getClass());
                Intent intent = new Intent(this, AddDeviceActivity.class);
                startActivity(intent);
            } else {
                AddGroupActivity.parents.push(getClass());
                Intent intent = new Intent(this, AddGroupActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Class fragmentClass = null;
        FragmentManager fm = getSupportFragmentManager();

        if (id == R.id.nav_devices) {
            fm.beginTransaction().hide(mFragmentSettings).commit();
            mViewPager.setVisibility(View.VISIBLE);
            mTabLayout.setVisibility(View.VISIBLE);
            mActionBarTitle.setText(getString(R.string.app_name));
        } else if (id == R.id.nav_sub_accounts) {
            fm.beginTransaction().show(mFragmentSettings).commit();
            fragmentClass = SubAccountsPreferenceFragment.class;
            mViewPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mActionBarTitle.setText(getString(R.string.pref_header_sub_accounts));
        } else if (id == R.id.nav_notifications) {
            fm.beginTransaction().show(mFragmentSettings).commit();
            fragmentClass = NotificationsPreferenceFragment.class;
            mViewPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mActionBarTitle.setText(getString(R.string.pref_header_notifications));
        } else if (id == R.id.nav_account) {
            fm.beginTransaction().show(mFragmentSettings).commit();
            fragmentClass = MyAccountPreferenceFragment.class;
            mViewPager.setVisibility(View.GONE);
            mTabLayout.setVisibility(View.GONE);
            mActionBarTitle.setText(getString(R.string.pref_header_my_account));
        } else if (id == R.id.nav_logout) {
            goToLogin();
        }

        try {
            if (fragmentClass != null) {
                fragment = (Fragment) fragmentClass.newInstance();
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void goToLogin() {
        DoorSensorIoT.mServiceCaller.mUserName = null;
        DoorSensorIoT.mServiceCaller.mPassword = null;
        SaveSharedPreference.clearUserCredentials(DoorSensorIoT.getContext());
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements BTDevice.OnItemClickListener, BTGroup.OnItemClickListener {

        private RecyclerView mRecyclerView;
        public static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {

        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onStart() {
            super.onStart();

            Bundle args = this.getArguments();
            int sectionNumber = args.getInt(ARG_SECTION_NUMBER);

            if (sectionNumber == 1) {
                getDevices();
            }
            else {
                getGroups();
            }
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_device_group, container, false);

            mRecyclerView = rootView.findViewById(R.id.paired_devices);
            mRecyclerView.setHasFixedSize(true);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            return rootView;
        }

        @Override
        public void onClick(View view, int position) {
            selectItem(position);
        }

        private void selectItem(int position) {
            Intent intent;
            Bundle args = this.getArguments();
            int sectionNumber = args.getInt(ARG_SECTION_NUMBER);

            if (sectionNumber == 1) {
                MainActivity.parents.push(getActivity().getClass());
                intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("mainContext", mDeviceList.get(position));
            }
            else {
                ListActivity.parents.push(getActivity().getClass());
                intent = new Intent(getActivity(), ListActivity.class);
                intent.putExtra("mainContext", mGroupList.get(position));
            }

            startActivity(intent);
        }

        public void getDevices() {
            //showProgress(true);
            CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller,"find-all-devices",
                    new ServiceCaller.VolleyCallback(){
                        @Override
                        public void onSuccess(@NonNull String result){
                            try {
                                Object json = new JSONTokener(result).nextValue();

                                if (json instanceof JSONObject) {
                                    //you have an object
                                    if (((JSONObject) json).has("message")) {
                                        String message = ((JSONObject) json).getString("message");
                                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                }
                                else if (json instanceof JSONArray) {
                                    JSONArray resultJSON = (JSONArray)json;
                                    if (resultJSON.length() > 0) {
                                        mDeviceList.clear();
                                        for (int i = 0; i < resultJSON.length(); i++) {
                                            JSONObject elementJson = resultJSON.getJSONObject(i);
                                            DeviceInfo temp = new DeviceInfo(elementJson.getInt("id"), elementJson.getString("name"), elementJson.optInt("groupId", -1), elementJson.getString("status"), elementJson.getInt("batteryStatus"), elementJson.getString("temperature"),false, false);
                                            mDeviceList.add(temp);
                                        }
                                        mRecyclerView.setAdapter(new BTDevice(mDeviceList, PlaceholderFragment.this));
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable throwable){
                            //do stuff here
                            Toast.makeText(getActivity().getApplicationContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

            mCallTask.execute();
        }

        public void getGroups() {
            CallerTask mCallTask = new CallerTask(DoorSensorIoT.mServiceCaller, "find-all-group",
                    new ServiceCaller.VolleyCallback(){
                        @Override
                        public void onSuccess(@NonNull String result){
                            try {
                                Object json = new JSONTokener(result).nextValue();

                                if (json instanceof JSONObject) {
                                    if (((JSONObject) json).has("message")) {
                                        String message = ((JSONObject) json).getString("message");
                                        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    }
                                }
                                else if (json instanceof JSONArray) {
                                    JSONArray resultJSON = (JSONArray)json;
                                    if (resultJSON.length() > 0) {
                                        mGroupList.clear();
                                        for (int i = 0; i < resultJSON.length(); i++) {
                                            JSONObject elementJson = resultJSON.getJSONObject(i);
                                            GroupInfo temp = new GroupInfo( elementJson.getInt("id"), elementJson.getString("name"), elementJson.getInt("iconId"));
                                            mGroupList.add(temp);
                                        }
                                        mRecyclerView.setAdapter(new BTGroup(mGroupList, PlaceholderFragment.this));
                                        mRecyclerView.getAdapter().notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(@NonNull Throwable throwable){
                            //do stuff here
                        }
                    });
            mCallTask.execute();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return DeviceGroupActivity.PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            return 2;
        }
    }
}