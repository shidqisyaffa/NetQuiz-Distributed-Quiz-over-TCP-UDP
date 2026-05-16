import java.io.*;
import java.net.*;
import java.util.*;

// Kelas untuk menyimpan data soal
class Question {
    String text, optionA, optionB, optionC, optionD, correctAnswer;

    public Question(String text, String a, String b, String c, String d, String correctAnswer) {
        this.text = text;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correctAnswer = correctAnswer;
    }
}

public class Server {
    private static List<Question> questionBank = new ArrayList<>();
    private static int clientCounter = 1;

    public static void main(String[] args) {
        initializeQuestions();
        System.out.println("====================================");
        System.out.println("  SERVER QUIZ DISTRIBUSI BERJALAN  ");
        System.out.println("====================================");
        System.out.println("Menunggu koneksi dari client pada port 5000...\n");

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (true) {
                // Menerima koneksi TCP dari client baru
                Socket clientSocket = serverSocket.accept();
                String clientId = "Client-" + clientCounter++;
                System.out.println("[KONEKSI BARU] " + clientId + " terhubung dari " + clientSocket.getInetAddress());
                
                // Menjalankan thread baru untuk melayani client secara asinkron
                new ClientHandler(clientSocket, clientId).start();
            }
        } catch (IOException e) {
            System.err.println("Gagal menjalankan server: " + e.getMessage());
        }
    }

    private static void initializeQuestions() {
        questionBank.add(new Question("Apa ibukota Indonesia?", "Bandung", "Jakarta", "Surabaya", "Medan", "B"));
        questionBank.add(new Question("Bahasa pemrograman yang memiliki lambang secangkir kopi adalah?", "Python", "C++", "Java", "PHP", "C"));
        questionBank.add(new Question("Protokol yang digunakan untuk mengirim email adalah?", "HTTP", "SMTP", "FTP", "SNMP", "B"));
        questionBank.add(new Question("Kepanjangan dari TCP adalah?", "Transmission Control Protocol", "Transfer Control Protocol", "Transmission Core Protocol", "Transfer Center Protocol", "A"));
        questionBank.add(new Question("Manakah yang merupakan connectionless protocol?", "TCP", "HTTP", "FTP", "UDP", "D"));
        questionBank.add(new Question("Perangkat keras yang menghubungkan komputer ke jaringan adalah?", "NIC", "RAM", "CPU", "VGA", "A"));
        questionBank.add(new Question("Port default untuk protokol HTTP adalah?", "21", "22", "80", "443", "C"));
        questionBank.add(new Question("Port default untuk protokol HTTPS adalah?", "80", "443", "8080", "21", "B"));
        questionBank.add(new Question("Lapisan ke-3 pada OSI Model adalah?", "Physical", "Data Link", "Network", "Transport", "C"));
        questionBank.add(new Question("Perintah pada CMD untuk memeriksa konektivitas jaringan adalah?", "ipconfig", "ping", "dir", "ls", "B"));
        questionBank.add(new Question("Sistem penamaan hierarkis untuk alamat web (misal www.google.com) disebut?", "IP Address", "DHCP", "DNS", "MAC Address", "C"));
        questionBank.add(new Question("Protokol yang memberikan IP address secara otomatis kepada client adalah?", "DNS", "DHCP", "FTP", "SMTP", "B"));
        questionBank.add(new Question("Topologi jaringan yang menggunakan sebuah perangkat pusat (hub/switch) adalah?", "Ring", "Bus", "Star", "Mesh", "C"));
        questionBank.add(new Question("Alamat fisik dari sebuah Network Interface Card (NIC) disebut?", "IP Address", "MAC Address", "URL", "Subnet Mask", "B"));
        questionBank.add(new Question("Berapa panjang alamat IPv4 dalam bit?", "16 bit", "32 bit", "64 bit", "128 bit", "B"));
        questionBank.add(new Question("Berapa panjang alamat IPv6 dalam bit?", "32 bit", "64 bit", "128 bit", "256 bit", "C"));
        questionBank.add(new Question("Siapa penemu World Wide Web (WWW)?", "Bill Gates", "Steve Jobs", "Tim Berners-Lee", "Linus Torvalds", "C"));
        questionBank.add(new Question("Metode sinkronisasi koneksi yang digunakan oleh protokol TCP disebut?", "Three-way handshake", "One-way handshake", "Two-way handshake", "No handshake", "A"));
        questionBank.add(new Question("Protokol yang mengamankan transmisi data di web dengan enkripsi adalah?", "HTTP", "HTTPS", "FTP", "Telnet", "B"));
        questionBank.add(new Question("IP address loopback (localhost) secara default adalah?", "192.168.1.1", "10.0.0.1", "172.16.0.1", "127.0.0.1", "D"));
        questionBank.add(new Question("Manakah yang merupakan tipe kabel jaringan komputer?", "VGA", "HDMI", "UTP", "SATA", "C"));
        questionBank.add(new Question("Lapisan paling atas (ke-7) dalam OSI model adalah?", "Application", "Presentation", "Session", "Transport", "A"));
        questionBank.add(new Question("Apa fungsi utama dari sebuah Router?", "Menyimpan data", "Menghubungkan dua atau lebih jaringan berbeda", "Menampilkan antarmuka grafis", "Mencetak dokumen", "B"));
        questionBank.add(new Question("Protokol yang dirancang khusus untuk transfer file adalah?", "SMTP", "POP3", "FTP", "IMAP", "C"));
        questionBank.add(new Question("Port default untuk akses SSH (Secure Shell) adalah?", "21", "22", "23", "25", "B"));
    }

    // Thread untuk menangani masing-masing client
    static class ClientHandler extends Thread {
        private Socket tcpSocket;
        private String clientId;

        public ClientHandler(Socket socket, String clientId) {
            this.tcpSocket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                PrintWriter out = new PrintWriter(tcpSocket.getOutputStream(), true);

                // 1. Terima informasi port UDP dari client melalui TCP
                String portMsg = in.readLine();
                if (portMsg == null || !portMsg.startsWith("UDP_PORT:")) {
                    tcpSocket.close();
                    return;
                }
                int clientUdpPort = Integer.parseInt(portMsg.split(":")[1]);

                // 2. Kirim ID client agar client mengetahui identitasnya
                out.println(clientId);

                // 3. Pilih 5 soal acak (Random) dan tidak kembar
                List<Question> selectedQuestions = new ArrayList<>(questionBank);
                Collections.shuffle(selectedQuestions);
                selectedQuestions = selectedQuestions.subList(0, 5);

                // 4. Kirim 5 soal satu per satu via UDP (Cepat)
                System.out.println("[" + clientId + "] Memilih 5 soal dan mengirim via UDP...");
                InetAddress clientIp = tcpSocket.getInetAddress();
                try (DatagramSocket udpSocket = new DatagramSocket()) {
                    for (int i = 0; i < selectedQuestions.size(); i++) {
                        Question q = selectedQuestions.get(i);
                        String msg = "Q" + (i + 1) + ":\n" + q.text + "\n" +
                                     "A. " + q.optionA + "\n" +
                                     "B. " + q.optionB + "\n" +
                                     "C. " + q.optionC + "\n" +
                                     "D. " + q.optionD;
                        byte[] sendData = msg.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientIp, clientUdpPort);
                        udpSocket.send(sendPacket);
                        
                        // Menambahkan delay kecil (1 detik) agar tidak terjadi tabrakan paket (packet loss)
                        Thread.sleep(1000);
                    }
                }
                System.out.println("[" + clientId + "] Semua soal berhasil dikirim.");

                // 5. Terima jawaban dari client melalui TCP (Reliable)
                int score = 0;
                System.out.println("[" + clientId + "] Menunggu jawaban via TCP...");
                for (int i = 0; i < 5; i++) {
                    String answerLine = in.readLine();
                    if (answerLine != null) {
                        // Menampilkan jawaban persis seperti permintaan: "Client-1 | Q1: B"
                        System.out.println(answerLine); 
                        
                        // Ekstrak jawaban client (mengambil huruf terakhir)
                        String[] parts = answerLine.split(":");
                        if (parts.length == 2) {
                            String clientAnswer = parts[1].trim().toUpperCase();
                            // Cek apakah jawaban benar
                            if (clientAnswer.equals(selectedQuestions.get(i).correctAnswer)) {
                                score++;
                            }
                        }
                    }
                }

                // 6. Output skor akhir di server dan beritahu client
                System.out.println(clientId + ": " + score + "/5 benar\n");
                out.println("Skor akhir Anda: " + score + "/5 benar");

                // Menutup koneksi TCP jika sudah selesai
                tcpSocket.close();
            } catch (Exception e) {
                System.out.println("Error pada " + clientId + ": " + e.getMessage());
            }
        }
    }
}
