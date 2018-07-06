package ic2015.password_keeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.CommonTextView;
import com.gc.materialdesign.views.ScrollView;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    //侧边栏
    private SlidingRootNav slidingRootNav;

    //侧边栏
    @BindView(R.id.homepage_TextView) CommonTextView homepage_textView; //首页
    @BindView(R.id.addPwd_TextView) CommonTextView addPwd_textView; //添加账户
    @BindView(R.id.checkPwd_TextView) CommonTextView checkPwd_textView; //查看账户
    @BindView(R.id.setting_TextView) CommonTextView setting_textView; //设置
    @BindView(R.id.about_TextView) CommonTextView about_textView; //关于界面
    @BindView(R.id.exit_TextView) CommonTextView exit_textView; //退出

    @BindView(R.id.toolbar_about) Toolbar toolbar_about; //首页 toolbar

    @BindView(R.id.linearlayout_about) LinearLayout linearLayout_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_about);

        //左侧滑动栏
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar_about)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(true) //侧边栏打开时右侧也能点击
                .withSavedState(savedInstanceState)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.left_drawer_menu)
                .inject();

        //黄油刀
        ButterKnife.bind(this);

        //toolbar 初始化设置
        this.initialToolBar();
        this.setSupportActionBar(toolbar_about);

        //初始化界面
        this.initialView();

        //关于界面
        linearLayout_about.addView(initAboutPage());
    }

    //初始化界面
    private void initialView(){

        //点击 toolbar 导航图标
        toolbar_about.setNavigationOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(AboutActivity.this, HomePageActivity.class);
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
                Intent intent = new Intent(AboutActivity.this, AddPwdActivity.class);
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
                Intent intent = new Intent(AboutActivity.this, CheckPwdActivity.class);
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
                Intent intent = new Intent(AboutActivity.this, SettingActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击关于，只需关闭侧边栏
        about_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
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

    //初始化 toolbar
    private void initialToolBar(){
        toolbar_about.setTitle("关于");
        toolbar_about.setTitleTextColor(getResources().getColor(R.color.white)); //白色
        toolbar_about.setNavigationIcon(R.drawable.menu_icon);
    }

    //退出到登录界面
    private void exitToLoginPage(){
        Intent intent = new Intent(AboutActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //关于界面
    private View initAboutPage(){

        //版本
        Element version = new Element();
        version.setTitle("Version 0.1.0");

        //介绍
        String description = "一个专用于保存互联网账户信息的密码托管软件。";

        View aboutpage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.logo)
                .setDescription(description)
                .addItem(version)
                .create();

        return aboutpage;
    }

}
