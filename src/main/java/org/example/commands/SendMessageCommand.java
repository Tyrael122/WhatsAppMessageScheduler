package org.example.commands;

import org.example.Bot;
import org.example.services.MessageScheduler;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.Date;

import static org.example.Bot.seleniumWhatsApp;

public class SendMessageCommand implements ReplyCommand {
    private static final String[] messagesToSend = {"Enter the name of the contact: ", "Enter the date and time (dd/MM/yyyy HH:mm): ", "Enter the message: "};
    private final int updateOffset;
    private final String[] messageData = new String[3];

    public SendMessageCommand(int updateId) {
        updateOffset = updateId + 1;
    }

    public static void subscribe(Long userId, int updateId, Bot bot) {
        bot.setCommandSubscriber(new SendMessageCommand(updateId));
        bot.sendText(userId, messagesToSend[0]);
        System.out.println("Command subscriber has been subscribed");
    }

    public static void unsubscribe(Bot bot) {
        bot.setCommandSubscriber(null);
        System.out.println("Command subscriber has been unsubscribed");
    }

    @Override
    public void update(Update update, Bot bot) {
        int updateId = update.getUpdateId();

        if (updateId - updateOffset == 1) { // It's the date and time message.
            String message = update.getMessage().getText();
            try {
                parseDateTime(message);
                messageData[updateId - updateOffset] = message;
            } catch (Exception e) {
                bot.sendText(update.getMessage().getFrom().getId(), "The date and time format is incorrect. Please, try again.");
                return;
            }
        } else {
            messageData[updateId - updateOffset] = update.getMessage().getText();
        }

        if (updateId - updateOffset == 2) { // The last message to receive.
            unsubscribe(bot);

            seleniumWhatsApp.setupDriver();
            seleniumWhatsApp.openWhatsApp();

            bot.sendText(update.getMessage().getFrom().getId(), "Message '" + messageData[2] + "' will be sent to " + messageData[0] + " at " + messageData[1]);
            Date dateTime = parseDateTime(messageData[1]);
            MessageScheduler.schedule(dateTime, messageData, bot);

            return;
        }

        bot.sendText(update.getMessage().getFrom().getId(), messagesToSend[updateId - updateOffset + 1]);

        System.out.println("The message has been received: " + messageData[updateId - updateOffset]);
    }

    private Date parseDateTime(String message) {
        String[] dateTime = message.split(" ");
        String[] date = dateTime[0].split("/");
        String[] time = dateTime[1].split(":");
        return Date.from(LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]),
                                Integer.parseInt(time[0]), Integer.parseInt(time[1]))
                                .atZone(java.time.ZoneId.systemDefault()).toInstant());
    }
}
