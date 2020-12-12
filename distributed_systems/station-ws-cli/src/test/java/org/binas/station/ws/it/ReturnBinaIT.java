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
            Assert.assertEquals(9, sv.getAvailableBinas());
            Assert.assertEquals(1, sv.getFreeDocks());
            Assert.assertEquals(1, sv.getTotalGets());

            client.returnBina();

            sv = client.getInfo();
            Assert.assertEquals(10, sv.getAvailableBinas());
            Assert.assertEquals(0, sv.getFreeDocks());
            Assert.assertEquals(1, sv.getTotalReturns());
        } catch (NoSlotAvail_Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void success2ReturnBinaTest() throws BadInit_Exception, NoBinaAvail_Exception {
        client.testInit(0, 0, 10, 0);
        try {
            client.getBina();
            client.getBina();
            client.getBina();

            StationView sv = client.getInfo();
            Assert.assertEquals(7, sv.getAvailableBinas());
            Assert.assertEquals(3, sv.getFreeDocks());
            Assert.assertEquals(3, sv.getTotalGets());

            client.returnBina();
            client.returnBina();

            sv = client.getInfo();
            Assert.assertEquals(9, sv.getAvailableBinas());
            Assert.assertEquals(1, sv.getFreeDocks());
            Assert.assertEquals(2, sv.getTotalReturns());
        } catch (NoSlotAvail_Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void noAvailSlotReturnBinaTest() throws BadInit_Exception {
        client.testInit(0, 0, 10, 0);
        try {
            client.returnBina();
            Assert.fail();
        } catch (NoSlotAvail_Exception e) {
            StationView sv = client.getInfo();
            Assert.assertEquals(10, sv.getAvailableBinas());
            Assert.assertEquals(0, sv.getFreeDocks());
            Assert.assertEquals(0, sv.getTotalReturns());
        }
    }

    @Override
    @After
    public void clear() {
        super.clear();
    }
}
