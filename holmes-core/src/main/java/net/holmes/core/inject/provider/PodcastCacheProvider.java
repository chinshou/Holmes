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

package net.holmes.core.inject.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.configuration.Parameter;
import net.holmes.core.media.model.AbstractNode;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Guice provider for podcast cache.
 */
public class PodcastCacheProvider implements Provider<Cache<String, List<AbstractNode>>> {

    private final Configuration configuration;

    /**
     * Instantiates a new podcast cache provider.
     *
     * @param configuration configuration
     */
    @Inject
    public PodcastCacheProvider(final Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Cache<String, List<AbstractNode>> get() {
        return CacheBuilder.newBuilder() //
                .maximumSize(configuration.getIntParameter(Parameter.PODCAST_CACHE_MAX_ELEMENTS)) //
                .expireAfterWrite(configuration.getIntParameter(Parameter.PODCAST_CACHE_EXPIRE_HOURS), TimeUnit.HOURS) //
                .build();
    }
}