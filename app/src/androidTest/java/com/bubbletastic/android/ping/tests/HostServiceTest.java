package com.bubbletastic.android.ping.tests;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.bubbletastic.android.ping.Ping;
import com.bubbletastic.android.ping.model.Host;
import com.bubbletastic.android.ping.service.HostService;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by brendanmartens on 12/31/15.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class HostServiceTest extends InstrumentationTestCase {

    private Ping ping;
    private HostService hostService;

    public HostServiceTest() {
        ping = (Ping) InstrumentationRegistry.getTargetContext().getApplicationContext();
        hostService = ping.getHostService();
    }

    @Test
    public void validateSetup() {
        assertNotNull(ping);
        assertNotNull(hostService);
    }

    @Test
    public void checkRefreshHost() {
        Host host = getReachableTestHost();
        String originalHostName = host.getHostName();
        Host returnedHost = hostService.refreshHost(host);

        assertNotNull(returnedHost);
        assertEquals(returnedHost.getHostName(), originalHostName);
    }

    private Host getReachableTestHost() {
        return new Host("bubbletastic.com");
    }
}
