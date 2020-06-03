package com.donbosco.jobservice.guest.search.models;

public class Nokri_CandidateSearchModel {

    private String title;
    private String location;
    private String type;
    private String experience;
    private String level;
    private String skill;
    private boolean isSearchOnly;

    public boolean isSearchOnly() {
        return isSearchOnly;
    }

    public void setSearchOnly(boolean searchOnly) {
        isSearchOnly = searchOnly;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    @Override
    public String toString() {
        return "Nokri_CandidateSearchModel{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", type='" + type + '\'' +
                ", experience='" + experience + '\'' +
                ", level='" + level + '\'' +
                ", skill='" + skill + '\'' +
                ", isSearchOnly=" + isSearchOnly +
                '}';
    }
}
