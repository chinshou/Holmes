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

package net.holmes.core.transport.device;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import net.holmes.core.transport.device.model.Device;
import net.holmes.core.transport.device.model.DeviceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * Device manager implementation.
 */
public class DeviceManagerImpl implements DeviceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagerImpl.class);
    private final Map<String, Device> devices;

    /**
     * Instantiates a new device manager implementation.
     */
    public DeviceManagerImpl() {
        this.devices = Maps.newHashMap();
    }

    @Override
    public void addDevice(final Device device) {
        LOGGER.info("Add device {}", device);
        devices.put(device.getId(), device);
    }

    @Override
    public void removeDevice(final String deviceId) {
        LOGGER.info("Remove device {}", deviceId);
        devices.remove(deviceId);
    }

    @Override
    public Device getDevice(final String deviceId) {
        return devices.get(deviceId);
    }

    @Override
    public Collection<Device> getDevices() {
        return devices.values();
    }

    @Override
    public Collection<Device> findDevices(final String hostAddress, final DeviceType type) {
        return Collections2.filter(devices.values(), new Predicate<Device>() {
            @Override
            public boolean apply(Device input) {
                return input.getHostAddress().equals(hostAddress) && input.getDeviceType() == type;
            }
        });
    }
}
