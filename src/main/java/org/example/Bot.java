package org.example;

import org.example.commands.ReplyCommand;
import org.example.commands.SendMessageCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private Long userId;

    private ReplyCommand commandSubscriber;

    public static final SeleniumWhatsApp seleniumWhatsApp = new SeleniumWhatsApp();

    @Override
    public void onUpdateReceived(Update update) {
        userId = update.getMessage().getFrom().getId();
        String message = update.getMessage().getText();

        System.out.println(update.getMessage().getFrom().getFirstName() + " wrote " + message);

        if (commandSubscriber != null) {
            System.out.println("Command subscriber is not null");
            commandSubscriber.update(update, this);
            System.out.println("Command subscriber has been successfully updated");
        }

        if (message.equals("/send")) {
            SendMessageCommand.subscribe(userId, update.getUpdateId(), this);
        }
    }

    public void sendText(Long chatId, String message) {
        SendMessage sm = SendMessage.builder()
                                    .chatId(chatId.toString())
                                    .text(message).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "whatsapp_auto_bot";
    }

    @Override
    public String getBotToken() {
        return "6017214444:AAFsJABKvc2jCQoHzplAf5NMvpHzSL2Mn-8";
    }

    public Long getUserId() {
        return userId;
    }

    public void setCommandSubscriber(ReplyCommand commandSubscriber) {
        this.commandSubscriber = commandSubscriber;
    }
}
