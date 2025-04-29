package com.example.mainbot.telegramBot;

import com.example.mainbot.model.Shop;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import com.example.mainbot.service.ShopService;

@Component
public class TelegramMainBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Autowired
    private ShopService shopService;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (messageText.startsWith("/addshop ")) {
                String[] parts = messageText.split(" ", 4);
                if (parts.length < 4) {
                    sendMsg(chatId, "❌ Format: /addshop <name> <botUsername> <token>");
                    return;
                }
                String name = parts[1];
                String username = parts[2];
                String token = parts[3];
                Shop shop = Shop.builder()
                        .name(name)
                        .botUsername(username)
                        .botToken(token)
                        .isActive(true)
                        .nextPaymentDate(LocalDate.now().plusMonths(1))
                        .lastPingAt(LocalDateTime.now())
                        .build();
                shopService.addShop(shop);
                sendMsg(chatId, "✅ Do‘kon qo‘shildi: " + name);
            }

            else if (messageText.equals("/listshops")) {
                List<Shop> shops = shopService.getAllShops();
                if (shops.isEmpty()) {
                    sendMsg(chatId, "📭 Hozircha hech qanday do‘kon yo‘q.");
                } else {
                    StringBuilder sb = new StringBuilder("📦 Do‘konlar ro‘yxati:\n\n");
                    for (Shop shop : shops) {
                        sb.append("ID: ").append(shop.getId())
                                .append("\nName: ").append(shop.getName())
                                .append("\nUsername: ").append(shop.getBotUsername())
                                .append("\nActive: ").append(shop.isActive() ? "✅" : "❌")
                                .append("\nNext Payment: ").append(shop.getNextPaymentDate())
                                .append("\n\n");
                    }
                    sendMsg(chatId, sb.toString());
                }
            }

            else if (messageText.startsWith("/deleteshop ")) {
                try {
                    Long id = Long.parseLong(messageText.split(" ")[1]);
                    shopService.deleteShop(id);
                    sendMsg(chatId, "🗑 Do‘kon o‘chirildi (ID: " + id + ")");
                } catch (Exception e) {
                    sendMsg(chatId, "❌ Format: /deleteshop <id>");
                }
            }

            else if (messageText.startsWith("/updateshop ")) {
                String[] parts = messageText.split(" ", 4);
                if (parts.length < 4) {
                    sendMsg(chatId, "❌ Format: /updateshop <id> <newName> <newToken>");
                    return;
                }
                try {
                    Long id = Long.parseLong(parts[1]);
                    String name = parts[2];
                    String token = parts[3];
                    shopService.updateShop(id, name, token);
                    sendMsg(chatId, "✅ Do‘kon yangilandi (ID: " + id + ")");
                } catch (Exception e) {
                    sendMsg(chatId, "⚠️ ID noto‘g‘ri.");
                }
            }

            else if (messageText.equals("/help")) {
                sendMsg(chatId,
                        """
                        ⚙️ Boshqaruv komandalar:
                        /addshop <name> <username> <token> - do‘kon qo‘shish
                        /listshops - ro‘yxat
                        /deleteshop <id> - o‘chirish
                        /updateshop <id> <name> <token> - yangilash
                        """
                );
            }

            else if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
            }
        }
    }

    private void sendMsg(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Salomlashuv xabarini yuborish va tugmalarni ko'rsatish
    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("🤖 Xush kelibsiz!\nQuyidagi tugmalardan birini tanlang:");
        message.setReplyMarkup(createMainMenu());
        try {
            execute(message);  // Yuborish
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Mahsulotlar ro'yxatini ko'rsatish
    private void showProducts(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Here are our products:\n1. Product 1\n2. Product 2\n3. Product 3");
        try {
            execute(message);  // Yuborish
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Buyurtma qilish jarayonini boshqarish
    private void handleOrder(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Please select the product you want to order.");
        try {
            execute(message);  // Yuborish
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    // Tugmalarni yaratish
    private ReplyKeyboardMarkup createMainMenu() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/addshop"));
        row1.add(new KeyboardButton("/listshops"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/deleteshop"));
        row2.add(new KeyboardButton("/updateshop"));

        KeyboardRow row3 = new KeyboardRow();
        row3.add(new KeyboardButton("/help"));

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(false);

        return keyboardMarkup;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
