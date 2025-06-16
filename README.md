# TMD Yattaqi Ahmad Faza

## Janji
Saya Yattaqi Ahmad Faza mengerjakan evaluasi Tugas Masa Depan dalam mata kuliah Desain dan Pemrograman Berorientasi Objek untuk keberkahanNya maka saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
## Deskripsi
Game ini membawakan tema collecect the ball menggunakan lasso dan game ini bertemakan pencuri yang sedang berusaha mencuri dan dimasukkan ke dalam satu kotak.

## Struktur Folder
```
TMD/
├─ src/                # Source code Java
│   ├─ main/
│   ├─ audio/
│   ├─ database/
│   ├─ model/
│   ├─ view/
│   ├─ assets/         # File gambar, audio, dll
│   └─ viewmodel/
├─ lib/                # Library eksternal (sqlite-jdbc-xxx.jar)
├─ out/                # Hasil compile (akan dibuat setelah compile, boleh dihapus)
```

## Prasyarat
- Java JDK 8 atau lebih baru
- File `sqlite-jdbc-<versi>.jar` di folder `lib` (misal: `lib/sqlite-jdbc-3.50.1.0.jar`)

## Cara Menjalankan (Windows)

1. **Compile semua file Java:**
   ```powershell
   javac -d out -cp "lib/sqlite-jdbc-3.50.1.0.jar" src\main\*.java src\audio\*.java src\database\*.java src\model\*.java src\view\*.java src\viewmodel\*.java
   ```

2. **Copy folder assets ke dalam folder out:**
   ```powershell
   xcopy /E /I src\assets out\assets
   ```

3. **Jalankan game:**
   ```powershell
   java -cp "out;lib\sqlite-jdbc-3.50.1.0.jar" main.Main
   ```

## Catatan Penting
- Pastikan semua file aset (gambar, audio, dll) ada di folder `assets`.
- Jika ada error "file tidak ditemukan", pastikan folder `assets` sudah dicopy ke dalam `out`.
- Untuk compile dan run di Linux/Mac, ganti `;` dengan `:` pada classpath.
- **Folder `out` boleh dihapus**. Jika dihapus, lakukan compile ulang seperti langkah di atas sebelum menjalankan game.

## Menjalankan di IntelliJ IDEA
- Pastikan `lib/sqlite-jdbc-xxx.jar` sudah di-add ke Project Structure > Libraries.
- Set working directory ke root project jika perlu.
- Jalankan `main.Main` dari IDE.
