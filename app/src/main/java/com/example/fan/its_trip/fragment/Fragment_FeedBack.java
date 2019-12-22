package com.example.fan.its_trip.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fan.its_trip.R;
import com.example.fan.its_trip.db.SuggestInfo;
import com.example.fan.its_trip.http.HttpRequest;
import com.example.fan.its_trip.toast.MyToast;

/**
 * Created by Fan on 2019/10/29.
 */

public class Fragment_FeedBack extends Fragment implements View.OnClickListener {
    private EditText ed_sug_text;
    private Button btn_sug_commit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg_feedback, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ed_sug_text = (EditText) view.findViewById(R.id.ed_sug_text);
        btn_sug_commit = (Button) view.findViewById(R.id.btn_sug_commit);

        btn_sug_commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sug_commit:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String text = ed_sug_text.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            MyToast.showInfo("请输入您宝贵的意见或建议（500字以内）");
            return;
        }
        // TODO validate success, do something
        SuggestInfo suggestInfo=new SuggestInfo();
        suggestInfo.setUser(HttpRequest.getUserName());
        suggestInfo.setText(text);
        suggestInfo.setAccept(false);
        suggestInfo.setTime(HttpRequest.getTime());
        suggestInfo.save();
        MyToast.showInfo("提交成功！");
        ed_sug_text.setText("");
    }
}
