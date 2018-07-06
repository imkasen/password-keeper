package ic2015.password_keeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.input_signup_username) MaterialEditText signup_username_input;
    @BindView(R.id.input_signup_email) MaterialEditText signup_email_input;
    @BindView(R.id.input_signup_password) MaterialEditText signup_password_input;
    @BindView(R.id.input_signup_password_check) MaterialEditText signup_password_check_input;
    @BindView(R.id.button_signup) ButtonRectangle signup_button;
    @BindView(R.id.link_login) TextView login_link;

    //注册者 属性 Box
    private Box<RegisterEntity> registerEntityBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_signup);

        //黄油刀
        ButterKnife.bind(this);

        //数据库和 Box 初始化
        BoxStore boxStore = ((App)getApplication()).getBoxStore();
        registerEntityBox = boxStore.boxFor(RegisterEntity.class);

        //注册按钮
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用注册过程方法
                signUp();
            }
        });

        //跳转到登录界面
        login_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //结束注册界面
                finish();
            }
        });

    }

    //注册过程
    private void signUp(){

        //停用注册按钮功能
        signup_button.setEnabled(false);

        //输入有效性检查失败
        if (!validate()){
            this.signUpFailed();
            return;
        }

        if (addRegister()) {
            signUpSuccess();
        }
        else {
            signUpFailed();
        }


    }

    //注册成功提示
    private void signUpSuccess(){
        signup_button.setEnabled(true);
        this.setResult(RESULT_OK, null);
        this.finish();
    }

    //注册失败提示
    private void signUpFailed(){
        Toasty.error(getBaseContext(), "注册失败，请检查！", Toast.LENGTH_SHORT, true).show();
        signup_button.setEnabled(true);
    }

    //注册失败提示（注册时同名）
    private void signUpFailed_usernameSame(){
        Toasty.error(getBaseContext(), "已存在同名用户！", Toast.LENGTH_SHORT, true).show();
        signup_button.setEnabled(true);
    }

    //注册失败提示（注册时同邮箱）
    private void signUpFailed_emailSame(){
        Toasty.error(getBaseContext(), "邮箱已存在！", Toast.LENGTH_SHORT, true).show();
        signup_button.setEnabled(true);
    }

    //输入有效性检查
    private boolean validate(){
        boolean valid = true;

        String username = signup_username_input.getText().toString().trim();
        String email = signup_email_input.getText().toString().trim();
        String password = signup_password_input.getText().toString().trim();
        String password_check = signup_password_check_input.getText().toString().trim();

        if (username.isEmpty() || username.length() < 4){
            signup_username_input.setError("用户名长度在 4 ~ 12 位之间！");
            valid = false;
        }

        //^[a-zA-Z0-9_]{4,12}+$ 正则表达式，用户名仅包含大小写英文，数字和下划线，下划线位置不限
        if (!username.matches("^[a-zA-Z0-9_]{4,12}+$")){
            signup_username_input.setError("用户名仅限大小写英文、数字和下划线！");
            valid = false;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signup_email_input.setError("请输入有效的邮箱地址！");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 16){
            signup_password_input.setError("密码长度应在 6 ~ 16 位之间！");
            valid = false;
        }

        if (password_check.isEmpty() || !Encrypt.SHA(password).equals(Encrypt.SHA(password_check))
                || password_check.length() < 6 || password_check.length() > 16){
            signup_password_check_input.setError("两次密码输入不一致！");
            valid = false;
        }

        return valid;
    }


    //向 registerBox 中添加 Register 数据
    private boolean addRegister(){

        boolean isSuccessful = false;

        String username = signup_username_input.getText().toString().trim();
        String email = signup_email_input.getText().toString().trim();
        String password_sha256 = Encrypt.SHA(signup_password_input.getText().toString().trim()); //密码 sha256 加密

        //非注册账户
        if (!registerQuery(username, email)) {

            RegisterEntity registerEntity = new RegisterEntity();

            registerEntity.setUsername(username);
            registerEntity.setEmail(email);
            registerEntity.setPassword_sha256(password_sha256);

            registerEntityBox.put(registerEntity);

            isSuccessful = true;
        }

        return isSuccessful;
    }

    //注册时对账户是否重复进行查询
    private boolean registerQuery(String username, String email){
        boolean isRegister = false;

        List<RegisterEntity> registerNameEntityList;
        List<RegisterEntity> registerEmailEntityList;

        registerNameEntityList = registerEntityBox.query().equal(RegisterEntity_.username, username).build().find();
        registerEmailEntityList = registerEntityBox.query().equal(RegisterEntity_.email, email).build().find();


        //判断数据库中是否已经存在同名或同邮箱的注册用户
        if ( !registerNameEntityList.isEmpty()){
            isRegister = true;
            signUpFailed_usernameSame();
        }

        if ( !registerEmailEntityList.isEmpty()){
            isRegister = true;
            signUpFailed_emailSame();
        }

        return isRegister;
    }

}
