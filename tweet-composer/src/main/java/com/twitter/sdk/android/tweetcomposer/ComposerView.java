/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.twitter.sdk.android.tweetcomposer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.internal.UserUtils;
import com.twitter.sdk.android.core.internal.util.ObservableScrollView;
import com.twitter.sdk.android.core.models.User;

public class ComposerView extends LinearLayout {
    ImageView avatarView;
    ImageView closeView;
    EditText tweetEditView;
    TextView charCountView;
    Button tweetButton;
    ObservableScrollView scrollView;
    View divider;
    // styled drawables for images
    ColorDrawable mediaBg;
    // callbacks
    ViewGroup cardView;
    ComposerController.ComposerCallbacks callbacks;

    private Picasso imageLoader;

    public ComposerView(Context context) {
        this(context, null);
    }

    public ComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ComposerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        imageLoader = Picasso.with(getContext());
        // TODO: make color vary depending on the style
        mediaBg = new ColorDrawable(context.getResources().getColor(R.color.tw__light_gray));
        inflate(context, R.layout.tw__composer_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findSubviews();

        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onCloseClick();
            }
        });

        tweetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                callbacks.onTweetPost(getTweetText());
            }
        });

        tweetEditView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                callbacks.onTweetPost(getTweetText());
                return true;
            }
        });

        tweetEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                callbacks.onTextChanged(getTweetText());
            }
        });

        scrollView.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(int scrollY) {
                if (scrollY > 0) {
                    divider.setVisibility(View.VISIBLE);
                } else {
                    divider.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    void findSubviews() {
        avatarView = (ImageView) findViewById(R.id.tw__author_avatar);
        closeView = (ImageView) findViewById(R.id.tw__composer_close);
        tweetEditView = (EditText) findViewById(R.id.tw__edit_tweet);
        charCountView = (TextView) findViewById(R.id.tw__char_count);
        tweetButton = (Button) findViewById(R.id.tw__post_tweet);
        scrollView = (ObservableScrollView) findViewById(R.id.tw__composer_scroll_view);
        divider = findViewById(R.id.tw__composer_profile_divider);
        cardView = (ViewGroup) findViewById(R.id.tw__card_view);
    }

    void setCallbacks(ComposerController.ComposerCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /*
     * Sets the profile photo from the User's profile image url or the placeholder background
     * color.
     */
    void setProfilePhotoView(User user) {
        final String url = UserUtils.getProfileImageUrlHttps(user,
                UserUtils.AvatarSize.REASONABLY_SMALL);
        if (imageLoader != null) {
            // Passing null url will not trigger any request, but will set the placeholder bg
            imageLoader.load(url).placeholder(mediaBg).into(avatarView);
        }
    }

    String getTweetText() {
        return tweetEditView.getText().toString();
    }

    void setTweetText(String text) {
        tweetEditView.setText(text);
    }

    void setCursorAtEnd() {
        tweetEditView.setSelection(getTweetText().length());
    }

    void setCharCount(int remainingCount) {
        charCountView.setText(Integer.toString(remainingCount));
    }

    void setCharCountTextStyle(int textStyleResId) {
        charCountView.setTextAppearance(getContext(), textStyleResId);
    }

    void postTweetEnabled(boolean enabled) {
        tweetButton.setEnabled(enabled);
    }

    void setCardView(View card) {
        cardView.addView(card);
        cardView.setVisibility(View.VISIBLE);
    }
}
