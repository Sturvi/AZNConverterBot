import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = null;
        Map<String, UserSetting> userSettingMap =new  HashMap<>();
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new InputOutputData(userSettingMap));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}