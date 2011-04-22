package org.glassfish.milestones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;

/**
 * Hello world!
 */
@SuppressWarnings({"StringContatenationInLoop"})
public class Milestones {
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private Calendar calendar;
    private Uid uid = new Uid("ba67eba8-ed76-450d-90bb-540ec69070d4");

    public Milestones() {
        calendar = new Calendar();
        calendar.getProperties().add(new ProdId("GlassFish 3.2 CalGen"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
    }

    @SuppressWarnings({"UnusedAssignment"})
    public void readMileStones(final String branch, InputStream uri) throws IOException, ParseException, ValidationException {
        String target = String.format("a name=\"GlassFishV3Schedule-GlassFishServerOpenSourceEdition%sInDevelopment\"", branch);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(uri));
        String line;
        while ((line = reader.readLine()) != null && !line
            .contains(target)) {
        }
        for (int i = 0; i < 10; i++) {
            line = trim(reader.readLine());
        }
        while (!(line = trim(reader.readLine())).endsWith("</table>")) {
            final Milestone milestone = new Milestone(
                branch, getText(reader.readLine()),
                parse(getText(reader.readLine())),
                parse(getText(reader.readLine())),
                getText(reader.readLine()),
                getText(reader.readLine()));
            calendar.getComponents().add(milestone.toEvent());
            line = trim(reader.readLine());  // consume the </tr>
        }
    }

    private void readBuilds(final String branch, InputStream inputStream, final int skipCount, boolean showBugsFixed) throws IOException, ParseException {
        String target = String.format("a name=\"%sBuildSchedule-GlassFishServerOpenSourceEdition%sBuildSchedule\"",
            branch, branch);

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null && !line
            .contains(target)) {
        }
        for (int index = 0; index < skipCount; index++) {
            line = reader.readLine();
        }
        while (!(line = trim(reader.readLine())).endsWith("</table>")) {
            Build build;
            if(showBugsFixed) {
                build = new Build(branch, getText(reader.readLine()),
                    parse(getText(reader.readLine())),
                    getText(reader.readLine()),
                    getText(reader.readLine()),
                    getText(reader.readLine()));

            } else {
                build = new Build(branch, getText(reader.readLine()),
                    parse(getText(reader.readLine())),
                    getText(reader.readLine()),
                    getText(reader.readLine()));

            }
            calendar.getComponents().add(build.toEvent());
            line = trim(reader.readLine());  // consume the </tr>
        }
    }

    private String getText(String line) {
        StringBuilder value = new StringBuilder(line);
        while (value.indexOf("<") != -1) {
            try {
                value = value.replace(value.indexOf("<"), value.indexOf(">") + 1, "");
            } catch (StringIndexOutOfBoundsException e) {
//                value = value.replaceAll("<br.*/>", "");
            }
        }
        return trim(value.toString());
    }

    public void generate(OutputStream fout) throws IOException, ValidationException {
        new CalendarOutputter().output(calendar, fout);
    }

    private String trim(String text) {
        return text == null ? null : text.replace("&nbsp;", "").trim();
    }

    private Date parse(String text) throws ParseException {
        return "".equals(text) ? null : sdf.parse(text);
    }

    public static void main(String[] args) throws ValidationException, IOException, ParseException {
        final Milestones milestones = new Milestones();
        milestones.readMileStones("3.2",
            new URL("http://wikis.sun.com/display/GlassFish/GlassFishV3Schedule")
            .openConnection().getInputStream()
        );
        milestones.readMileStones("3.1.1",
            new URL("http://wikis.sun.com/display/GlassFish/GlassFishV3Schedule")
            .openConnection().getInputStream()
        );
        milestones.readBuilds("3.2",
            new URL("http://wikis.sun.com/display/GlassFish/3.2BuildSchedule")
                .openConnection().getInputStream(), 11,
            false);
        milestones.readBuilds("3.1.1",
            new URL("http://wikis.sun.com/display/GlassFish/3.1.1BuildSchedule")
            .openConnection().getInputStream(), 12,
            true);
        final FileOutputStream fout = new FileOutputStream(new File("milestones.ics"));
        milestones.generate(fout);
        fout.flush();
        fout.close();
    }
}
