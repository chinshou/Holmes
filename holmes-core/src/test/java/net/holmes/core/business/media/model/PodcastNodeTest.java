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

package net.holmes.core.business.media.model;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * PodcastNode Tester.
 */
public class PodcastNodeTest {

    /**
     * Method: hashCode()
     */
    @Test
    public void testHashCode() throws Exception {
        PodcastNode node1 = buildPodcastNode();
        PodcastNode node2 = buildPodcastNode();
        assertNotNull(node1.hashCode());
        assertEquals(node1.hashCode(), node2.hashCode());
    }

    /**
     * Method: equals(final Object obj)
     */
    @Test
    public void testEquals() throws Exception {
        PodcastNode node1 = buildPodcastNode();
        PodcastNode node2 = buildPodcastNode();
        assertEquals(node1, node1);
        assertEquals(node1, node2);
        assertNotEquals(node1, null);
        assertNotEquals(node1, "node1");
    }

    /**
     * Method: toString()
     */
    @Test
    public void testToString() throws Exception {
        PodcastNode node1 = buildPodcastNode();
        PodcastNode node2 = buildPodcastNode();
        assertNotNull(node1.toString());
        assertEquals(node1.toString(), node2.toString());
    }

    private PodcastNode buildPodcastNode() {
        return new PodcastNode("id", "parentId", "name", "url");
    }
}
