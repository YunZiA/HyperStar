package com.yunzia.hyperstar.hook.init;


import com.yunzia.hyperstar.hook.app.systemui.os1.NavigationBarBackground;
import com.yunzia.hyperstar.hook.base.Init;
import com.yunzia.hyperstar.hook.base.InitHooker;

@Init(packageName = "com.android.systemui")
public class SystemUIHookForOS1 extends InitHooker {

    private final PluginHookForOS1 pluginHookForOS1 = new PluginHookForOS1();


    @Override
    public void initResources() {
        initResource(pluginHookForOS1);
        if (!resparam.packageName.equals(mPackageName)) return;
    }

    @Override
    public void initHook() {
        initHooker(pluginHookForOS1);
        initHooker(new NavigationBarBackground());

        doTestHook();
    }


    private void doTestHook() {



    }


}
