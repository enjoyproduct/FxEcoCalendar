package com.fxecocal.free.model;

/**
 * Created by dell17 on menu6/23/2016.
 */
public class FactModel {
    String description;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String time;
    String currency;
    String actual;

    public String getActual_status() {
        return actual_status;
    }

    public void setActual_status(String actual_status) {
        this.actual_status = actual_status;
    }

    String actual_status;
    String forcast;
    String impact;
    long remaining_timestamp;

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }

    long releaseTime;

    public long getRemaining_timestamp() {
        return remaining_timestamp;
    }

    public void setRemaining_timestamp(long remaining_timestamp) {
        this.remaining_timestamp = remaining_timestamp;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getForcast() {
        return forcast;
    }

    public void setForcast(String forcast) {
        this.forcast = forcast;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String previous;
    boolean enabledAlarm;

    public boolean isEnabledAlarm() {
        return enabledAlarm;
    }

    public void setEnabledAlarm(boolean enabledAlarm) {
        this.enabledAlarm = enabledAlarm;
    }
}
