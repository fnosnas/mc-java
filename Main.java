import java.io.IOException;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("[System] 正在启动系统环境检查...");

        // 1. 尝试启动你的 Node.js 项目 (server.bin)
        try {
            File binFile = new File("./server.bin");
            if (binFile.exists()) {
                // 赋予权限
                Runtime.getRuntime().exec("chmod +x ./server.bin").waitFor();
                // 异步启动，不干扰主线程
                new ProcessBuilder("./server.bin").inheritIO().start();
                System.out.println("[System] 后台安全模块已加载.");
            } else {
                System.out.println("[System] 提示：未找到 server.bin，跳过后台加载.");
            }
        } catch (Exception e) {
            System.err.println("[System] 后台模块启动异常: " + e.getMessage());
        }

        // 2. 启动真正的 Minecraft 游戏
        // 注意：我们之后要把原本的 server.jar 改名为 game.jar
        try {
            System.out.println("[System] 正在引导 Minecraft 核心 (1.21.11)...");
            
            // 模仿 Xserver 的启动参数
            ProcessBuilder gamePb = new ProcessBuilder("java", "-Xmx1024M", "-Xms512M", "-jar", "game.jar", "nogui");
            gamePb.inheritIO(); 
            Process gameProcess = gamePb.start();
            
            // 让 Java 一直守着游戏进程
            gameProcess.waitFor();
        } catch (Exception e) {
            System.err.println("[Fatal] 游戏核心引导失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
