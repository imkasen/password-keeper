package ic2015.password_keeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.CommonTextView;
import com.gc.materialdesign.views.Button;
import com.gc.materialdesign.views.ScrollView;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.QueryBuilder;

public class AddPwdActivity extends AppCompatActivity {

    //侧边栏
    private SlidingRootNav slidingRootNav;

    //缓存
    ACache mCache;

    //账户 属性 Box
    private Box<AccountEntity> accountEntityBox;
    private Box<RegisterEntity> registerEntityBox;

    //toolbar
    @BindView(R.id.toolbar_ap) Toolbar toolbar_ap; //添加密码页 toolbar
    @BindView(R.id.scrollview_addpwd) ScrollView scrollView;

    //侧边栏
    @BindView(R.id.homepage_TextView) CommonTextView homepage_textView; //首页
    @BindView(R.id.addPwd_TextView) CommonTextView addPwd_textView; //添加账户
    @BindView(R.id.checkPwd_TextView) CommonTextView checkPwd_textView; //查看账户
    @BindView(R.id.setting_TextView) CommonTextView setting_textView; //设置
    @BindView(R.id.about_TextView) CommonTextView about_textView; //关于
    @BindView(R.id.exit_TextView) CommonTextView exit_textView; //退出

    //界面
    @BindView(R.id.clear_button_addpwd) Button clear_button_apppwd; //清除按钮
    @BindView(R.id.randompwd_button_addpwd) Button randompwd_button_addpwd; //随机密码按钮
    @BindView(R.id.submit_button_addpwd) Button submit_button_addpwd; //提交按钮

    @BindView(R.id.title_add_pwd) MaterialEditText title_addpwd_EditText; //标题
    @BindView(R.id.username_add_pwd) MaterialEditText username_addpwd_EditText; //用户名
    @BindView(R.id.password_add_pwd) MaterialEditText password_addpwd_EditText; //密码
    @BindView(R.id.email_add_pwd) MaterialEditText email_addpwd_EditText; //邮箱
    @BindView(R.id.url_add_pwd) MaterialEditText url_addpwd_EditText; //地址
    @BindView(R.id.phone_add_pwd) MaterialEditText phone_addpwd_EditText; //电话
    @BindView(R.id.question_add_pwd) MaterialEditText question_addpwd_EditText; //密保问题
    @BindView(R.id.answer_add_pwd) MaterialEditText answer_addpwd_EditText; //密保回答

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_add_pwd);

        //左侧滑动栏
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar_ap)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true) //侧边栏打开时右侧也能点击
                .withSavedState(savedInstanceState)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.left_drawer_menu)
                .inject();

        //黄油刀
        ButterKnife.bind(this);

        //数据库初始化
        BoxStore boxStore = ((App)getApplication()).getBoxStore();
        accountEntityBox = boxStore.boxFor(AccountEntity.class);
        registerEntityBox = boxStore.boxFor(RegisterEntity.class);

        //缓存
        mCache = ACache.get(this);

        //toolbar 初始化设置
        this.initialToolBar();
        this.setSupportActionBar(toolbar_ap);

        //初始化界面
        this.initialView();

        //清除按钮
        clear_button_apppwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear_button_apppwd.setEnabled(false);

                clearInput();
                Toasty.success(getBaseContext(), "已清除！", Toast.LENGTH_SHORT, true).show();

                clear_button_apppwd.setEnabled(true);
            }
        });

        //随机密码生成按钮
        randompwd_button_addpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                randompwd_button_addpwd.setEnabled(false);

                password_addpwd_EditText.setText(randomPassword());
                Toasty.success(getBaseContext(), "已生成随机密码！", Toast.LENGTH_SHORT, true).show();

                randompwd_button_addpwd.setEnabled(true);
            }
        });

        //提交按钮
        submit_button_addpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProcess();
            }
        });

    }

    //初始化界面
    private void initialView(){

        //点击 toolbar 导航图标
        toolbar_ap.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果侧边栏关着，点击图标打开
                if (slidingRootNav.isMenuClosed()){
                    slidingRootNav.openMenu();
                }
                //如果侧边栏开着，点击图标关闭
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
            }
        });

        //----------- 侧边栏设置 -----------

        //点击首页栏，关闭侧边栏，跳转
        homepage_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPwdActivity.this, HomePageActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击添加密码栏，只需关闭侧边栏
        addPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
            }
        });

        //点击查看账户栏，关闭侧边栏，跳转
        checkPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPwdActivity.this, CheckPwdActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击设置，关闭侧边栏，跳转
        setting_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPwdActivity.this, SettingActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击关于，关闭侧边栏，跳转
        about_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddPwdActivity.this, AboutActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //侧边栏退出
        exit_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEvent();
            }
        });

    }

    //初始化 toolbar
    private void initialToolBar(){
        toolbar_ap.setTitle("添加账户");
        toolbar_ap.setTitleTextColor(getResources().getColor(R.color.white)); //白色
        toolbar_ap.setNavigationIcon(R.drawable.menu_icon);
    }

    //侧边栏 退出事件
    private void exitEvent(){

        //跳出对话框
        new MaterialStyledDialog.Builder(this)
                .setIcon(R.drawable.dialog_exit_icon)
                .setHeaderColor(R.color.colorDoderBlue)
                .setDescription("确认退出？")
                .withIconAnimation(true)
                //确认按钮
                .setPositiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        exitToLoginPage();
                    }
                })
                //退出按钮
                .setNegativeText(R.string.cancel)
                .setCancelable(true) //点击对话框外区域取消
                .show();

    }

    //退出到登录界面
    private void exitToLoginPage(){
        Intent intent = new Intent(AddPwdActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //清除输入
    private void clearInput(){

        title_addpwd_EditText.setText("");
        username_addpwd_EditText.setText("");
        password_addpwd_EditText.setText("");
        email_addpwd_EditText.setText("");
        url_addpwd_EditText.setText("");
        phone_addpwd_EditText.setText("");
        question_addpwd_EditText.setText("");
        answer_addpwd_EditText.setText("");

        scrollView.fullScroll(View.FOCUS_UP); //回到顶部

    }

    //生成随机密码
    private String randomPassword(){
        String result;

        //随机密码长度
        Random random = new Random();
        int len = random.nextInt(10) + 9 ; //密码长度 9 - 19 位

        result = Encrypt.getRandomPassWord(len);

        return result;
    }

    //提交
    private void submitProcess(){

        submit_button_addpwd.setEnabled(false);

        if (!validate()){
            this.submitFailed();
            return;
        }


        //提交进度对话框
        final ProgressDialog progressDialog = new ProgressDialog(AddPwdActivity.this, "提交中...");
        progressDialog.show();

        //定时器
        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing()){

                            if (addAccount()){
                                submitSuccess();
                            }
                            else {
                                submitFailed();
                            }

                            progressDialog.dismiss();
                        }

                    }

                },500);

        submit_button_addpwd.setEnabled(true);

    }

    //输入验证
    private boolean validate(){
        boolean valid = true;

        String title = title_addpwd_EditText.getText().toString().trim();
        String username = username_addpwd_EditText.getText().toString().trim();
        String password = password_addpwd_EditText.getText().toString().trim();

        String email = email_addpwd_EditText.getText().toString().trim();

        if (title.isEmpty()){
            title_addpwd_EditText.setError("标题为空！");
            valid = false;
        }

        if (username.isEmpty()){
            username_addpwd_EditText.setError("用户名不能为空！");
            valid = false;
        }

        if (password.isEmpty()){
            password_addpwd_EditText.setError("密码不能为空！");
            valid = false;
        }

        if (!email.equals("")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                email_addpwd_EditText.setError("请输入有效地址！");
                valid = false;
            }
        }

        return valid;
    }

    //提交失败
    private void submitFailed(){
        Toasty.error(getBaseContext(), "提交失败，请检查！", Toast.LENGTH_SHORT, true).show();
        submit_button_addpwd.setEnabled(true);
    }

    //提交成功
    private void submitSuccess(){
        Toasty.success(getBaseContext(), "提交成功！", Toast.LENGTH_SHORT, true).show();
        scrollView.fullScroll(View.FOCUS_UP);
        submit_button_addpwd.setEnabled(true); //回到顶部
        this.clearInput();
    }

    //提交时存在同标题同用户名账户
    private void submitFailed_SameTitleUsername(){
        Toasty.error(getBaseContext(), "已存在同标题同用户名账户！", Toast.LENGTH_SHORT, true).show();
        submit_button_addpwd.setEnabled(true);
    }

    //向 accountBox 中添加 账户信息
    private boolean addAccount(){
        boolean isSuccessful = false;

        //ID 和 加解密规则（邮箱地址）
        String encrypt_rule = "";
        long id = 0;


        if (!mCache.getAsString("email").equals("")) {
            encrypt_rule = mCache.getAsString("email");
        }
        if (!mCache.getAsString("registerID").equals("")){
            id = Long.parseLong(mCache.getAsString("registerID"));
        }


        String title = ""; String username = ""; String password_aes = "";
        String email = ""; String url = ""; String phone = ""; String question = ""; String answer = "";

        title = title_addpwd_EditText.getText().toString().trim();
        username = username_addpwd_EditText.getText().toString().trim();
        password_aes = Encrypt.AESEncrypt(password_addpwd_EditText.getText().toString().trim(), encrypt_rule); //AES 加密

        email = email_addpwd_EditText.getText().toString().trim();
        url = url_addpwd_EditText.getText().toString().trim();
        phone = phone_addpwd_EditText.getText().toString().trim();
        question = question_addpwd_EditText.getText().toString().trim();
        answer = answer_addpwd_EditText.getText().toString().trim();


        //无相同账户
        if (!accountQuery(title, username)){

            RegisterEntity registerEntity =  registerEntityBox.get(id);
            AccountEntity accountEntity = new AccountEntity();

            //设置一对一的 target 对象
            accountEntity.accountToRegister.setTarget(registerEntity);

            accountEntity.setTitle(title);
            accountEntity.setUser_name(username);
            accountEntity.setPassword(password_aes);
            accountEntity.setEmail(email);
            accountEntity.setUrl_address(url);
            accountEntity.setPhone(phone);
            accountEntity.setQuestion(question);
            accountEntity.setAnswer(answer);

            accountEntityBox.put(accountEntity);

            isSuccessful = true;
        }

        return isSuccessful;
    }

    //重复查询
    private boolean accountQuery(String title, String username){
        boolean isAccount = false;

        //多条件查询
        QueryBuilder<AccountEntity> builder = accountEntityBox.query();
        builder.equal(AccountEntity_.title, title)
                .equal(AccountEntity_.user_name, username);

        List<AccountEntity> accountEntityList = builder.build().find();

        //已存在同标题同用户名账户
        if (!accountEntityList.isEmpty()){
            isAccount = true;
            submitFailed_SameTitleUsername();
        }

        return isAccount;
    }

}
