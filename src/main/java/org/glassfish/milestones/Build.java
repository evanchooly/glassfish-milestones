package org.glassfish.milestones;

import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Uid;

public class Build {
    private String number;
    private Date date;
    private String comment;
    private String svnRev;

    public Build(String number, Date date, String comment, String svnRev) {
        this.number = number;
        this.date = date;
        this.comment = comment;
        this.svnRev = svnRev;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSvnRev() {
        return svnRev;
    }

    public void setSvnRev(String svnRev) {
        this.svnRev = svnRev;
    }

    public VEvent toEvent() {
        Calendar reminder = Calendar.getInstance();
        reminder.setTime(getDate());
        reminder.add(Calendar.DAY_OF_MONTH, -2);
        String desc = String.format("Build %s %s", getNumber(), getComment());
        if(getSvnRev() != null) {
            desc += " rev: " + getSvnRev();
        }
        VEvent event = new VEvent(new net.fortuna.ical4j.model.Date(getDate()), desc);
        final String alarmDesc = "GlassFish 3.2 Build " + getNumber();
        event.getProperties().add(new Uid(alarmDesc));
        final VAlarm alarm = new VAlarm(new DateTime(reminder.getTime()));
        alarm.getProperties().add(Action.DISPLAY);
        alarm.getProperties().add(new Description(alarmDesc + " is in 2 days"));
//        event.getAlarms().add(alarm);
        return event;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Build");
        sb.append("{number='").append(number).append('\'');
        sb.append(", start=").append(date);
        sb.append(", comment='").append(comment).append('\'');
        sb.append(", svnRev='").append(svnRev).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
