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

package net.holmes.core.business.configuration;

/**
 * Configuration parameters.
 */
public enum Parameter {
    AIRPLAY_STREAMING_ENABLE("enable_airplay_streaming", "true"),
    CACHE_CLEAN_DELAY_MINUTES("cache_clean_delay_minutes", "30"),
    HTTP_SERVER_PORT("http_server_port", "8085"),
    HTTP_SERVER_CACHE_SECOND("http_server_cache_second", "60"),
    ICECAST_ENABLE("enable_icecast_directory", "true"),
    ICECAST_GENRE_LIST("icecast_genre_list", "70s,80s,90s,adult,alternative,ambient,anime,bass,best,blues,chill,christian,classic,classical,club,college,community,contemporary,country,dance,deep,disco,dj,downtempo,drum,dubstep,eclectic,electro,folk,fun,funk,game,gospel,hip hop,hiphop,hit,house,indie,instrumental,international,jazz,jpop,jrock,latin,live,lounge,metal,minecraft,minimal,misc,mix,mixed,music,musique,news,oldies,pop,progressive,promodj,punk,radio,rap,reggae,religious,rnb,rock,salsa,scanner,smooth,soul,sport,sports,talk,techno,top,trance,urban,various,webradio,world"),
    ICECAST_MAX_DOWNLOAD_RETRY("icecast_max_download_retry", "3"),
    ICECAST_YELLOW_PAGE_DOWNLOAD_DELAY_HOURS("icecast_yellow_page_download_delay_hours", "24"),
    ICECAST_YELLOW_PAGE_URL("icecast_yellow_page_url", "http://dir.xiph.org/yp.xml"),
    PODCAST_CACHE_EXPIRE_HOURS("podcast_cache_expire_hours", "2"),
    PODCAST_CACHE_MAX_ELEMENTS("podcast_cache_max_elements", "50"),
    PODCAST_PREPEND_ENTRY_NAME("podcast_prepend_entry_name", "true"),
    RELEASE_CHECK_DELAY_HOURS("release_check_delay_hours", "30"),
    STREAMING_STATUS_UPDATE_DELAY_SECONDS("streaming_status_update_delay_seconds", "3"),
    SYSTRAY_ENABLE("enable_systray", "true"),
    SYSTRAY_ICONS_IN_MENU("icons_in_systray_menu", "true"),
    UPNP_ADD_SUBTITLE("upnp_add_subtitle", "true"),
    UPNP_ENABLE("enable_upnp", "true"),
    UPNP_SERVER_NAME("upnp_server_name", "Holmes media server"),
    UPNP_SERVICE_PORT("upnp_service_port", "5002");

    private final String name;
    private final String defaultValue;

    /**
     * Instantiates a new parameter.
     *
     * @param name         parameter name
     * @param defaultValue parameter default value
     */
    Parameter(final String name, final String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
