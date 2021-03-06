/*
 * Copyright (C) 2012-2015  Cedric Cheneau
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

import com.google.common.cache.Cache;
import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.*;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.MediaType;
import net.holmes.core.common.exception.HolmesException;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.cache.CacheBuilder.newBuilder;
import static net.holmes.core.business.media.dao.index.MediaIndexElementFactory.buildConfigMediaIndexElement;
import static net.holmes.core.business.media.model.AbstractNode.NodeType.TYPE_UNKNOWN;
import static net.holmes.core.business.media.model.RootNode.PODCAST;
import static net.holmes.core.common.ConfigurationParameter.*;
import static net.holmes.core.common.FileUtils.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Media dao implementation.
 */
@Singleton
public class MediaDaoImpl implements MediaDao {
    private static final Logger LOGGER = getLogger(MediaDaoImpl.class);

    private final ConfigurationManager configurationManager;
    private final MimeTypeManager mimeTypeManager;
    private final MediaIndexDao mediaIndexDao;
    private final Cache<String, List<AbstractNode>> podcastCache;

    /**
     * Instantiates a new media dao implementation.
     *
     * @param configurationManager configuration dao
     * @param mimeTypeManager      mime type manager
     * @param mediaIndexDao        media index dao
     */
    @Inject
    public MediaDaoImpl(final ConfigurationManager configurationManager, final MimeTypeManager mimeTypeManager, final MediaIndexDao mediaIndexDao) {
        this.configurationManager = configurationManager;
        this.mimeTypeManager = mimeTypeManager;
        this.mediaIndexDao = mediaIndexDao;
        this.podcastCache = newBuilder()
                .maximumSize(configurationManager.getParameter(PODCAST_CACHE_MAX_ELEMENTS))
                .expireAfterWrite(configurationManager.getParameter(PODCAST_CACHE_EXPIRE_HOURS), TimeUnit.HOURS)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractNode getNode(final String nodeId) {
        AbstractNode node = null;
        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexDao.get(nodeId);
        if (indexElement != null) {
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            switch (mediaType) {
                case TYPE_PODCAST:
                    // Podcast node
                    node = new PodcastNode(nodeId, PODCAST.getId(), indexElement.getName(), indexElement.getPath());
                    break;
                case TYPE_RAW_URL:
                    // Raw Url node
                    node = new RawUrlNode(TYPE_UNKNOWN, nodeId, indexElement.getParentId(), indexElement.getName(), MimeType.valueOf(indexElement.getMimeType()), indexElement.getPath(), null);
                    break;
                default:
                    // File node
                    node = getFileNode(nodeId, indexElement, mediaType);
                    break;
            }
        } else {
            LOGGER.warn("[getNode] {} not found in media index", nodeId);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractNode> getChildNodes(final String parentNodeId) {
        List<AbstractNode> childNodes;

        // Get node in mediaIndex
        MediaIndexElement indexElement = mediaIndexDao.get(parentNodeId);
        if (indexElement != null) {
            // Get media type
            MediaType mediaType = MediaType.getByValue(indexElement.getMediaType());
            switch (mediaType) {
                case TYPE_PODCAST:
                    // Get podcast entries
                    childNodes = getPodcastEntries(parentNodeId, indexElement.getPath());
                    break;
                case TYPE_RAW_URL:
                    // Nothing
                    childNodes = new ArrayList<>(0);
                    break;
                default:
                    // Get folder child nodes
                    childNodes = getFolderChildNodes(parentNodeId, indexElement.getPath(), mediaType);
                    break;
            }
        } else {
            childNodes = new ArrayList<>(0);
            LOGGER.error("[getChildNodes] {} node not found in media index", parentNodeId);
        }

        return childNodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractNode> getRootNodeChildren(final RootNode rootNode) {
        // Add nodes defined in configuration
        List<ConfigurationNode> configNodes = configurationManager.getNodes(rootNode);
        List<AbstractNode> nodes = new ArrayList<>(configNodes.size());
        for (ConfigurationNode configNode : configNodes) {
            // Add node to mediaIndex
            mediaIndexDao.put(configNode.getId(), buildConfigMediaIndexElement(rootNode, configNode));
            // Add child node
            if (rootNode == PODCAST) {
                nodes.add(new PodcastNode(configNode.getId(), PODCAST.getId(), configNode.getLabel(), configNode.getPath()));
            } else {
                nodes.add(new FolderNode(configNode.getId(), rootNode.getId(), configNode.getLabel(), new File(configNode.getPath())));
            }
        }
        return nodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanUpCache() {
        podcastCache.cleanUp();
        mediaIndexDao.clean();
    }

    /**
     * Get file or folder node
     *
     * @param nodeId       node id
     * @param indexElement index element
     * @param mediaType    media type
     * @return file or folder node
     */
    private AbstractNode getFileNode(final String nodeId, final MediaIndexElement indexElement, final MediaType mediaType) {
        AbstractNode node = null;
        File nodeFile = new File(indexElement.getPath());
        if (isValidFile(nodeFile)) {
            // Content node
            MimeType mimeType = mimeTypeManager.getMimeType(nodeFile.getName());
            if (mimeType != null) {
                node = buildContentNode(nodeId, indexElement.getParentId(), nodeFile, mediaType, mimeType);
            }
        } else if (isValidDirectory(nodeFile)) {
            // Folder node
            String nodeName = indexElement.getName() != null ? indexElement.getName() : nodeFile.getName();
            node = new FolderNode(nodeId, indexElement.getParentId(), nodeName, nodeFile);
        }
        return node;
    }

    /**
     * Get children of a folder node.
     *
     * @param folderNodeId folder node id
     * @param folderPath   folder path
     * @param mediaType    media type
     * @return folder child nodes matching media type
     */
    private List<AbstractNode> getFolderChildNodes(final String folderNodeId, final String folderPath, final MediaType mediaType) {
        List<File> children = listChildren(folderPath, true);
        List<AbstractNode> nodes = new ArrayList<>(children.size());
        for (File child : children) {
            // Add node to mediaIndex
            if (child.isDirectory()) {
                // Add folder node
                String nodeId = mediaIndexDao.add(new MediaIndexElement(folderNodeId, mediaType.getValue(), null, child.getAbsolutePath(), null, true, false));
                nodes.add(new FolderNode(nodeId, folderNodeId, child.getName(), child));
            } else {
                // Add content node
                addContentNode(nodes, folderNodeId, child, mediaType);
            }
        }
        return nodes;
    }

    /**
     * Gets pod-cast entries. A pod-cast is a RSS.
     *
     * @param podcastNodeId podcast node id
     * @param podcastUrl    podcast url
     * @return entries parsed from podcast RSS feed
     */
    @SuppressWarnings("unchecked")
    private List<AbstractNode> getPodcastEntries(final String podcastNodeId, final String podcastUrl) {
        try {
            return podcastCache.get(podcastUrl, new PodcastCacheCallable(podcastNodeId, podcastUrl));
        } catch (ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new ArrayList<>(0);
    }

    /**
     * Add content node to node list.
     *
     * @param nodes     node list
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     */
    private void addContentNode(final List<AbstractNode> nodes, final String parentId, final File file, final MediaType mediaType) {
        MimeType mimeType = mimeTypeManager.getMimeType(file.getName());
        if (mimeType != null) {
            // Add file node
            String nodeId = mediaIndexDao.add(new MediaIndexElement(parentId, mediaType.getValue(), mimeType.getMimeType(), file.getAbsolutePath(), null, true, false));
            ContentNode node = buildContentNode(nodeId, parentId, file, mediaType, mimeType);
            if (node != null) {
                nodes.add(node);
            }
        }
    }

    /**
     * Build content node.
     *
     * @param nodeId    node id
     * @param parentId  parent id
     * @param file      file
     * @param mediaType media type
     * @return content node
     */
    private ContentNode buildContentNode(final String nodeId, final String parentId, final File file, final MediaType mediaType, final MimeType mimeType) {
        // Check mime type
        return mimeType.getType() == mediaType || mimeType.isSubTitle() ? new ContentNode(nodeId, parentId, file.getName(), file, mimeType) : null;
    }

    /**
     * Podcast cache callable
     */
    private class PodcastCacheCallable implements Callable<List<AbstractNode>> {
        private final String podcastId;
        private final String podcastUrl;

        /**
         * Instantiates a new podcast cache callable.
         *
         * @param podcastId  podcast id
         * @param podcastUrl podcast URL
         */
        PodcastCacheCallable(final String podcastId, final String podcastUrl) {
            this.podcastId = podcastId;
            this.podcastUrl = podcastUrl;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<AbstractNode> call() throws HolmesException {
            // No entries in cache, read them from RSS feed
            // First remove children from media index
            mediaIndexDao.removeChildren(podcastId);

            // Then parse podcast
            return new PodcastParser() {
                @Override
                public String addMediaIndexElement(MediaIndexElement mediaIndexElement) {
                    // Add element to media index
                    return mediaIndexDao.add(mediaIndexElement);
                }
            }.parse(podcastUrl, podcastId);
        }
    }
}
