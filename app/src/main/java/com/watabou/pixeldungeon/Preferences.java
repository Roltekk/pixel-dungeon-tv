/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon;

import com.watabou.noosa.Game;

import android.content.SharedPreferences;

public enum Preferences {

	INSTANCE;
	
	public static final String KEY_TELEVISION	= "television";
	public static final String KEY_LANDSCAPE	= "landscape";
	public static final String KEY_IMMERSIVE	= "immersive";
	public static final String KEY_GOOGLE_PLAY	= "google_play";
	public static final String KEY_SCALE_UP		= "scaleup";
	public static final String KEY_MUSIC		= "music";
	public static final String KEY_SOUND_FX		= "soundfx";
	public static final String KEY_ZOOM			= "zoom";
	public static final String KEY_LAST_CLASS	= "last_class";
	public static final String KEY_CHALLENGES	= "challenges";
	public static final String KEY_DONATED		= "donated";
	public static final String KEY_INTRO		= "intro";
	public static final String KEY_BRIGHTNESS	= "brightness";
	public static final String KEY_DEBUG_INFO				= "debug_info";
	public static final String KEY_DEBUG_SHOW_TOUCH_AREAS	= "debug_show_touch_areas";
	public static final String KEY_DEBUG_SHOW_FPS 			= "debug_show_fps";
	
	private SharedPreferences prefs;
	
	private SharedPreferences get() {
		if (prefs == null) {
			prefs = Game.instance.getPreferences( Game.MODE_PRIVATE );
		}
		return prefs;
	}
	
	public int getInt( String key, int defValue  ) {
		return get().getInt( key, defValue );
	}
	
	public boolean getBoolean( String key, boolean defValue  ) {
		return get().getBoolean( key, defValue );
	}
	
	public String getString( String key, String defValue  ) {
		return get().getString( key, defValue );
	}
	
	public void put( String key, int value ) {
		get().edit().putInt( key, value ).commit();
	}
	
	public void put( String key, boolean value ) {
		get().edit().putBoolean( key, value ).commit();
	}
	
	public void put( String key, String value ) {
		get().edit().putString( key, value ).commit();
	}
}
