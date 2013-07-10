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

package net.holmes.core.media.index;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class MediaIndexElementTest {

    @Test
    public void testEquals() {
        MediaIndexElement element1 = new MediaIndexElement("parentId", "mediaType", "path", "name", true);
        MediaIndexElement element2 = new MediaIndexElement("parentId", "mediaType", "path", "name", true);
        MediaIndexElement element3 = new MediaIndexElement("parentId1", "mediaType", "path", "name", true);
        MediaIndexElement element4 = new MediaIndexElement("parentId", "mediaType1", "path", "name", true);
        MediaIndexElement element5 = new MediaIndexElement("parentId", "mediaType", "path1", "name", true);
        MediaIndexElement element6 = new MediaIndexElement("parentId", "mediaType", "path", "name1", false);
        assertEquals(element1, element1);
        assertEquals(element1, element2);
        assertNotEquals(element1, null);
        assertNotEquals(element1, "element1");
        assertNotEquals(element1, element3);
        assertNotEquals(element1, element4);
        assertNotEquals(element1, element5);
        assertNotEquals(element1, element6);
    }
}