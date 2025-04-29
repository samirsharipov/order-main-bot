
# Main Telegram Bot

Bu Telegram bot orqali siz do‘kon botlarini markazlashtirib boshqarishingiz mumkin.

## API

- `POST /api/register-bot` — Yangi bot ro‘yxatdan o‘tadi
- `POST /api/ping?botUsername=x` — Do‘kon botlari ping yuboradi
- `GET /api/shops` — Barcha do‘konlarni ro‘yxati

## Ishga tushirish

- PostgreSQL database yaratish: `mainbotdb`
- `application.yml` faylini sozlash
- `mvn spring-boot:run`
