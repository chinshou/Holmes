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

package net.holmes.core.transport;

import net.holmes.core.common.configuration.Configuration;
import net.holmes.core.common.mimetype.MimeType;
import net.holmes.core.media.model.AbstractNode;
import net.holmes.core.media.model.ContentNode;
import net.holmes.core.transport.airplay.AirplayDevice;
import net.holmes.core.transport.device.Device;
import net.holmes.core.transport.device.DeviceDao;
import net.holmes.core.transport.device.DeviceStreamer;
import net.holmes.core.transport.device.UnknownDeviceException;
import net.holmes.core.transport.session.SessionDao;
import net.holmes.core.transport.session.UnknownSessionException;
import net.holmes.core.transport.upnp.UpnpDevice;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static net.holmes.core.common.configuration.Parameter.STREAMING_STATUS_UPDATE_DELAY_SECONDS;
import static org.easymock.EasyMock.*;

public class TransportServiceImplTest {

    @Test
    public void testAddUpnpDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        deviceDao.addDevice(isA(UpnpDevice.class));
        expectLastCall().atLeastOnce();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.addDevice(new UpnpDevice("id", "name", "hostAddress", null, null));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testAddAirplayDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        deviceDao.addDevice(isA(AirplayDevice.class));
        expectLastCall().atLeastOnce();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.addDevice(new AirplayDevice("id", "name", "hostAddress", 0));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testRemoveDevice() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        sessionDao.removeDevice("deviceId");
        expectLastCall();
        deviceDao.removeDevice("deviceId");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.removeDevice("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testFindDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.findDevices("hostAddress")).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.findDevices("hostAddress");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetDevices() {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevices()).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getDevices();

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    public void testGetSession() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(sessionDao.getSession("deviceId")).andReturn(null);
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getSession("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = UnknownSessionException.class)
    public void testGetSessionUnknownDevice() throws UnknownSessionException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(sessionDao.getSession("deviceId")).andThrow(new UnknownSessionException("deviceId"));
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.getSession("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnUpnpDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        upnpDeviceStreamer.play(isA(UpnpDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPlayOnAirplayDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        airplayDeviceStreamer.play(isA(AirplayDevice.class), eq("contentUrl"), isA(AbstractNode.class));
        expectLastCall();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayOnFakeDevice() throws UnknownDeviceException, IOException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));
        sessionDao.initSession("deviceId", "contentUrl", "contentName");
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        File file = File.createTempFile("contentNode", "avi");
        file.deleteOnExit();

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.play("deviceId", "contentUrl", new ContentNode("contentNodeId", "parentNodeId", "contentName", file, new MimeType("video/avi")));

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.stop(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testStopOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.stop(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.stop("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.pause(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testPauseOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.pause(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPauseOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.pause("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnUpnpDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new UpnpDevice("id", "name", "hostAddress", null, null));
        upnpDeviceStreamer.resume(isA(UpnpDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testResumeOnAirplayDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new AirplayDevice("id", "name", "hostAddress", 0));
        airplayDeviceStreamer.resume(isA(AirplayDevice.class));
        expectLastCall();
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testResumeOnFakeDevice() throws UnknownDeviceException {
        DeviceDao deviceDao = createMock(DeviceDao.class);
        SessionDao sessionDao = createMock(SessionDao.class);
        DeviceStreamer upnpDeviceStreamer = createMock(DeviceStreamer.class);
        DeviceStreamer airplayDeviceStreamer = createMock(DeviceStreamer.class);
        Configuration configuration = createMock(Configuration.class);

        expect(deviceDao.getDevice("deviceId")).andReturn(new FakeDevice("id", "name", "hostAddress"));
        expect(configuration.getIntParameter(STREAMING_STATUS_UPDATE_DELAY_SECONDS)).andReturn(0).atLeastOnce();

        replay(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);

        TransportServiceImpl transportService = new TransportServiceImpl(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
        transportService.resume("deviceId");

        verify(configuration, deviceDao, sessionDao, upnpDeviceStreamer, airplayDeviceStreamer);
    }

    private class FakeDevice extends Device {
        /**
         * Instantiates a new device
         *
         * @param id          device id
         * @param name        device name
         * @param hostAddress device host
         */
        public FakeDevice(String id, String name, String hostAddress) {
            super(id, name, hostAddress);
        }
    }
}