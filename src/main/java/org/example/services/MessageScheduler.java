package org.example.services;

import org.example.Bot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static org.example.Bot.seleniumWhatsApp;

public class MessageScheduler {
    static final Timer timer = new Timer();

    public static void schedule(Date dateTime, String[] data, Bot bot) {
        String contactName = data[0];
        String message = data[2];

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Task is running");

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                // Always setup the driver and open WhatsApp before using it.
                seleniumWhatsApp.setupDriver();
                seleniumWhatsApp.openWhatsApp();
                try {
                    seleniumWhatsApp.sendMessage(contactName, message, 5);
                    bot.sendText(bot.getUserId(), "Message '" + message + "' has been sent to "
                                                            + contactName + " at " + LocalDateTime.now().format(dtf));
                } catch (RuntimeException e) {
                    bot.sendText(bot.getUserId(), "Message '" + message + "' has not been sent to "
                                                            + contactName + " at " + LocalDateTime.now().format(dtf));
                }

                System.out.println("Task has been completed");
            }
        }, dateTime);
    }
}
