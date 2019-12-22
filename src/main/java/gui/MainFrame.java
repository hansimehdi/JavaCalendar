package gui;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import dao.CalendarDao;
import de.costache.calendar.JCalendar;
import de.costache.calendar.events.*;
import de.costache.calendar.model.CalendarEvent;
import de.costache.calendar.ui.strategy.DisplayStrategy;
import de.costache.calendar.util.CalendarUtil;
import helpers.DateHelper;
import models.EventModel;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm:ss");

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem exitMenuItem, deleteAll, addNew;
    private JCalendar jCalendar;
    private JSplitPane content;
    private JToolBar toolBar;
    private JTextArea description;
    private JPopupMenu popup;
    private JMenuItem removeMenuItem;
    private JTextField email;
    private JTextArea emailDescription;
    private DatePicker startDate, endDate;
    private TimePicker startTime, endTime;
    private JColorChooser colorPicker;
    private JLabel emailValidation, startDateValidation, endDateValidation, startTimeValidation, endTimeValidation, descriptionValidation;

    private List<EventModel> eventModels;

    private EventModel selectedEvent;

    private ActionType actionType;

    private JButton removeButton, addButton, update;

    private MainFrame self;

    private final Random r = new Random();

    private enum ActionType {
        ADD,
        UPDATE
    }

    public MainFrame() throws Exception {
        initGui();
        initData();
        resetGui();
        bindListeners();
        self = this;

        email = new JTextField();
        emailDescription = new JTextArea("", 5, 10);
        startTime = new TimePicker();
        DatePickerSettings datePickerSettings = new DatePickerSettings();
        datePickerSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);
        DatePickerSettings endDatePickerSettings = datePickerSettings.copySettings();
        startDate = new DatePicker(datePickerSettings);
        endDate = new DatePicker(endDatePickerSettings);
        endTime = new TimePicker();
        emailValidation = new JLabel();
        startDateValidation = new JLabel();
        startTimeValidation = new JLabel();
        endDateValidation = new JLabel();
        endTimeValidation = new JLabel();
        descriptionValidation = new JLabel();
        colorPicker = new JColorChooser();
    }


    private void initGui() {
        actionType = ActionType.ADD;

        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        addNew = new JMenuItem("New");
        deleteAll = new JMenuItem("Delete all");
        exitMenuItem = new JMenuItem("Exit");

        fileMenu.add(addNew);
        fileMenu.add(deleteAll);
        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        toolBar = new JToolBar("Controls");
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        update = new JButton("Update");

        toolBar.add(addButton);
        toolBar.add(update);
        toolBar.add(removeButton);

        removeMenuItem = new JMenuItem("Remove");

        popup = new JPopupMenu();
        popup.add(removeMenuItem);
        popup.add(new JSeparator());

        description = new JTextArea();
        description.setLineWrap(true);
        description.setRows(10);
        jCalendar = new JCalendar();
        jCalendar.setJPopupMenu(popup);
        jCalendar.getConfig().setAllDayPanelVisible(false);
        jCalendar.setDisplayStrategy(DisplayStrategy.Type.MONTH, null);

        content = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        content.add(jCalendar);

        this.getContentPane().setLayout(new BorderLayout(10, 10));
        this.getContentPane().add(toolBar, BorderLayout.PAGE_START);
        this.getContentPane().add(content, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
    }

    private void resetGui() {
        if (jCalendar.getSelectedCalendarEvents().size() != 0) {
            try {
                EventModel model = (EventModel) jCalendar.getSelectedCalendarEvents().iterator().next();
                if (model.getId() == null) {
                    update.setEnabled(false);
                    removeButton.setEnabled(false);
                }
            } catch (NoSuchElementException e) {
                update.setEnabled(false);
                removeButton.setEnabled(false);
            }
        } else {
            update.setEnabled(false);
            removeButton.setEnabled(false);
        }
        try {
            if (CalendarDao.getDao().countOf() <= 0) {
                deleteAll.setEnabled(false);
            } else {
                deleteAll.setEnabled(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enableActions() {
        if (jCalendar.getSelectedCalendarEvents().size() != 0) {
            try {
                EventModel model = (EventModel) jCalendar.getSelectedCalendarEvents().iterator().next();
                if (model.getId() == null) {
                    update.setEnabled(false);
                    removeButton.setEnabled(false);
                    return;
                } else {
                    System.out.println(model.getId());
                }
            } catch (NoSuchElementException e) {
                update.setEnabled(false);
                removeButton.setEnabled(false);
                return;
            }
        } else {
            update.setEnabled(false);
            removeButton.setEnabled(false);
            return;
        }
        update.setEnabled(true);
        removeButton.setEnabled(true);
    }


    private void initData() throws Exception {
        eventModels = CalendarDao.getDao().queryForAll();
        eventModels.forEach(e -> {
            e.setEnd(e.getEndDate());
            e.setStart(e.getStartDate());
            e.setBgColor(e.getBgColor());
            try {
                e.setDateDescription(e.getDateDescription());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            jCalendar.addCalendarEvent(e);
        });

    }

    private void bindListeners() {

        exitMenuItem.addActionListener(e -> {
            int decision = JOptionPane.showConfirmDialog(null, "Do you want to close the calendar ?", "Question", JOptionPane.YES_NO_OPTION);
            if (decision == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        update.addActionListener(e -> {
            ResetForm();
            actionType = ActionType.UPDATE;
            selectedEvent = (EventModel) jCalendar.getSelectedCalendarEvents().iterator().next();
            email.setText(selectedEvent.getEmail());
            emailDescription.setText(selectedEvent.getDateDescription());
            startDate.setDate(DateHelper.convertDateToLocalDate(selectedEvent.getStartDate()));
            endDate.setDate(DateHelper.convertDateToLocalDate(selectedEvent.getEndDate()));
            startTime.setTime(DateHelper.convertDateToLocalTime(selectedEvent.getStartDate()));
            endTime.setTime(DateHelper.convertDateToLocalTime(selectedEvent.getEndDate()));
            colorPicker.setColor(Color.decode(selectedEvent.getBgColor()));
            try {
                BuildFormGui();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "An Error occurred while fetching the event", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        deleteAll.addActionListener(e -> {
            int decision = JOptionPane.showConfirmDialog(null, "This action cannot be undone", "Are you sure ?", JOptionPane.YES_NO_OPTION);
            if (decision == JOptionPane.YES_OPTION) {
                try {
                    CalendarDao.getDao().queryForAll().forEach(eventModel -> {
                        try {
                            CalendarDao.getDao().delete(eventModel);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "An Error occurred while deleting all data", "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    });
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "An Error occurred while deleting all data", "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }

                jCalendar.getCalendarEvents().forEach(calendarEvent -> {
                    jCalendar.removeCalendarEvent(calendarEvent);
                });
                JToast.makeToast(self, "Calendar has been cleared !", 5, new Color(0, 230, 118));

            }
        });

        addNew.addActionListener(e -> {
            ResetForm();
            try {
                BuildFormGui();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "An Error occurred while adding event", "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        addButton.addActionListener(arg0 -> {
            ResetForm();
            try {
                BuildFormGui();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An Error occurred while adding event", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        removeButton.addActionListener(arg0 -> {
            int decision = JOptionPane.showConfirmDialog(null, "This action cannot be undone", "Are you sure ?", JOptionPane.YES_NO_OPTION);
            if (decision == JOptionPane.YES_OPTION) {
                final Collection<CalendarEvent> selected = jCalendar.getSelectedCalendarEvents();
                for (final CalendarEvent event : selected) {
                    EventModel e = (EventModel) event;
                    try {
                        CalendarDao.getDao().delete(e);
                        jCalendar.removeCalendarEvent(event);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "An Error occurred while removing event", "Error", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }

                }
            }
        });

        removeMenuItem.addActionListener(arg0 -> {
            final Collection<CalendarEvent> selected = jCalendar.getSelectedCalendarEvents();
            for (final CalendarEvent event : selected) {
                jCalendar.removeCalendarEvent(event);
            }
        });

        jCalendar.addCollectionChangedListener(new ModelChangedListener() {

            @Override
            public void eventRemoved(final ModelChangedEvent event) {
                if (actionType != ActionType.UPDATE) {
                    JToast.makeToast(self, "Event has been removed !", 5, new Color(0, 230, 118));
                    resetGui();
                }
            }

            @Override
            public void eventChanged(final ModelChangedEvent event) {
                description.append("Event changed " + event.getCalendarEvent() + "\n");
                resetGui();
            }

            @Override
            public void eventAdded(final ModelChangedEvent event) {
                if (actionType == ActionType.UPDATE) {
                    JToast.makeToast(self, "Email has been updated !", 5, new Color(0, 230, 118));
                    resetGui();
                } else {
                    JToast.makeToast(self, "Email has been scheduled !", 5, new Color(0, 230, 118));
                    resetGui();
                }
            }
        });

        jCalendar.addSelectionChangedListener(event -> {
            enableActions();
            if (event.getCalendarEvent() != null) {
                if (event.getCalendarEvent().isSelected()) {
                    description.append("Event selected " + event.getCalendarEvent());
                } else {
                    description.append("Event deselected " + event.getCalendarEvent());
                }
            } else {
                description.append("Selection cleared");
            }
            description.append("\n");
        });

        jCalendar.addIntervalChangedListener(event -> description.append("Interval changed " + sdf.format(event.getIntervalStart()) + " "
                + sdf.format(event.getIntervalEnd()) + "\n"));

        jCalendar.addIntervalSelectionListener(event -> description.append("Interval selection changed " + sdf.format(event.getIntervalStart()) + " "
                + sdf.format(event.getIntervalEnd()) + "\n"));

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent arg0) {
                removeMenuItem.setEnabled(jCalendar.getSelectedCalendarEvents().size() > 0);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent arg0) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent arg0) {
            }
        });
    }

    private void BuildFormGui() throws Exception {
        email.setText(email.getText());
        startDate.setDate(startDate.getDate());
        startTime.setTime(startTime.getTime());
        endDate.setDate(endDate.getDate());
        endTime.setTime(endTime.getTime());
        colorPicker.setColor(colorPicker.getColor());
        emailDescription.setText(emailDescription.getText());

        Object[] fields = {
                "Enter email",
                email,
                emailValidation,
                "Pick start date",
                startDate,
                startDateValidation,
                "Pick start time",
                startTime,
                startTimeValidation,
                "Pick end date",
                endDate,
                endDateValidation,
                "Pick end time",
                endTime,
                endTimeValidation,
                "Pick color",
                colorPicker,
                "Description",
                emailDescription,
                descriptionValidation
        };

        int decision = JOptionPane.showConfirmDialog(null, fields, "Add new item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (decision == JOptionPane.OK_OPTION) {
            if (!ValidateInputs()) {
                BuildFormGui();
            } else {
                if (actionType == ActionType.ADD) {
                    EventModel model = new EventModel();
                    PerformAction(model);
                    jCalendar.addCalendarEvent(model);
                    CalendarDao.getDao().create(model);
                } else {
                    PerformAction(selectedEvent);
                    jCalendar.removeCalendarEvent(selectedEvent);
                    jCalendar.addCalendarEvent(selectedEvent);
                    CalendarDao.getDao().update(selectedEvent);
                }

            }
        }
    }

    private void PerformAction(EventModel selectedEvent) throws Exception {
        selectedEvent.setEmail(email.getText());
        selectedEvent.setDateDescription(emailDescription.getText());
        selectedEvent.setStartDate(
                CalendarUtil.createDate(startDate.getDate().getYear(),
                        startDate.getDate().getMonth().getValue(),
                        startDate.getDate().getDayOfMonth(),
                        startTime.getTime().getHour(),
                        startTime.getTime().getMinute(),
                        startTime.getTime().getMinute(), 0)
        );
        selectedEvent.setEndDate(
                CalendarUtil.createDate(endDate.getDate().getYear(),
                        endDate.getDate().getMonthValue(),
                        endDate.getDate().getDayOfMonth(),
                        endTime.getTime().getHour(),
                        endTime.getTime().getMinute(),
                        endTime.getTime().getMinute(), 0)
        );
        if (colorPicker.getColor() == null || colorPicker.getColor() == Color.WHITE) {
            selectedEvent.setBgColor("#673AB7");
        } else {
            String hex = String.format("#%02x%02x%02x", colorPicker.getColor().getRed(), colorPicker.getColor().getGreen(), colorPicker.getColor().getBlue());
            selectedEvent.setBgColor(hex);
        }
    }

    private boolean ValidateInputs() {
        if (email.getText().equals("") || email.getText() == null || !email.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            emailValidation.setText("Invalid email");
            emailValidation.setForeground(Color.RED);
            emailValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            return false;
        } else {
            emailValidation.setText("");
        }

        if (CheckDateAndTime(startDate, startDateValidation, startTime, startTimeValidation)) return false;

        if (CheckDateAndTime(endDate, endDateValidation, endTime, endTimeValidation)) return false;


        if (!CheckDateAndTime(startDate, startDateValidation, startTime, startTimeValidation) && !CheckDateAndTime(endDate, endDateValidation, endTime, endTimeValidation)) {
            if (endDate.getDate().isBefore(startDate.getDate())) {
                endDateValidation.setText("End date should be greater than start date");
                endDateValidation.setForeground(Color.RED);
                endDateValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
                return false;
            } else if (endDate.getDate().isEqual(startDate.getDate())) {
                if (endTime.getTime().isBefore(startTime.getTime()) || endTime.getTime().equals(startTime.getTime())) {
                    endTimeValidation.setText("End time should be greater than start time");
                    endTimeValidation.setForeground(Color.RED);
                    endTimeValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
                    return false;
                } else {
                    endTimeValidation.setText("");
                }
            } else {
                endDateValidation.setText("");
            }
        }
        if (emailDescription.getText().equals("") || emailDescription.getText() == null) {
            descriptionValidation.setText("Description is required");
            descriptionValidation.setForeground(Color.RED);
            descriptionValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            return false;
        } else {
            descriptionValidation.setText("");
        }

        return true;
    }

    private boolean CheckDateAndTime(DatePicker startDate, JLabel startDateValidation, TimePicker startTime, JLabel startTimeValidation) {
        if (startDate.getText().equals("") || startDate.getText() == null) {
            startDateValidation.setText("Invalid date");
            startDateValidation.setForeground(Color.RED);
            startDateValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            return true;
        } else {
            startDateValidation.setText("");
        }

        if (startTime.getText().equals("") || startTime.getText() == null) {
            startTimeValidation.setText("Invalid time");
            startTimeValidation.setForeground(Color.RED);
            startTimeValidation.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
            return true;
        } else {
            startTimeValidation.setText("");
        }

        return false;
    }

    private void ResetForm() {
        actionType = ActionType.ADD;
        email.setText("");
        emailValidation.setText("");
        startDate.setText("");
        startDateValidation.setText("");
        startTime.setText("");
        startDateValidation.setText("");
        endDate.setText("");
        endDateValidation.setText("");
        endTime.setText("");
        endTimeValidation.setText("");
        colorPicker.setColor(Color.decode("#ffffff"));
        emailDescription.setText("");
        descriptionValidation.setText("");
    }
}
