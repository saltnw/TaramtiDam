package com.taramtidam.taramtidam.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by Shoshan on 5/10/2017.
 */

public class ShareOnFacebook extends Activity {

    private static final String TAG = "ShareOnFacebook";

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareOnWall();
    }

    void shareOnWall() {
        ShareDialog shareDialog = new ShareDialog(this);
        callbackManager = CallbackManager.Factory.create();
        shareDialog.registerCallback(callbackManager, new

                FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Log.d(TAG, "onSuccess: ");
                        Toast.makeText(ShareOnFacebook.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel: ");
                        //Toast.makeText(ShareOnFacebook.this, "", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: ");
                        Toast.makeText(ShareOnFacebook.this, "onError" + error.toString(), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle("Get Tarmati Dam App")
                    .setContentDescription("תרמתי דם! כל תרומת דם מצילה חיים")
                    .setContentUrl(Uri.parse("https://www.mdais.org"))
                    .build();

            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
