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
package com.roltekk.util;

import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Game;

public class FPSText extends BitmapTextMultiline {
	private static final float DELAY = 1.0f;
	private float time;

	public FPSText( Font font ) {
		super( "FPS\n999", font );
	}
	
	@Override
	public void update() {
		super.update();
		
		if (( time -= Game.elapsed ) <= 0) {
			this.text = "FPS\n" + String.valueOf( (int) ( 1.0f / Game.elapsed ) );
			time = DELAY;
			this.dirty = true;
		}
	}
	
	@Override
	public void draw() {
		if (Debug.DEBUG_INFO && Debug.DRAW_FPS) {
			super.draw();
		}
	}
}
