package com.insane.simpleachievements;

import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

/**
 * Created by Michael on 28/07/2014.
 */
public class AchievementHandler {

    private NBTTagCompound achievements;
    private ArrayList<String> listOfAchievements = new ArrayList<String>();

    public AchievementHandler() {
        achievements = new NBTTagCompound();

        listOfAchievements = SimpleAchievements.readInAchievements();
        for (int i=0; i<listOfAchievements.size(); i++) {
            achievements.setBoolean(listOfAchievements.get(i),false);
        }
    }


    public boolean addAchievement(String text) {
        if (achievements.hasKey(text)) {
            return false;
        } else {
            achievements.setBoolean(text,false);
            return true;
        }
    }

    public boolean removeAchievement(String text) {
        if (achievements.hasKey(text)) {
            achievements.removeTag(text);
            return true;
        } else {
            return false;
        }
    }

    public boolean toggleAchievement(String text) {
        if (achievements.hasKey(text)) {
            achievements.setBoolean(text,!achievements.getBoolean(text));
            return true;
        } else {
            return false;
        }
    }

    public NBTTagCompound getAchievements() {
        return this.achievements;
    }

    public ArrayList<String> getListOfAchievements() {
        return listOfAchievements;
    }

}
