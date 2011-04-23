package org.glassfish.milestones;

import java.util.Date;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Uid;

public class Build {
    private String number;
    private Date date;
    private String comment;
    private String bugsFixed;
    private String svnRev;
    private String branch;

    public Build(final String branch, String number, Date date, String comment, String svnRev) {
        this.number = number;
        this.date = date;
        this.comment = comment;
        this.svnRev = svnRev;
        this.branch = branch;
    }

    public Build(final String branch, String number, Date date, String comment, String bugsFixed, String svnRev) {
        this.number = number;
        this.date = date;
        this.comment = comment;
        this.bugsFixed = bugsFixed;
        this.svnRev = svnRev;
        this.branch = branch;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getBugsFixed() {
        return bugsFixed;
    }

    public void setBugsFixed(String bugsFixed) {
        this.bugsFixed = bugsFixed;
    }

    public VEvent toEvent() {
        VEvent event = new VEvent(new net.fortuna.ical4j.model.Date(getDate()),
            String.format("GlassFish %s Build %s", getBranch(), getNumber()));
        event.getProperties().add(new Uid("GlassFish " + branch + " Build " + getNumber()));
        event.getProperties().add(new Description(String.format(
            "* Download: %s\n"
                + "* Comments: %s\n"
                + "* Subversion revision: %s\n"
                + "* Subversion tag: %s\n"
                + "* Bugs fixed: %s",
            String.format("http://dlc.sun.com.edgesuite.net/glassfish/%s/promoted/glassfish-%s-b%s.zip",
                getBranch(), getBranch(), getNumber()),
            getComment(),
            getSvnRev(),
            String.format("https://svn.java.net/svn/glassfish~svn/tags/%s-b%s", getBranch(), getNumber()),
            getBugsFixed() == null ? "" : getBugsFixed())));
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
