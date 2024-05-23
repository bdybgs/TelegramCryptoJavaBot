package com.example.telegrambot;

import com.example.telegrambot.coinmarketcap.CoinMarketCapClient;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBot extends TelegramLongPollingBot {

    public TelegramBot(DefaultBotOptions options, String botToken) {
        super(options, botToken);
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var text = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            var response = CoinMarketCapClient.getCoinPrice(text.toUpperCase());
            String message;

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject data = jsonObject.getJSONObject("data");
                JSONObject coinData = data.getJSONObject(text.toUpperCase());
                JSONObject quote = coinData.getJSONObject("quote");
                JSONObject usdQuote = quote.getJSONObject("USD");
                double price = usdQuote.getFloat("price");
                message = "Цена " + text.toUpperCase() + " составляет $" + price;
            } catch (Exception e) {
                message = "Ошибка: не удалось получить цену для " + text.toUpperCase();
            }
            SendMessage sendMessage = new SendMessage(chatId.toString(), message);
            sendApiMethod(sendMessage);

        }
    }

    @Override
    public String getBotUsername() {
        return "Crypto-checker";
    }
}
