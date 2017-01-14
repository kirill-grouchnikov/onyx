/*
 * Copyright (c) 2009-2010 Onyx Kirill Grouchnikov. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of Onyx Kirill Grouchnikov nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.pushingpixels.onyx.details;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.pushingpixels.onyx.OnyxUtils;
import org.pushingpixels.onyx.data.Album;
import org.pushingpixels.onyx.data.Track;

/**
 * Component for showing track listing of a single album item from Amazon.
 * 
 * @author Kirill Grouchnikov
 */
public class TrackListing extends JPanel implements Scrollable {
	/**
	 * The album item.
	 */
	private Album album;

	/**
	 * Album performer.
	 */
	private String artist;

	/**
	 * The title of {@link #albumItem}.
	 */
	private String albumTitle;

	/**
	 * The release date of {@link #albumItem}.
	 */
	private String released;

	/**
	 * List of the {@link #albumItem} discs.
	 */
	private List<Track> tracks;

	/**
	 * Information on a single disc.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private static class DiscInfo {
		/**
		 * Disc caption.
		 */
		private String caption;

		/**
		 * Disc tracks.
		 */
		private List<String> tracks = new ArrayList<String>();
	}

	/**
	 * Creates a new component that shows a list of all album tracks.
	 */
	public TrackListing() {
		this.setOpaque(false);
		this.setBorder(new EmptyBorder(6, 6, 6, 6));
	}

	/**
	 * Sets the specified album item for the track display.
	 * 
	 * @param albumItem
	 *            Album item.
	 */
	public void setAlbum(Album album, List<Track> tracks) {
		this.album = album;
		this.artist = this.album.artist;
		this.albumTitle = "\"" + this.album.name + "\"";
		this.released = "Released " + this.album.releaseDate;
		this.tracks = Collections.unmodifiableList(tracks);

		this.revalidate();
		this.getParent().invalidate();
		this.getParent().validate();
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.album == null) {
			return super.getPreferredSize();
		}

		if (this.getWidth() < 0)
			return super.getPreferredSize();

		Font keyFont = UIManager.getFont("Label.font").deriveFont(16.0f);
		Font detailsFont = UIManager.getFont("Label.font").deriveFont(13.0f);

		Insets ins = this.getInsets();
		int width = getWidth() - ins.left - ins.right;

		float keyFontHeight = keyFont.getLineMetrics(this.artist,
				new FontRenderContext(new AffineTransform(), false, false))
				.getHeight();
		float detailsFontHeight = detailsFont.getLineMetrics(this.artist,
				new FontRenderContext(new AffineTransform(), false, false))
				.getHeight();

		float height = keyFontHeight / 2;
		// performers
		height += OnyxUtils.getMultilineTextHeight(keyFont, this.artist, width);
		height += keyFontHeight / 3;
		// title
		height += OnyxUtils.getMultilineTextHeight(keyFont, this.albumTitle,
				width);
		height += keyFontHeight / 3;
		// release date
		height += OnyxUtils.getMultilineTextHeight(keyFont, this.released,
				width);
		height += keyFontHeight / 2;

		// tracks
		height += detailsFontHeight / 2;
		for (Track track : this.tracks) {
			height += OnyxUtils.getMultilineTextHeight(detailsFont,
					track.title, width);
			height += detailsFontHeight / 3;
		}
		return new Dimension(this.getWidth(), (int) height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth();
		Font keyFont = UIManager.getFont("Label.font").deriveFont(16.0f);
		Font detailsFont = UIManager.getFont("Label.font").deriveFont(13.0f);
		g2d.setFont(keyFont);
		int keyFontHeight = g2d.getFontMetrics().getHeight();

		Insets ins = getInsets();
		int width = getWidth() - ins.left - ins.right;
		if (this.album != null) {
			int height = ins.top + 3 * g2d.getFontMetrics().getAscent() / 2
					- g2d.getFontMetrics().getHeight();
			// performers
			height += OnyxUtils.getMultilineTextHeight(keyFont, this.artist,
					width);
			height += keyFontHeight / 3;
			// title
			height += OnyxUtils.getMultilineTextHeight(keyFont,
					this.albumTitle, width);
			height += keyFontHeight / 3;
			// release date
			height += OnyxUtils.getMultilineTextHeight(keyFont, this.released,
					width);
			height += keyFontHeight / 3;

			g2d.setColor(Color.black);
			g2d.fillRect(-4, 0, w + 1, height);
		}

		if (this.album != null) {
			int x = ins.left;

			g2d.setFont(keyFont);
			g2d.setColor(Color.white);
			int y = ins.top + 3 * g2d.getFontMetrics().getAscent() / 2;

			y = OnyxUtils.paintMultilineText(g2d, this.artist, x, width, y, -1);
			y += keyFontHeight / 3;
			y = OnyxUtils.paintMultilineText(g2d, this.albumTitle, x, width, y,
					-1);
			y += keyFontHeight / 3;
			y = OnyxUtils.paintMultilineText(g2d, this.released, x, width, y,
					-1);
			y += keyFontHeight / 3;

			// tracks
			g2d.setFont(detailsFont);
			int detailsFontHeight = g2d.getFontMetrics().getHeight();
			y += detailsFontHeight / 2;
			for (Track track : tracks) {
				g2d.setColor(new Color(44, 44, 44));
				g2d.drawLine(x + 5, y - detailsFontHeight + 2, width - 10, y
						- detailsFontHeight + 2);
				g2d.setColor(new Color(35, 35, 35));
				g2d.drawLine(x + 5, y - detailsFontHeight + 3, width - 10, y
						- detailsFontHeight + 3);

				g2d.setColor(new Color(192, 192, 192));
				y = OnyxUtils.paintMultilineText(g2d, track.title, x, width, y,
						-1);
				y += detailsFontHeight / 3;
			}
		}

		g2d.dispose();
	}

	public Dimension getPreferredScrollableViewportSize() {
		return this.getPreferredSize();
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 30;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}
}
