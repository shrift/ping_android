package com.bubbletastic.android.ping.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.bubbletastic.android.ping.Ping;
import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.model.proto.HostStatus;
import com.bubbletastic.android.ping.model.proto.PingResult;
import com.bubbletastic.android.ping.service.HostService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.InetAddress;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Created by brendanmartens on 12/31/15.
 */
@RunWith(AndroidJUnit4.class)
public class HostServiceTests extends InstrumentationTestCase {

    private Ping ping;
    private HostService spyService;

    public HostServiceTests() {
        ping = (Ping) InstrumentationRegistry.getTargetContext().getApplicationContext();
        spyService = spy(ping.getHostService());
    }

    @Test
    public void validateSetup() {
        assertNotNull(ping);
        assertNotNull(spyService);
    }

    /**
     * Checks that a call to refreshHost returns the same host.
     */
    @Test
    public void checkRefreshHostHostName() {
        Host host = getTestHost();
        String originalHostName = host.getHostName();
        Host returnedHost = spyService.refreshHost(host);

        assertNotNull(returnedHost);
        assertEquals(returnedHost.getHostName(), originalHostName);
    }

    /**
     * Checks that the correct disconnected state is returned if network is not available.
     */
    @Test
    public void checkRefreshHostStatusDisconnected() {
        when(spyService.isNetworkAvailable()).thenReturn(false);

        Host host = getTestHost();
        Host returnedHost = spyService.refreshHost(host);
        assertNotNull(returnedHost);

        assertEquals(HostStatus.disconnected, returnedHost.getCurrentStatus());
    }

    /**
     * Checks that if a successful ping occurred, refreshHost returns a reachable status.
     */
    @Test
    public void checkRefreshHostReachableStatus() throws IOException {
        Host host = getTestHost();
        when(spyService.isNetworkAvailable()).thenReturn(true);
        when(spyService.pingHost(any(Host.class), anyInt())).thenReturn(getPingResultWithStatus(HostStatus.reachable));

        Host returnedHost = spyService.refreshHost(host);
        assertNotNull(returnedHost);

        assertEquals(HostStatus.reachable, returnedHost.getCurrentStatus());
    }

    /**
     * Checks that if a ping failed, refreshHost returns an unreachable status.
     */
    @Test
    public void checkRefreshHostUnreachableStatus() {
        Host host = getTestHost();
        when(spyService.isNetworkAvailable()).thenReturn(true);
        when(spyService.pingHost(any(Host.class), anyInt())).thenReturn(getPingResultWithStatus(HostStatus.unreachable));

        Host returnedHost = spyService.refreshHost(host);
        assertNotNull(returnedHost);

        assertEquals(HostStatus.unreachable, returnedHost.getCurrentStatus());
    }

    /**
     * Checks that if a ping failed, refreshHost returns an unreachable status.
     */
    @Test
    public void checkRefreshHostUnknownStatus() {
        Host host = getTestHost();
        when(spyService.isNetworkAvailable()).thenReturn(true);
        when(spyService.pingHost(any(Host.class), anyInt())).thenThrow(new RuntimeException());

        Host returnedHost = spyService.refreshHost(host);
        assertNotNull(returnedHost);

        assertEquals(HostStatus.unknown, returnedHost.getCurrentStatus());
    }

    /**
     * Checks that pingHost returns reachable given the correct conditions.
     * @throws IOException
     */
    @Test
    public void checkPingHostReachable() throws IOException {
        Host host = getTestHost();
        //Set a host name that should be unreachable just to help validate our mock is overriding the return result.
        host.setHostName("sdjklsfdjsdfjlksdfjsdfjlksdfjlkfsd.sjklssdf");
        when(spyService.doPing(any(InetAddress.class), anyInt())).thenReturn(true);

        PingResult pingResult = spyService.pingHost(host, 789);
        assertEquals(HostStatus.reachable, pingResult.status);
    }

    /**
     * Checks that pingHost returns unreachable given the correct conditions.
     * @throws IOException
     */
    @Test
    public void checkPingHostUnreachable() throws IOException {
        Host host = getTestHost();
        when(spyService.doPing(any(InetAddress.class), anyInt())).thenReturn(false);

        PingResult pingResult = spyService.pingHost(host, 789);
        assertEquals(HostStatus.unreachable, pingResult.status);
    }

    /**
     * Checks that pingHost returns unknown given the correct conditions.
     * @throws IOException
     */
    @Test
    public void checkPingHostUnknown() throws IOException {
        Host host = getTestHost();
        when(spyService.doPing(any(InetAddress.class), anyInt())).thenThrow(new IOException());
        when(spyService.isNetworkAvailable()).thenReturn(true);

        PingResult pingResult = spyService.pingHost(host, 789);
        assertEquals(HostStatus.unknown, pingResult.status);
    }

    /**
     * Checks that pingHost returns disconnected given the correct conditions.
     * @throws IOException
     */
    @Test
    public void checkPingHostDisconnected() throws IOException {
        Host host = getTestHost();
        when(spyService.doPing(any(InetAddress.class), anyInt())).thenThrow(new IOException());
        when(spyService.isNetworkAvailable()).thenReturn(false);

        PingResult pingResult = spyService.pingHost(host, 789);
        assertEquals(HostStatus.disconnected, pingResult.status);
    }

    /**
     * Checks that getMean actually returns a mean.
     */
    @Test
    public void testGetMean() {
        int[] ints = {88, 78, 36, 279};
        //It is expected for the getMean method to throw away decimal values and only return an integer.
        Integer expected = 120;
        Integer result = spyService.getMean(ints);

        assertEquals(expected, result);

        ints = new int[]{90, 56, 7000, 88, 78, 36, 279};
        expected = 1089;
        result = spyService.getMean(ints);

        assertEquals(expected, result);
    }

    private PingResult getPingResultWithStatus(HostStatus status) {
        return new PingResult.Builder().pinged_at(System.currentTimeMillis()).status(status).round_trip_avg(56).build();
    }

    private Host getTestHost() {
        return new Host("google-public-dns-a.google.com");
    }
}
