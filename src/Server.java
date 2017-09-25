import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IDE : IntelliJ IDEA
 * Created by minho on 2017. 9. 25..
 */

public class Server {

    ExecutorService executorService;
    ServerSocket serverSocket;

    List<Client> connections = new Vector<>();

    void startServer() {

        executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors()
        );

        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress("localhost", 5001));
        } catch (Exception e) {

            if(!serverSocket.isClosed()) {
                stopServer();
            }

            return;
        }

        Runnable runnable = () -> {
            System.out.println("서버 시작");

            while(true) {
                try {

                    Socket socket = serverSocket.accept();
                    System.out.println("[연결 수락 : " + socket.getRemoteSocketAddress() + "]");
                    Client client = new Client(socket);
                    connections.add(client);
                    System.out.println("[연결 개수: " + connections.size() + "]");

                } catch (Exception e) {
                    if (!serverSocket.isClosed()) {
                        break;
                    }
                }
            }
        };

        executorService.submit(runnable);
    }

    void stopServer() {

        try {

            connections.stream().forEach(
                    client -> {
                        try {
                            client.socket.close();
                        } catch (Exception e) {

                        }
                    }
            );

            if (isOpeningServerScoket()) {
                serverSocket.close();
            }

            if (isExecutingService()) {
                executorService.shutdown();
            }

            System.out.println("서버 종료");
        } catch (Exception e) {

        }

    }

    private boolean isOpeningServerScoket() {
        return serverSocket != null && !serverSocket.isClosed();
    }

    private boolean isExecutingService() {
        return executorService != null && !executorService.isShutdown();
    }



}
