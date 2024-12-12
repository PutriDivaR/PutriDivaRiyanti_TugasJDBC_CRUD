import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Simulator {
    //koneksi database JDBC
    private static final String DB_URL = "jdbc:mysql://localhost:3306/supermarket";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Login
            login();
    
            // Menu utama
            while (true) {
                JPanel menuPanel = new JPanel(new GridLayout(0, 1, 5, 5));
                JButton btnTransaksiBaru = new JButton("Transaksi Baru");
                JButton btnLihatData = new JButton("Lihat Data Transaksi");
                JButton btnEditTransaksi = new JButton("Update/Edit Transaksi");
                JButton btnHapusTransaksi = new JButton("Hapus Transaksi");
                JButton btnKeluar = new JButton("Keluar");
    
                btnTransaksiBaru.addActionListener(e -> transaksiBaru(connection));
                btnLihatData.addActionListener(e -> lihatDataTransaksi(connection));
                btnEditTransaksi.addActionListener(e -> editTransaksi(connection));
                btnHapusTransaksi.addActionListener(e -> hapusTransaksi(connection));
                btnKeluar.addActionListener(e -> {
                    JOptionPane.showMessageDialog(null, "Terima kasih telah menggunakan aplikasi!");
                    System.exit(0);
                });
    
                menuPanel.add(btnTransaksiBaru);
                menuPanel.add(btnLihatData);
                menuPanel.add(btnEditTransaksi);
                menuPanel.add(btnHapusTransaksi);
                menuPanel.add(btnKeluar);
    
                JOptionPane.showMessageDialog(null, menuPanel, "Menu Utama", JOptionPane.PLAIN_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi ke database gagal: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private static void login() {
        while (true) {
            try {
                JPanel loginPanel = new JPanel(new GridLayout(3, 2, 5, 5));
                JTextField usernameField = new JTextField();
                JPasswordField passwordField = new JPasswordField();
                JTextField captchaField = new JTextField();

                String generatedCaptcha = generateCaptcha();

                loginPanel.add(new JLabel("Username:"));
                loginPanel.add(usernameField);
                loginPanel.add(new JLabel("Password:"));
                loginPanel.add(passwordField);
                loginPanel.add(new JLabel("Captcha (" + generatedCaptcha + "):"));
                loginPanel.add(captchaField);

                int loginResult = JOptionPane.showConfirmDialog(
                        null,
                        loginPanel,
                        "Log in",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

                if (loginResult != JOptionPane.OK_OPTION) {
                    JOptionPane.showMessageDialog(null, "Login dibatalkan!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }

                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String captcha = captchaField.getText().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    throw new Exception("Username dan Password tidak boleh kosong!");
                }

                if (!captcha.equals(generatedCaptcha)) {
                    throw new Exception("Captcha tidak sesuai!");
                }

                JOptionPane.showMessageDialog(null, "Login berhasil!", "Info", JOptionPane.INFORMATION_MESSAGE);
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Login Gagal", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

//BAGIAN CRUD
    //CREATE Data Transaksi Baru
    private static void transaksiBaru(Connection connection) {
        try {
            JPanel panel = new JPanel(new GridLayout(6, 2, 5, 5));
            JTextField noFakturField = new JTextField();
            JTextField kodeBarangField = new JTextField();
            JTextField namaBarangField = new JTextField();
            JTextField hargaBarangField = new JTextField();
            JTextField jumlahBeliField = new JTextField();
            JTextField namaKasirField = new JTextField(); // Tambahan untuk nama kasir
    
            panel.add(new JLabel("No Faktur:"));
            panel.add(noFakturField);
            panel.add(new JLabel("Kode Barang:"));
            panel.add(kodeBarangField);
            panel.add(new JLabel("Nama Barang:"));
            panel.add(namaBarangField);
            panel.add(new JLabel("Harga Barang:"));
            panel.add(hargaBarangField);
            panel.add(new JLabel("Jumlah Beli:"));
            panel.add(jumlahBeliField);
            panel.add(new JLabel("Nama Kasir:"));
            panel.add(namaKasirField);
    
            int result = JOptionPane.showConfirmDialog(null, panel, "Input Data Transaksi",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
            if (result == JOptionPane.OK_OPTION) {
                String noFaktur = noFakturField.getText().trim();
                String kodeBarang = kodeBarangField.getText().trim();
                String namaBarang = namaBarangField.getText().trim();
                double hargaBarang = Double.parseDouble(hargaBarangField.getText().trim());
                int jumlahBeli = Integer.parseInt(jumlahBeliField.getText().trim());
                String namaKasir = namaKasirField.getText().trim(); // Ambil nama kasir
                double total = hargaBarang * jumlahBeli;
    
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String formattedDate = now.format(formatter);
    
                // Pesan struk transaksi
                JOptionPane.showMessageDialog(null,
                        "Selamat Datang di Supermarket DivaRy\n" +
                        "Tanggal dan Waktu : " + formattedDate + "\n" +
                        "+----------------------------------------------------+\n" +
                        "No Faktur      : " + noFaktur + "\n" +
                        "Kode Barang    : " + kodeBarang + "\n" +
                        "Nama Barang    : " + namaBarang + "\n" +
                        "Harga Barang   : Rp " + String.format("%.2f", hargaBarang) + "\n" +
                        "Jumlah Beli    : " + jumlahBeli + "\n" +
                        "TOTAL          : Rp " + String.format("%.2f", total) + "\n" +
                        "+----------------------------------------------------+\n" +
                        "Kasir          : " + namaKasir + "\n" +
                        "+----------------------------------------------------+",
                        "Faktur Transaksi", JOptionPane.INFORMATION_MESSAGE);
    
                // Simpan ke database
                String sql = "INSERT INTO transaksi (no_faktur, kode_barang, nama_barang, harga_barang, jumlah_beli, total, namaKasir) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, noFaktur);
                    statement.setString(2, kodeBarang);
                    statement.setString(3, namaBarang);
                    statement.setDouble(4, hargaBarang);
                    statement.setInt(5, jumlahBeli);
                    statement.setDouble(6, total);
                    statement.setString(7, namaKasir); 
                    statement.executeUpdate();
    
                    JOptionPane.showMessageDialog(null, "Data transaksi berhasil disimpan!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //READ data Transaksi
    private static void lihatDataTransaksi(Connection connection) {
        try {
            String sql = "SELECT * FROM transaksi";
            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {
                StringBuilder data = new StringBuilder("Data Transaksi:\n");
                while (resultSet.next()) {
                    data.append("No Faktur: ").append(resultSet.getString("no_faktur")).append("\n");
                    data.append("Kode Barang: ").append(resultSet.getString("kode_barang")).append("\n");
                    data.append("Nama Barang: ").append(resultSet.getString("nama_barang")).append("\n");
                    data.append("Harga Barang: ").append(resultSet.getDouble("harga_barang")).append("\n");
                    data.append("Jumlah Beli: ").append(resultSet.getInt("jumlah_beli")).append("\n");
                    data.append("Total: ").append(resultSet.getDouble("total")).append("\n\n");
                }
                JOptionPane.showMessageDialog(null, data.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //UPDATE data Transaksi
    private static void editTransaksi(Connection connection) {
        try {
            String noFaktur = JOptionPane.showInputDialog("Masukkan No Faktur transaksi yang ingin diubah:");
            String sql = "SELECT * FROM transaksi WHERE no_faktur = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, noFaktur);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
                        JTextField kodeBarangField = new JTextField(resultSet.getString("kode_barang"));
                        JTextField namaBarangField = new JTextField(resultSet.getString("nama_barang"));
                        JTextField hargaBarangField = new JTextField(String.valueOf(resultSet.getDouble("harga_barang")));
                        JTextField jumlahBeliField = new JTextField(String.valueOf(resultSet.getInt("jumlah_beli")));
    
                        panel.add(new JLabel("Kode Barang:"));
                        panel.add(kodeBarangField);
                        panel.add(new JLabel("Nama Barang:"));
                        panel.add(namaBarangField);
                        panel.add(new JLabel("Harga Barang:"));
                        panel.add(hargaBarangField);
                        panel.add(new JLabel("Jumlah Beli:"));
                        panel.add(jumlahBeliField);
    
                        int result = JOptionPane.showConfirmDialog(null, panel, "Edit Transaksi",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    
                        if (result == JOptionPane.OK_OPTION) {
                            String kodeBarangBaru = kodeBarangField.getText().trim();
                            String namaBarangBaru = namaBarangField.getText().trim();
                            double hargaBarangBaru = Double.parseDouble(hargaBarangField.getText().trim());
                            int jumlahBeliBaru = Integer.parseInt(jumlahBeliField.getText().trim());
                            double totalBaru = hargaBarangBaru * jumlahBeliBaru;
    
                            String updateSql = "UPDATE transaksi SET kode_barang = ?, nama_barang = ?, harga_barang = ?, jumlah_beli = ?, total = ? WHERE no_faktur = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                                updateStatement.setString(1, kodeBarangBaru);
                                updateStatement.setString(2, namaBarangBaru);
                                updateStatement.setDouble(3, hargaBarangBaru);
                                updateStatement.setInt(4, jumlahBeliBaru);
                                updateStatement.setDouble(5, totalBaru);
                                updateStatement.setString(6, noFaktur);
                                updateStatement.executeUpdate();
    
                                JOptionPane.showMessageDialog(null, "Data transaksi berhasil diubah!");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No Faktur tidak ditemukan!");
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    //DELETE data Transaksi
    private static void hapusTransaksi(Connection connection) {
        try {
            String noFaktur = JOptionPane.showInputDialog("Masukkan No Faktur transaksi yang ingin dihapus:");
            String sql = "DELETE FROM transaksi WHERE no_faktur = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, noFaktur);
                int rowsAffected = statement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Data transaksi berhasil dihapus!");
                } else {
                    JOptionPane.showMessageDialog(null, "No Faktur tidak ditemukan!");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String generateCaptcha() {
        String chars = "1234567890";
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            int randomIndex = (int) (Math.random() * chars.length());
            captcha.append(chars.charAt(randomIndex));
        }
        return captcha.toString();
    }
}
