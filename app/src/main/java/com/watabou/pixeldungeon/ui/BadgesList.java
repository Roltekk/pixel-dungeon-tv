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
package com.watabou.pixeldungeon.ui;

import java.util.ArrayList;

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.noosa.ui.Component;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Badges;
import com.watabou.pixeldungeon.effects.BadgeBanner;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.pixeldungeon.windows.WndBadge;

public class BadgesList extends ScrollPane {

	private ArrayList<ListItem> items = new ArrayList<ListItem>();
	
	public BadgesList( boolean global ) {
		super( new Component() );
		
		for (Badges.Badge badge : Badges.filtered( global )) {
			
			if (badge.image == -1) {
				continue;
			}
			
			ListItem item = new ListItem( badge );
			content.add( item );
			items.add( item );
		}
	}
	
	@Override
	protected void layout() {
		float pos = 0;
		
		int size = items.size();
		for (int i=0; i < size; i++) {
			items.get( i ).setRect( 0, pos, width, ListItem.HEIGHT );
			pos += ListItem.HEIGHT;
		}
		
		content.setSize( width, pos );
		
		super.layout();
	}

	private class ListItem extends Button {
		
		private static final float HEIGHT	= 20;
		
		private Badges.Badge badge;
		
		private Image icon;
		private BitmapText label;
		
		public ListItem( Badges.Badge badge ) {
			super();
			
			this.badge = badge;
			icon.copy( BadgeBanner.image( badge.image ));
			label.text( badge.description );
		}
		
		@Override
		protected void createChildren() {
			icon = new Image();
			add( icon );
			
			label = PixelScene.createText( 6 );
			add( label );
			super.createChildren();
		}
		
		@Override
		protected void layout() {
			icon.x = x;
			icon.y = PixelScene.align( y + (height - icon.height) / 2 );
			
			label.x = icon.x + icon.width + 2;
			label.y = PixelScene.align( y + (height - label.baseLine()) / 2 );
			
			super.layout();
		}
		
		@Override
		protected void onTouchDown() {
			icon.brightness( 1.5f );
			Sample.INSTANCE.play( Assets.SND_CLICK, 0.7f, 0.7f, 1.2f );
		}
		
		@Override
		protected void onTouchUp() {
			icon.brightness( 1.0f );
		}
		
		@Override
		protected void onClick() {
			Game.scene().add( new WndBadge( badge ) );
		}
	}
}
