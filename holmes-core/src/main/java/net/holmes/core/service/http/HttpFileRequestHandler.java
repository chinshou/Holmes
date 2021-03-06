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

package net.holmes.core.service.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import net.holmes.core.business.configuration.ConfigurationManager;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.channel.ChannelFutureListener.CLOSE;
import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.Values.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static io.netty.handler.codec.http.LastHttpContent.EMPTY_LAST_CONTENT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.TimeZone.getTimeZone;
import static net.holmes.core.common.ConfigurationParameter.HTTP_SERVER_CACHE_SECOND;
import static net.holmes.core.common.Constants.HOLMES_HTTP_SERVER_NAME;
import static net.holmes.core.common.FileUtils.isValidFile;

/**
 * Http file request handler.
 */
public final class HttpFileRequestHandler extends SimpleChannelInboundHandler<HttpFileRequest> {
    private static final Pattern PATTERN_RANGE_START_OFFSET = Pattern.compile("^(?i)\\s*bytes\\s*=\\s*(\\d+)\\s*-.*$");
    private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final int CHUNK_SIZE = 8192;
    private static final TimeZone GMT_TIMEZONE = getTimeZone("GMT");

    private static final CharSequence X_SERVER = newEntity(SERVER);
    private static final CharSequence X_SERVER_NAME = newEntity(HOLMES_HTTP_SERVER_NAME.toString());
    private static final CharSequence X_ACCEPT_RANGES = newEntity(ACCEPT_RANGES);
    private static final CharSequence X_CONTENT_RANGE = newEntity(CONTENT_RANGE);
    private static final CharSequence X_BYTES = newEntity(BYTES);
    private static final CharSequence X_DATE = newEntity(DATE);
    private static final CharSequence X_LAST_MODIFIED = newEntity(LAST_MODIFIED);
    private static final CharSequence X_EXPIRES = newEntity(EXPIRES);
    private static final CharSequence X_CACHE_CONTROL = newEntity(CACHE_CONTROL);
    private static final CharSequence X_CONTENT_TYPE = newEntity(CONTENT_TYPE);
    private static final CharSequence X_CONTENT_TYPE_UTF8 = newEntity("text/plain; charset=" + UTF_8);
    private static final CharSequence X_CONNECTION = newEntity(CONNECTION);
    private static final CharSequence X_KEEP_ALIVE = newEntity(KEEP_ALIVE);

    private final int httpCacheSecond;
    private final SimpleDateFormat httpDateFormatter;

    /**
     * Instantiates a new Http file request handler.
     *
     * @param configurationManager configuration manager
     */
    @Inject
    public HttpFileRequestHandler(final ConfigurationManager configurationManager) {
        httpCacheSecond = configurationManager.getParameter(HTTP_SERVER_CACHE_SECOND);
        httpDateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT);
        httpDateFormatter.setTimeZone(GMT_TIMEZONE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void channelRead0(final ChannelHandlerContext context, final HttpFileRequest request) throws HttpFileRequestException, IOException {
        // Check file
        File file = request.getFile();
        if (!isValidFile(file)) {
            throw new HttpFileRequestException(file.getPath(), NOT_FOUND);
        }

        // Get file descriptor
        RandomAccessFile randomFile = new RandomAccessFile(file, "r");
        long fileLength = randomFile.length();

        // Get start offset
        long startOffset = getStartOffset(request.getHttpMessage());

        // Build HTTP response
        HttpResponse response = buildHttpResponse(startOffset, fileLength);

        // Add HTTP headers to response
        addContentHeaders(response, fileLength - startOffset, request);
        addDateAndCacheHeaders(response, request);
        boolean keepAlive = addKeepAliveHeader(response, request);

        // Write the response headers
        context.write(response);

        // Write the content
        context.write(new ChunkedFile(randomFile, startOffset, fileLength - startOffset, CHUNK_SIZE));

        // Write the end marker
        ChannelFuture lastContentFuture = context.writeAndFlush(EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not when the whole content is written out.
        if (!keepAlive) {
            lastContentFuture.addListener(CLOSE);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exceptionCaught(final ChannelHandlerContext context, final Throwable cause) {
        if (context.channel().isActive()) {
            if (cause instanceof HttpFileRequestException) {
                sendError(context, cause.getMessage(), ((HttpFileRequestException) cause).getStatus());
            } else {
                sendError(context, cause.getMessage(), INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Build HTTP response.
     *
     * @param startOffset start offset
     * @param fileLength  file length
     * @return HTTP response
     * @throws HttpFileRequestException indicates that start offset is invalid
     */
    private HttpResponse buildHttpResponse(final long startOffset, final long fileLength) throws HttpFileRequestException {
        HttpResponse response;
        if (startOffset == 0) {
            // Instantiates a new default HTTP response
            response = new DefaultHttpResponse(HTTP_1_1, OK, false);
            response.headers().set(X_ACCEPT_RANGES, X_BYTES);
        } else if (startOffset < fileLength) {
            // Instantiates a new default HTTP response with content range
            response = new DefaultHttpResponse(HTTP_1_1, PARTIAL_CONTENT, false);
            response.headers().set(X_CONTENT_RANGE, startOffset + "-" + (fileLength - 1) + "/" + fileLength);
        } else {
            // Invalid start offset
            throw new HttpFileRequestException("Invalid start offset: " + startOffset, REQUESTED_RANGE_NOT_SATISFIABLE);
        }

        // Add server header
        response.headers().set(X_SERVER, X_SERVER_NAME);

        return response;
    }

    /**
     * Get start offset from Http message.
     *
     * @param httpMessage Http message
     * @return start offset
     * @throws HttpFileRequestException indicates that start offset is invalid
     */
    private long getStartOffset(final HttpMessage httpMessage) throws HttpFileRequestException {
        long startOffset = 0;
        String range = httpMessage.headers().get(RANGE);
        if (range != null) {
            Matcher matcher = PATTERN_RANGE_START_OFFSET.matcher(range);
            if (matcher.find()) {
                startOffset = Long.parseLong(matcher.group(1));
            } else {
                throw new HttpFileRequestException(range, REQUESTED_RANGE_NOT_SATISFIABLE);
            }
        }
        return startOffset;
    }

    /**
     * Add content length and type headers.
     *
     * @param response   HTTP response
     * @param fileLength file length
     * @param request    file request
     */
    private void addContentHeaders(final HttpResponse response, final long fileLength, final HttpFileRequest request) {
        setContentLength(response, fileLength);
        response.headers().set(X_CONTENT_TYPE, request.getMimeType().getMimeType());
    }

    /**
     * Add date and cache headers to response.
     *
     * @param response HTTP response
     * @param request  file request
     */
    private void addDateAndCacheHeaders(final HttpResponse response, final HttpFileRequest request) {
        Calendar calendar = Calendar.getInstance();

        // Add date header
        response.headers().set(X_DATE, httpDateFormatter.format(calendar.getTime()));
        response.headers().set(X_LAST_MODIFIED, httpDateFormatter.format(new Date(request.getFile().lastModified())));

        // Add cache header for static resources
        if (request.isStaticResource() && httpCacheSecond > 0) {
            calendar.add(Calendar.SECOND, httpCacheSecond);
            response.headers().set(X_EXPIRES, httpDateFormatter.format(calendar.getTime()));
            response.headers().set(X_CACHE_CONTROL, "private, max-age=" + httpCacheSecond);
        }
    }

    /**
     * Add keep alive header
     *
     * @param response HTTP response
     * @param request  HTTP request
     * @return true if keep alive is requested
     */
    private boolean addKeepAliveHeader(final HttpResponse response, final HttpFileRequest request) {
        boolean keepAlive = isKeepAlive(request.getHttpMessage());
        if (keepAlive) {
            response.headers().set(X_CONNECTION, X_KEEP_ALIVE);
        }
        return keepAlive;
    }

    /**
     * Send error.
     *
     * @param context channel context                                                    z
     * @param message message
     * @param status  response status
     */
    private void sendError(final ChannelHandlerContext context, final String message, final HttpResponseStatus status) {
        // Build error response
        ByteBuf buffer = copiedBuffer("Failure: " + message + " " + status.toString() + "\r\n", UTF_8);
        HttpMessage response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);
        response.headers().set(X_CONTENT_TYPE, X_CONTENT_TYPE_UTF8);

        // Close the connection as soon as the error message is sent.
        context.channel().writeAndFlush(response).addListener(CLOSE);
    }
}
