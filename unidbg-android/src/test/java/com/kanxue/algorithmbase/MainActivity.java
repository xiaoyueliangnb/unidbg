package com.kanxue.algorithmbase;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.util.IOUtils;
import com.bytedance.frameworks.core.encrypt.TTEncrypt;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.linux.android.AndroidARMEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.StringObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.utils.Inspector;
import com.github.unidbg.Module;
import com.github.unidbg.TraceMemoryHook;

public class MainActivity extends AbstractJni{
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    private final DvmClass MainActivityClass;

    private final boolean logging;
    public MainActivity(boolean logging){
        this.logging = logging;

        emulator = AndroidEmulatorBuilder.for32Bit()
                .setProcessName("com.kanxue.algorithmbase")
                .addBackendFactory(new Unicorn2Factory(true))
                .build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分

        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        vm = emulator.createDalvikVM(); // 创建Android虚拟机
        vm.setVerbose(logging); // 设置是否打印Jni调用细节
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/armeabi-v7a/algorithmbase/libnative-lib.so"), false); // 加载libttEncrypt.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        // dm.callJNI_OnLoad(emulator); // 手动执行JNI_OnLoad函数
        module = dm.getModule(); // 加载好的libttEncrypt.so对应为一个模块

        MainActivityClass = vm.resolveClass("com/kanxue/algorithmbase/MainActivity");
    }
    void test_encodeFromJni_60() throws FileNotFoundException{
        PrintStream redirect = new PrintStream(new FileOutputStream("encodeFromJni_60_" + System.currentTimeMillis() + ".log"), true);
        emulator.traceCode().setRedirect(redirect); // 开启指令级别的trace
        emulator.traceRead().setRedirect(redirect);
        emulator.traceWrite().setRedirect(redirect);


        StringObject result = MainActivityClass.callStaticJniMethodObject(emulator, 
        "encodeFromJni_60()Ljava/lang/String;", 
        new StringObject(vm, "kanxue_0123456789"));
        System.out.println("encodeFromJni_60: " + result.getValue());
    }

    void destroy() {
        IOUtils.close(emulator);
    }

    public static void main(String[] args) throws Exception {
        MainActivity test = new MainActivity(true);
        test.test_encodeFromJni_60();

        test.destroy();
    }
}
