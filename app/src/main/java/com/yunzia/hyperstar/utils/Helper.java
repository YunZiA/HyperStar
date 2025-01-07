package com.yunzia.hyperstar.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Helper {

    public static boolean isModuleActive(){
        return false;
    }

    public static boolean isRoot = getRootPermission() != 0;

    public static int getRootPermission(){
        Process process = null;
        int exitCode = -1;
        try {
            process = Runtime.getRuntime().exec("su -c true");
            exitCode = process.waitFor();
        } catch (IOException e) {
            // Handle IOException
        } catch (InterruptedException e) {
            // Handle InterruptedException
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return exitCode;
    }

    public static String rootShell(String cmd) {
        StringBuilder output = new StringBuilder();
        Process process = null;
        DataOutputStream outputStream = null;
        BufferedReader reader = null;

        try {
            // 启动 root shell
            process = Runtime.getRuntime().exec("su");

            // 获取进程的输出流，用于发送命令
            outputStream = new DataOutputStream(process.getOutputStream());
            outputStream.writeBytes(cmd + "\n");
            outputStream.flush();
            outputStream.writeBytes("exit\n");
            outputStream.flush();

            // 获取进程的输入流，用于读取输出
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程结束
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            // 关闭资源
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return output.toString();
    }




}
