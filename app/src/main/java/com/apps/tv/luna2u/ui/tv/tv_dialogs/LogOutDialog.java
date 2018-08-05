package com.apps.tv.luna2u.ui.tv.tv_dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v17.leanback.app.GuidedStepSupportFragment;
import android.support.v17.leanback.widget.GuidanceStylist;
import android.support.v17.leanback.widget.GuidedAction;

import com.apps.tv.luna2u.NewApplication;
import com.apps.tv.luna2u.R;
import com.apps.tv.luna2u.ui.tv.tv_activities.TV_LoginActivity;

import java.util.List;
import java.util.Objects;

public class LogOutDialog extends GuidedStepSupportFragment {

    private static final int ACTION_ID_POSITIVE = 1;
    private static final int ACTION_ID_NEGATIVE = ACTION_ID_POSITIVE + 1;

    @NonNull
    @Override
    public GuidanceStylist.Guidance onCreateGuidance(Bundle savedInstanceState) {
        return new GuidanceStylist.Guidance("Exit Luna2u",
                "Are you sure you want to logout and clear all data ?",
        "", NewApplication.getAppContext()
                .getResources().getDrawable(R.drawable.logo));
    }

    @Override
    public void onCreateActions(@NonNull List<GuidedAction> actions, Bundle savedInstanceState) {
        GuidedAction action = new GuidedAction.Builder()
                .id(ACTION_ID_POSITIVE)
                .title("Yes").build();
        actions.add(action);
        action = new GuidedAction.Builder()
                .id(ACTION_ID_NEGATIVE)
                .title("No").build();
        actions.add(action);
    }

    @Override
    public void onGuidedActionClicked(GuidedAction action) {
        if (ACTION_ID_POSITIVE == action.getId()) {
            String code=NewApplication.getPreferencesHelper().getActivationCode();
            NewApplication.getPreferencesHelper().clear();
            NewApplication.getPreferencesHelper().setActivationCode(code);
            TV_LoginActivity.start(getActivity());
        } else {
            Objects.requireNonNull(getActivity()).finish();
        }
    }


}