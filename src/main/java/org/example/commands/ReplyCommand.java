package org.example.commands;

import org.example.Bot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ReplyCommand {
    void update(Update update, Bot bot);
}
