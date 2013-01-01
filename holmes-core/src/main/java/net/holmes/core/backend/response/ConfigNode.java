/**
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
package net.holmes.core.backend.response;

import java.util.Collection;

public class ConfigNode {
    private final String id;
    private final Collection<String> cell;

    public ConfigNode(String id, Collection<String> cell) {
        this.id = id;
        this.cell = cell;
    }

    public String getId() {
        return id;
    }

    public Collection<String> getCell() {
        return cell;
    }
}
