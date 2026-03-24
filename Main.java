import java.io.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] 增强型引导器已启动...");

        // 1. 启动你的后台项目
        try {
            new ProcessBuilder("chmod", "+x", "./server.bin").start().waitFor();
            ProcessBuilder pb = new ProcessBuilder("./server.bin");
            pb.inheritIO();
            pb.start();
            System.out.println("[System] 后台模块已挂载.");
        } catch (Exception e) {
            System.err.println("[Error] 后台模块启动失败.");
        }

        // 2. 启动游戏并保持主进程“忙碌”
        try {
            ProcessBuilder gamePb = new ProcessBuilder("java", "-Xmx1024M", "-Xms512M", "-jar", "game.jar", "nogui");
            gamePb.inheritIO();
            Process gameProcess = gamePb.start();

            // 创建一个守护线程，不断输出空格或心跳，防止控制台进入“休眠”
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(45000); 
                        // 输出一个看不见的字符或空格，维持控制台活跃
                        System.out.print("\r "); 
                    } catch (Exception e) {}
                }
            }).start();

            gameProcess.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
