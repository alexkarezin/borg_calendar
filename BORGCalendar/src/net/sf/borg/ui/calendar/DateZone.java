package net.sf.borg.ui.calendar;

import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import net.sf.borg.common.Errmsg;
import net.sf.borg.common.PrefName;
import net.sf.borg.common.Prefs;
import net.sf.borg.common.Resource;
import net.sf.borg.common.XTree;
import net.sf.borg.model.AppointmentModel;
import net.sf.borg.model.beans.Appointment;
import net.sf.borg.model.beans.AppointmentXMLAdapter;
import net.sf.borg.ui.MultiView;

public class DateZone {

    private Rectangle bounds;

    private Date date;

    public DateZone(Date d, Rectangle bounds) {
	this.date = d;
	this.bounds = bounds;
    }

    public void createAppt(int topmins, int botmins, String text) {

	// get default appt values, if any
	Appointment appt = null;
	String defApptXml = Prefs.getPref(PrefName.DEFAULT_APPT);
	if (!defApptXml.equals("")) {
	    try {
		XTree xt = XTree.readFromBuffer(defApptXml);
		AppointmentXMLAdapter axa = new AppointmentXMLAdapter();
		appt = (Appointment) axa.fromXml(xt);

	    } catch (Exception e) {
		Errmsg.errmsg(e);
	    }
	}

	if (appt == null) {
	    appt = AppointmentModel.getReference().newAppt();
	}

	// System.out.println(top + " " + bottom);
	int realtime = topmins;
	int hour = realtime / 60;
	int min = realtime % 60;
	min = (min / 5) * 5;
	Calendar startCal = new GregorianCalendar();
	startCal.setTime(date);
	startCal.set(Calendar.HOUR_OF_DAY, hour);
	startCal.set(Calendar.MINUTE, min);
	appt.setDate(startCal.getTime());

	int realend = botmins;
	int ehour = realend / 60;
	int emin = realend % 60;
	emin = (emin / 5) * 5;
	int dur = 60 * (ehour - hour) + emin - min;

	appt.setDuration(new Integer(dur));
	appt.setText(text);
	AppointmentModel.getReference().saveAppt(appt, true);
    }

    public void edit() {
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(date);
	AppointmentListView ag = new AppointmentListView(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
	MultiView.getMainView().addView(ag);

    }

    public Rectangle getBounds() {
	return bounds;
    }

    public void setBounds(Rectangle bounds) {
	this.bounds = bounds;
    }

    private JPopupMenu popmenu = null;

    public JPopupMenu getMenu() {
	JMenuItem mnuitm;
	if (popmenu == null) {
	    popmenu = new JPopupMenu();
	    popmenu.add(mnuitm = new JMenuItem(Resource.getPlainResourceString("Add_New")));
	    mnuitm.addActionListener(new ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    edit();
		}
	    });

	}
	return popmenu;
    }
}
