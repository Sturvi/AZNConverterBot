import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

public class InputOutputData extends TelegramLongPollingBot {

    Map<String, UserSetting> userSettingMap;
    Document doc;
    LocalDateTime nextUpdateTime;

    public InputOutputData(Map<String, UserSetting> userSettingMap) {
        this.userSettingMap = userSettingMap;
    }


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage() == null ? update.getCallbackQuery().getMessage() : update.getMessage();
        if (!userSettingMap.containsKey(message.getChatId().toString())) {
            userSettingMap.put(message.getChatId().toString(), new UserSetting());
        }
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else {
            handleTextMessage(message);
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

    private void handleTextMessage(Message message) {
        String text = message.getText();
        switch (text) {
            case ("/start"):
                sendMessage(message, "Select the currency to convert");
                break;
            default:
                sendCurrencyConversion(message);
        }
    }

    private void sendCurrencyConversion(Message message) {
        double coefficient = getCoefficient(message);
        double meaning;
        DecimalFormat df = new DecimalFormat("#.##");

        try {
            meaning = Double.valueOf(message.getText());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Can`t parse to Double");
        }

        String result = meaning + " AZN = " +
                df.format(meaning / coefficient) + " " +
                userSettingMap.get(message.getChatId().toString()).getSelectedCurrency() +
                "\n" +
                meaning + " " + userSettingMap.get(message.getChatId().toString()).getSelectedCurrency() +
                " = " + df.format(meaning * coefficient) + " AZN";

        sendMessage(message, result);
    }

    private double getCoefficient(Message message) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedDate = date.format(formatter);
        ZoneId bakuZone = ZoneId.of("Asia/Baku");

        try {

            LocalDateTime currentTime = LocalDateTime.now().atZone(bakuZone).toLocalDateTime();
            if (nextUpdateTime == null || currentTime.isAfter(nextUpdateTime)) {
                // Create a URL for the desired page
                URL url = new URL("https://www.cbar.az/currencies/" + formattedDate + ".xml");

                // Read all the text returned by the server
                InputStream is = url.openStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(is);
                doc.getDocumentElement().normalize();

                nextUpdateTime = currentTime.plusDays(1).withHour(11).withMinute(0).withSecond(0);
            }


            NodeList nList = doc.getElementsByTagName("Valute");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    String code = nNode.getAttributes().getNamedItem("Code").getNodeValue();
                    if (code.equals(userSettingMap.get(message.getChatId().toString()).getSelectedCurrency())) {
                        NodeList children = nNode.getChildNodes();
                        for (int i = 0; i < children.getLength(); i++) {
                            Node child = children.item(i);
                            if (child.getNodeName().equals("Value")) {
                                String rate = child.getTextContent();
                                return Double.parseDouble(rate);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Currency not found", e);
        }
        return 0;
    }

    @Override
    public String getBotUsername() {
        return "AZN_Converter_Bot";
    }

    @Override
    public String getBotToken() {
        return "5841837131:AAGcdwoHbp0bgqZuC1x8wZJs7PELKD43pTY";
    }
}
