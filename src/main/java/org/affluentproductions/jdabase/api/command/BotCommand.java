package org.affluentproductions.jdabase.api.command;

import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.affluentproductions.jdabase.JDABase;
import org.affluentproductions.jdabase.api.AffluentAdapter;
import org.affluentproductions.jdabase.enums.Load;

import java.util.Arrays;
import java.util.function.Consumer;

public abstract class BotCommand extends AffluentAdapter {

    protected String name = "";
    protected String[] aliases = new String[]{};
    protected double cooldown = 0.75;
    protected Load load = Load.PRELOAD;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public Load getLoad() {
        return load;
    }

    public double getCooldown() {
        return cooldown;
    }

    public boolean isAuthorOwner(String userId) {
        return userId.equals("335051227324743682") || userId.equals("429307019229397002");
    }

    public abstract void onCommand(BotCommand.CommandEvent event);

    public static class CommandEvent {

        private final JDABase jdaBase;
        private final User author;
        private final BotCommand command;
        private final ChannelType channelType;
        private final MessageChannel channel;
        private final MessageReceivedEvent messageReceivedEvent;

        public CommandEvent(JDABase jdaBase, BotCommand command, User author,
                            MessageReceivedEvent messageReceivedEvent) {
            this.jdaBase = jdaBase;
            this.author = author;
            this.command = command;
            this.messageReceivedEvent = messageReceivedEvent;
            this.channel = messageReceivedEvent.getChannel();
            this.channelType = messageReceivedEvent.getChannelType();
        }

        public JDABase getJDABase() {
            return jdaBase;
        }

        public void reply(MessageEmbed messageEmbed) {
            reply(messageEmbed, null, null);
        }

        public void reply(MessageEmbed messageEmbed, Consumer<Message> success, Consumer<Throwable> failure) {
            channel.sendMessage(messageEmbed).queue(success, failure);
        }

        public void reply(Message message) {
            reply(message, null, null);
        }

        public void reply(Message message, Consumer<Message> success, Consumer<Throwable> failure) {
            channel.sendMessage(message).queue(success, failure);
        }

        public void reply(String message) {
            reply(message, null, null);
        }

        public void reply(String message, Consumer<Message> success, Consumer<Throwable> failure) {
            String[] messages = new String[]{message};
            if (message.length() > 2047) {
                int partCount = (message.length() / 2047) + 1;
                if (partCount > 2) message = message.substring(0, 4094);
                String cut = message.substring(0, 2047);
                String first = cut.contains("\n") ? cut.substring(0, cut.lastIndexOf("\n")) : cut.substring(0, 2047);
                String second = message.substring(first.length());
                messages = new String[]{first, second};
            }
            reply(messages, success, failure);
        }

        public void reply(String[] messages) {
            for (String message : messages) channel.sendMessage(new MessageBuilder(message).build()).queue();
        }

        public void reply(String[] messages, Consumer<Message> success, Consumer<Throwable> failure) {
            for (String message : messages)
                channel.sendMessage(new MessageBuilder(message).build()).queue(success, failure);
        }

        public String[] getArgs() {
            String[] splitted = messageReceivedEvent.getMessage().getContentRaw().split("\\s+");
            return Arrays.copyOfRange(splitted, 1, splitted.length);
        }

        public BotCommand getCommand() {
            return command;
        }

        public User getAuthor() {
            return author;
        }

        public MessageReceivedEvent getMessageReceivedEvent() {
            return messageReceivedEvent;
        }

        public MessageChannel getChannel() {
            return channel;
        }

        public ChannelType getChannelType() {
            return channelType;
        }
    }
}