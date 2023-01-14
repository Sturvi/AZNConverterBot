import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserSetting {
    private String selectedCurrency = "USD";


    public InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(getKeyboardButtonList());

        return keyboardMarkup;
    }

    private List<List<InlineKeyboardButton>> getKeyboardButtonList() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        keyboard.add(new ArrayList<>());
        keyboard.add(new ArrayList<>());

        InlineKeyboardButton usd = new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDF8 USD");
        usd.setCallbackData("USD");
        InlineKeyboardButton eur = new InlineKeyboardButton("\uD83C\uDDEA\uD83C\uDDFA EUR");
        eur.setCallbackData("EUR");
        InlineKeyboardButton rub = new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFA RUB");
        rub.setCallbackData("RUB");
        InlineKeyboardButton tl = new InlineKeyboardButton("\uD83C\uDDF9\uD83C\uDDF7 TL");
        tl.setCallbackData("TRY");
        InlineKeyboardButton other = new InlineKeyboardButton("\uD83D\uDCB3 Other currencies (manual entry)");
        other.setCallbackData("OTHER");

        switch (selectedCurrency) {
            case ("USD"):
                usd.setText("\uD83C\uDDFA\uD83C\uDDF8 USD  ✅");
                break;
            case ("EUR"):
                eur.setText("\uD83C\uDDEA\uD83C\uDDFA EUR  ✅");
                break;
            case ("RUB"):
                rub.setText("\uD83C\uDDF7\uD83C\uDDFA RUB  ✅");
                break;
            case ("TRY"):
                tl.setText("\uD83C\uDDF9\uD83C\uDDF7 TL  ✅");
                break;
        }

        keyboard.get(0).add(usd);
        keyboard.get(0).add(eur);
        keyboard.get(1).add(rub);
        keyboard.get(1).add(tl);

        return keyboard;
    }

    public String getSelectedCurrency() {
        return selectedCurrency;
    }

    public void setSelectedCurrency(String selectedCurrency) {
        this.selectedCurrency = selectedCurrency;
    }
}
