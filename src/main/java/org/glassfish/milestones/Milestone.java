package org.glassfish.milestones;

import java.util.Calendar;
import java.util.Date;

import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VAlarm;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Action;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Uid;

public class Milestone {
    private String milestone;
    private Date start;
    private Date end;
    private String description;
    private String status;

    public Milestone(String milestone, Date start, Date end, String description, String status) {
        this.description = description;
        setEnd(end);
        this.milestone = milestone;
        setStart(start);
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getEnd() {
        return end;
    }

    public final void setEnd(Date end) {
        this.end = end == null ? new Date(Integer.MAX_VALUE) : end;
    }

    public String getMilestone() {
        return milestone;
    }

    public void setMilestone(String milestone) {
        this.milestone = milestone;
    }

    public Date getStart() {
        return start;
    }

    public final void setStart(Date date) {
        start = date == null ? new Date(0) : date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VEvent toEvent() {
        Calendar weekPrior = Calendar.getInstance();
        weekPrior.setTime(getEnd());
        weekPrior.add(Calendar.WEEK_OF_YEAR, -1);
        VEvent event = new VEvent(new net.fortuna.ical4j.model.Date(getEnd()), getDescription());
        final String alarmDesc = "GlassFish 3.2 " + getMilestone();
        event.getProperties().add(new Uid(alarmDesc));
        final VAlarm alarm = new VAlarm(new DateTime(weekPrior.getTime()));
        alarm.getProperties().add(Action.DISPLAY);
        alarm.getProperties().add(new Description(alarmDesc + " is due in one week"));
        event.getAlarms().add(alarm);
        return event;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Milestone");
        sb.append("{milestone='").append(milestone).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append(", description='").append(description).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append('}');
        return sb.toString();
    }
}

