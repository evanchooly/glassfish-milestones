package org.glassfish.milestones;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private List<Milestone> markers = new ArrayList<Milestone>();
    private Calendar calendar;
    private Uid uid = new Uid("ba67eba8-ed76-450d-90bb-540ec69070d4");

    @SuppressWarnings({"UnusedAssignment"})
    public void read(InputStream uri) throws IOException, ParseException, ValidationException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(uri));
        String line;
        while ((line = reader.readLine()) != null && !line
            .contains("a name=\"GlassFishV3Schedule-GlassFishServerOpenSourceEdition3.2InDevelopment\"")) {
        }
        for (int i = 0; i < 10; i++) {
            line = trim(reader.readLine());
        }
        while (!(line = trim(reader.readLine())).endsWith("</table>")) {
            markers.add(new Milestone(
                getText(reader.readLine()),
                parse(getText(reader.readLine())),
                parse(getText(reader.readLine())),
                getText(reader.readLine()),
                getText(reader.readLine())));
            line = trim(reader.readLine());  // consume the </tr>
        }
        calendar = new Calendar();
        calendar.getProperties().add(new ProdId("GlassFish 3.2 CalGen"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        for (Milestone marker : markers) {
            calendar.getComponents().add(marker.toEvent());
        }
//        calendar.validate();
    }

    private String getText(String line) {
        String value = line;
        while (value.contains("<")) {
            try {
                value = value.substring(value.indexOf(">") + 1, value.lastIndexOf("<"));
            } catch (StringIndexOutOfBoundsException e) {
                value = value.replaceAll("<br.*/>", "");
                System.out.println("value = " + value);
            }
        }
        return trim(value);
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
        milestones.read(new FileInputStream("milestones.html"));
        final FileOutputStream fout = new FileOutputStream(new File("milestones.ics"));
        milestones.generate(fout);
        fout.flush();
        fout.close();
    }
}
