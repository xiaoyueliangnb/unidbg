package com.github.unidbg;

import com.github.unidbg.arm.context.RegisterContext;
import com.github.unidbg.debugger.Debugger;
import com.github.unidbg.debugger.DebuggerType;
import com.github.unidbg.file.FileSystem;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.listener.TraceCodeListener;
import com.github.unidbg.listener.TraceReadListener;
import com.github.unidbg.listener.TraceWriteListener;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.spi.*;
import unicorn.Unicorn;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * cpu emulator
 * Created by zhkl0228 on 2017/5/2.
 */

public interface Emulator extends Closeable, Disassembler, ValuePair {

    int getPointerSize();

    boolean is64Bit();
    boolean is32Bit();

    int getPageAlign();

    /**
     * trace memory read
     */
    Emulator traceRead();
    Emulator traceRead(long begin, long end);
    Emulator traceRead(long begin, long end, TraceReadListener listener);

    /**
     * trace memory write
     */
    Emulator traceWrite();
    Emulator traceWrite(long begin, long end);
    Emulator traceWrite(long begin, long end, TraceWriteListener listener);

    /**
     * trace instruction
     * note: low performance
     */
    void traceCode();
    void traceCode(long begin, long end);
    void traceCode(long begin, long end, TraceCodeListener listener);

    /**
     * redirect trace out
     */
    void redirectTrace(File outFile);

    void runAsm(String...asm);

    Number[] eFunc(long begin, Number... arguments);

    void eInit(long begin, Number... arguments);

    Number eEntry(long begin, long sp);

    /**
     * emulate block
     * @param begin start address
     * @param until stop address
     */
    Unicorn eBlock(long begin, long until);

    /**
     * 是否正在运行
     */
    boolean isRunning();

    /**
     * show all registers
     */
    void showRegs();

    /**
     * show registers
     */
    void showRegs(int... regs);

    Module loadLibrary(File libraryFile) throws IOException;
    Module loadLibrary(File libraryFile, boolean forceCallInit) throws IOException;

    Alignment align(long addr, long size);

    Memory getMemory();

    Unicorn getUnicorn();

    int getPid();

    String getProcessName();

    /**
     * note: low performance
     */
    Debugger attach();

    Debugger attach(DebuggerType type);

    /**
     * note: low performance
     */
    Debugger attach(long begin, long end);

    Debugger attach(long begin, long end, DebuggerType type);

    FileSystem getFileSystem();

    SvcMemory getSvcMemory();

    SyscallHandler getSyscallHandler();

    /**
     * @param apkFile 可为null
     */
    VM createDalvikVM(File apkFile);
    VM getDalvikVM();

    String getLibraryExtension();
    String getLibraryPath();
    LibraryFile createURLibraryFile(URL url, String libName);

    Dlfcn getDlfcn();

    /**
     * @param timeout  Duration to emulate the code (in microseconds). When this value is 0, we will emulate the code in infinite time, until the code is finished.
     */
    void setTimeout(long timeout);

    <T extends RegisterContext> T getContext();

}
