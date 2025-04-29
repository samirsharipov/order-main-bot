package com.example.mainbot.telegramBot;

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

@Component
public class TelegramMainBot extends TelegramLongPollingBot {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // /start komandasi
            if (messageText.equals("/start")) {
                sendWelcomeMessage(chatId);
            }
            // Buyurtma qilish
            else if (messageText.equals("/order")) {
                handleOrder(chatId);
            }
            // Mahsulotlar ro'yxati
            else if (messageText.equals("/products")) {
                showProducts(chatId);
            }
        }
    }

    // Salomlashuv xabarini yuborish va tugmalarni ko'rsatish
    private void sendWelcomeMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("Welcome to MainBot. How can I assist you today?");
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

        // Row 1
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("/order"));
        row1.add(new KeyboardButton("/products"));

        // Row 2
        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("Contact Us"));

        keyboard.add(row1);
        keyboard.add(row2);

        keyboardMarkup.setKeyboard(keyboard);
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
