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

import net.holmes.core.business.configuration.ConfigurationManager;
import net.holmes.core.business.configuration.ConfigurationManagerImpl;
import net.holmes.core.business.configuration.model.ConfigurationNode;
import net.holmes.core.business.media.dao.index.MediaIndexDao;
import net.holmes.core.business.media.dao.index.MediaIndexElement;
import net.holmes.core.business.media.model.*;
import net.holmes.core.business.mimetype.MimeTypeManager;
import net.holmes.core.business.mimetype.model.MimeType;
import net.holmes.core.common.UniqueIdGenerator;
import net.holmes.core.test.TestConfigurationDao;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static net.holmes.core.business.media.model.RootNode.*;
import static net.holmes.core.business.mimetype.model.MimeType.MIME_TYPE_SUBTITLE;
import static net.holmes.core.common.MediaType.*;
import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class MediaDaoImplTest {

    @Test
    public void testGetNodeNotInIndex() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetPodcastNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, "path", "name", PODCAST.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), PodcastNode.class);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetRawUrlNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        MediaIndexElement rawUrlElement = new MediaIndexElement(VIDEO.getId(), TYPE_RAW_URL.getValue(), "video/avi", "path", "name", false, true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(rawUrlElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), RawUrlNode.class);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetBadFileNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", "path", "name", VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetVideoFolderNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoNode.getPath(), "name", VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), FolderNode.class);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetVideoFolderNodeWithNoName() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoNode.getPath(), null, VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), FolderNode.class);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetVideoFileNode() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);
        Path videoFilePath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFilePath.toFile().getPath(), videoFilePath.toFile().getName(), VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi"));

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNotNull(result);
        assertEquals(result.getClass(), ContentNode.class);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetVideoFileNodeNoMimeType() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);
        Path videoFilePath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFilePath.toFile().getPath(), videoFilePath.toFile().getName(), VIDEO.isLocalPath(), true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        AbstractNode result = mediaDao.getNode("nodeId");
        assertNull(result);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesNotInIndex() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(null);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfPodcast() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode podcastNode = configurationManager.getNodes(PODCAST).get(0);
        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, podcastNode.getPath(), podcastNode.getLabel(), PODCAST.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);
        mediaIndexDao.removeChildren(eq("nodeId"));
        expectLastCall();
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfBadPodcast() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        MediaIndexElement podcastElement = new MediaIndexElement(PODCAST.getId(), TYPE_PODCAST.getValue(), null, "badPath", "badLabel", PODCAST.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(podcastElement);
        mediaIndexDao.removeChildren(eq("nodeId"));
        expectLastCall();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfRawUrl() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        MediaIndexElement rawUrlElement = new MediaIndexElement("id", TYPE_RAW_URL.getValue(), null, "rawUrl", "rawUrl", false, true);
        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(rawUrlElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfVideoFolder() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "subFolder");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi")).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.unknown"))).andReturn(null).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.srt"))).andReturn(MIME_TYPE_SUBTITLE).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfVideoFile() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "video.avi");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_VIDEO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertTrue(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetChildNodesOfVideoFolderBadMediaType() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        ConfigurationNode videoNode = configurationManager.getNodes(VIDEO).get(0);
        Path videoFolderPath = Paths.get(videoNode.getPath(), "subFolder");

        MediaIndexElement videoElement = new MediaIndexElement(VIDEO.getId(), TYPE_AUDIO.getValue(), "video/avi", videoFolderPath.toFile().getPath(), videoFolderPath.toFile().getName(), VIDEO.isLocalPath(), true);

        expect(mediaIndexDao.get(eq("nodeId"))).andReturn(videoElement);
        expect(mediaIndexDao.add(isA(MediaIndexElement.class))).andReturn(UniqueIdGenerator.newUniqueId()).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.avi"))).andReturn(MimeType.valueOf("video/avi")).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.unknown"))).andReturn(null).atLeastOnce();
        expect(mimeTypeManager.getMimeType(eq("video.srt"))).andReturn(MIME_TYPE_SUBTITLE).atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getChildNodes("nodeId");
        assertFalse(result.isEmpty());

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testCleanupCache() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        mediaIndexDao.clean();
        expectLastCall();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        mediaDao.cleanUpCache();

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetRootNodeChildrenOfPodcast() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        mediaIndexDao.put(isA(String.class), isA(MediaIndexElement.class));
        expectLastCall().atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getRootNodeChildren(PODCAST);
        assertNotNull(result);

        verify(mimeTypeManager, mediaIndexDao);
    }

    @Test
    public void testGetRootNodeChildrenNodesOfVideo() {
        ConfigurationManager configurationManager = new ConfigurationManagerImpl(new TestConfigurationDao());
        MimeTypeManager mimeTypeManager = createMock(MimeTypeManager.class);
        MediaIndexDao mediaIndexDao = createMock(MediaIndexDao.class);

        mediaIndexDao.put(isA(String.class), isA(MediaIndexElement.class));
        expectLastCall().atLeastOnce();

        replay(mimeTypeManager, mediaIndexDao);
        MediaDaoImpl mediaDao = new MediaDaoImpl(configurationManager, mimeTypeManager, mediaIndexDao);

        List<AbstractNode> result = mediaDao.getRootNodeChildren(VIDEO);
        assertNotNull(result);

        verify(mimeTypeManager, mediaIndexDao);
    }
}
