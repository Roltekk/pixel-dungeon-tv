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

import com.watabou.noosa.NinePatch;

public class HotAreaUI extends NinePatch {
	
	public HotAreaUI(Object tx, int x, int y, int w, int h, int margin) {
		super( tx, x, y, w, h, margin, margin, margin, margin );
	}
	
	@Override
	public void draw() {
		if (Debug.DEBUG_INFO && Debug.DRAW_TOUCH_AREAS) {
			super.draw();
		}
	}
}
