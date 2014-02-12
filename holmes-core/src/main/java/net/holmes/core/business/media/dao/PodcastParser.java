/*
 * Copyright (C) 2012-2014  Cedric Cheneau
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.holmes.core.business.media.dao;

import com.sun.syndication.feed.module.itunes.EntryInformation;
import com.sun.syndication.feed.module.itunes.ITunes;
import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.RawUrlNode;
import net.holmes.core.common.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static net.holmes.core.business.media.model.AbstractNode.NodeType.TYPE_PODCAST_ENTRY;
import static net.holmes.core.common.MediaType.TYPE_RAW_URL;

/**
 * Podcast parser.
 */
abstract class PodcastParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(PodcastParser.class);

    /**
     * Parse podcast
     */
    @SuppressWarnings("unchecked")
    public void parse(String podcastUrl, String podcastId) throws IOException, FeedException {
        try (XmlReader reader = new XmlReader(new URL(podcastUrl))) {
            // Get RSS feed entries
            List<SyndEntry> rssEntries = new SyndFeedInput().build(reader).getEntries();
            for (SyndEntry rssEntry : rssEntries) {
                for (SyndEnclosure enclosure : (List<SyndEnclosure>) rssEntry.getEnclosures()) {
                    MimeType mimeType = enclosure.getType() != null ? MimeType.valueOf(enclosure.getType()) : null;
                    if (mimeType != null && mimeType.isMedia()) {
                        // Add to media index
                        String podcastEntryId = addMediaIndexElement(new MediaIndexElement(podcastId, TYPE_RAW_URL.getValue(), mimeType.getMimeType(), enclosure.getUrl(), rssEntry.getTitle(), false, false));

                        // Build podcast entry node
                        RawUrlNode podcastEntryNode = new RawUrlNode(TYPE_PODCAST_ENTRY, podcastEntryId, podcastId, rssEntry.getTitle(), mimeType, enclosure.getUrl(), getDuration(rssEntry));
                        podcastEntryNode.setIconUrl(getIconUrl(rssEntry));
                        podcastEntryNode.setModifiedDate(getPublishedDate(rssEntry));

                        // Add podcast entry node
                        addPodcastEntryNode(podcastEntryNode);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Add entry to media index.
     *
     * @param mediaIndexElement media index element
     * @return index element id
     */
    public abstract String addMediaIndexElement(final MediaIndexElement mediaIndexElement);

    /**
     * Add podcast entry node.
     *
     * @param podcastEntryNode new podcast entry node
     */
    public abstract void addPodcastEntryNode(final RawUrlNode podcastEntryNode);

    /**
     * Get RSS entry duration.
     *
     * @param rssEntry RSS entry
     * @return duration
     */
    private String getDuration(SyndEntry rssEntry) {
        EntryInformation itunesInfo = (EntryInformation) (rssEntry.getModule(ITunes.URI));
        return itunesInfo != null && itunesInfo.getDuration() != null ? itunesInfo.getDuration().toString() : null;
    }

    /**
     * Get RSS entry icon Url.
     *
     * @param rssEntry RSS entry
     * @return icon Url
     */
    private String getIconUrl(SyndEntry rssEntry) {
        MediaModule mediaInfo = (MediaModule) (rssEntry.getModule(MediaModule.URI));
        return mediaInfo != null && mediaInfo.getMetadata() != null && !mediaInfo.getMetadata().getThumbnails().isEmpty() ?
                mediaInfo.getMetadata().getThumbnails().get(0).getUrl().toString() : null;
    }

    /**
     * Get RSS entry published date
     *
     * @param rssEntry RSS entry
     * @return published date
     */
    private Long getPublishedDate(SyndEntry rssEntry) {
        return rssEntry.getPublishedDate() != null ? rssEntry.getPublishedDate().getTime() : null;
    }
}

