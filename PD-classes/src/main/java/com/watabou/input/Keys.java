/*
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

package com.watabou.input;

import java.util.ArrayList;

import com.watabou.utils.Signal;

import android.view.KeyEvent;

public class Keys {
	
	public static final int BACK		= KeyEvent.KEYCODE_BACK;
	public static final int MENU		= KeyEvent.KEYCODE_MENU;
	public static final int VOLUME_UP	= KeyEvent.KEYCODE_VOLUME_UP;
	public static final int VOLUME_DOWN	= KeyEvent.KEYCODE_VOLUME_DOWN;

	// game controller buttons
	public static final int DPAD_UP = KeyEvent.KEYCODE_DPAD_UP;
	public static final int DPAD_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
	public static final int DPAD_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
	public static final int DPAD_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
	public static final int DPAD_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;
	public static final int BUTTON_A = KeyEvent.KEYCODE_BUTTON_A;
	public static final int BUTTON_B = KeyEvent.KEYCODE_BUTTON_B;

	public static Signal<Key> event = new Signal<Key>( true );
	
	public static void processKeyEvents( ArrayList<KeyEvent> events ) {
		
		int size = events.size();
		for (int i=0; i < size; i++) {
			
			KeyEvent e = events.get( i );
			
			switch (e.getAction()) {
			case KeyEvent.ACTION_DOWN:
				event.dispatch( new Key( e.getKeyCode(), true ) );
				break;
			case KeyEvent.ACTION_UP:
				event.dispatch( new Key( e.getKeyCode(), false ) );
				break;
			}
		}
	}
	
	public static class Key {
		
		public int code;
		public boolean pressed;
		
		public Key( int code, boolean pressed ) {
			this.code = code;
			this.pressed = pressed;
		}
	}
}
