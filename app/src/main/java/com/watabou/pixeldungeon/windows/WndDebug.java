/*
 * Pixel Dungeon TV
 * Copyright (C) 2016 Ryan Wilson
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
package com.watabou.pixeldungeon.windows;

import com.roltekk.util.Debug;
import com.watabou.input.Keys;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndDebug extends Window {
	private static final String TXT_SHOW_DEBUG_INFO = "Show Debug Info";
	private static final String TXT_SHOW_TOUCHAREAS = "Touch Areas";
	private static final String TXT_SHOW_FPS        = "FPS";
	
	private static final int WIDTH = 112;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP = 2;
	
	private boolean keyHandled;
	private Button  focusedButton;
	
	public WndDebug() {
		super();
		
		CheckBox btnShowDebugInfo = new CheckBox( TXT_SHOW_DEBUG_INFO ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.debugInfo( checked() );
				Debug.DEBUG_INFO = checked();
			}
		};
		btnShowDebugInfo.setRect( 0, 0, WIDTH, BTN_HEIGHT );
		btnShowDebugInfo.checked( PixelDungeon.debugInfo() );
		add( btnShowDebugInfo );

		CheckBox btnShowTouchAreas = new CheckBox( TXT_SHOW_TOUCHAREAS ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.debugShowTouchAreas( checked() );
				Debug.DRAW_TOUCH_AREAS = checked();
			}
		};
		btnShowTouchAreas.setRect( 0, btnShowDebugInfo.bottom() + GAP, WIDTH, BTN_HEIGHT );
		btnShowTouchAreas.checked( PixelDungeon.debugShowTouchAreas() );
		add( btnShowTouchAreas );
		
		CheckBox btnShowFPS = new CheckBox( TXT_SHOW_FPS ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.debugShowFPS( checked() );
				Debug.DRAW_FPS = checked();
			}
		};
		btnShowFPS.setRect( 0, btnShowTouchAreas.bottom() + GAP, WIDTH, BTN_HEIGHT );
		btnShowFPS.checked( PixelDungeon.debugShowFPS() );
		add( btnShowFPS );
		
		resize( WIDTH, (int) btnShowFPS.bottom() );
		
		// set button navigation associations
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false )) {
			btnShowDebugInfo.set_down_button( btnShowTouchAreas );
			btnShowTouchAreas.set_up_button( btnShowDebugInfo );
			btnShowTouchAreas.set_down_button( btnShowFPS );
			btnShowFPS.set_up_button( btnShowTouchAreas );
			
			focusedButton = btnShowDebugInfo;
			((RedButton)focusedButton).active_selection.visible = true;
			
			Keys.event.add( this );
		}
	}
	
	@Override
	public void destroy() {
		super.destroy();
		Keys.event.remove( this );
	}
	
	@Override
	public void onSignal( Keys.Key key ) {
		final boolean handled;
		
		if (key.pressed) {
			handled = onKeyDown( key );
		} else {
			handled = onKeyUp( key );
		}
		
		if (handled) {
			Keys.event.cancel();
		} else {
			super.onSignal( key );
		}
	}

	private boolean onKeyDown( Keys.Key key ) {
		// - moves highlight indicator (dpad)
		// - A/CENTER press confirms selection
		keyHandled = true;
		switch (key.code) {
			case Keys.DPAD_UP:
				if (focusedButton.get_up_button() != null) {
					((RedButton)focusedButton).active_selection.visible = false;
					focusedButton = focusedButton.get_up_button();
					((RedButton)focusedButton).active_selection.visible = true;
				}
				break;
			case Keys.DPAD_DOWN:
				if (focusedButton.get_down_button() != null) {
					((RedButton)focusedButton).active_selection.visible = false;
					focusedButton = focusedButton.get_down_button();
					((RedButton)focusedButton).active_selection.visible = true;
				}
				break;
			case Keys.DPAD_LEFT:
				if (focusedButton.get_left_button() != null) {
					((RedButton)focusedButton).active_selection.visible = false;
					focusedButton = focusedButton.get_left_button();
					((RedButton)focusedButton).active_selection.visible = true;
				}
				break;
			case Keys.DPAD_RIGHT:
				if (focusedButton.get_right_button() != null) {
					((RedButton)focusedButton).active_selection.visible = false;
					focusedButton = focusedButton.get_right_button();
					((RedButton)focusedButton).active_selection.visible = true;
				}
				break;
			case Keys.DPAD_CENTER:
			case Keys.BUTTON_A:
				if (focusedButton.isActive()) {
					focusedButton.onKeyTouchDown();
				}
				// handled
				break;
			default:
				keyHandled = false;
				break;
		}
		
		// TODO: still use this to indicate focused button with active animation/effect
		if (keyHandled) {
//			if (xNewIndex != -1 && yNewIndex != -1) {
//				int index = xNewIndex + nCols * yNewIndex;
//				if (badgeButtons.get( index ).badge != null) {
//					xIndex = xNewIndex;
//					yIndex = yNewIndex;
//					hoveringSelection.x = left + xIndex * size + size / 2;
//					hoveringSelection.y = top + yIndex * size + size / 2;
//				}
//			}
		}
		
		return keyHandled;
	}
	
	public boolean onKeyUp( Keys.Key key ) {
		keyHandled = true;
		switch (key.code) {
			case Keys.DPAD_UP:
			case Keys.DPAD_DOWN:
			case Keys.DPAD_LEFT:
			case Keys.DPAD_RIGHT:
				// handled
				break;
			case Keys.DPAD_CENTER:
			case Keys.BUTTON_A:
				if (focusedButton.isActive()) {
					focusedButton.onKeyTouchUp();
					focusedButton.onKeyClick();
				}
				break;
			default:
				keyHandled = false;
				break;
		}
		
		return keyHandled;
	}
}
