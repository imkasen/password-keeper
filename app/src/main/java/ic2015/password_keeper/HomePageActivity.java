package ic2015.password_keeper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;

public class HomePageActivity extends AppCompatActivity {

    //侧边栏
    private SlidingRootNav slidingRootNav;

    //缓存
    ACache mCache;

    //注册者 属性 Box
    private Box<RegisterEntity> registerEntityBox;
    private Box<AccountEntity> accountEntityBox;

    //复用查询
    Query<RegisterEntity> query;

    //侧边栏
    @BindView(R.id.homepage_TextView) CommonTextView homepage_textView; //首页
    @BindView(R.id.addPwd_TextView) CommonTextView addPwd_textView; //添加账户
    @BindView(R.id.checkPwd_TextView) CommonTextView checkPwd_textView; //查看账户
    @BindView(R.id.setting_TextView) CommonTextView setting_textView; //设置
    @BindView(R.id.about_TextView) CommonTextView about_textView; //关于界面
    @BindView(R.id.exit_TextView) CommonTextView exit_textView; //退出

    @BindView(R.id.toolbar_hp) Toolbar toolbar_hp; //首页 toolbar

    @BindView(R.id.hello_TextView) CommonTextView hello_textView; //欢迎 TextView
    @BindView(R.id.sum_TextView) TextView sum_textView;//统计账户 TextView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_home_page);

        //左侧滑动栏
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar_hp)
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
        this.setSupportActionBar(toolbar_hp);

        //初始化界面
        this.initialView();

        //统计已保存账户数量
        this.initAccountSum();
    }

    //初始化界面
    private void initialView(){

        //点击 toolbar 导航图标
        toolbar_hp.setNavigationOnClickListener(new View.OnClickListener() {
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

        //hello_TextView 设置
        if (!getUserName().equals("")) {
            String username = mCache.getAsString("user_name").toUpperCase().trim();
            String hello = "欢迎，" + username + "！";
            hello_textView.setLeftTextString(hello);
        }

        //----------- 侧边栏设置 -----------

        //点击首页栏，只需关闭侧边栏
        homepage_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
            }
        });

        //点击添加密码栏，关闭侧边栏，跳转
        addPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, AddPwdActivity.class);
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
                Intent intent = new Intent(HomePageActivity.this, CheckPwdActivity.class);
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
                Intent intent = new Intent(HomePageActivity.this, SettingActivity.class);
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
                Intent intent = new Intent(HomePageActivity.this, AboutActivity.class);
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
        toolbar_hp.setTitle("首页");
        toolbar_hp.setTitleTextColor(getResources().getColor(R.color.white)); //白色
        toolbar_hp.setNavigationIcon(R.drawable.menu_icon);
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
        Intent intent = new Intent(HomePageActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //利用邮箱查询用户名和 ID
    private String getUserName(){
        String user_name;
        String id;

        String userEmail = mCache.getAsString("email"); //从缓存中获得邮箱信息

        query = registerEntityBox.query().equal(RegisterEntity_.email, userEmail).build();

        List<RegisterEntity> registerEntityList = query.find();

        user_name = registerEntityList.get(0).getUsername();
        id = Long.toString(registerEntityList.get(0).getId());

        if (!user_name.equals("")){
            mCache.put("user_name", user_name);
            mCache.put("registerID", id);
        }

        return user_name;
    }

    //统计 TextView
    private void initAccountSum(){
        long id = 0;

        if (!mCache.getAsString("registerID").equals("")){
            id = Long.parseLong(mCache.getAsString("registerID"));
        }

        //获取账户列表
        List<AccountEntity> accountEntityList =
                accountEntityBox.query().equal(AccountEntity_.accountToRegisterId, id).build().find();

        String size = String.valueOf(accountEntityList.size());

        String sum_text = "您共保存了 " + size + " 个账户";
        sum_textView.setText(sum_text);

    }

}
