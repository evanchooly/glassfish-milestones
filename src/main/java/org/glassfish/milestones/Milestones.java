package org.glassfish.milestones;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Version;

/**
 * Hello world!
 */
@SuppressWarnings({"StringContatenationInLoop", "unchecked"})
public class Milestones {
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private Calendar calendar;

    public Milestones() {
        calendar = new Calendar();
        calendar.getProperties().add(new ProdId("GlassFish 3.2 CalGen"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
    }

    public void generate(OutputStream fout) throws IOException, ValidationException {
        new CalendarOutputter().output(calendar, fout);
    }

    private String trim(String text) {
        return text == null ? null : text.replace("&nbsp;", "").replace((char) 160, ' ').trim();
    }

    private Date parse(String text) throws ParseException {
        return "".equals(text) || "TBD".equals(text) ? null : sdf.parse(text);
    }

    private void readBuilds(String branch, URL url, int tableNumber, boolean showBugsFixed)
        throws IOException, ParseException {
        Source source=new Source(url);
        final List tables = source.findAllElements("table");
        Element table = (Element) tables.get(tableNumber);
        final List<? extends Element> elements = table.findAllElements("tr");
        for (int i = 1, elementsSize = elements.size(); i < elementsSize; i++) {
            final List<Element> tds = elements.get(i).getChildElements();
            int index = 0;
            Build build = null;
            if (showBugsFixed) {
                final String number = trim(tds.get(index++).extractText());
                final String date = trim(tds.get(index++).extractText());
                final String comment = trim(tds.get(index++).extractText());
                final String bugsFixed = trim(tds.get(index++).extractText());
                final String rev = trim(tds.get(index++).extractText());
                if(!"TBD".equals(date)) {
                    build = new Build(branch, number, parse(date), comment, bugsFixed, rev);
                }

            } else {
                build = new Build(branch, trim(tds.get(index++).extractText()),
                    parse(trim(tds.get(index++).extractText())),
                    trim(tds.get(index++).extractText()),
                    trim(tds.get(index++).extractText()));

            }
            if(build != null) {
                calendar.getComponents().add(build.toEvent());
            }
        }
    }

    @SuppressWarnings({"UnusedAssignment"})
    public void readMileStones(String branch, URL url, int tableNumber) throws IOException, ParseException {
        Source source=new Source(url);
        Element table = (Element) source.findAllElements("table").get(tableNumber);
        final List<? extends Element> elements = table.findAllElements("tr");
        for (int i = 1, elementsSize = elements.size(); i < elementsSize; i++) {
            final List<Element> tds = elements.get(i).getChildElements();
            int index = 0;
            calendar.getComponents().add(new Milestone(branch, trim(tds.get(index++).extractText()), parse(
                trim(tds.get(index++).extractText())), parse(trim(tds.get(index++).extractText())),
                trim(tds.get(index++).extractText()), trim(tds.get(index++).extractText())).toEvent());
        }
    }

    public static void main(String[] args) throws ValidationException, IOException, ParseException {
        final Milestones milestones = new Milestones();
        System.out.println("Reading 3.2 Milestones");
        milestones.readMileStones("3.2", new URL("http://wikis.sun.com/display/GlassFish/GlassFishV3Schedule"), 3);
        System.out.println("Reading 3.1.1 Milestones");
        milestones.readMileStones("3.1.1", new URL("http://wikis.sun.com/display/GlassFish/GlassFishV3Schedule"), 4);
        System.out.println("Reading 3.2 Builds");
        milestones.readBuilds("3.2", new URL("http://wikis.sun.com/display/GlassFish/3.2BuildSchedule"), 3, false);
        System.out.println("Reading 3.1.1 Builds");
        milestones.readBuilds("3.1.1", new URL("http://wikis.sun.com/display/GlassFish/3.1.1BuildSchedule"), 3, true);
        final FileOutputStream fout = new FileOutputStream(new File("milestones.ics"));
        milestones.generate(fout);
        fout.flush();
        fout.close();
    }
}
