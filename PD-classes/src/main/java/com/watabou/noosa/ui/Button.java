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

package com.watabou.noosa.ui;

import android.util.Log;

import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.Game;
import com.watabou.noosa.TouchArea;

public class Button extends Component {
	private static final String TAG = "Button";
	public static float longClick = 1f;
	
	protected TouchArea hotArea;
	
	protected boolean pressed;
	protected float pressTime;
	
	protected boolean processed;
	
	protected Button left_button = null;
	protected Button right_button = null;
	protected Button up_button = null;
	protected Button down_button = null;
	
	public void set_down_button( Button down_button ) { this.down_button = down_button; }
	
	public void set_left_button( Button left_button ) { this.left_button = left_button; }
	
	public void set_right_button( Button right_button ) { this.right_button = right_button; }
	
	public void set_up_button( Button up_button ) { this.up_button = up_button; }
	
	public Button get_up_button() { return up_button; }
	
	public Button get_right_button() { return right_button; }
	
	public Button get_left_button() { return left_button; }
	
	public Button get_down_button() { return down_button; }
	
	@Override
	protected void createChildren() {
		hotArea = new TouchArea( 0, 0, 0, 0 ) {
			@Override
			protected void onTouchDown(Touch touch) {
				pressed = true;
				pressTime = 0;
				processed = false;
				Button.this.onTouchDown();
			}
			@Override
			protected void onTouchUp(Touch touch) {
				pressed = false;
				Button.this.onTouchUp();
			}
			@Override
			protected void onClick( Touch touch ) {
				if (!processed) {
					Button.this.onClick();
				}
			}
		};
		add( hotArea );
		add( hotArea.debug_outline );
	}
	
	@Override
	public void update() {
		super.update();
		
		hotArea.active = visible;
		
		if (pressed) {
			if ((pressTime += Game.elapsed) >= longClick) {
				pressed = false;
				if (onLongClick()) {

					hotArea.reset();
					processed = true;
					onTouchUp();
					
					Game.vibrate( 50 );
				}
			}
		}
	}

    protected void onTouchDown() {}
	protected void onTouchUp() {}
	protected void onClick() {}
	
	public void onKeyTouchDown() { onTouchDown(); }
	public void onKeyTouchUp() { onTouchUp(); }
	public void onKeyClick() { onClick(); }
	
	protected boolean onLongClick() {
		return false;
	}
	
	@Override
	protected void layout() {
		hotArea.target.x = x;
		hotArea.target.y = y;
		hotArea.target.width = width;
		hotArea.target.height = height;
		hotArea.resizeDebugOutline();
	}
}
