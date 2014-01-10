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

package net.holmes.core.transport.airplay.controlpoint;

/**
 * Control point command response
 */

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;

/**
 * Command response.
 */
public class CommandResponse {
    private static final String EOL = "\n";
    private static final String SPACE = " ";
    private static final char PARAMETER_SEPARATOR = ':';

    private int code;
    private String message;
    private Map<String, String> httpHeaders = Maps.newHashMap();
    private Map<String, String> contentParameters = Maps.newHashMap();

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getContentParameters() {
        return contentParameters;
    }

    /**
     * Get content length.
     *
     * @return content length
     */
    public int getContentLength() {
        return httpHeaders.get(CONTENT_LENGTH) != null ? Integer.valueOf(httpHeaders.get(CONTENT_LENGTH)) : 0;
    }

    /**
     * Get content type.
     *
     * @return content type
     */
    public String getContentType() {
        return httpHeaders.get(CONTENT_TYPE);
    }

    /**
     * Decode Http response.
     *
     * @param responseLines response lines
     */
    public void decodeHttpResponse(final List<String> responseLines) {
        // Decode http response on first line
        Iterable<String> responseIt = Splitter.on(SPACE).split(responseLines.get(0));
        code = Integer.valueOf(Iterables.get(responseIt, 1));
        String responseMessage = "";
        for (int i = 2; i < Iterables.size(responseIt); i++) {
            responseMessage += Iterables.get(responseIt, i) + SPACE;
        }
        message = responseMessage.trim();

        // Decode http headers on next lines
        for (int i = 1; i < responseLines.size(); i++) {
            Iterable<String> it = Splitter.on(PARAMETER_SEPARATOR).trimResults().split(responseLines.get(i));
            httpHeaders.put(Iterables.get(it, 0), Iterables.getLast(it));
        }
    }

    /**
     * Decode content parameters.
     *
     * @param content content
     */
    public void decodeContentParameters(String content) {
        for (String line : Splitter.on(EOL).split(content)) {
            Iterable<String> it = Splitter.on(PARAMETER_SEPARATOR).trimResults().split(line);
            if (Iterables.size(it) > 1)
                contentParameters.put(Iterables.get(it, 0), Iterables.getLast(it));
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("code", code)
                .add("message", message)
                .add("httpHeaders", httpHeaders)
                .add("contentParameters", contentParameters)
                .toString();
    }
}
