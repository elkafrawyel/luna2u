package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.ChoosePlayerDialog;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.ChoosePlayerDialog.EXO;
import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.ChoosePlayerDialog.VLC;

public class LivePlayer_activity extends AppCompatActivity implements
        SimpleExoPlayer.VideoListener,
        Player.EventListener,
        VideoRendererEventListener {

    @BindView(R.id.player_root_view)
    ConstraintLayout rootView;
    @BindView(R.id.live_player_loading_pb)
    ProgressBar loadingProgressBar;
    @BindView(R.id.video_view)
    SimpleExoPlayerView playerView;
    private SimpleExoPlayer player;

    public static final String VLC_PackageName="org.videolan.vlc";
    public String Video_Url;
    public static final String VLC_Link="https://play.google.com/store/apps/details?id=" + VLC_PackageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_player);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(
                        ContextCompat.getColor(this, R.color.progressbar_color),
                        android.graphics.PorterDuff.Mode.MULTIPLY
                );

        Video_Url=getIntent().getStringExtra("url");

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        hideSystemUi();

        if (player != null) {
            releasePlayer();
        }

        String ask=NewApplication.getPreferencesHelper().getASK();
        if (ask==null||ask.equals("0")) {
            ChoosePlayerDialog dialog = new ChoosePlayerDialog(this, new ChoosePlayerDialog.IPlayerChooseCallback() {
                @Override
                public void whichPlayer(String player,Boolean Ask_again) {

                    Play(Video_Url, player);

                    if (Ask_again){
                        NewApplication.getPreferencesHelper().setASK("1");
                        NewApplication.getPreferencesHelper().setPlayer(player);
                    } else
                        NewApplication.getPreferencesHelper().setASK("0");

                }

            });
            dialog.show();
        }else {
            String player=NewApplication.getPreferencesHelper().getPlayer();
            Play(Video_Url,player);
        }
    }

    private void Play(String mUrl,String player){
        if (player.equals(EXO))
            initializePlayer(mUrl);
        else if (player.equals(VLC))
            playVideoWith_VLC(mUrl);
    }

    private void playVideoWith_VLC(String mUrl){
        if(if_VLC_Installed(VLC_PackageName)) {
            Uri uri = Uri.parse(mUrl);
            Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
            vlcIntent.setPackage(VLC_PackageName);
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
            startActivity(vlcIntent);
            LivePlayer_activity.this.finish();
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(VLC_Link)));
            finish();
        }
    }

    private boolean if_VLC_Installed(String uri){
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {

        }

        return false;
    }


    public void releasePlayer() {

        if (player != null) {
            player.removeListener(this);
            player.removeVideoListener(this);

            player.release();
            player = null;
        }

    }
    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    private void initializePlayer(String mUrl){
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        playerView.setPlayer(player);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(mUrl);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player with the source.
        player.prepare(videoSource);

        player.setPlayWhenReady(true);

        HideLoading();

    }

    private void ShowLoading(){
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void HideLoading(){
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player!=null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onVideoEnabled(DecoderCounters counters) {
    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {
    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    @Override
    public void onRenderedFirstFrame() {

    }
}