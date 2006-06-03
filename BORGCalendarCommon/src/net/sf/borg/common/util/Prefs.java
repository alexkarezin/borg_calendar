/*
 This file is part of BORG.
 
 BORG is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.
 
 BORG is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU General Public License
 along with BORG; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 
 Copyright 2003 by Mike Berger
 */
package net.sf.borg.common.util;

import java.util.ArrayList;

/**
 * 
 * Convenience class for retrieving preferences.
 *  
 */
public class Prefs {

	public interface Listener 
	{
		public abstract void prefsChanged();
	}
	
	static private ArrayList listeners = new ArrayList();
	
    static public void addListener(Listener listener)
    {
        listeners.add(listener);
    }
    
    static public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }
    
    // send a notification to all registered views
    static public void notifyListeners()
    {
        for( int i = 0; i < listeners.size(); i++ )
        {
            Listener v = (Listener) listeners.get(i);
            v.prefsChanged();
        }
    }
    
	public static String getPref(PrefName pn) {

		return ((String) getPrefs().getPref(pn));
	}

	public static void putPref(PrefName pn, Object val) {

		getPrefs().putPref(pn, val);
	}

	public static int getIntPref(PrefName pn) {
		return (((Integer) getPrefs().getPref(pn)).intValue());
	}


	// private //
	private static IPrefs getPrefs() {

		return PrefsHome.getInstance().getPrefs();
	}

	private Prefs() {

	}
}
