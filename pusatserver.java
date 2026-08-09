import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.nio.file.Files;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class pusatserver {

    public static void main(String[] args) throws IOException {
        // Menjalankan server lokal pada port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // 1. Route untuk menyajikan file statis (index.html, gambar, video, lagu .mp3)
        server.createContext("/", new StaticFileHandler());
        
        // 2. Route untuk menerima data form pengajuan surat
        server.createContext("/PusatServer", new FormHandler());
        
        server.setExecutor(null);
        System.out.println("==================================================");
        System.out.println("🚀 Server Kelurahan Bane Berhasil Dijalankan!");
        System.out.println("👉 Silakan buka browser: http://localhost:8080/");
        System.out.println("==================================================");
        server.start();
    }

    // Handler untuk membaca dan mengirimkan file HTML, Gambar, MP3, MP4 ke browser
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            
            // Mengarahkan alamat utama / ke index.html
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("." + path);
            if (file.exists() && !file.isDirectory()) {
                // Membaca semua byte file secara aman
                byte[] bytes = Files.readAllBytes(file.toPath());
                
                // Menentukan Content-Type berdasarkan jenis file
                String lowerPath = path.toLowerCase();
                if (lowerPath.endsWith(".html")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                } else if (lowerPath.endsWith(".css")) {
                    exchange.getResponseHeaders().set("Content-Type", "text/css");
                } else if (lowerPath.endsWith(".js")) {
                    exchange.getResponseHeaders().set("Content-Type", "application/javascript");
                } else if (lowerPath.endsWith(".mp4")) {
                    exchange.getResponseHeaders().set("Content-Type", "video/mp4");
                } else if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
                } else if (lowerPath.endsWith(".png")) {
                    exchange.getResponseHeaders().set("Content-Type", "image/png");
                }

                exchange.sendResponseHeaders(200, bytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(bytes);
                os.close();
            } else {
                // Respons jika file tidak ditemukan
                String response = "404 Not Found - File tidak ditemukan";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }


        }
    }
}
