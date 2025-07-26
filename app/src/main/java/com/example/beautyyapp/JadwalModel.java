package com.example.beautyyapp;

public class JadwalModel {
    public String hari, jam_pagi, jam_malam, catatan;

    public JadwalModel() {
        // default constructor required
    }

    public JadwalModel(String hari, String jam_pagi, String jam_malam, String catatan) {
        this.hari = hari;
        this.jam_pagi = jam_pagi;
        this.jam_malam = jam_malam;
        this.catatan = catatan;
    }


    public Object getHari() {
        return null;
    }

    public long getTanggalMulai() {
        return 0;
    }

    public void setHari(String hari) {
    }
}
