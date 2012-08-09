/**
* Copyright (C) 2012  Cedric Cheneau
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
package net.holmes.core.http.request;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.HttpRequest;

public interface IHttpRequestHandler {

    /**
     * Initialize handler
     */
    public void initHandler();

    /**
     * Check if handler can process request
     */
    public boolean canProcess(String requestPath);

    /**
     * Process request
     */
    public void processRequest(HttpRequest request, Channel channel) throws HttpRequestException;
}
