package org.glassfish.milestones;

import java.io.BufferedReader;
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
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * Hello world!
 */
public class Milestones {
    private Element current;
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    private List<Milestone> markers = new ArrayList<Milestone>();
    private Calendar calendar;

    public void read(InputStream uri)
        throws IOException, SAXException, JDOMException, ParseException, ValidationException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(uri));
        String line;
        while ((line = reader.readLine()) != null && !line
            .contains("a name=\"GlassFishV3Schedule-GlassFishServerOpenSourceEdition3.2InDevelopment\"")) {
        }
        for (int i = 0; i < 10; i++) {
            line = trim(reader.readLine());
//            System.out.println("reader.readLine() = " + line);
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
        for (Milestone marker : markers) {
            System.out.println("marker = " + marker);
            calendar.getComponents().add(marker.toEvent());
        }
        System.out.println("calendar = " + calendar);
        calendar.validate();
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

    public void generate(OutputStream fout)
        throws IOException, SAXException, JDOMException, ParseException, ValidationException {
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, fout);
    }

    private String trim(String text) {
        return text == null ? null : text.replace("&nbsp;", "").trim();
    }

    private Date parse(String text) throws ParseException {
        return "".equals(text) ? null : sdf.parse(text);
    }

    private void getChild(String name, int i) {
        setCurrent((Element) current.getChildren(name).get(i));
    }

    private Milestones getChild(int index) {
        setCurrent((Element) current.getChildren().get(index));
        return this;
    }

    private Milestones getChild(String name) {
        System.out.println("name = " + name);
        setCurrent(current.getChild(name));
        return this;
    }

    private void setCurrent(final Element child) {
        if (child == null) {
            throw new RuntimeException("null child");
        }
        current = child;
    }

    private Milestones getChild(String name, String attr, String value) {
        System.out.printf("name: %s, attr: %s, value %s\n", name, attr, value);
        final List children = current.getChildren(name);
        for (Object child : children) {
            final Element element = (Element) child;
            final Attribute attribute = element.getAttribute(attr);
            if (attribute != null && attribute.getValue().equals(value)) {
                setCurrent(element);
            }
        }
        return this;
    }

    public static void main(String[] args)
        throws JDOMException, IOException, SAXException, ParseException, ValidationException {
        final Milestones milestones = new Milestones();
        milestones.read(new FileInputStream("milestones.html"));
        FileOutputStream fout = new FileOutputStream("mycalendar.ics");
        milestones.generate(fout);
    }
}
