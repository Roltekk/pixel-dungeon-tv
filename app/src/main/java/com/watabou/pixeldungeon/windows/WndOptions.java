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
package com.watabou.pixeldungeon.windows;

import com.watabou.input.Keys;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

import java.util.ArrayList;

public class WndOptions extends Window {

	private static final int WIDTH			= 120;
	private static final int MARGIN 		= 2;
	private static final int BUTTON_HEIGHT	= 20;
	
	private ArrayList<Button> buttons = new ArrayList<Button>();
	private boolean keyHandled;
	private Button  focusedButton;
	
	public WndOptions( String title, String message, String... options ) {
		super();
		
		BitmapTextMultiline tfTitle = PixelScene.createMultiline( title, 9 );
		tfTitle.hardlight( TITLE_COLOR );
		tfTitle.x = tfTitle.y = MARGIN;
		tfTitle.maxWidth = WIDTH - MARGIN * 2;
		tfTitle.measure();
		add( tfTitle );
		
		BitmapTextMultiline tfMesage = PixelScene.createMultiline( message, 8 );
		tfMesage.maxWidth = WIDTH - MARGIN * 2;
		tfMesage.measure();
		tfMesage.x = MARGIN;
		tfMesage.y = tfTitle.y + tfTitle.height() + MARGIN;
		add( tfMesage );
		
		float pos = tfMesage.y + tfMesage.height() + MARGIN;
		
		for (int i=0; i < options.length; i++) {
			final int index = i;
			RedButton btn = new RedButton( options[i] ) {
				@Override
				protected void onClick() {
					super.onClick();
					hide();
					onSelect( index );
				}
			};
			btn.setRect( MARGIN, pos, WIDTH - MARGIN * 2, BUTTON_HEIGHT );
			add( btn );
			buttons.add( btn );
			
			pos += BUTTON_HEIGHT + MARGIN;
		}
		
		resize( WIDTH, (int)pos );

		// set button navigation associations
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false )) {
			for (int i = 0; i < options.length; i++) {
				if (i > 0) {
					buttons.get( i ).set_up_button( buttons.get( i - 1 ) );
				}
				
				if (i < options.length - 1) {
					buttons.get( i ).set_down_button( buttons.get( i + 1 ) );
				}
			}
			
			focusedButton = buttons.get( 0 );
			((RedButton)focusedButton).active_selection.visible = true;
			
			Keys.event.add( this );
		}
	}
	
	protected void onSelect( int index ) {}
	
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
