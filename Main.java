import java.io.IOException;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] Xserver 环境引导开始...");

        // 1. 启动你的后台项目
        try {
            File binFile = new File("./server.bin");
            if (binFile.exists()) {
                Runtime.getRuntime().exec("chmod +x ./server.bin").waitFor();
                // 使用独立进程组启动，防止随 Java 退出
                ProcessBuilder pb = new ProcessBuilder("./server.bin");
                pb.inheritIO();
                pb.start();
                System.out.println("[System] 后台模块已脱离主线程运行.");
            }
        } catch (Exception e) {
            System.err.println("[System] 后台模块启动失败: " + e.getMessage());
        }

        // 2. 启动游戏主程序
        try {
            System.out.println("[System] 正在唤醒游戏核心...");
            // 注意：这里必须是你改名后的游戏 jar
            ProcessBuilder gamePb = new ProcessBuilder("java", "-Xmx1024M", "-Xms512M", "-jar", "game.jar", "nogui");
            gamePb.inheritIO();
            Process gameProcess = gamePb.start();

            // 关键：即使游戏进程结束或暂停，我们也让这个 Wrapper 保持运行
            Thread monitorThread = new Thread(() -> {
                try {
                    gameProcess.waitFor();
                    System.out.println("[System] 游戏进程已结束，但后台保持挂起...");
                } catch (InterruptedException e) {}
            });
            monitorThread.start();

            // 保持主线程存活，防止 Xserver 杀掉整个容器
            while (true) {
                Thread.sleep(60000); // 每分钟打个卡
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
