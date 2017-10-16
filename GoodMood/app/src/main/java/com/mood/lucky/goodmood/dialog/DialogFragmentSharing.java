package com.mood.lucky.goodmood.dialog;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mood.lucky.goodmood.R;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKShareDialogBuilder;

public class DialogFragmentSharing extends DialogFragment {

    private ImageButton btnSharingVK;
    private ImageButton btnSharingFB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_sharing,null);
        btnSharingVK = (ImageButton) view.findViewById(R.id.btnVKSharing);
        btnSharingFB = (ImageButton) view.findViewById(R.id.btnFBSharing);

        btnSharingVK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"sharing vk",Toast.LENGTH_SHORT).show();
                VKShareDialogBuilder builder = new VKShareDialogBuilder();
                builder.setText("text from textMood");
                builder.setShareDialogListener(new VKShareDialogBuilder.VKShareDialogListener() {
                    @Override
                    public void onVkShareComplete(int postId) {

                    }

                    @Override
                    public void onVkShareCancel() {

                    }

                    @Override
                    public void onVkShareError(VKError error) {

                    }
                });
                builder.show(getFragmentManager(),"VK_SHARE_DIALOG");
            }
        });

        btnSharingFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"sharing fb",Toast.LENGTH_SHORT).show();
            }
        });

        return view;

    }
}
