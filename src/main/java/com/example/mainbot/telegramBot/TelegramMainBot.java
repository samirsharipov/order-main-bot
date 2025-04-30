package com.example.mainbot.telegramBot;

import com.example.mainbot.model.Category;
import com.example.mainbot.model.Shop;
import com.example.mainbot.model.template.BotState;
import com.example.mainbot.repository.ShopRepository;
import com.example.mainbot.service.CategoryService;
import com.example.mainbot.service.cashService.RedisService;
import lombok.RequiredArgsConstructor;
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
import java.util.Optional;

import com.example.mainbot.service.ShopService;

@Component
@RequiredArgsConstructor
public class TelegramMainBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    private final ShopService shopService;
    private final CategoryService categoryService;
    private final ShopRepository shopRepository;
    private final RedisService redisService;


    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            String state = redisService.getState(chatId);

            if (messageText.equals("/addshop")) {
                redisService.saveState(chatId, BotState.WAITING_FOR_SHOP_NAME.name());
                redisService.saveTempShop(chatId, new Shop());
                sendMsg(chatId, "📝 Iltimos, do‘kon nomini kiriting:");
            } else if (messageText.equals("/listshops")) {
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
            } else if (messageText.equals("/categories")) {
                Optional<Shop> optionalShop = shopRepository.findByBotUsername(getBotUsername());
                if (optionalShop.isEmpty()) {
                    sendMsg(chatId, "❌ Do‘kon topilmadi.");
                    return;
                }

                List<Category> categories = categoryService.getAllByShopId(optionalShop.get().getId());
                if (categories.isEmpty()) {
                    sendMsg(chatId, "📭 Sizda hali hech qanday kategoriya mavjud emas.");
                } else {
                    StringBuilder sb = new StringBuilder("📂 Kategoriyalar:\n");
                    for (Category category : categories) {
                        sb.append("🟢 ").append(category.getName()).append("\n");
                    }
                    sendMsg(chatId, sb.toString());
                }
            } else if (messageText.startsWith("/deleteshop ")) {
                try {
                    Long id = Long.parseLong(messageText.split(" ")[1]);
                    shopService.deleteShop(id);
                    sendMsg(chatId, "🗑 Do‘kon o‘chirildi (ID: " + id + ")");
                } catch (Exception e) {
                    sendMsg(chatId, "❌ Format: /deleteshop <id>");
                }
            } else if (messageText.startsWith("/updateshop ")) {
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
            } else if (messageText.equals("/help")) {
                sendMsg(chatId,
                        """
                                ⚙️ Boshqaruv komandalar:
                                /addshop <name> <username> <token> - do‘kon qo‘shish
                                /listshops - ro‘yxat
                                /deleteshop <id> - o‘chirish
                                /updateshop <id> <name> <token> - yangilash
                                """
                );
            } else if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
            } else {
                checkState(chatId, state, messageText);
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

    private void checkState(long chatId, String state, String messageText) {
        switch (state) {
            case "WAITING_FOR_SHOP_NAME" -> {
                Shop shop = redisService.getTempShop(chatId);
                shop.setName(messageText);
                redisService.saveTempShop(chatId, shop);
                redisService.saveState(chatId, BotState.WAITING_FOR_SHOP_USERNAME.name());
                sendMsg(chatId, "📛 Bot username’ni kiriting (misol: @myshopbot):");
            }
            case "WAITING_FOR_SHOP_USERNAME" -> {
                Shop shop = redisService.getTempShop(chatId);
                shop.setBotUsername(messageText);
                redisService.saveTempShop(chatId, shop);
                redisService.saveState(chatId, BotState.WAITING_FOR_SHOP_TOKEN.name());
                sendMsg(chatId, "🔑 Bot token’ni kiriting:");
            }
            case "WAITING_FOR_SHOP_TOKEN" -> {
                Shop shop = redisService.getTempShop(chatId);
                shop.setBotToken(messageText);
                shop.setActive(true);
                shopRepository.save(shop);

                redisService.saveState(chatId, BotState.NONE.name());
                redisService.deleteTempShop(chatId);

                sendMsg(chatId, "✅ Do‘kon muvaffaqiyatli qo‘shildi:\n🛍 Nomi: "
                        + shop.getName() + "\n🤖 Username: " + shop.getBotUsername());
            }
        }
    }
}
