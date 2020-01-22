package com.zerochass.noble.a17;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DailySummaryActivity extends AppCompatActivity implements SummaryFragment.OnFragmentInteractionListener, ExpensesFragment.OnFragmentInteractionListener {
    private FragmentManager mFragmentManager = getSupportFragmentManager();
    private Long mSelectedDate;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;

            switch (item.getItemId()) {
                case R.id.navigation_summary:
                    fragment = mFragmentManager.findFragmentByTag("DailySummaryFragment");
                    if (fragment == null) {
                        mFragmentManager.beginTransaction().add(R.id.fl_daily, SummaryFragment.newInstance(), "DailySummaryFragment").commit();
                    } else {
                        mFragmentManager.beginTransaction().replace(R.id.fl_daily, fragment, "DailySummaryFragment").commit();
                    }
                    return true;
                case R.id.navigation_expenses:
                    fragment = mFragmentManager.findFragmentByTag("DailyExpensesFragment");
                    if (fragment == null) {
                        mFragmentManager.beginTransaction().add(R.id.fl_daily, ExpensesFragment.newInstance(), "DailyExpensesFragment").commit();
                    } else {
                        mFragmentManager.beginTransaction().replace(R.id.fl_daily, fragment, "DailyExpensesFragment").commit();
                    }
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_summary);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragmentManager.beginTransaction().add(R.id.fl_daily, SummaryFragment.newInstance(), "DailySummaryFragment").commit();
    }


    @Override
    public Long onFragmentInteraction() {
        return mSelectedDate;
    }

    @Override
    public void onFragmentInteraction(Long date) {
        this.mSelectedDate = date;
    }
}
