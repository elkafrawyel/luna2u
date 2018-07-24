package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.LunaDatabase;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.data.model.LiveGroupsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_ChoosePlayerDialog;
import com.apps.hmaserv.luna2u.ui.tv.tv_adapters.TV_QuickListAdapter;
import com.apps.hmaserv.luna2u.ui.tv.tv_dialogs.QuickListDialog;
import com.apps.hmaserv.luna2u.utils.CommonMethods;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
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
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.KeyEvent.KEYCODE_BUTTON_SELECT;
import static android.view.KeyEvent.KEYCODE_DPAD_CENTER;
import static com.apps.hmaserv.luna2u.ui.LauncherActivity.MOBILE;
import static com.apps.hmaserv.luna2u.ui.LauncherActivity.TV;
import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_ChoosePlayerDialog.EXO;
import static com.apps.hmaserv.luna2u.ui.phone.phone_dialogs.Phone_ChoosePlayerDialog.VLC;

public class Player extends AppCompatActivity implements
        SimpleExoPlayer.VideoListener,
        com.google.android.exoplayer2.Player.EventListener,
        VideoRendererEventListener, QuickListDialog.ISelectedItem {
    @BindView(R.id.channel_info_root)
    RelativeLayout channel_info_root;
    @BindView(R.id.tv_channel_name)
    TextView tv_channel_name;
    @BindView(R.id.tv_group_name)
    TextView tv_group_name;
    @BindView(R.id.player_root_view)
    RelativeLayout rootView;
    @BindView(R.id.live_player_loading_pb)
    ProgressBar loadingProgressBar;
    @BindView(R.id.video_view)
    SimpleExoPlayerView playerView;
    private SimpleExoPlayer player_View;
    public static final String VLC_PackageName = "org.videolan.vlc";
    public static final String VLC_Link = "https://play.google.com/store/apps/details?id="
            + VLC_PackageName;

    public String Video_Url, Video_Group_Id, Video_Group_Name, Video_Name;
    private String device_type;
    private static final String TAG = "Luna2u";
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_player);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        loadingProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(this,
                        R.color.text_color),
                        android.graphics.PorterDuff.Mode.MULTIPLY);
        ShowLoading();

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        Video_Url = getIntent().getStringExtra("url");
        Video_Group_Id = getIntent().getStringExtra("group_id");
        Video_Group_Name = getIntent().getStringExtra("group_name");
        Video_Name = getIntent().getStringExtra("name");

        int type = CommonMethods.getDeviceType(this);
        switch (type) {
            case ServerURL.DEVICE_TYPE_PHONE:
                device_type = MOBILE;
                break;

            case ServerURL.DEVICE_TYPE_TABLET:
            case ServerURL.DEVICE_TYPE_TV:
                device_type = TV;
                break;
        }

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

        if (player_View != null) {
            releasePlayer();
        }

        String ask = NewApplication.getPreferencesHelper().getASK();
        if (ask == null || ask.equals("0")) {
            Phone_ChoosePlayerDialog dialog = new Phone_ChoosePlayerDialog(this, new Phone_ChoosePlayerDialog.IPlayerChooseCallback() {
                @Override
                public void whichPlayer(String player, Boolean Ask_again) {
                    Play(Video_Url, player);
                    if (Ask_again) {
                        NewApplication.getPreferencesHelper().setASK("1");
                        NewApplication.getPreferencesHelper().setPlayer(player);
                    } else
                        NewApplication.getPreferencesHelper().setASK("0");
                }

            });
            dialog.show();
        } else {
            String player = NewApplication.getPreferencesHelper().getPlayer();
            Play(Video_Url, player);
        }
        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
            ShowChannelsInfo();

        LoadGroupChannel();

    }

    private void ShowChannelsInfo() {
        channel_info_root.setVisibility(View.GONE);
        tv_channel_name.setText(Video_Name);
        tv_group_name.setText(Video_Group_Name);
        channel_info_root.setVisibility(View.VISIBLE);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {

                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Do some stuff
                        channel_info_root.setVisibility(View.GONE);
                    }
                });
            }
        };
        thread.start();
    }

    private void Play(String mUrl, String mPlayer) {
        switch (mPlayer) {
            case EXO:
                initializePlayer(mUrl);
                break;
            case VLC:
                playVideoWith_VLC(mUrl);
                break;
            default:
                initializePlayer(mUrl);
                break;
        }


    }

    private void playVideoWith_VLC(String mUrl) {
        if (if_VLC_Installed(VLC_PackageName)) {
            Uri uri = Uri.parse(mUrl);
            Intent vlcIntent = new Intent(Intent.ACTION_VIEW);
            vlcIntent.setPackage(VLC_PackageName);
            vlcIntent.setDataAndTypeAndNormalize(uri, "video/*");
            startActivity(vlcIntent);
            Player.this.finish();
        } else {
            // Do whatever we want to do if application not installed
            // For example, Redirect to play store
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(VLC_Link)));
            finish();
        }
    }

    private boolean if_VLC_Installed(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            MDToast.makeText(this, "VLC App Not Installed.",
                    MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
        }

        return false;
    }

    @Override
    public void SelectedItem(ArrayList<LiveChannelsModel>Channels
            , LiveChannelsModel channel
    , LiveGroupsModel liveGroupsModel) {
        if (player_View != null) {
            releasePlayer();
        }
        this.Channels.clear();
        this.Channels=Channels;
        mCurrentChannel = channel;
        mCurrentIndex = Channels.indexOf(channel);

        Video_Group_Id=liveGroupsModel.getId();
        Video_Group_Name=liveGroupsModel.getName();

        Video_Name = mCurrentChannel.getName();

        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
            ShowChannelsInfo();

        Play(channel.getUrl(), EXO);
    }

    private int mCurrentIndex = -1;
    private LiveChannelsModel mCurrentChannel = null;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                OpenSubMenu();
                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
                //Show System Volume View
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_PLAY_SOUND);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);                return true;

            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_ALT_LEFT:
                //Show System Volume View
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_PLAY_SOUND);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);                return true;

            case KeyEvent.KEYCODE_DPAD_UP:
                if (Channels != null && Channels.size() > 0) {

                    if (mCurrentIndex == -1 && mCurrentChannel == null) {
                        for (LiveChannelsModel model : Channels) {
                            if (model.getName().equals(Video_Name)) {
                                mCurrentChannel = model;
                                mCurrentIndex = Channels.indexOf(model);
                            }
                        }
                    }
                    if (player_View != null) {
                        releasePlayer();
                    }
                    mCurrentIndex++;
                    if (mCurrentIndex<Channels.size()){
                        mCurrentChannel = Channels.get(mCurrentIndex);
                        Play(mCurrentChannel.getUrl(), EXO);

                        Video_Name = mCurrentChannel.getName();
                        Video_Group_Name = mCurrentChannel.getGroup();

                        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
                            ShowChannelsInfo();
                    }else {
                        mCurrentIndex=0;
                        mCurrentChannel = Channels.get(0);
                        Play(mCurrentChannel.getUrl(), EXO);

                        Video_Name = mCurrentChannel.getName();
                        Video_Group_Name = mCurrentChannel.getGroup();

                        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
                            ShowChannelsInfo();
                    }
                }
                return true;
                 case KeyEvent.KEYCODE_DPAD_DOWN:

                if (Channels != null && Channels.size() > 0) {
                    if (mCurrentIndex == -1 && mCurrentChannel == null) {
                        for (LiveChannelsModel model : Channels) {
                            if (model.getName().equals(Video_Name)) {
                                mCurrentChannel = model;
                                mCurrentIndex = Channels.indexOf(model);
                            }
                        }
                    }
                    if (player_View != null) {
                        releasePlayer();
                    }
                    mCurrentIndex--;
                    if (mCurrentIndex>=0){
                        mCurrentChannel = Channels.get(mCurrentIndex);
                        Play(mCurrentChannel.getUrl(), EXO);

                        Video_Name = mCurrentChannel.getName();
                        Video_Group_Name = mCurrentChannel.getGroup();

                        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
                            ShowChannelsInfo();
                    }else {
                        mCurrentIndex=Channels.size()-1;
                        mCurrentChannel = Channels.get(Channels.size()-1);
                        Play(mCurrentChannel.getUrl(), EXO);

                        Video_Name = mCurrentChannel.getName();
                        Video_Group_Name = mCurrentChannel.getGroup();

                        if (device_type.equals(TV) && Video_Name != null && Video_Group_Name != null)
                            ShowChannelsInfo();
                    }
                }
                return true;
               default:
                Log.d("OnKey", String.valueOf(keyCode));
                return super.onKeyDown(keyCode, event);
        }
    }

    ArrayList<LiveChannelsModel> Channels = new ArrayList<>();

    private void LoadGroupChannel() {
        String code = NewApplication.getPreferencesHelper().getActivationCode();

        String Channels_Url = ServerURL.LiveChannels_Url.concat(code).concat("/")
                .concat(String.valueOf(Video_Group_Id));
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(Channels_Url,
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                JSONArray array = Groups.getJSONArray("channels");

                                for (int i = 0; i < array.length(); i++) {

                                    LiveChannelsModel model = new Gson().fromJson(array.get(i)
                                            .toString(), LiveChannelsModel.class);
                                    LiveChannelsModel temp = LunaDatabase.getInstance(Player.this)
                                            .getUserDao().getChannelById(model.getId());
                                    if (temp != null)
                                        model.setIs_favorite(true);

                                    Channels.add(model);
                                }
                            }
                        }, new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {

                            }
                        })
        ).setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 0;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }

    private void OpenSubMenu() {
        if (Video_Group_Id != null && Video_Group_Id.equals("1")) {
            QuickListDialog dialog = new QuickListDialog(
                    Player.this, Video_Group_Id
                    , Player.this);
            dialog.show();
        } else if (Video_Group_Id != null && !Video_Group_Id.equals("1") &&
                Channels != null && Channels.size() > 0) {
            QuickListDialog dialog = new QuickListDialog(
                    Player.this, Video_Group_Id
                    , Player.this);
            dialog.show();
        }
    }

    public void releasePlayer() {

        if (player_View != null) {
            player_View.removeListener(this);
            player_View.removeVideoListener(this);
            player_View.release();
            player_View = null;
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

    private void initializePlayer(String mUrl) {
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Initialize the player_View
        player_View = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        //Initialize simpleExoPlayerView
        playerView.setPlayer(player_View);

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, "CloudinaryExoplayer"));

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        Uri videoUri = Uri.parse(mUrl);
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

        // Prepare the player_View with the source.
        player_View.prepare(videoSource);

        player_View.setPlayWhenReady(true);

        //hide player controls
        playerView.setUseController(false);
        HideLoading();
    }

    private void ShowLoading() {
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void HideLoading() {
        loadingProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player_View != null) {
            player_View.release();
            player_View = null;
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
        switch (playbackState) {
            case com.google.android.exoplayer2.Player.STATE_IDLE:
                Log.i(TAG, "playbackState:" + "STATE_IDLE");
                break;
            case com.google.android.exoplayer2.Player.STATE_BUFFERING:
                Log.i(TAG, "playbackState:" + "STATE_BUFFERING");
                ShowLoading();
                break;
            case com.google.android.exoplayer2.Player.STATE_READY:
                Log.i(TAG, "playbackState:" + "STATE_READY");
                HideLoading();
                break;
            case com.google.android.exoplayer2.Player.STATE_ENDED:
                Log.i(TAG, "playbackState:" + "STATE_ENDED");
                HideLoading();
                break;
            default:
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        HideLoading();
        MDToast.makeText(Player.this, "Something went wrong while loading this channel please change the channel or try again later."
                , MDToast.TYPE_ERROR).show();
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