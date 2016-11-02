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
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.ui.CheckBox;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Toolbar;
import com.watabou.pixeldungeon.ui.Window;

public class WndSettings extends Window {
	
	private static final String TXT_ZOOM_IN      = "+";
	private static final String TXT_ZOOM_OUT     = "-";
	private static final String TXT_ZOOM_DEFAULT = "Default Zoom";
	private static final String TXT_SCALE_UP     = "Scale up UI";
	private static final String TXT_IMMERSIVE    = "Immersive mode";
	private static final String TXT_MUSIC        = "Music";
	private static final String TXT_SOUND        = "Sound FX";
	private static final String TXT_BRIGHTNESS   = "Brightness";
	private static final String TXT_QUICKSLOT    = "Second quickslot";
	private static final String TXT_DEBUG_INFO   = "Debug Info";
	
	private static final int WIDTH		= 112;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP 		= 2;
	
	private RedButton btnZoomOut;
	private RedButton btnDefaultZoom;
	private RedButton btnZoomIn;
	private RedButton btnDebugInfo;

	private CheckBox btnScaleUp;
	private CheckBox btnImmersive = null;
	private CheckBox btnMusic;
	private CheckBox btnSound;
	private CheckBox btnBrightness;
	private CheckBox btnQuickslot;

//	private Flare                     hoveringSelection;
	private boolean                   keyHandled;
	private Button                    focusedButton;
	
	public WndSettings( boolean inGame ) {
		super();

		// game state dependant UI
		if (inGame) {
			int w = BTN_HEIGHT;
			
			btnZoomOut = new RedButton( TXT_ZOOM_OUT ) {
				@Override
				protected void onClick() {
					super.onClick();
					zoom( Camera.main.zoom - 1 );
				}
			};
			add( btnZoomOut.setRect( 0, 0, w, BTN_HEIGHT) );
			
			btnZoomIn = new RedButton( TXT_ZOOM_IN ) {
				@Override
				protected void onClick() {
					super.onClick();
					zoom( Camera.main.zoom + 1 );
				}
			};
			add( btnZoomIn.setRect( WIDTH - w, 0, w, BTN_HEIGHT) );
			
			btnDefaultZoom = new RedButton( TXT_ZOOM_DEFAULT ) {
				@Override
				protected void onClick() {
					super.onClick();
					zoom( PixelScene.defaultZoom );
				}
			};
			add( btnDefaultZoom.setRect( btnZoomOut.right(), 0, WIDTH - btnZoomIn.width() - btnZoomOut.width(), BTN_HEIGHT ));

			updateEnabled();
			
		} else {
			
			btnScaleUp = new CheckBox( TXT_SCALE_UP ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.scaleUp( checked() );
				}
			};
			btnScaleUp.setRect( 0, 0, WIDTH, BTN_HEIGHT );
			btnScaleUp.checked( PixelDungeon.scaleUp() );
			add( btnScaleUp );

			if (android.os.Build.VERSION.SDK_INT >= 19 && !Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false ) ) {
				btnImmersive = new CheckBox( TXT_IMMERSIVE ) {
					@Override
					protected void onClick() {
						super.onClick();
						PixelDungeon.immerse( checked() );
					}
				};
				btnImmersive.setRect( 0, btnScaleUp.bottom() + GAP, WIDTH, BTN_HEIGHT );
				btnImmersive.checked( PixelDungeon.immersed() );
				add( btnImmersive );
			}
		}
		
		// always present UI
		btnMusic = new CheckBox( TXT_MUSIC ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.music( checked() );
			}
		};
		btnMusic.setRect( 0, (btnImmersive != null ? btnImmersive.bottom() : BTN_HEIGHT) + GAP, WIDTH, BTN_HEIGHT );
		btnMusic.checked( PixelDungeon.music() );
		add( btnMusic );
		
		btnSound = new CheckBox( TXT_SOUND ) {
			@Override
			protected void onClick() {
				super.onClick();
				PixelDungeon.soundFx( checked() );
				Sample.INSTANCE.play( Assets.SND_CLICK );
			}
		};
		btnSound.setRect( 0, btnMusic.bottom() + GAP, WIDTH, BTN_HEIGHT );
		btnSound.checked( PixelDungeon.soundFx() );
		add( btnSound );

		btnDebugInfo = new RedButton( TXT_DEBUG_INFO ) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
				Game.scene().add( new WndDebug() );
			}
		};
		
		// game state dependant UI
		if (inGame) {
			
			btnBrightness = new CheckBox( TXT_BRIGHTNESS ) {
				@Override
				protected void onClick() {
					super.onClick();
					PixelDungeon.brightness( checked() );
				}
			};
			btnBrightness.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnBrightness.checked( PixelDungeon.brightness() );
			add( btnBrightness );
			
			btnQuickslot = new CheckBox( TXT_QUICKSLOT ) {
				@Override
				protected void onClick() {
					super.onClick();
					Toolbar.secondQuickslot( checked() );
				}
			};
			btnQuickslot.setRect( 0, btnBrightness.bottom() + GAP, WIDTH, BTN_HEIGHT );
			btnQuickslot.checked( Toolbar.secondQuickslot() );
			add( btnQuickslot );

			btnDebugInfo.setRect( 0, btnQuickslot.bottom() + GAP, WIDTH, BTN_HEIGHT );
		} else {
			btnDebugInfo.setRect( 0, btnSound.bottom() + GAP, WIDTH, BTN_HEIGHT );
		}

		add( btnDebugInfo );
		
		resize( WIDTH, (int) btnDebugInfo.bottom() );
		
		// set button navigation associations
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false )) {
			if (inGame) {
				btnZoomOut.set_right_button( btnDefaultZoom );
				btnDefaultZoom.set_left_button( btnZoomOut );
				btnDefaultZoom.set_right_button( btnZoomIn );
				btnZoomIn.set_left_button( btnDefaultZoom );
				
				btnZoomOut.set_down_button( btnMusic );
				btnDefaultZoom.set_down_button( btnMusic );
				btnZoomIn.set_down_button( btnMusic );
				
				btnMusic.set_up_button( btnDefaultZoom );
				btnMusic.set_down_button( btnSound );
				
				btnSound.set_up_button( btnMusic );
				btnSound.set_down_button( btnBrightness );
				
				btnBrightness.set_up_button( btnSound );
				btnBrightness.set_down_button( btnQuickslot );
				
				btnQuickslot.set_up_button( btnBrightness );
				btnQuickslot.set_down_button( btnDebugInfo );
				
				btnDebugInfo.set_up_button( btnQuickslot );
			} else {
				if (btnImmersive != null) {
					btnScaleUp.set_down_button( btnImmersive );
					
					btnImmersive.set_up_button( btnScaleUp );
					btnImmersive.set_down_button( btnMusic );
					btnMusic.set_up_button( btnImmersive );
					
				} else {
					btnScaleUp.set_down_button( btnMusic );
					btnMusic.set_up_button( btnScaleUp );
				}
				
				btnMusic.set_down_button( btnSound );
				
				btnSound.set_up_button( btnMusic );
				btnSound.set_down_button( btnDebugInfo );
				
				btnDebugInfo.set_up_button( btnSound );
			}
			
			focusedButton = btnDebugInfo;
			((RedButton)focusedButton).active_selection.visible = true;
			
			Keys.event.add( this );
		}
	}
	
	private void zoom( float value ) {
		Camera.main.zoom( value );
		PixelDungeon.zoom( (int)(value - PixelScene.defaultZoom) );

		updateEnabled();
	}
	
	private void updateEnabled() {
		float zoom = Camera.main.zoom;
		btnZoomIn.enable( zoom < PixelScene.maxZoom );
		btnZoomOut.enable( zoom > PixelScene.minZoom );
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
