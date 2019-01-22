package com.nextopen.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nextopen.R;
import com.nextopen.common.util.IContants;
import com.nextopen.common.util.Utility;

/**
 * A login screen that offers login via email/password.
 */
public class OTPActivity extends Activity implements IContants{
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEtxOne;
    private EditText mEtxTwo;
    private EditText mEtxThree;
    private EditText mEtxFour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        mContext = this;

        mProgressBar = findViewById(R.id.otp_progress);
        TextView mTxtPhone = findViewById(R.id.text_code_phone_content);
        TextView mTxtEmail = findViewById(R.id.text_code_email_content);
        mEtxOne = findViewById(R.id.etx_code_one);
        mEtxTwo = findViewById(R.id.etx_code_two);
        mEtxThree = findViewById(R.id.etx_code_three);
        mEtxFour = findViewById(R.id.etx_code_four);
        ImageView mImgBack = findViewById(R.id.img_back);

        Intent loginIntent = getIntent();
        String phone = " "+loginIntent.getStringExtra(LOGIN_PHONE);
        String email = " "+loginIntent.getStringExtra(LOGIN_EMAIL);

        mTxtPhone.setText(phone);
        mTxtEmail.setText(email);

        mEtxOne.addTextChangedListener(textWatcher);
        mEtxTwo.addTextChangedListener(textWatcher);
        mEtxThree.addTextChangedListener(textWatcher);
        mEtxOne.setOnEditorActionListener(editorActionListener);
        mEtxTwo.setOnEditorActionListener(editorActionListener);
        mEtxThree.setOnEditorActionListener(editorActionListener);
        mEtxFour.setOnEditorActionListener(editorActionListener);
        //mEtxFour.setOnFocusChangeListener(focusChangeListener);

        mImgBack.setOnClickListener(clickListener);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptOTP() {
        mEtxOne.setError(null);
        mEtxTwo.setError(null);
        mEtxThree.setError(null);
        mEtxFour.setError(null);

        String one = mEtxOne.getText().toString();
        String two = mEtxTwo.getText().toString();
        String three = mEtxThree.getText().toString();
        String four = mEtxFour.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(one)) {
            mEtxOne.setError(getString(R.string.error_field_required));
            focusView = mEtxOne;
            cancel = true;
        } else if (TextUtils.isEmpty(two)) {
            mEtxTwo.setError(getString(R.string.error_field_required));
            focusView = mEtxTwo;
            cancel = true;
        } else if (TextUtils.isEmpty(three)) {
            mEtxThree.setError(getString(R.string.error_field_required));
            focusView = mEtxThree;
            cancel = true;
        } else if (TextUtils.isEmpty(four)) {
            mEtxFour.setError(getString(R.string.error_field_required));
            focusView = mEtxFour;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Utility.showProgress(true, mProgressBar, mContext);
            Intent mainIntent = new Intent(OTPActivity.this, FeedActivity.class);
            OTPActivity.this.startActivity(mainIntent);
            Utility.showProgress(false, mProgressBar, mContext);
        }
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            switch (actionId){
                case EditorInfo.IME_ACTION_NEXT:
                    boolean result = false;
                    TextView v1 = (TextView) textView.focusSearch(View.FOCUS_RIGHT);
                    if (v1 != null)
                        result = v1.requestFocus(View.FOCUS_RIGHT);
                    else if (!result)
                    {
                        v1 = (TextView) textView.focusSearch(View.FOCUS_DOWN);
                        if (v1 != null)
                            result = v1.requestFocus(View.FOCUS_DOWN);
                    }
                    if (!result)
                        textView.onEditorAction(actionId);
                    break;
                case EditorInfo.IME_ACTION_DONE:
                    attemptOTP();
                    break;
                default:
                    textView.onEditorAction(actionId);
                    break;
            }
            return true;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            if(mEtxOne.getText().toString().length()==1)     //size as per your requirement
            {
                mEtxTwo.requestFocus();
            }
            if(mEtxTwo.getText().toString().length()==1)     //size as per your requirement
            {
                mEtxThree.requestFocus();
            }
            if(mEtxThree.getText().toString().length()==1)     //size as per your requirement
            {
                mEtxFour.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case  R.id.img_back:
                    Intent mainIntent = new Intent(OTPActivity.this, LoginActivity.class);
                    OTPActivity.this.startActivity(mainIntent);
                    OTPActivity.this.finish();
                    break;
            }
        }
    };

    /*
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
    }
    */
}

