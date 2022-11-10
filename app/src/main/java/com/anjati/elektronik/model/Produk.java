package com.anjati.elektronik.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "keranjang")
public class Produk implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "idTb")
    public int idTb;

    public int id;
    public String kodebrg;
    public String barcode;
    public String kodeklmpk;
    public String kodedept;
    public String name;
    public String slug;
    public String price;
    public String stock;
    public String weight;
    public String description;
    public String category;
    public String image;
    public String hgros1;
    public String hgros2;
    public String quantity1;
    public String quantity2;
    public String created_at;
    public String updated_at;
    public String is_promo;
    public String discont;
    public int potongan_harga;
    public int d_price;
    public int d_hgros1;
    public int d_hgros2;
    public String expired;

    public int discount = 0;
    public int jumlah = 1 ;
    public boolean selected = true;
//    public boolean isFavorite = false;
}
