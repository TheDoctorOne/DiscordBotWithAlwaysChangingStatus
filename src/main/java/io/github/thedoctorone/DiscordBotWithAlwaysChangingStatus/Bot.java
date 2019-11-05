/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.thedoctorone.DiscordBotWithAlwaysChangingStatus;

import java.util.Calendar;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author DoctorOne
 */
public class Bot extends ListenerAdapter implements  Runnable{
    private JDA bot;
    private String[] statusList;
    private int secBetween;
    Bot (String TOKEN, int secBetween) throws LoginException {
        bot = new JDABuilder(TOKEN).addEventListeners(this).build();
        this.secBetween = secBetween * 1000;
    }
    
    public JDA getBot(){
        return bot;
    }
    
    public void setActivity(int i, String status){
        switch(i) {
            case 1: //Playing
                bot.getPresence().setActivity(Activity.playing(status));
                break;
            case 2: //Watching
                bot.getPresence().setActivity(Activity.watching(status));
                break;
            case 3: //Listening
                bot.getPresence().setActivity(Activity.listening(status));
                break;
            default:
                break;
        }
    }
    
    public void setSure(int sure){
        secBetween = sure * 1000;
    }
    
    public void setStatusList(String[] statusList) {
        this.statusList = statusList;
        for (String s : statusList) {
            System.out.println(s);
        }
    }
    private long timeMilisCurrent;
    private long timeMilisCatch;
    private boolean ready = false;
    @Override
    public void onReady(ReadyEvent event) {
        ready = true;
        timeMilisCurrent = Calendar.getInstance().getTimeInMillis();
        timeMilisCatch = timeMilisCurrent - 6000;
        new Thread(this).start();
    }

    @Override
    public void run() {
        int lenOfList = statusList.length;
        int next = 0;
        while(true) {
            try {
                timeMilisCurrent = Calendar.getInstance().getTimeInMillis();
                if(ready && timeMilisCatch + secBetween < timeMilisCurrent) {
                    timeMilisCatch = timeMilisCurrent;
                    if(lenOfList != statusList.length || next == lenOfList) {
                        next = 0;
                        lenOfList = statusList.length;
                    }
                    int i = Integer.parseInt(statusList[next].trim().charAt(0) + "");
                    String status = statusList[next].trim().replaceFirst(i + "", "").trim();
                    System.out.println(status);
                    setActivity(i, status);
                    next++;
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    
}
