package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.NoSlotAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class ReturnBinaIT extends BaseIT {

    @Test
    public void successReturnBinaTest() throws BadInit_Exception, NoBinaAvail_Exception {
        client.testInit(0, 0, 10, 0);
        try {
            client.getBina();

            StationView sv = client.getInfo();
            Assert.assertEquals(sv.getAvailableBinas(), 9);
            Assert.assertEquals(sv.getFreeDocks(), 1);
            Assert.assertEquals(sv.getTotalGets(), 1);

            client.returnBina();

            sv = client.getInfo();
            Assert.assertEquals(sv.getAvailableBinas(), 10);
            Assert.assertEquals(sv.getFreeDocks(), 0);
            Assert.assertEquals(sv.getTotalReturns(), 1);
        } catch (NoSlotAvail_Exception e) {
            Assert.fail();
        }
    }

    @Test(expected = NoSlotAvail_Exception.class)
    public void noAvailSlotReturnBinaTest() throws BadInit_Exception, NoSlotAvail_Exception {
        client.testInit(0, 0, 10, 0);
        client.returnBina();

    }

    @Override
    @After
    public void clear() {
        super.clear();
    }
}
