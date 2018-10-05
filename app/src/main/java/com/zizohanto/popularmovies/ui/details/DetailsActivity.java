package com.zizohanto.popularmovies.ui.details;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zizohanto.popularmovies.R;
import com.zizohanto.popularmovies.utils.ActivityUtils;

public class DetailsActivity extends AppCompatActivity {
    public static final String MOVIE_ID_EXTRA = "MOVIE_ID_EXTRA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_act);

        Integer id = getIntent().getIntExtra(MOVIE_ID_EXTRA, 0);

        addFragmentToActivity(id);
    }

    private void addFragmentToActivity(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(MOVIE_ID_EXTRA, id);

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
