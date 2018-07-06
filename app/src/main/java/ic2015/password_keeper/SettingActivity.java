package ic2015.password_keeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.CommonTextView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SettingActivity extends AppCompatActivity {

    //侧边栏
    private SlidingRootNav slidingRootNav;

    //缓存
    ACache mCache;

    //注册者 属性 Box
    private Box<RegisterEntity> registerEntityBox;
    private Box<AccountEntity> accountEntityBox;

    //侧边栏
    @BindView(R.id.homepage_TextView) CommonTextView homepage_textView; //首页
    @BindView(R.id.addPwd_TextView) CommonTextView addPwd_textView; //添加账户
    @BindView(R.id.checkPwd_TextView) CommonTextView checkPwd_textView; //查看账户
    @BindView(R.id.setting_TextView) CommonTextView setting_textView; //设置
    @BindView(R.id.about_TextView) CommonTextView about_textView; //关于
    @BindView(R.id.exit_TextView) CommonTextView exit_textView; //退出

    @BindView(R.id.empty_account) CommonTextView empty_account_textView; //清空账户
    @BindView(R.id.delete_register) CommonTextView delete_register_textView; //删除账号

    @BindView(R.id.toolbar_setting) Toolbar toolbar_setting; //首页 toolbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_setting);

        //左侧滑动栏
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar_setting)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true) //侧边栏打开时右侧也能点击
                .withSavedState(savedInstanceState)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.left_drawer_menu)
                .inject();

        //黄油刀
        ButterKnife.bind(this);

        //数据库和 Box 初始化
        BoxStore boxStore = ((App)getApplication()).getBoxStore();
        registerEntityBox = boxStore.boxFor(RegisterEntity.class);
        accountEntityBox= boxStore.boxFor(AccountEntity.class);

        //缓存
        mCache = ACache.get(this);

        //toolbar 初始化设置
        this.initialToolBar();
        this.setSupportActionBar(toolbar_setting);

        //初始化界面
        this.initialView();

        //清空已保存的账户
        empty_account_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyAccount();
            }
        });

        //删除该账号
        delete_register_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRegister();
            }
        });

    }

    //初始化界面
    private void initialView(){

        //点击 toolbar 导航图标
        toolbar_setting.setNavigationOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(SettingActivity.this, HomePageActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击添加密码栏，关闭侧边栏，跳转
        addPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AddPwdActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击添加账户栏，关闭侧边栏，跳转
        checkPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, CheckPwdActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击设置，只需关闭侧边栏
        setting_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
            }
        });

        //点击关于，关闭侧边栏，跳转
        about_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
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
        toolbar_setting.setTitle("设置");
        toolbar_setting.setTitleTextColor(getResources().getColor(R.color.white)); //白色
        toolbar_setting.setNavigationIcon(R.drawable.menu_icon);
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
        Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //清空保存的账户
    private void emptyAccount(){

        long id = 0;

        if (!mCache.getAsString("registerID").equals("")){
            id = Long.parseLong(mCache.getAsString("registerID"));
        }

        //获取账户列表
        final List<AccountEntity> accountEntityList =
                accountEntityBox.query().equal(AccountEntity_.accountToRegisterId, id).build().find();

        if (!accountEntityList.isEmpty()) {

            //提示框
            new MaterialStyledDialog.Builder(this)
                    .setIcon(R.drawable.dialog_warning_icon)
                    .setHeaderColor(R.color.colorRed)
                    .setTitle("清空账户？")
                    .setDescription("此账号下所有已经保存的账户信息将被删除！")
                    .withIconAnimation(true)
                    //确认按钮
                    .setPositiveText(R.string.confirm)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            int size = accountEntityList.size();

                            for (int i = 0 ; i < size; i++){
                                accountEntityBox.remove(accountEntityList.get(i));
                            }

                            Toasty.info(getBaseContext(), "已清空 " + String.valueOf(size) + " 个账户！", Toast.LENGTH_SHORT, true).show();
                        }
                    })
                    //取消按钮
                    .setNegativeText(R.string.cancel)
                    .setCancelable(true) //点击对话框外区域取消
                    .show();

        }
        else {
            Toasty.info(getBaseContext(), "已没有任何账户！", Toast.LENGTH_SHORT, true).show();
        }

    }

    //删除账户
    private void deleteRegister(){
        long id = 0;

        if (!mCache.getAsString("registerID").equals("")){
            id = Long.parseLong(mCache.getAsString("registerID"));
        }

        //获取注册者列表
        final List<RegisterEntity> registerEntityList =
                registerEntityBox.query().equal(RegisterEntity_.id, id).build().find();

        //获取账户列表
        final List<AccountEntity> accountEntityList =
                accountEntityBox.query().equal(AccountEntity_.accountToRegisterId, id).build().find();

        if (!registerEntityList.isEmpty()){

            new MaterialStyledDialog.Builder(this)
                    .setIcon(R.drawable.dialog_warning_icon)
                    .setHeaderColor(R.color.colorRed)
                    .setTitle("删除此账号？")
                    .setDescription("此账号以及账号下所有已保存的账户信息都将被删除！")
                    .withIconAnimation(true)
                    //确认按钮
                    .setPositiveText(R.string.confirm)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                            //先清空账号下保存的账户
                            int accountlist_size = accountEntityList.size();

                            if (accountlist_size != 0) {

                                for (int i = 0; i < accountlist_size; i++) {
                                    accountEntityBox.remove(accountEntityList.get(i));
                                }

                            }

                            //再删除账号
                            int registerlist_size = registerEntityList.size();

                            for (int i = 0 ; i < registerlist_size; i++){
                                registerEntityBox.remove(registerEntityList.get(i));
                            }

                            exitToLoginPage();

                        }
                    })
                    //取消按钮
                    .setNegativeText(R.string.cancel)
                    .setCancelable(true) //点击对话框外区域取消
                    .show();
        }

    }

}
