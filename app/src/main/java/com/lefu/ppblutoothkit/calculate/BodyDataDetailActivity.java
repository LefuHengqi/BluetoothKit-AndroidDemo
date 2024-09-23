package com.lefu.ppblutoothkit.calculate;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.lefu.ppblutoothkit.R;
import com.lefu.ppblutoothkit.util.DataUtil;
import com.lefu.ppcalculate.PPBodyFatModel;
import com.lefu.ppcalculate.vo.PPBodyDetailModel;

public class BodyDataDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_data_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.calculation_results);
        toolbar.setTitleTextColor(Color.WHITE);

        TextView textView = findViewById(R.id.data_detail);
        final PPBodyFatModel bodyData = DataUtil.INSTANCE.getBodyDataModel();
        if (bodyData != null) {
            textView.setText(bodyData.toString());
        }
        findViewById(R.id.copyBodyFatInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bodyData != null) {
                    CopyTextToClip(BodyDataDetailActivity.this, bodyData.toString());
                    Toast.makeText(BodyDataDetailActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                }

            }
        });

        findViewById(R.id.catBodyFatDetailState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BodyDataDetailActivity.this, BodyDataStateActivity.class);
                startActivity(intent);
            }
        });

    }

    public void CopyTextToClip(Context context, String chatPhone) {
        ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(ClipData.newPlainText("text", chatPhone));
    }

}
