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

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Button;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Chrome;
import com.watabou.pixeldungeon.ChromePlus;
import com.watabou.pixeldungeon.scenes.PixelScene;

public class RedButton extends Button {
	
	protected NinePatch bg;
	protected BitmapText text;
	protected Image icon;
	protected NinePatch active_selection;

	public RedButton( String label ) {
		super();
		
		text.text( label );
		text.measure();
	}
	
	@Override
	protected void createChildren() {
		bg = Chrome.get( Chrome.Type.BUTTON );
		add( bg );
		
		active_selection = ChromePlus.get( ChromePlus.Type.BUTTON_HOVER );
		active_selection.visible = false;
		add( active_selection );
		
		text = PixelScene.createText( 9 );
		add( text );
		
		super.createChildren();
	}
	
	@Override
	protected void layout() {
		
		super.layout();
		
		bg.x = x;
		bg.y = y;
		bg.size( width, height );
		
		active_selection.x = x;
		active_selection.y = y;
		active_selection.size( width, height );
		
		text.x = x + (int)(width - text.width()) / 2;
		text.y = y + (int)(height - text.baseLine()) / 2;
		
		if (icon != null) {
			icon.x = x + text.x - icon.width() - 2;
			icon.y = y + (height - icon.height()) / 2;
		}
	}
	
	@Override
	protected void onTouchDown() {
		bg.brightness( 1.2f );
		Sample.INSTANCE.play( Assets.SND_CLICK );
	}
	
	@Override
	protected void onTouchUp() {
		bg.resetColor();
	}
	
	public void enable( boolean value ) {
		active = value;
		text.alpha( value ? 1.0f : 0.3f );
	}
	
	public void text( String value ) {
		text.text( value );
		text.measure();
		layout();
	}
	
	public void textColor( int value ) {
		text.hardlight( value );
	}
	
	public void icon( Image icon ) {
		if (this.icon != null) {
			remove( this.icon );
		}
		this.icon = icon;
		if (this.icon != null) {
			add( this.icon );
			layout();
		}
	}
	
	public float reqWidth() {
		return text.width() + 4;
	}
	
	public float reqHeight() {
		return text.baseLine() + 4;
	}
}
