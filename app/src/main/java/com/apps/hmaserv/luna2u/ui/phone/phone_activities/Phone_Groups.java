package com.apps.hmaserv.luna2u.ui.phone.phone_activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.apps.hmaserv.luna2u.NewApplication;
import com.apps.hmaserv.luna2u.R;
import com.apps.hmaserv.luna2u.data.model.LiveGroupsModel;
import com.apps.hmaserv.luna2u.ui.phone.phone_adapters.Phone_Groups_Adapter;
import com.apps.hmaserv.luna2u.utils.Handler;
import com.apps.hmaserv.luna2u.utils.ServerURL;
import com.apps.hmaserv.luna2u.utils.VolleySingleton;
import com.google.gson.Gson;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Phone_Groups extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_LiveCat)
    RecyclerView recycler_LiveCat;
    @BindView(R.id.progress_bar_live_cat)
    ProgressBar progressBar;
    RecyclerView.LayoutManager layoutManager;
    Phone_Groups_Adapter adapter;
    ArrayList<LiveGroupsModel> LiveCats = new ArrayList<>();
    RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_groups);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_color));
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Live Groups");

        initialize();
    }

    private void initialize() {
        layoutManager = new LinearLayoutManager(this);
        recycler_LiveCat.setHasFixedSize(true);
        recycler_LiveCat.setLayoutManager(layoutManager);

        String code = NewApplication.getPreferencesHelper().getActivationCode();
        if (code != null) {
            CheckCodeValidation(code);
        } else
            progressBar.setVisibility(View.GONE);
    }

    private void LoadGroups(String code) {
        mRequestQueue.add(
                VolleySingleton.getInstance().
                        makeStringResponse(ServerURL.LiveGroups_Url.concat(code),
                                new VolleySingleton.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) throws JSONException {
                                        JSONObject object = new JSONObject(result);
                                        JSONArray array = object.getJSONArray("groups");

                                        for (int i = 0; i < array.length(); i++) {

                                            LiveGroupsModel model = new Gson().fromJson(array.get(i)
                                                    .toString(), LiveGroupsModel.class);
                                            LiveCats.add(model);
                                        }
                                        adapter = new Phone_Groups_Adapter(LiveCats, Phone_Groups.this);

                                        recycler_LiveCat.setAdapter(adapter);
                                        progressBar.setVisibility(View.GONE);

                                    }
                                }
                                , new VolleySingleton.JsonVolleyCallbackError() {
                                    @Override
                                    public void onError(VolleyError error) {
                                        Handler.volleyErrorHandler(error,Phone_Groups.this);

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


    private void CheckCodeValidation(final String Code_fromUser) {
        mRequestQueue.add(
                VolleySingleton.getInstance().makeStringResponse(ServerURL.Code_Url.concat(Code_fromUser),
                        new VolleySingleton.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) throws JSONException {
                                JSONObject Groups = new JSONObject(result);
                                String code = Groups.getString("code");
                                String status = Groups.getString("status");
                                String message = Groups.getString("message");

                                if (code.equals("0") && status.equals("success") && message.equals("Valid")) {
                                    LoadGroups(Code_fromUser);
                                } else if (code.equals("2") && status.equals("error") && message.equals("Subscription expired")) {
                                    MDToast.makeText(Phone_Groups.this, "Your Activation Code is Expired!!", Toast.LENGTH_SHORT,MDToast.TYPE_WARNING).show();
                                    NewApplication.getPreferencesHelper().clear();
                                    Phone_Groups.this.finish();
                                    startActivity(new Intent(Phone_Groups.this, Phone_Login.class));
                                }
                            }
                        }
                        , new VolleySingleton.JsonVolleyCallbackError() {
                            @Override
                            public void onError(VolleyError error) {
                                Handler.volleyErrorHandler(error,Phone_Groups.this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_live_groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, Phone_Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
