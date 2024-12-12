public class Transaksi extends Barang {
    private String noFaktur;
    private int jumlahBeli;

    // Konstruktor
    public Transaksi(String noFaktur, String kodeBarang, String namaBarang, double hargaBarang, int jumlahBeli) {
        super(kodeBarang, namaBarang, hargaBarang); // Panggil konstruktor dari superclass (Inheritance)
        this.noFaktur = noFaktur;
        this.jumlahBeli = jumlahBeli;
    }

    // Getter dan Setter
    public String getNoFaktur() {
        return noFaktur;
    }

    public void setNoFaktur(String noFaktur) {
        this.noFaktur = noFaktur;
    }

    public int getJumlahBeli() {
        return jumlahBeli;
    }

    public void setJumlahBeli(int jumlahBeli) {
        this.jumlahBeli = jumlahBeli;
    }

    // Method total
    public double calculateTotal() {
        return getHargaBarang() * jumlahBeli; // Mengakses hargaBarang dari superclass
    }
}
