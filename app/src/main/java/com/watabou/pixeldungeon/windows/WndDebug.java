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
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.Window;

public class WndDebug extends Window {
	private static final String TXT_SHOW_DEBUG_INFO = "Show Debug Info";
	private static final String TXT_SHOW_TOUCHAREAS = "Touch Areas";
	private static final String TXT_SHOW_FPS        = "FPS";
	
	private static final int WIDTH = 112;
	private static final int BTN_HEIGHT = 20;
	private static final int GAP = 2;
	
	public WndDebug() {
		super();
		
		CheckBox btnDebugInfo = new CheckBox( TXT_SHOW_DEBUG_INFO ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.debugInfo( checked() );
				Debug.DEBUG_INFO = checked();
			}
		};
		btnDebugInfo.setRect( 0, 0, WIDTH, BTN_HEIGHT );
		btnDebugInfo.checked( PixelDungeon.debugInfo() );
		add( btnDebugInfo );

		CheckBox btnShowTouchAreas = new CheckBox( TXT_SHOW_TOUCHAREAS ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.debugShowTouchAreas( checked() );
				Debug.DRAW_TOUCH_AREAS = checked();
			}
		};
		btnShowTouchAreas.setRect( 0, btnDebugInfo.bottom() + GAP, WIDTH, BTN_HEIGHT );
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
	}
}
