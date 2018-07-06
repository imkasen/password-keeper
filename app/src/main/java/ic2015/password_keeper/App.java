package ic2015.password_keeper;

import android.app.Application;

import es.dmoral.toasty.Toasty;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

public class App extends Application {

    //ObjectBox 数据库初始化
    private BoxStore boxStore;

    @Override
    public void onCreate(){

        super.onCreate();

        //气泡通知框自定义颜色
        Toasty.Config.getInstance()
                .setErrorColor(getResources().getColor(R.color.colorRed))
                .apply();

        //数据库初始化
        boxStore = MyObjectBox.builder().androidContext(App.this).build();
        //调试
        if (BuildConfig.DEBUG){
            new AndroidObjectBrowser(boxStore).start(this);
        }

    }

    //获得 BoxStore
    public BoxStore getBoxStore() {
        return boxStore;
    }

}
