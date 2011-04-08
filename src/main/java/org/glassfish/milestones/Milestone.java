package org.glassfish.milestones;

import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;
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

    public void setEnd(Date end) {
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

    public void setStart(Date date) {
        start = date == null ? new Date(0) : date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public VEvent toEvent() {
        VEvent event = new VEvent(new net.fortuna.ical4j.model.Date(getEnd()), getDescription());
//        event.getProperties().getProperty(Property.DTSTART).getParameters().add(Value.DATE);
//        event.getProperties().getProperty(Property.DTEND).getParameters().add(Value.DATE);
        event.getProperties().add(new Uid(getMilestone()));
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

