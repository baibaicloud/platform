package com.loon.bridge.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地命令相关的工具
 *
 * @author nbflow
 */
public final class CmdUtil {

    /** * The logger. */
    private static Logger logger = LoggerFactory.getLogger(CmdUtil.class);

    /** 错误前缀 */
    public static final String ERR_PREFIX = "[ERR]";

    private CmdUtil() {
    }

    /**
     * 执行命令
     * 
     * @param command 命令
     * @return 执行结果，0表示成功，非0表示失败
     */
    public static int execute(String command) {
        return execute(command, null);
    }

    /**
     * 执行命令
     * 
     * @param command 命令
     * @return 执行结果，0表示成功，非0表示失败
     */
    public static int execute(String[] command) {
        return execute(command, null);
    }

    /**
     * 执行命令
     * 
     * @param command 命令
     * @param os 将执行结果输出
     * @return 执行结果，0表示成功，非0表示失败
     */
    public static int execute(String command, OutputStream os) {
        return execute(new String[]{command}, os);
    }

    /**
     * 执行命令
     * 
     * @param command 命令
     * @param os 将执行结果输出
     * @return 执行结果，0表示成功，非0表示失败
     */
    public static int execute(String[] command, OutputStream os) {
        int ret = -1;

        try {
            Process p = null;
            if (command.length == 1) {
                p = Runtime.getRuntime().exec(command[0]);
            } else {
                p = Runtime.getRuntime().exec(command);
            }

            // any error message
            StreamGobbler error = new StreamGobbler(p.getErrorStream(), StreamGobbler.TYPE_ERR);
            error.start();
            // any output
            StreamGobbler output = new StreamGobbler(p.getInputStream(), StreamGobbler.TYPE_OUT, os);
            output.start();

            ret = p.waitFor();
            logger.debug("exit code: {}", ret);
        } catch (Exception e) {
            logger.error("execute command error.", e);
        }

        return ret;
    }

    /**
     * 负责读取数据流
     *
     * @author nbflow
     */
    private static class StreamGobbler extends Thread {

        public static final String TYPE_OUT = "Output";
        public static final String TYPE_ERR = "Error";

        private InputStream is = null;
        private String type = null;
        private OutputStream os = null;

        public StreamGobbler(InputStream is, String type) {
            this(is, type, null);
        }

        public StreamGobbler(InputStream is, String type, OutputStream redirect) {
            this.setName("cmd-exec-" + type.toLowerCase() + "-" + Thread.currentThread().getId());
            this.is = is;
            this.type = type;
            this.os = redirect;
        }

        @Override
        public void run() {
            try {
                PrintWriter pw = null;
                if (os != null) {
                    pw = new PrintWriter(os);
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    if (pw != null) {
                        pw.println(line);
                    }

                    if (TYPE_ERR.equals(type)) {
                        logger.error(line);
                    } else {
                        logger.debug(line);
                    }
                }
                if (pw != null) {
                    pw.flush();
                }
            } catch (IOException ioe) {
                logger.error(ioe.getMessage(), ioe);
            }
        }
    }

}
