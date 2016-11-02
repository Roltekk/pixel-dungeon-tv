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

import java.io.IOException;

import com.watabou.input.Keys;
import com.watabou.noosa.Game;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.PixelDungeon;
import com.watabou.pixeldungeon.Preferences;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.pixeldungeon.scenes.InterlevelScene;
import com.watabou.pixeldungeon.scenes.RankingsScene;
import com.watabou.pixeldungeon.scenes.TitleScene;
import com.watabou.pixeldungeon.ui.Icons;
import com.watabou.pixeldungeon.ui.RedButton;
import com.watabou.pixeldungeon.ui.Window;

public class WndGame extends Window {
	
	private static final String TXT_SETTINGS	= "Settings";
	private static final String TXT_CHALLENGES	= "Challenges";
	private static final String TXT_RANKINGS	= "Rankings";
	private static final String TXT_START		= "Start New Game";
	private static final String TXT_MENU		= "Main Menu";
	private static final String TXT_EXIT		= "Exit Game";
	private static final String TXT_RETURN		= "Return to Game";
	
	private static final String TXT_QUIT_GAME     = "Quit game";
	private static final String TXT_R_U_SURE_QUIT = "Are you sure you want to quit?";
	private static final String TXT_YES           = "Yes";
	private static final String TXT_NO            = "No";
	
	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 20;
	private static final int GAP		= 2;
	
	private int pos;
	
	private RedButton btnSettings;
	private RedButton btnMainMenu;
	private RedButton btnExitGame;
	private RedButton btnResumeGame;
	
	private RedButton btnChallenges;
	private RedButton btnStart;
	private RedButton btnRankings;

	private boolean keyHandled;
	private Button  focusedButton;
	
	public WndGame() {
		
		super();
		
		addButton( btnSettings = new RedButton( TXT_SETTINGS ) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
				GameScene.show( new WndSettings( true ) );
			}
		} );
		
		if (Dungeon.challenges > 0) {
			addButton( btnChallenges = new RedButton( TXT_CHALLENGES ) {
				@Override
				protected void onClick() {
					super.onClick();
					hide();
					GameScene.show( new WndChallenges( Dungeon.challenges, false ) );
				}
			} );
		}
		
		if (!Dungeon.hero.isAlive()) {
			addButton( btnStart = new RedButton( TXT_START ) {
				@Override
				protected void onClick() {
					super.onClick();
					Dungeon.hero = null;
					PixelDungeon.challenges( Dungeon.challenges );
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					InterlevelScene.noStory = true;
					Game.switchScene( InterlevelScene.class );
				}
			} );
			btnStart.icon( Icons.get( Dungeon.hero.heroClass ) );
			
			addButton( btnRankings = new RedButton( TXT_RANKINGS ) {
				@Override
				protected void onClick() {
					super.onClick();
					InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
					Game.switchScene( RankingsScene.class );
				}
			} );
		}
		
		addButtons(
				btnMainMenu = new RedButton( TXT_MENU ) {
					@Override
					protected void onClick() {
						super.onClick();
						try {
							Dungeon.saveAll();
						} catch (IOException e) {
							// Do nothing
						}
						Game.switchScene( TitleScene.class );
					}
				},
				
				btnExitGame = new RedButton( TXT_EXIT ) {
					@Override
					protected void onClick() {
						super.onClick();
						// show quit confirm window
						hide();
						Game.scene().add(
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
				} );

		addButton( btnResumeGame = new RedButton( TXT_RETURN ) {
			@Override
			protected void onClick() {
				super.onClick();
				hide();
			}
		} );
		
		resize( WIDTH, pos );
		
		// set button navigation associations
		if (Preferences.INSTANCE.getBoolean( Preferences.KEY_TELEVISION, false )) {
			if (Dungeon.challenges > 0) {
				btnSettings.set_down_button( btnChallenges );
				btnChallenges.set_up_button( btnSettings );
				
				if (!Dungeon.hero.isAlive()) {
					btnChallenges.set_down_button( btnStart );
					btnStart.set_up_button( btnChallenges );
					
					btnStart.set_down_button( btnRankings );
					btnRankings.set_up_button( btnStart );
					
					btnRankings.set_down_button( btnMainMenu );
					btnMainMenu.set_up_button( btnRankings );
					btnExitGame.set_up_button( btnRankings );
				} else {
					btnChallenges.set_down_button( btnMainMenu );
					btnMainMenu.set_up_button( btnChallenges );
					btnExitGame.set_up_button( btnChallenges );
				}
			} else {
				if (!Dungeon.hero.isAlive()) {
					btnSettings.set_down_button( btnStart );
					btnStart.set_up_button( btnSettings );
					
					btnStart.set_down_button( btnRankings );
					btnRankings.set_up_button( btnStart );
					
					btnRankings.set_down_button( btnMainMenu );
					btnMainMenu.set_up_button( btnRankings );
					btnExitGame.set_up_button( btnRankings );
				} else {
					btnSettings.set_down_button( btnMainMenu );
					btnMainMenu.set_up_button( btnSettings );
					btnExitGame.set_up_button( btnSettings );
				}
			}
			
			btnMainMenu.set_right_button( btnExitGame );
			btnExitGame.set_left_button( btnMainMenu );
			
			btnMainMenu.set_down_button( btnResumeGame );
			btnExitGame.set_down_button( btnResumeGame );

			btnResumeGame.set_up_button( btnMainMenu );		

			focusedButton = btnResumeGame;
			((RedButton)focusedButton).active_selection.visible = true;
			
			Keys.event.add( this );
		} 
	}
	
	private void addButton( RedButton btn ) {
		add( btn );
		btn.setRect( 0, pos > 0 ? pos += GAP : 0, WIDTH, BTN_HEIGHT );
		pos += BTN_HEIGHT;
	}
	
	private void addButtons( RedButton btn1, RedButton btn2 ) {
		add( btn1 );
		btn1.setRect( 0, pos > 0 ? pos += GAP : 0, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btn2 );
		btn2.setRect( btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT );
		pos += BTN_HEIGHT;
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
