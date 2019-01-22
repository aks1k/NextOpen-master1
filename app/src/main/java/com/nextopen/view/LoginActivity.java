package com.nextopen.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.nextopen.R;
import com.nextopen.business.LoginClient;
import com.nextopen.common.adapter.CountryAdapter;
import com.nextopen.common.util.Constants;
import com.nextopen.common.util.IContants;
import com.nextopen.common.util.Utility;
import com.nextopen.common.util.Validator;
import com.nextopen.vo.LoginRequestVO;
import com.nextopen.vo.LoginResponseVO;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A login screen that offers login via email/phone.
 */
public class LoginActivity extends Activity implements IContants{
    private Context mContext;
    private LoginClient loginClient;
    private Validator validator;
    private ProgressBar mProgressBar;

    private String isdCode;
    private String phone;
    private String email;
    private String fbId;
    private String token;

    private TextView mTxtPhone;
    private EditText mEtxPhone;
    private EditText mEtxEmail;
    private LoginButton btnFbLogin;

    private CallbackManager callbackManager;
    private SharedPreferences sharedPref;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(this);

        mContext = this;

        mProgressBar = findViewById(R.id.login_progress);
        mTxtPhone = findViewById(R.id.txt_phone);
        mEtxPhone = findViewById(R.id.etx_phone);
        mEtxEmail = findViewById(R.id.etx_email);
        Button mBtnLogin = findViewById(R.id.btn_login);
        Button mBtnFBLogin = findViewById(R.id.btn_login_fb);
        Spinner mSpnCountry = findViewById(R.id.spn_countries);
        btnFbLogin = findViewById(R.id.fb_login);

        CountryAdapter adapter = new CountryAdapter(this.getApplicationContext(), R.id.txt_phone, Utility.COUNTRIES);
        mSpnCountry.setAdapter(adapter);
        mSpnCountry.setOnItemSelectedListener(itemSelectedListener);

        mTxtPhone.setText(SIGN_PLUS+ Utility.COUNTRIES[0].getIsdCode());
        mBtnLogin.setOnClickListener(loginButtonListener);
        mBtnFBLogin.setOnClickListener(loginButtonListener);
        btnFbLogin.setOnClickListener(loginButtonListener);
        callbackManager = CallbackManager.Factory.create();

        validator = Validator.getInstance();

        sharedPref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPref.getLong(PREF_USERID, 0);

        if (!Utility.isNetworkAvailable(this)) {
            Utility.alertDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 111);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    LoginActivity.this.finish();
                }
            }, Constants.INTERNET);
        } else {
            checkLogin();
        }
    }

    private void checkLogin() {
        if (userId > 0) {
            Intent feedIntent = new Intent(LoginActivity.this, FeedActivity.class);
            LoginActivity.this.startActivity(feedIntent);
            this.finish();
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        mEtxEmail.setError(null);
        mEtxPhone.setError(null);

        //isdCode = mTxtPhone.getText().toString();
        phone = mEtxPhone.getText().toString();
        email = mEtxEmail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)) {
            mEtxPhone.setError(getString(R.string.error_field_required));
            focusView = mEtxPhone;
            cancel = true;
        } else if (!validator.isValidPhone(phone)) {
            mEtxPhone.setError(getString(R.string.error_invalid_phone));
            focusView = mEtxPhone;
            cancel = true;
        }else if (TextUtils.isEmpty(email)) {
            mEtxEmail.setError(getString(R.string.error_field_required));
            focusView = mEtxEmail;
            cancel = true;
        } else if (!validator.isValidEmail(email)) {
            mEtxEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEtxEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            Utility.showProgress(true, mProgressBar, mContext);
            new LoginTask().execute();
        }
    }

    public class LoginTask extends  AsyncTask<Void, Void, Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            mProgressBar.setVisibility(View.VISIBLE);
            loginClient = new LoginClient();
            LoginRequestVO requestVO = new LoginRequestVO();

            requestVO.setMobileDeviceType(DEVICE_TYPE);
            requestVO.setMobileCountry(isdCode);
            requestVO.setMobilePhoneLocal(phone);
            requestVO.setFacebookId("123");
            if (TextUtils.isEmpty(phone) || !TextUtils.isEmpty(fbId)) {
                requestVO.setMobileCountry("1");
                requestVO.setMobilePhoneLocal("9999999999");
                requestVO.setFacebookId(fbId);
            }

            loginClient.manageRequest(requestVO);

            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            LoginResponseVO responseVO = loginClient.getLoginResponseVO();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong(PREF_USERID, responseVO.getUserID());
            editor.apply();

            if (!TextUtils.isEmpty(fbId)) {
                Intent feedIntent = new Intent(LoginActivity.this, FeedActivity.class);
                LoginActivity.this.startActivity(feedIntent);
            } else {
                Intent otpIntent = new Intent(LoginActivity.this, OTPActivity.class);
                otpIntent.putExtra(LOGIN_PHONE, SIGN_PLUS + isdCode + phone);
                otpIntent.putExtra(LOGIN_EMAIL, email);
                otpIntent.putExtra(LOGIN_USERID, responseVO.getUserID());
                LoginActivity.this.startActivity(otpIntent);
            }
            Utility.showProgress(false, mProgressBar, mContext);
            //System.out.print("Response: "+ responseVO.getMobileCountry());
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Utility.showProgress(false, mProgressBar, mContext);
        }
    }

    private OnClickListener loginButtonListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_login:
                    attemptLogin();
                    break;
                case R.id.btn_login_fb:
                    btnFbLogin.performClick();
                    break;
                case R.id.fb_login:
                    fbLogin();
                    break;
            }
        }
    };

    private void fbLogin(){
        btnFbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                token = loginResult.getAccessToken().getToken();
                Log.e("Accesstoken", "is :" + token);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            fbId = object.getString("id");
                            Utility.showProgress(true, mProgressBar, mContext);
                            new LoginTask().execute();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
            }
        });

    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            isdCode = Utility.COUNTRIES[position].getIsdCode();
            mTxtPhone.setText(SIGN_PLUS+isdCode);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            isdCode = Utility.COUNTRIES[0].getIsdCode();
            mTxtPhone.setText(SIGN_PLUS+isdCode);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (resultCode == RESULT_OK) {
                checkLogin();
            } else {
                LoginActivity.this.finish();
            }
        }
    }

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

