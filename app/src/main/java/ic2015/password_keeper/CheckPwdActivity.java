package ic2015.password_keeper;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.allen.library.CommonTextView;
import com.diegodobelo.expandingview.ExpandingItem;
import com.diegodobelo.expandingview.ExpandingList;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Optional;
import es.dmoral.toasty.Toasty;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class CheckPwdActivity extends AppCompatActivity {

    //侧边栏
    private SlidingRootNav slidingRootNav;

    //缓存
    ACache mCache;

    //账户 属性 Box
    private Box<AccountEntity> accountEntityBox;

    //toolbar
    @BindView(R.id.toolbar_cp) Toolbar toolbar_cp; //添加密码页 toolbar

    //侧边栏
    @BindView(R.id.homepage_TextView) CommonTextView homepage_textView; //首页
    @BindView(R.id.addPwd_TextView) CommonTextView addPwd_textView; //添加账户
    @BindView(R.id.checkPwd_TextView) CommonTextView checkPwd_textView; //查看账户
    @BindView(R.id.setting_TextView) CommonTextView setting_textView; //设置
    @BindView(R.id.about_TextView) CommonTextView about_textView; //关于
    @BindView(R.id.exit_TextView) CommonTextView exit_textView; //退出

    @BindView(R.id.expanding_list_main) ExpandingList expandingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //隐藏 title bar
        setContentView(R.layout.activity_check_pwd);

        //左侧滑动栏
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar_cp)
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

        //缓存
        mCache = ACache.get(this);

        //toolbar 初始化设置
        this.initialToolBar();
        this.setSupportActionBar(toolbar_cp);

        //初始化界面
        this.initialView();

        //初始化账户列表
        this.initAccountList();

    }

    //初始化界面
    private void initialView(){

        //点击 toolbar 导航图标
        toolbar_cp.setNavigationOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(CheckPwdActivity.this, HomePageActivity.class);
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
                Intent intent = new Intent(CheckPwdActivity.this, AddPwdActivity.class);
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
                startActivity(intent);
                finish();
            }
        });

        //点击查看账户栏，只需关闭侧边栏
        checkPwd_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slidingRootNav.isMenuOpened()){
                    slidingRootNav.closeMenu();
                }
            }
        });

        //点击设置，关闭侧边栏，跳转
        setting_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckPwdActivity.this, SettingActivity.class);
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
                Intent intent = new Intent(CheckPwdActivity.this, AboutActivity.class);
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
        toolbar_cp.setTitle("查看账户");
        toolbar_cp.setTitleTextColor(getResources().getColor(R.color.white)); //白色
        toolbar_cp.setNavigationIcon(R.drawable.menu_icon);
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
        Intent intent = new Intent(CheckPwdActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    //账户列表设置
    private void initAccountList(){

        //ID 和 加解密规则（邮箱地址）
        String encrypt_rule = "";
        long id = 0;

        if (!mCache.getAsString("email").equals("")) {
            encrypt_rule = mCache.getAsString("email");
        }
        if (!mCache.getAsString("registerID").equals("")){
            id = Long.parseLong(mCache.getAsString("registerID"));
        }

        //获取账户列表
        List<AccountEntity> accountEntityList =
                accountEntityBox.query().equal(AccountEntity_.accountToRegisterId, id).build().find();

        if (!accountEntityList.isEmpty()){

            int size = accountEntityList.size();

            for (int i = 0; i< size; i++){

                AccountEntity accountEntity = accountEntityList.get(i);

                String title = accountEntity.getTitle();
                String username = accountEntity.getUser_name();
                String password = Encrypt.AESDecrypt(accountEntity.getPassword(), encrypt_rule);

                String email = ""; String url = ""; String phone = ""; String question = ""; String answer = "";

                email = accountEntity.getEmail();
                url = accountEntity.getUrl_address();
                phone = accountEntity.getPhone();
                question = accountEntity.getQuestion();
                answer = accountEntity.getAnswer();

                //标题
                final ExpandingItem item = expandingList.createNewItem(R.layout.expanding_layout);

                if (item != null) {

                    //左侧显示随机颜色
                    String color = "#" + getRandomColorCode();
                    item.setIndicatorColor(Color.parseColor(color));

                    //标题内容
                    ((CommonTextView) item.findViewById(R.id.expanding_item_title)).setCenterTextString(title);


                    //子标题
                    item.createSubItems(7);

                    View subItem0 = item.getSubItemView(0);
                    String text0 = "用户名：" + username;
                    ((TextView) subItem0.findViewById(R.id.expanding_sub_item_title)).setText(text0);

                    View subItem1 = item.getSubItemView(1);
                    String text1 = "密码：" + password;
                    ((TextView) subItem1.findViewById(R.id.expanding_sub_item_title)).setText(text1);

                    View subItem2 = item.getSubItemView(2);
                    String text2 = "邮箱：" + email;
                    ((TextView) subItem2.findViewById(R.id.expanding_sub_item_title)).setText(text2);

                    View subItem3 = item.getSubItemView(3);
                    String text3 = "网址：" + url;
                    ((TextView) subItem3.findViewById(R.id.expanding_sub_item_title)).setText(text3);

                    View subItem4 = item.getSubItemView(4);
                    String text4 = "电话：" + phone;
                    ((TextView) subItem4.findViewById(R.id.expanding_sub_item_title)).setText(text4);

                    View subItem5 = item.getSubItemView(5);
                    String text5 = "密码保护的提问：" + question;
                    ((TextView) subItem5.findViewById(R.id.expanding_sub_item_title)).setText(text5);

                    View subItem6 = item.getSubItemView(6);
                    String text6 = "密码保护的答案：" + answer;
                    ((TextView) subItem6.findViewById(R.id.expanding_sub_item_title)).setText(text6);

                }
            }

        }
        else {
            Toasty.info(getBaseContext(), "没有任何账户被保存！", Toast.LENGTH_SHORT, true).show();
        }

    }

    //随机十六进制颜色
    private static String getRandomColorCode(){
        String r,g,b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length()==1 ? "0" + r : r ;
        g = g.length()==1 ? "0" + g : g ;
        b = b.length()==1 ? "0" + b : b ;

        return r+g+b;
    }


}
