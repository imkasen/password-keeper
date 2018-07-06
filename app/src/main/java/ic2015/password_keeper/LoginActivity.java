package ic2015.password_keeper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_login_email) MaterialEditText login_email_input;
    @BindView(R.id.input_login_password) MaterialEditText login_password_input;
    @BindView(R.id.button_login) ButtonRectangle login_button;
    @BindView(R.id.link_signup) TextView signup_link;

    //注册者 属性 Box
    private Box<RegisterEntity> registerEntityBox;

    //缓存
    ACache mCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_login);

        //黄油刀
        ButterKnife.bind(this);

        //数据库和 Box 初始化
        BoxStore boxStore = ((App)getApplication()).getBoxStore();
        registerEntityBox = boxStore.boxFor(RegisterEntity.class);

        //缓存
        mCache = ACache.get(this);

        //登录按钮
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用登录过程方法
                login();
            }
        });

        //跳转到注册界面
        signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }


    //登录过程
    private void login(){

        //停用登录按钮功能
        login_button.setEnabled(false);

        //输入有效性检查失败
        if (!validate()){
            this.loginFailed();
            return;
        }

        //登录进度对话框
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, "登录中...");
        progressDialog.show();

        //定时器
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing()) { //防止内存泄露

                            progressDialog.dismiss();

                            if (checkRegister()) {
                                loginSuccess();
                            }
                            else {
                                loginFailed();
                            }
                        }
                    }
                }, 500);


    }

    //注册界面返回参数检测
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //注册界面返回
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_SIGNUP){
                Toasty.success(getBaseContext(), "注册成功，请登录！", Toast.LENGTH_SHORT, true).show();
            }
        }

    }

    //登录成功
    private void loginSuccess(){
        login_button.setEnabled(true);
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        mCache.put("email", login_email_input.getText().toString().trim(), ACache.TIME_HOUR); //利用缓存传递用户邮箱信息
        startActivityForResult(intent, REQUEST_SIGNUP);
        login_email_input.setText("");
        login_password_input.setText("");
    }

    //登录失败提示
    private void loginFailed(){
        Toasty.error(getBaseContext(), "登录失败，请检查！", Toast.LENGTH_SHORT, true).show();
        login_button.setEnabled(true);
    }

    //登录失败提示（没有此邮箱）
    private void loginFailed_noEmailSame(){
        Toasty.error(getBaseContext(), "邮箱地址错误！", Toast.LENGTH_SHORT, true).show();
        login_button.setEnabled(true);
    }

    //登录失败提示（密码错误）
    private void loginFailed_wrongPwd(){
        Toasty.error(getBaseContext(), "密码错误", Toast.LENGTH_SHORT, true).show();
        login_button.setEnabled(true);
    }

    //输入有效性检查
    private boolean validate(){
        boolean valid = true;

        String email = login_email_input.getText().toString().trim();
        String password = login_password_input.getText().toString().trim();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            login_email_input.setError("请输入有效的邮箱地址！");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16){
            login_password_input.setError("密码长度应在 6 ~ 16 位之间！");
            valid = false;
        }

        return valid;
    }


    //登录信息查询
    private boolean checkRegister(){
        boolean isSuccessful = false;

        String email = login_email_input.getText().toString().trim();
        String password_sha256 = Encrypt.SHA(login_password_input.getText().toString().trim());

        //信息正确
        if (registerQuery(email, password_sha256)){
            isSuccessful = true;
        }

        return isSuccessful;
    }

    //数据库检索，对比输入信息
    private boolean registerQuery(String email, String password_sha256){
        boolean isRegister = true;

        List<RegisterEntity> registerEntityList;

        registerEntityList = registerEntityBox.query().equal(RegisterEntity_.email, email).build().find();

        //邮箱不存在
        if (registerEntityList.isEmpty()){
            loginFailed_noEmailSame();
            isRegister = false;
        }
        //密码错误
        else {
            String password_sha256_ob = registerEntityList.get(0).getPassword_sha256().trim();
            if (!password_sha256.equals(password_sha256_ob)) {
                loginFailed_wrongPwd();
                isRegister = false;
            }
        }

        return isRegister;
    }
}
