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

package com.watabou.noosa;

import com.roltekk.util.DebugUI;
import com.roltekk.util.HotAreaUI;
import com.watabou.input.Keys;
import com.watabou.input.Touchscreen;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.utils.Signal;

public class TouchArea extends Visual implements Signal.Listener<Touchscreen.Touch> {
	
	// Its target can be toucharea itself
	public Visual target;
	public HotAreaUI debug_outline = DebugUI.get( DebugUI.UIType.HOTAREA );
	
	protected Touchscreen.Touch touch = null;
	
	private Signal.Listener<Keys.Key> keyListener = new Signal.Listener<Keys.Key>() {
		@Override
		public void onSignal(Keys.Key key) {
			final boolean handled;

			if (key.pressed) {
				handled = onKeyDown(key);
			} else {
				handled = onKeyUp(key);
			}

			if (handled) {
				Keys.event.cancel();
			}
		}
	};

	public TouchArea( Visual target ) {
		super( 0, 0, 0, 0 );
		this.target = target;
		
		Touchscreen.event.add( this );
		Keys.event.add( keyListener );
	}
	
	public TouchArea( float x, float y, float width, float height ) {
		super( x, y, width, height );
		this.target = this;
		
		Touchscreen.event.add( this );
		Keys.event.add( keyListener );
	}

	@Override
	public void onSignal( Touch touch ) {
		
		if (!isActive()) {
			return;
		}
		
		boolean hit = touch != null && target.overlapsScreenPoint( (int)touch.start.x, (int)touch.start.y );
		
		if (hit) {

			Touchscreen.event.cancel();
			
			if (touch.down) {
				
				if (this.touch == null) {
					this.touch = touch;
				}
				onTouchDown( touch );
				
			} else {
				
				onTouchUp( touch );
				
				if (this.touch == touch) {
					this.touch = null;
					onClick( touch );
				}

			}
			
		} else {
			
			if (touch == null && this.touch != null) {
				onDrag( this.touch );
			}
			
			else if (this.touch != null && touch != null && !touch.down) {
				onTouchUp( touch );
				this.touch = null;
			}
			
		}
	}
	
	protected boolean onKeyDown(Keys.Key key) {
		return false;
	}

	protected boolean onKeyUp(Keys.Key key) {
		return false;
	}
	
	protected void onTouchDown(Touch touch) { }
	
	protected void onTouchUp(Touch touch) { }
	
	protected void onClick(Touch touch) { }
	
	protected void onDrag(Touch touch) { }
	
	public void reset() {
		touch = null;
	}
	
	@Override
	public void destroy() {
		Touchscreen.event.remove( this );
		Keys.event.remove( keyListener );
		super.destroy();
	}
	
	public void resizeDebugOutline() {
		debug_outline.x = target.x;
		debug_outline.y = target.y;
		debug_outline.size( target.width, target.height );
	}
}
