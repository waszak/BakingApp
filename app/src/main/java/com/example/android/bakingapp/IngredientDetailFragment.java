package com.example.android.bakingapp;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment representing a single Ingriedient detail screen.
 * This fragment is either contained in a {@link IngredientListActivity}
 * in two-pane mode (on tablets) or a {@link IngredientDetailActivity}
 * on handsets.
 */
public class IngredientDetailFragment extends Fragment {

    private static final String TAG = IngredientDetailFragment.class.getSimpleName();
    private Step mStep;
    private Recipe mRecipe;
    @BindView(R.id.step_description)
    TextView mDescription;
    @BindView(R.id.step_video)
    SimpleExoPlayerView mExoPlayerView;

    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public IngredientDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args.containsKey(Step.TAG)) {
            if(args.containsKey(Recipe.TAG)){
                mRecipe = args.getParcelable(Recipe.TAG);
            } if(args.containsKey(Step.TAG)){
                mStep = args.getParcelable(Step.TAG);
            }

        }
        if(savedInstanceState != null && savedInstanceState.containsKey(Recipe.TAG)){
            mRecipe = savedInstanceState.getParcelable(Recipe.TAG);
        } if(savedInstanceState != null && savedInstanceState.containsKey(Step.TAG)){
            mStep = savedInstanceState.getParcelable(Step.TAG);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRecipe != null){
            outState.putParcelable(Recipe.TAG, mRecipe);
        }if(mStep != null){
            outState.putParcelable(Step.TAG, mStep);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ingredient_detail, container, false);
        ButterKnife.bind(this,rootView);
        mDescription.setText(mStep.getDescription());
        initializeMediaSession();
        initializePlayer(mStep);
        return rootView;
    }

    private void initializePlayer(Step step) {
        if (mExoPlayer == null && !Strings.isNullOrEmpty(step.getVideoURL()) ) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            //mExoPlayer.addListener(this);

            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.StepPlayer));
            Uri mediaUri = Uri.parse(step.getVideoURL());
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }else{
            mExoPlayerView.setVisibility(View.INVISIBLE);
        }
    }
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

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
    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
}
