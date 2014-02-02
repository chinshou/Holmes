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

package net.holmes.core.upnp.metadata;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UpnpDeviceMetadataTest {

    @Test
    public void testUpnpDeviceMetadata() {
        UpnpDeviceMetadata upnpDeviceMetadata = new UpnpDeviceMetadataImpl();

        upnpDeviceMetadata.addDevice("device", Lists.newArrayList("something"));
        assertNotNull(upnpDeviceMetadata.getAvailableMimeTypes("device"));

        upnpDeviceMetadata.removeDevice("device");
        assertNull(upnpDeviceMetadata.getAvailableMimeTypes("device"));
    }
}