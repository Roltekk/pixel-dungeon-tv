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

import com.roltekk.util.FPSText;
import com.watabou.input.Keys;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.effects.BadgeBanner;
import com.watabou.pixeldungeon.effects.Flare;
import com.watabou.pixeldungeon.ui.Archs;
import com.watabou.pixeldungeon.ui.Window;
import com.watabou.pixeldungeon.windows.WndBadge;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Signal;

import java.util.ArrayList;
import java.util.List;

public class BadgesScene extends PixelScene {
	
	private static final String TXT_TITLE = "Your Badges";
	
	private Flare                     hoveringSelection;
	private Signal.Listener<Keys.Key> keyListener;
	private boolean                   keyHandled;
	private ArrayList<BadgeButton> badgeButtons = new ArrayList<BadgeButton>();
	
	private float size;
	private int   nCols, nRows;
	private float left, top;
	private int xIndex, yIndex;

	@Override
	public void create() {
		super.create();
		
		Music.INSTANCE.play( Assets.THEME, true );
		Music.INSTANCE.volume( 1f );
		
		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		int pw = (int)Math.min( w, (PixelDungeon.landscape() ? MIN_WIDTH_L : MIN_WIDTH_P) * 3 ) - 16;
		int ph = (int)Math.min( h, (PixelDungeon.landscape() ? MIN_HEIGHT_L : MIN_HEIGHT_P) * 3 ) - 32;
		
		size = (float)Math.sqrt( pw * ph / 27f );
		nCols = (int)Math.ceil( pw / size );
		nRows = (int)Math.ceil( ph / size );
		size = Math.min( pw / nCols, ph / nRows );
		
		left = (w - size * nCols) / 2;
		top = (h - size * nRows) / 2;
		
		BitmapText title = PixelScene.createText( TXT_TITLE, 9 );
		title.hardlight( Window.TITLE_COLOR );
		title.measure();
		title.x = align( (w - title.width()) / 2 );
		title.y = align( (top - title.baseLine()) / 2 );
		add( title );
		
		xIndex = yIndex = 0;
		
		hoveringSelection = new Flare( 6, 24 );
		hoveringSelection.angularSpeed = 90;
		hoveringSelection.color( 0xFFFFFF, true );
		hoveringSelection.x = left + xIndex * size + size / 2;
		hoveringSelection.y = top + yIndex * size + size / 2;
		add( hoveringSelection );
		
		Badges.loadGlobal();
		
		List<Badges.Badge> badges = Badges.filtered( true );
		hoveringSelection.visible = Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false ) && (badges.size() > 0);

		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nCols; j++) {
				int index = i * nCols + j;
				Badges.Badge b = index < badges.size() ? badges.get( index ) : null;
				BadgeButton button = new BadgeButton( b );
				button.setPos(
						left + j * size + ( size - button.width() ) / 2,
						top + i * size + ( size - button.height() ) / 2 );
				badgeButtons.add( button );
				add( button );
			}
		}
		
//		ExitButton btnExit = new ExitButton();
//		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
//		add( btnExit );
		
		FPSText fpsText = PixelScene.createFPSText( 9 );
		fpsText.measure();
		fpsText.x = Camera.main.width - fpsText.width();
		fpsText.y = ( Camera.main.height - fpsText.height() ) / 2;
		add( fpsText );

		Badges.loadingListener = new Callback() {
			@Override
			public void call() {
				if (Game.scene() == BadgesScene.this) {
					PixelDungeon.switchNoFade( BadgesScene.class );
				}
			}
		};
		
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false )) {
			Keys.event.add(keyListener = new Signal.Listener<Keys.Key>() {
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
			});
		}
		
		fadeIn();
	}
	
	@Override
	public void destroy() {
		Keys.event.remove( keyListener );
		Badges.saveGlobal();
		Badges.loadingListener = null;
		
		super.destroy();
	}
	
	private boolean onKeyDown( Keys.Key key ) {
		// moves highlight indicator and a A/CENTER press confirms selection
		keyHandled = true;
		int xNewIndex, yNewIndex;
		xNewIndex = yNewIndex = -1;
		switch (key.code) {
			case Keys.DPAD_UP:
				yNewIndex = ( yIndex < 1 ) ? nRows - 1 : yIndex - 1;
				xNewIndex = xIndex;
				break;
			case Keys.DPAD_DOWN:
				yNewIndex = ( yIndex > nRows - 2 ) ? 0 : yIndex + 1;
				xNewIndex = xIndex;
				break;
			case Keys.DPAD_LEFT:
				xNewIndex = ( xIndex < 1 ) ? nCols - 1 : xIndex - 1;
				yNewIndex = yIndex;
				break;
			case Keys.DPAD_RIGHT:
				xNewIndex = ( xIndex > nCols - 2 ) ? 0 : xIndex + 1;
				yNewIndex = yIndex;
				break;
			case Keys.DPAD_CENTER:
			case Keys.BUTTON_A:
				// handled
				break;
			default:
				keyHandled = false;
				break;
		}
		
		if (keyHandled) {
			if (xNewIndex != -1 && yNewIndex != -1) {
				int index = xNewIndex + nCols * yNewIndex;
				if (badgeButtons.get( index ).badge != null) {
					xIndex = xNewIndex;
					yIndex = yNewIndex;
					hoveringSelection.x = left + xIndex * size + size / 2;
					hoveringSelection.y = top + yIndex * size + size / 2;
				}
			}
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
				int index = xIndex + nCols * yIndex;
				badgeButtons.get(index).onClick();
				break;
			default:
				keyHandled = false;
				break;
		}
		
		return keyHandled;
	}
	
	@Override
	protected void onBackPressed() {
		PixelDungeon.switchNoFade( TitleScene.class );
	}
	
	private static class BadgeButton extends Button {
		private Badges.Badge badge;
		private Image        icon;
		
		public BadgeButton( Badges.Badge badge ) {
			super();
			
			this.badge = badge;
			active = (badge != null);

			icon = active ? BadgeBanner.image( badge.image ) : new Image( Assets.LOCKED );
			add(icon);

			setSize( icon.width(), icon.height() );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			if (icon != null) {
				icon.x = align( x + (width - icon.width()) / 2 );
				icon.y = align( y + (height - icon.height()) / 2 );
			}
		}
		
		@Override
		public void update() {
			super.update();
			
			if (Random.Float() < Game.elapsed * 0.1) {
				BadgeBanner.highlight( icon, badge.image );
			}
		}
		
		@Override
		protected void onTouchDown() {
			icon.brightness( 1.5f );
		}
		
		@Override
		protected void onTouchUp() {
			icon.resetColor();
		}
		
		@Override
		protected void onClick() {
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
			Game.scene().add( new WndBadge( badge ) );
		}
	}
}
