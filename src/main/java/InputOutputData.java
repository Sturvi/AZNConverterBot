import org.checkerframework.common.aliasing.qual.MaybeAliased;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InputOutputData extends TelegramLongPollingBot {
    private final String API = "5841837131:AAGcdwoHbp0bgqZuC1x8wZJs7PELKD43pTY";
    private final String BOT_NAME = "AZN_Converter_Bot";

    Map<String, UserSetting> userSettingMap;

    public InputOutputData(Map<String, UserSetting> userSettingMap) {
        this.userSettingMap = userSettingMap;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
        if (!userSettingMap.containsKey(message.getChatId().toString())){
            userSettingMap.put(message.getChatId().toString(), new UserSetting());
        }
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else {
            sendMessage(message, "test");
        }

    }

    private void sendMessage(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(userSettingMap.get(message.getChatId().toString()).getKeyboard());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleCallback(CallbackQuery callbackQuery) {
        Message message = callbackQuery.getMessage();
        String newCurrency = callbackQuery.getData();
        userSettingMap.get(message.getChatId().toString()).setSelectedCurrency(newCurrency);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(message.getChatId().toString());
        editMessageReplyMarkup.setMessageId(message.getMessageId());
        editMessageReplyMarkup.setReplyMarkup(userSettingMap.get(message.getChatId().toString()).getKeyboard());
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return API;
    }
}
