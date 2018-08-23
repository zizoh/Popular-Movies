package com.zizohanto.popularmovies.ui.details;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.utils.ActivityUtils;

public class DetailsActivity extends AppCompatActivity {
    public static final String MOVIE_TITLE_EXTRA = "MOVIE_TITLE_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_act);

        String title = getIntent().getStringExtra(MOVIE_TITLE_EXTRA);

        addFragmentToActivity(title);
    }

    private void addFragmentToActivity(String title) {
        Bundle bundle = new Bundle();
        bundle.putString(MOVIE_TITLE_EXTRA, title);

        DetailsFragment tasksFragment =
                (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (tasksFragment == null) {
            // Create the fragment
            tasksFragment = DetailsFragment.newInstance();
            tasksFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), tasksFragment, R.id.contentFrame);
        }
    }
}
