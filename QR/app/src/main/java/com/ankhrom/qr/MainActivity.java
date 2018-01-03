package com.ankhrom.qr;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ankhrom.base.GlobalCode;
import com.ankhrom.base.common.BasePermission;
import com.ankhrom.base.common.statics.AppsHelper;
import com.ankhrom.base.common.statics.StringHelper;
import com.ankhrom.base.custom.builder.ToastBuilder;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zxing;
    private View permissionView;
    private boolean isStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        zxing = findView(R.id.zxing);
        zxing.setResultHandler(this);

        if (BasePermission.isAvailable(this, Manifest.permission.CAMERA)) {
            startZXing();
        } else {
            permissionView = findView(R.id.permission_view);
            permissionView.setVisibility(View.VISIBLE);
            zxing.setVisibility(View.GONE);
            requestCameraPermission();
        }
    }

    public void startZXing() {

        if (isStarted || zxing == null) {
            return;
        }

        isStarted = true;
        zxing.startCamera();
        zxing.resumeCameraPreview(this);
    }

    public void stopZXing() {

        if (!isStarted || zxing == null) {
            return;
        }

        isStarted = false;
        zxing.stopCamera();
    }

    public void onRequestCameraPermissionPressed(View view) {

        if (BasePermission.isAvailable(this, Manifest.permission.CAMERA)) {
            startZXing();
        } else {
            requestCameraPermission();
        }
    }

    public void requestCameraPermission() {

        BasePermission.with(this)
                .requestCode(GlobalCode.CAMERA_REQUEST)
                .require(Manifest.permission.CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == GlobalCode.CAMERA_REQUEST && BasePermission.isGranted(Manifest.permission.CAMERA, permissions, grantResults)) {

            if (permissionView != null) {
                permissionView.setVisibility(View.GONE);
                permissionView = null;
            }

            AppsHelper.startActivity(this, MainActivity.class);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startZXing();
    }

    @Override
    protected void onPause() {
        stopZXing();
        super.onPause();
    }

    @Override
    public void handleResult(Result result) {

        String text = result.getText();

        if (StringHelper.isEmpty(text)) {
            return;
        }

        if (text.startsWith("http") || text.startsWith("www")) {
            AppsHelper.openWeb(this, text);
        } else if (text.startsWith("mailto")) {
            AppsHelper.openMail(this, text.split(":")[1], "", "");
        } else if (StringHelper.startsWith(text, "MATMSG")) {
            text = text.substring("MATMSG:TO:".length()); //MATMSG:TO:mail;SUB:sub;BODY:message;;
            String[] data = text.split(";");
            AppsHelper.openMail(this, data[0], data[1].split(":")[0], data[2].split(":")[1]);
        } else if (StringHelper.startsWith(text, "SMS")) {
            String[] data = text.split(":");
            AppsHelper.openSms(this, data[1], data[2]);
        } else if (StringHelper.startsWith(text, "TEL")) {
            String[] data = text.split(":");
            AppsHelper.openDial(this, data[1]);
        } else if (StringHelper.startsWith(text, "BEGIN:VCARD")) {
            AppsHelper.openIntent(this, ContactCard.parseVCard(text).toIntent());
        } else {
            try {
                AppsHelper.openIntent(this, Uri.parse(text));
            } catch (Exception e) {
                e.printStackTrace();
                showResultPopup(text);
            }
        }

        stopZXing();
    }

    private void showResultPopup(String text) {

        FrameLayout container = findViewById(R.id.root_container);

        View popup = LayoutInflater.from(this).inflate(R.layout.result_popup, container, false);

        TextView resultText = popup.findViewById(R.id.result_text);
        resultText.setText(text);

        container.addView(popup);
    }

    public void onClosePressed(View view) {

        FrameLayout container = findViewById(R.id.root_container);
        container.removeViewAt(container.getChildCount() - 1);

        startZXing();
    }

    public void onCopyPressed(View view) {

        TextView resultText = findViewById(R.id.result_text);

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("QR", resultText.getText());

        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }

        ToastBuilder.with(this).text("Text copied to clipboard").buildAndShow();
    }

    public void onSearchPressed(View view) {

        TextView resultText = findViewById(R.id.result_text);

        AppsHelper.openIntent(this, Uri.parse("https://www.google.com/search?q=" + resultText.getText()));

        onClosePressed(view);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(@IdRes int id) {

        return (T) super.findViewById(id);
    }
}
