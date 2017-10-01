package com.example.android.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.bakingapp.models.Recipe;
import com.example.android.bakingapp.models.Step;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Strings;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Ingriedient detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link IngredientListActivity}.
 */
public class IngredientDetailActivity extends AppCompatActivity {

    private static final String TAG = IngredientDetailActivity.class.getSimpleName();

    @BindView(R.id.step_description) TextView mDescription;
    @BindView(R.id.step_video)
    SimpleExoPlayerView mExoPlayerView;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @BindBool(R.bool.two_pane) boolean mTwoPane;

    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Step mStep;
    private Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_detail);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(Step.TAG) && intent.hasExtra(Recipe.TAG)) {
            mStep = intent.getExtras().getParcelable(Step.TAG);
            mRecipe = intent.getExtras().getParcelable(Recipe.TAG);
        }
        onRestoreInstanceState(savedInstanceState);
        if( mStep == null)
        {
            throw new IllegalArgumentException("Pass step");
        }
        if( mRecipe == null)
        {
            throw new IllegalArgumentException("Pass recipe");
        }
        mDescription.setText(mStep.getDescription());


        if (mTwoPane) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Context context = IngredientDetailActivity.this;
            Class destinationActivity = IngredientListActivity.class;
            Intent startChildActivityIntent = new Intent(context, destinationActivity);
            startChildActivityIntent.putExtra(Step.TAG, mStep);
            startChildActivityIntent.putExtra(Recipe.TAG, mRecipe);

            startActivity(startChildActivityIntent);
        }

        initializeMediaSession();
        initializePlayer(mStep);
    }

    private void initializePlayer(Step step) {
        if (mExoPlayer == null && !Strings.isNullOrEmpty(step.getVideoURL())) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            //mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this, getString(R.string.StepPlayer));
            Uri mediaUri = Uri.parse(step.getVideoURL());
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this, userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }else{
            mExoPlayerView.setVisibility(View.INVISIBLE);
        }
    }
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRecipe != null){
            outState.putParcelable(Recipe.TAG, mRecipe);
        }if(mStep != null){
            outState.putParcelable(Step.TAG, mStep);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState == null){
            return;
        }
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.containsKey(Recipe.TAG)){
            mRecipe = savedInstanceState.getParcelable(Recipe.TAG);
        } if(savedInstanceState.containsKey(Step.TAG)){
            mStep = savedInstanceState.getParcelable(Step.TAG);
        }
    }
    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        mMediaSession.setActive(false);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, IngredientListActivity.class);
            intent.putExtra(Recipe.TAG, mRecipe);
            intent.putExtra(Step.TAG, mStep);
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
