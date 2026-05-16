import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static void main(String[] args) {
        String serverIp = "127.0.0.1";
        int serverPort = 5000;

        try {
            // 1. Buat socket UDP dengan port acak untuk menerima broadcast soal dari server
            DatagramSocket udpSocket = new DatagramSocket();
            int myUdpPort = udpSocket.getLocalPort();

            System.out.println("Menghubungkan ke Server " + serverIp + ":" + serverPort + "...");

            // 2. Konek ke server menggunakan TCP
            Socket tcpSocket = new Socket(serverIp, serverPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);

            // 3. Informasikan port UDP yang kita gunakan ke server (via TCP)
            out.println("UDP_PORT:" + myUdpPort);

            // 4. Dapatkan ID client dari server (Misal: Client-1)
            String clientId = in.readLine();
            System.out.println("Terhubung! Anda terdaftar sebagai: " + clientId);
            System.out.println("Bersiap menerima soal via UDP (delay 1 detik antar soal)...\n");

            // 5. Menerima 5 soal dari server via UDP
            List<String> questions = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                udpSocket.receive(receivePacket);
                
                String questionStr = new String(receivePacket.getData(), 0, receivePacket.getLength());
                questions.add(questionStr);
                System.out.println("> Soal " + (i + 1) + " diterima.");
            }
            // Tutup UDP socket karena semua soal sudah diterima
            udpSocket.close(); 
            
            System.out.println("\n=======================");
            System.out.println("       MULAI QUIZ      ");
            System.out.println("=======================");
            Scanner scanner = new Scanner(System.in);

            // 6. Iterasi untuk menampilkan soal satu per satu dan meminta input jawaban
            for (int i = 0; i < 5; i++) {
                System.out.println("\n" + questions.get(i));
                String answer = "";
                
                // Validasi input hanya A, B, C, atau D
                while (true) {
                    System.out.print("Jawaban Anda (A/B/C/D): ");
                    answer = scanner.nextLine().trim().toUpperCase();
                    if (answer.equals("A") || answer.equals("B") || answer.equals("C") || answer.equals("D")) {
                        break;
                    }
                    System.out.println("(!) Input tidak valid! Harap masukkan huruf A, B, C, atau D saja.");
                }

                // 7. Kirim jawaban yang valid ke server menggunakan TCP (Reliable)
                // Sesuai format permintaan: Client-1 | Q1: B
                out.println(clientId + " | Q" + (i + 1) + ": " + answer);
            }

            // 8. Menunggu dan menampilkan hasil kalkulasi skor dari server via TCP
            String finalScore = in.readLine();
            System.out.println("\n=======================");
            System.out.println("         HASIL         ");
            System.out.println("=======================");
            System.out.println(finalScore);

            // Bersih-bersih: tutup koneksi TCP dan scanner
            tcpSocket.close();
            scanner.close();

        } catch (ConnectException e) {
            System.out.println("Tidak dapat terhubung ke server. Pastikan Server sudah berjalan.");
        } catch (IOException e) {
            System.out.println("Terjadi error koneksi: " + e.getMessage());
        }
    }
}
