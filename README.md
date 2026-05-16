# 🎯 NetQuiz: Distributed Quiz over TCP & UDP

Projek ini adalah sebuah aplikasi jaringan berbasis *Client-Server* yang menyajikan simulasi kuis interaktif secara terdistribusi. 

Proyek ini dibangun menggunakan bahasa Java Standard Library dan bertujuan untuk mendemonstrasikan perpaduan fungsional antara protokol **TCP** (Transmission Control Protocol) dan **UDP** (User Datagram Protocol) dalam satu program utuh.

---

## 🧠 2. Konsep Sistem

Proyek ini sengaja dirancang untuk memanfaatkan kekuatan utama dari dua protokol yang berbeda:

* **TCP (Reliable):** Digunakan pada saat koneksi awal (Handshake) dan untuk **mengirim jawaban** serta **menerima skor akhir**. TCP dipilih karena kita tidak boleh kehilangan paket jawaban client (jika hilang/packet loss, nilai mahasiswa bisa salah).
* **UDP (Cepat / Fire-and-forget):** Digunakan oleh Server untuk **men-distribusikan soal (broadcast)** ke client. Mengingat UDP jauh lebih cepat tanpa perlu proses pembentukan *session*, hal ini sangat efisien untuk proses distribusi soal dalam jumlah banyak.

---

## ✨ 3. Fitur Utama

* **Bank Soal:** Terdapat 25 soal seputar Jaringan & IT di memori (tidak memerlukan database).
* **Random 5 Soal per Client:** Setiap *client* yang terhubung akan mendapatkan 5 soal unik secara acak.
* **Multi-Client Support:** Server memanfaatkan *Threading* sehingga dapat melayani banyak client di waktu yang bersamaan.
* **Arsitektur Hybrid:** Menggunakan **UDP** untuk mendistribusikan soal dan **TCP** untuk memvalidasi/mengirim jawaban klien secara aman.

---

## 🚀 4. Cara Menjalankan

Berikut adalah panduan cara menjalankan aplikasi di terminal/cmd.

### Compile:

```bash
javac Server.java
javac Client.java
```

### Jalankan Server:

```bash
java Server
```

### Jalankan Client:

```bash
java Client
```

**Penjelasan Eksekusi:**
* **Server harus dijalankan terlebih dahulu** agar port TCP (5000) terbuka dan siap menerima pendaftaran client.
* **Client dijalankan setelah server aktif**, sehingga ia tidak akan terkena error *Connection Refused*.

---

## 👥 5. Uji Multi-Client

Sistem ini telah mendemonstrasikan arsitektur *Concurrent Server* yang bisa melayani banyak *client*.

**Cara mengujinya:**
* Buka **2–3 terminal** terpisah di komputer Anda.
* Pada masing-masing terminal baru tersebut, jalankan perintah ini secara bersamaan:

```bash
java Client
```

**Hasil yang didapatkan:**
* Di terminal server, Anda akan melihat munculnya notifikasi bahwa client masuk dan diberi identitas unik secara berurutan, seperti:
  * `Client-1`
  * `Client-2`
  * `Client-3`
* Ini menunjukkan bahwa sistem mendukung *multi-client* dan setiap client memiliki sesi *quiz* masing-masing tanpa tabrakan.

---

## 🔄 6. Alur Program

Berikut adalah step-by-step bagaimana program ini bekerja:

1. **Client connect ke server (TCP):** Client mendaftarkan diri dan menyertakan port UDP kosong yang siap menerima *broadcast*.
2. **Server kirim soal (UDP):** Server mengacak bank soal dan mengirimkan **5 buah soal** ke client tersebut secara bergantian lewat UDP.
3. **Client jawab:** Klien menerima dan menampilkan pertanyaan di layar, lalu user memasukkan jawabannya (A/B/C/D).
4. **Client kirim jawaban (TCP):** Setiap kali klien menginput jawaban, informasi dikirim ke server secara *reliable* melalui jalur TCP.
5. **Server hitung skor:** Server mencocokkan jawaban dengan kunci jawaban dan mengirimkan nilai skor akhir ke client.

---

## 💻 7. Contoh Output

**Tampilan di layar Server:**
```text
====================================
  SERVER QUIZ DISTRIBUSI BERJALAN  
====================================
Menunggu koneksi dari client pada port 5000...

[KONEKSI BARU] Client-1 terhubung dari /127.0.0.1
[Client-1] Memilih 5 soal dan mengirim via UDP...
[Client-1] Semua soal berhasil dikirim.
[Client-1] Menunggu jawaban via TCP...
Client-1 | Q1: B
Client-1 | Q2: A
Client-1 | Q3: D
Client-1 | Q4: C
Client-1 | Q5: A
Client-1: 4/5 benar
```

**Tampilan di layar Client:**
```text
Menghubungkan ke Server 127.0.0.1:5000...
Terhubung! Anda terdaftar sebagai: Client-1
Bersiap menerima soal via UDP (delay 1 detik antar soal)...

> Soal 1 diterima.
> Soal 2 diterima.
> Soal 3 diterima.
> Soal 4 diterima.
> Soal 5 diterima.

=======================
       MULAI QUIZ      
=======================

Q1:
Protokol yang digunakan untuk mengirim email adalah?
A. HTTP
B. SMTP
C. FTP
D. SNMP
Jawaban Anda (A/B/C/D): B

... [Soal Q2-Q5]

=======================
         HASIL         
=======================
Skor akhir Anda: 4/5 benar
```

---

## ⚠️ 8. Catatan Penting

* Program berjalan menggunakan IP **localhost** (`127.0.0.1`), tidak perlu pengaturan koneksi internet.
* Pastikan **Java Development Kit (JDK)** sudah ter-install di komputer Anda.
* Wajib menjalankan **server terlebih dahulu** sebelum menjalankan client.
* Jawaban bersifat *Case-Insensitive* (huruf besar/kecil seperti 'a' atau 'A' akan dianggap sama).
