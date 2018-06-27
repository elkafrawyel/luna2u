package com.apps.hmaserv.luna2u.ui.phone.phone_dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.model.LiveChannelsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_adapters.SearchLiveChannels_Adapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SearchLiveChannelsDialog extends Dialog {

    @BindView(R.id.phone_search_live_rv)
    RecyclerView search_live_channels_rv;
    @BindView(R.id.phone_search_live_et)
    TextView phone_search_live_channels_et;
    private Context mContext;
    private ArrayList<LiveChannelsModel> Live_Channels=new ArrayList<>();
    private SearchLiveChannels_Adapter adapter;
    private ISearchDialog iSearchDialog;
    public SearchLiveChannelsDialog(@NonNull Context context,
                                    ArrayList<LiveChannelsModel> Live_Channels
    ,ISearchDialog iSearchDialog) {
        super(context);
        mContext = context;
        this.Live_Channels=Live_Channels;
        this.iSearchDialog=iSearchDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SearchLiveChannelsDialog.this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.setCancelable(true);
        setContentView(R.layout.search_live_channels);
        ButterKnife.bind(this);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        ApplySearch();
    }

    private void ApplySearch(){
        search_live_channels_rv.setHasFixedSize(true);
        search_live_channels_rv.setLayoutManager(new GridLayoutManager(mContext,2));
        adapter=new SearchLiveChannels_Adapter(Live_Channels,mContext);
        search_live_channels_rv.setAdapter(adapter);

        phone_search_live_channels_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s))
                    adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s))
                    adapter.getFilter().filter(s.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        iSearchDialog.DialogClosed();
    }

   public interface ISearchDialog{
        void DialogClosed();
    }
}