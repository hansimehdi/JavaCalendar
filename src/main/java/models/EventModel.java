package models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import de.costache.calendar.model.CalendarEvent;
import de.costache.calendar.model.EventType;
import java.awt.*;
import java.util.Date;
import java.util.UUID;

@DatabaseTable(tableName = "events")
public class EventModel extends CalendarEvent {

    @DatabaseField(columnName = "id", generatedId = true,canBeNull = false)
    private UUID Id;

    @DatabaseField(columnName = "email",canBeNull = false)
    private String Email;

    @DatabaseField(columnName = "desciption",canBeNull = false)
    private String Description;

    @DatabaseField(columnName = "start_date",canBeNull = false)
    private Date startDate;

    @DatabaseField(columnName = "endDate",canBeNull = false)
    private Date endDate;

    @DatabaseField(columnName = "color")
    private String color;

    public EventModel() {
        super();
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        this.setStart(startDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
        this.setEnd(endDate);
    }

    public String getBgColor() {
        return color;
    }

    public void setBgColor(String color) {
        this.color = color;
        EventType eventType = new EventType();
        eventType.setBackgroundColor(Color.decode(color));
        this.setType(eventType);
    }

    public UUID getId() {
        return Id;
    }

    public void setId(UUID id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getDateDescription() {
        return Description;
    }

    public void setDateDescription(String description) throws Exception {
        if(getEmail().equals("") || getEmail()==null){
            throw new Exception("Email should be set before setting the date description");
        }
        Description = description;
        this.setSummary("Email to: " + this.getEmail());
    }

    @Override
    public String toString() {
        return "EventModel{" +
                "Id=" + Id +
                ", Email='" + Email + '\'' +
                ", Description='" + Description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", color='" + color + '\'' +
                '}';
    }
}
