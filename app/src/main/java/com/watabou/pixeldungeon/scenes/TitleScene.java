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

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

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
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.effects.BannerSprites;
import com.watabou.pixeldungeon.effects.Fireball;
import com.watabou.pixeldungeon.effects.Flare;
import com.watabou.pixeldungeon.ui.Archs;
import com.watabou.pixeldungeon.ui.ExitButton;
import com.watabou.pixeldungeon.ui.PrefsButton;
import com.watabou.pixeldungeon.windows.WndOptions;
import com.watabou.pixeldungeon.windows.WndSettings;
import com.watabou.utils.Signal;

import java.util.ArrayList;

public class TitleScene extends PixelScene {

	private static final String TXT_PLAY		= "Play";
	private static final String TXT_HIGHSCORES	= "Rankings";
	private static final String TXT_BADGES		= "Badges";
	private static final String TXT_ABOUT		= "About";
	private static final String TXT_SETTINGS	= "Settings";
	
	private static final String TXT_QUIT_GAME     = "Quit game";
	private static final String TXT_R_U_SURE_QUIT = "Are you sure you want to quit?";
	private static final String TXT_YES           = "Yes";
	private static final String TXT_NO            = "No";
	
	private Flare                     hoveringSelection;
	private Signal.Listener<Keys.Key> keyListener;
	private boolean                   keyHandled;
	private ArrayList<DashboardItem> dashboardItems = new ArrayList<DashboardItem>();
	
	private float left, top;
	private int xIndex;
	
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
		
		Image title = BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON );
		add( title );
		
		float height = title.height + 
			(PixelDungeon.landscape() ? DashboardItem.SIZE : DashboardItem.SIZE * 2.5f);
		
		title.x = (w - title.width()) / 2;
		title.y = (h - height) / 2;
		
		placeTorch( title.x + 18, title.y + 20 );
		placeTorch( title.x + title.width - 18, title.y + 20 );
		
		Image signs = new Image( BannerSprites.get( BannerSprites.Type.PIXEL_DUNGEON_SIGNS ) ) {
			private float time = 0;
			@Override
			public void update() {
				super.update();
				am = (float)Math.sin( -(time += Game.elapsed) );
			}
			@Override
			public void draw() {
				GLES20.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE );
				super.draw();
				GLES20.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
			}
		};
		signs.x = title.x;
		signs.y = title.y;
		add( signs );
		
		hoveringSelection = new Flare( 6, 44 );
		hoveringSelection.angularSpeed = 30;
		hoveringSelection.color( 0xFFFFFF, true );
		add( hoveringSelection );

		DashboardItem btnPlay = new DashboardItem( TXT_PLAY, 0 ) {
			@Override
			protected void onClick() {
				PixelDungeon.switchNoFade( StartScene.class );
			}
		};
		dashboardItems.add( btnPlay );
		add( btnPlay );
		
		DashboardItem btnHighscores = new DashboardItem( TXT_HIGHSCORES, 2 ) {
			@Override
			protected void onClick() {
				PixelDungeon.switchNoFade( RankingsScene.class );
			}
		};
		dashboardItems.add( btnHighscores );
		add( btnHighscores );

		DashboardItem btnBadges = new DashboardItem( TXT_BADGES, 3 ) {
			@Override
			protected void onClick() {
				PixelDungeon.switchNoFade( BadgesScene.class );
			}
		};
		dashboardItems.add( btnBadges );
		add( btnBadges );
		
		DashboardItem btnAbout = new DashboardItem( TXT_ABOUT, 1 ) {
			@Override
			protected void onClick() {
				PixelDungeon.switchNoFade( AboutScene.class );
			}
		};
		dashboardItems.add( btnAbout );
		add( btnAbout );
		
		DashboardItem btnSettings = new DashboardItem( TXT_SETTINGS, 4 ) {
			@Override
			protected void onClick() {
				parent.add( new WndSettings( false ) );
			}
		};
		dashboardItems.add( btnSettings );
		add( btnSettings );
		
		if (PixelDungeon.landscape()) {
			top = (h + height) / 2 - DashboardItem.SIZE;
			btnBadges		.setPos( ( w - btnBadges.width() ) / 2, top ); // center button
			btnHighscores	.setPos( btnBadges.left() - btnHighscores.width(), top );
			btnPlay			.setPos( btnHighscores.left() - btnPlay.width(), top );
			btnAbout		.setPos( btnBadges.right(), top );
			btnSettings		.setPos( btnAbout.right(), top );
			left = btnPlay.left();
		} else {
			btnBadges		.setPos( w / 2 - btnBadges.width(), (h + height) / 2 - (DashboardItem.SIZE * 1.5f) );
			btnAbout		.setPos( w / 2, (h + height) / 2 - (DashboardItem.SIZE * 1.5f) );
			btnPlay			.setPos( w / 2 - btnPlay.width(), btnAbout.top() - DashboardItem.SIZE );
			btnHighscores	.setPos( w / 2, btnPlay.top() );
			btnSettings		.setPos( ( w - btnSettings.width() ) / 2, btnAbout.bottom());
		}
		
		hoveringSelection.x = btnPlay.centerImageX();
		hoveringSelection.y = btnPlay.centerImageY();
		hoveringSelection.visible = Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false );
		
		BitmapText version = new BitmapText( "v " + Game.version, font1x );
		version.measure();
		version.hardlight( 0x888888 );
		version.x = w - version.width();
		version.y = h - version.height();
		add( version );
		
//		ExitButton btnExit = new ExitButton();
//		btnExit.setPos( w - btnExit.width(), 0 );
//		add( btnExit );

		FPSText fpsText = PixelScene.createFPSText( 9 );
		fpsText.measure();
		fpsText.x = w - fpsText.width();
		fpsText.y = ( Camera.main.height - fpsText.height() ) / 2;
		add( fpsText );
		
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
		Keys.event.remove(keyListener);
		super.destroy();
	}
	
	private boolean onKeyDown( Keys.Key key ) {
		// moves highlight indicator and a A/CENTER press confirms selection
		keyHandled = true;
		int xNewIndex;
		xNewIndex = -1;
		switch (key.code) {
			case Keys.DPAD_LEFT:
				xNewIndex = ( xIndex < 1 ) ? dashboardItems.size() - 1 : xIndex - 1;
				break;
			case Keys.DPAD_RIGHT:
				xNewIndex = ( xIndex > dashboardItems.size() - 2 ) ? 0 : xIndex + 1;
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
			if (xNewIndex != -1) {
				xIndex = xNewIndex;
				hoveringSelection.x = left + (xIndex * DashboardItem.SIZE) + (DashboardItem.SIZE / 2);
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
				dashboardItems.get(xIndex).onClick();
				break;
			default:
				keyHandled = false;
				break;
		}
		
		return keyHandled;
	}
	
	@Override
	protected void onBackPressed() {
		// show quit confirm window
		this.add(
				new WndOptions( TXT_QUIT_GAME, TXT_R_U_SURE_QUIT, TXT_NO, TXT_YES ) {
					@Override
					protected void onSelect( int index ) {
						// quit game if yes selected
						if (index == 1) {
							Game.instance.finish();
						}
					}
				}
		);
	}
	
	private void placeTorch( float x, float y ) {
		Fireball fb = new Fireball();
		fb.setPos( x, y );
		add( fb );
	}
	
	private static class DashboardItem extends Button {
		
		public static final float SIZE	= 48;
		
		private static final int IMAGE_SIZE	= 32;
		
		private Image image;
		private BitmapText label;
		
		public DashboardItem( String text, int index ) {
			super();
			
			image.frame( image.texture.uvRect( index * IMAGE_SIZE, 0, (index + 1) * IMAGE_SIZE, IMAGE_SIZE ) );
			this.label.text( text );
			this.label.measure();
			
			setSize( SIZE, SIZE );
		}
		
		@Override
		protected void createChildren() {
			image = new Image( Assets.DASHBOARD );
			add( image );
			
			label = createText( 9 );
			add( label );
			
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			image.x = align( x + (width - image.width()) / 2 );
			image.y = align( y );
			
			label.x = align( x + (width - label.width()) / 2 );
			label.y = align( image.y + image.height() + 2 );
		}
		
		@Override
		protected void onTouchDown() {
			image.brightness( 1.5f );
		}
		
		@Override
		protected void onTouchUp() {
			image.resetColor();
		}
		
		@Override
		protected void onClick() {
			Sample.INSTANCE.play( Assets.SND_CLICK, 1, 1, 0.8f );
			super.onClick();
		}
		
		public float centerImageX() {
			return image.x + image.width / 2;
		}
		
		public float centerImageY() {
			return image.y + image.height / 2;
		}
	}
}
