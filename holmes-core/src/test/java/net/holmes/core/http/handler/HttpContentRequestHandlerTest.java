/*
 * Copyright (C) 2012-2013  Cedric Cheneau
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

package net.holmes.core.http.handler;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import net.holmes.core.media.MediaManager;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.media.model.RootNode;
import net.holmes.core.test.TestModule;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class HttpContentRequestHandlerTest {

    private FullHttpRequest request = createMock(FullHttpRequest.class);
    private Channel channel = createMock(Channel.class);
    @Inject
    private MediaManager mediaManager;
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new TestModule());
        injector.injectMembers(this);
    }

    private HttpContentRequestHandler getHandler() {
        HttpContentRequestHandler contentRequestHandler = new HttpContentRequestHandler(mediaManager);
        injector.injectMembers(contentRequestHandler);
        return contentRequestHandler;
    }

    public AbstractNode getContentNodeFromMediaManager() {
        AbstractNode rootNode = mediaManager.getNode(RootNode.PICTURE.getId());
        assertNotNull(rootNode);
        List<AbstractNode> childNodes = mediaManager.getChildNodes(rootNode);
        assertNotNull(childNodes);
        assertNotNull(mediaManager.getNode(childNodes.get(0).getId()));

        List<AbstractNode> nodes = mediaManager.getChildNodes(childNodes.get(0));
        assertNotNull(nodes);
        for (AbstractNode node : nodes) {
            if (node instanceof ContentNode) return node;
        }
        fail();
        return null;
    }

    @Test
    public void testCanProcess() {
        HttpContentRequestHandler contentRequestHandler = getHandler();
        assertTrue(contentRequestHandler.canProcess("/content/request", HttpMethod.GET));
        assertFalse(contentRequestHandler.canProcess("/content/request", HttpMethod.POST));
        assertFalse(contentRequestHandler.canProcess("bad_request", HttpMethod.GET));
        assertFalse(contentRequestHandler.canProcess("bad_request", HttpMethod.POST));
    }

    @Test(expected = NullPointerException.class)
    public void testProcessRequestNoContent() throws Exception {
        expect(request.getUri()).andReturn("/content").atLeastOnce();

        replay(request, channel);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, channel);
        }

    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestNullContent() throws Exception {
        expect(request.getUri()).andReturn("/content?id=").atLeastOnce();

        replay(request, channel);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, channel);
        }
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestUnknownContent() throws Exception {
        expect(request.getUri()).andReturn("/content?id=25").atLeastOnce();

        replay(request, channel);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, channel);
        }
    }

    @Test
    public void testProcessRequestWithoutKeepAlive() throws Exception {
        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");
        headers.add(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();

        expect(channel.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.write(isA(DefaultFileRegion.class), isA(ChannelProgressivePromise.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.newProgressivePromise()).andReturn(new DefaultChannelProgressivePromise(channel)).atLeastOnce();

        replay(request, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.processRequest(request, channel);
        verify(request, channel);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestEmptyOffset() throws Exception {
        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();

        replay(request, channel);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, channel);
        }
    }

    @Test
    public void testProcessRequestWithOffset() throws Exception {
        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "bytes=5-");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getProtocolVersion()).andReturn(HttpVersion.HTTP_1_1).atLeastOnce();

        expect(channel.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.write(isA(DefaultFileRegion.class), isA(ChannelProgressivePromise.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.newProgressivePromise()).andReturn(new DefaultChannelProgressivePromise(channel)).atLeastOnce();

        replay(request, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.processRequest(request, channel);
        verify(request, channel);
    }

    @Test(expected = HttpRequestException.class)
    public void testProcessRequestWithBadOffset() throws Exception {
        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");
        headers.add(HttpHeaders.Names.RANGE, "bytes=500000-");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();

        replay(request, channel);
        try {
            HttpContentRequestHandler contentRequestHandler = getHandler();
            contentRequestHandler.processRequest(request, channel);
        } finally {
            verify(request, channel);
        }
    }

    @Test
    public void testProcessRequest() throws Exception {
        AbstractNode node = getContentNodeFromMediaManager();
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaders.Names.HOST, "localhost");

        expect(request.getUri()).andReturn("/content?id=" + node.getId()).atLeastOnce();
        expect(request.headers()).andReturn(headers).atLeastOnce();
        expect(request.getProtocolVersion()).andReturn(HttpVersion.HTTP_1_1).atLeastOnce();

        expect(channel.write(isA(HttpResponse.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.write(isA(DefaultFileRegion.class), isA(ChannelProgressivePromise.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.writeAndFlush(isA(LastHttpContent.class))).andReturn(new DefaultChannelPromise(channel)).atLeastOnce();
        expect(channel.newProgressivePromise()).andReturn(new DefaultChannelProgressivePromise(channel)).atLeastOnce();

        replay(request, channel);
        HttpContentRequestHandler contentRequestHandler = getHandler();
        contentRequestHandler.processRequest(request, channel);
        verify(request, channel);
    }
}
