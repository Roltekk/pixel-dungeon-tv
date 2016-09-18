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
package com.watabou.pixeldungeon.scenes;

import com.watabou.input.Keys;
import com.watabou.input.Touchscreen.Touch;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.TouchArea;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Signal;

public class CellSelector extends TouchArea {
	public Listener listener = null;
	public boolean enabled;
	private boolean pinching = false;
	private boolean dragging = false;
	private PointF lastPos = new PointF();
	private Touch another;
	private float startZoom;
	private float startSpan;
	private float dragThreshold;
	private boolean handled;
	private int x, y;
	private Signal.Listener<Keys.Key> keyListener;
	private boolean higlightVisible;
	private CellSelection selectedCell = new CellSelection();

	public CellSelector( DungeonTilemap map ) {
		super( map );
		camera = map.camera();
		x = 0; y = 0;
		higlightVisible = false;
		selectedCell.size( map.SIZE, map.SIZE );
		
		dragThreshold = PixelScene.defaultZoom * DungeonTilemap.SIZE / 2;
		Keys.event.add( keyListener = new Signal.Listener<Keys.Key>() {
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
				}
			}
		});
	}
	
	@Override
	protected void onClick( Touch touch ) {
		if (dragging) {
			dragging = false;
		} else {
			select( ((DungeonTilemap)target).screenToTile( 
				(int)touch.current.x, 
				(int)touch.current.y ) );
		}
	}
	
	private boolean onKeyDown(Keys.Key key) {
		// TODO: instead of instantly changing position, could move a highlight square (not beyond fog of war) and a A press confirms
		// would mean that x y would have to accumulate until confirm press
		handled = true;
		switch (key.code) {
			case Keys.DPAD_UP:
				y--;
				higlightVisible = true;
				break;
			case Keys.DPAD_DOWN:
				y++;
				higlightVisible = true;
				break;
			case Keys.DPAD_LEFT:
				x--;
				higlightVisible = true;
				break;
			case Keys.DPAD_RIGHT:
				x++;
				higlightVisible = true;
				break;
			case Keys.BUTTON_A:
				Point point = DungeonTilemap.tileToPoint( Dungeon.hero.pos );
				point.x += x;
				point.y += y;
				select( DungeonTilemap.pointToTile( point ) );
				x = 0; y = 0;
				higlightVisible = false;
				// TODO:
				break;
//			case Keys.BUTTON_B:
//				// NOTE: this would override the "Back"/"Cancel" action handled in Scene class
//				break;
			case Keys.BUTTON_X:
				// TODO:
				break;
			case Keys.BUTTON_Y:
				// TODO:
				break;
			case Keys.BUTTON_L1:
				// TODO:
				break;
			case Keys.BUTTON_R1:
				// TODO:
				break;
			case Keys.BUTTON_L2:
				// TODO:
				break;
			case Keys.BUTTON_R2:
				// TODO:
				break;
			case Keys.BUTTON_THUMBL:
				// TODO:
				break;
			case Keys.BUTTON_THUMBR:
				// TODO:
				break;
			default:
				handled = false;
				break;
		}

		if (handled) {
			Point point = DungeonTilemap.tileToPoint( Dungeon.hero.pos);
			point.x += x;
			point.y += y;
			selectedCell.place( point );
		}

		return handled;
	}
	
	public boolean onKeyUp(Keys.Key key) {
		handled = true;
		return handled;
	}
	
	public void select( int cell ) {
		if (enabled && listener != null && cell != -1) {
			listener.onSelect( cell );
			GameScene.ready();
		} else {
			GameScene.cancel();
		}
	}

	@Override
	protected void onTouchDown( Touch t ) {
		if (t != touch && another == null) {
					
			if (!touch.down) {
				touch = t;
				onTouchDown( t );
				return;
			}
			
			pinching = true;
			
			another = t;
			startSpan = PointF.distance( touch.current, another.current );
			startZoom = camera.zoom;

			dragging = false;
		}
	}
	
	@Override
	protected void onTouchUp( Touch t ) {
		if (pinching && (t == touch || t == another)) {

			pinching = false;
			
			int zoom = Math.round( camera.zoom );
			camera.zoom( zoom );
			PixelDungeon.zoom( (int)(zoom - PixelScene.defaultZoom) );

			dragging = true;
			if (t == touch) {
				touch = another;
			}
			another = null;
			lastPos.set( touch.current );
		}
	}	

	// NOTE: dragging while pinching zooms in and out. dragging otherwise moves camera (to view other parts of map)
	// maybe use right analog stick for zoom/move
	@Override
	protected void onDrag( Touch t ) {
		camera.target = null;
		
		if (pinching) {
			float curSpan = PointF.distance( touch.current, another.current );
			camera.zoom( GameMath.gate(
					PixelScene.minZoom,
					startZoom * curSpan / startSpan,
					PixelScene.maxZoom ) );
		} else {
			if (!dragging && PointF.distance( t.current, t.start ) > dragThreshold) {
				dragging = true;
				lastPos.set( t.current );
			} else if (dragging) {
				camera.scroll.offset( PointF.diff( lastPos, t.current ).invScale( camera.zoom ) );
				lastPos.set( t.current );
			}
		}
	}
	
	@Override
	public void destroy() {
		Keys.event.remove( keyListener );
		super.destroy();
	}
	
	public void cancel() {
		if (listener != null) {
			listener.onSelect( null );
		}
		
		GameScene.ready();
	}
	
	public interface Listener {
		void onSelect( Integer cell );
		String prompt();
	}
	
	@Override
	public void draw() {
		if (higlightVisible) {
			selectedCell.draw();
		}
	}
	
	// UI
	public class CellSelection extends NinePatch {
		public static final float SIZE = 16;
		public CellSelection() {
			super( Assets.CELL_SELECT, 1 );
		}
		
		
		public void place( Point point ) {
			point( worldToCamera( pointToTile( point ) ) );
		}
		
		public PointF worldToCamera( int cell ) {
			final int csize = DungeonTilemap.SIZE;
			
			return new PointF(
					cell % Level.WIDTH * csize + ( csize - SIZE ) * 0.5f,
					cell / Level.WIDTH * csize + ( csize - SIZE ) * 0.5f
			);
		}
		
		public int pointToTile( Point point ) {
			return point.y * Level.WIDTH + point.x;
		}
	}
}
