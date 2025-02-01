package dhbw.mos.bot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Main {
    public static void main(String[] args) {
        String token = System.getenv("BOT_TOKEN");
        JDA jda = JDABuilder.createLight(token).build();
    }
}