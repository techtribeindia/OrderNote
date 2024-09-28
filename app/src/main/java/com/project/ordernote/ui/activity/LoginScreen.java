package com.project.ordernote.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.ordernote.R;
import com.project.ordernote.viewmodel.LoginViewModel;

public class LoginScreen extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    private LinearLayout loginMain;
    private ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        EditText etMobileNumber = findViewById(R.id.et_name);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_create);
        final View rootView = findViewById(R.id.login_main);
        final ScrollView scrollView = findViewById(R.id.scroll_view);

        // Toggle password visibility
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eyeoff, 0);
                    } else {
                        etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye, 0);
                    }
                    return true;
                }
            }
            return false;
        });
        final ViewTreeObserver observer = rootView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // Keyboard is opened
                    scrollView.setPadding(0, 0, 0, keypadHeight);
                } else { // Keyboard is closed
                    scrollView.setPadding(0, 0, 0, 0);
                }
            }
        });
        btnLogin.setOnClickListener(v -> {
            String mobileNumber = etMobileNumber.getText().toString();
            String password = etPassword.getText().toString();
            if (mobileNumber.length() != 10)
            {
                showSnackbar(v, "Please enter the 10 digit mobile number");
                return;
            }
            if(password.equals(""))
            {
                showSnackbar(v, "Please enter the Password");
                return;
            }

            loginViewModel.loginUser(mobileNumber, password).observe(this, loginResult -> {
                if (loginResult.getLoginResult()) {
                    Intent intent = new Intent(LoginScreen.this, SplashScreen.class);
                    LoginScreen.this.startActivity(intent);
                    LoginScreen.this.finish();
                    // Navigate to the next screen
                } else {
                    showSnackbar(v, loginResult.getLoginMessage());
                }
            });
        });
    }
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("X", v -> snackbar.dismiss());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

        // Get the Snackbar's layout view
        View snackbarView = snackbar.getView();

        // Change the Snackbar's position to top and add top margin
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
        params.setMargins(0, marginInDp, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbar.show();
    }


}