# LENTERA

Aplikasi pembelajaran budaya Indonesia (Jawa, Sumatra, Kalimantan) — Project Akhir PAPB Kelompok 8.

## Pembagian folder

Semua kode ada di `app/src/main/java/com/kelompok8/lentera/`. Kerjakan hanya di folder milikmu supaya tidak bentrok.

| Folder | Isi | PJ |
|---|---|---|
| `auth/`, `settings/` | Login, Register, sesi pengguna, Pengaturan | Zulfan |
| `home/`, `map/`, `leaderboard/` | Beranda, pilih pulau & chapter, leaderboard | Hilal |
| `learn/` | Glosarium budaya | Noya |
| `game/` | Pilihan ganda, blok kata, susun huruf, validasi jawaban | Daud |
| `score/` | Hitung skor, nilai terbaik, progres, medali | Panji |
| `core/` | Model data, interface repository, navigasi, komponen bersama | Zulfan (ubah lewat diskusi) |

- `core/model/Models.kt` berisi bentuk data bersama (User, Question, ChapterResult, BestScore, GlossaryItem, dll).
- `core/data/Repositories.kt` berisi interface yang harus diimplementasikan tiap PJ.
- `core/navigation/` berisi semua rute halaman. Kalau butuh halaman baru, kabari Zulfan.
- Format ID chapter: `jawa_1` … `jawa_5`, `sumatra_1` …, `kalimantan_1` … (huruf kecil).

## Alur kerja Git

1. Branch utama: `main` (versi stabil) dan `develop` (tempat penggabungan). **Jangan push langsung ke `main`.**
2. Sebelum mulai kerja: pindah ke `develop`, lalu **Update Project** (Ctrl+T).
3. Buat branch sendiri dari `develop`: `feature/login`, `feature/peta`, `feature/glosarium`, `feature/game`, `feature/skor`, dst.
4. Commit kecil-kecil dengan pesan jelas, contoh: `tambah validasi soal pilihan ganda`.
5. Push (Ctrl+Shift+K), lalu buka Pull Request ke `develop` di GitHub. Zulfan yang review dan merge.
6. Setelah PR di-merge, update `develop` lagi sebelum lanjut fitur berikutnya.

## Timeline

| Tanggal | Target |
|---|---|
| 16–17 Nov | Kerangka project, Firebase, semua anggota clone repo |
| 18–22 Nov | Fitur masing-masing dengan data dummy + isi soal & glosarium |
| 23–25 Nov | Integrasi dengan data asli |
| 26–27 Nov | Testing, bug fix, screenshot |
| 28 Nov | Video demo 1080p, laporan BAB III–V |
| 29 Nov | Pengumpulan |
