package org.binas.station.ws.it;

import org.binas.station.ws.BadInit_Exception;
import org.binas.station.ws.NoBinaAvail_Exception;
import org.binas.station.ws.StationView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class GetBinaIT extends BaseIT  {
    @Test
    public void successGetBinasTest1() throws NoBinaAvail_Exception, BadInit_Exception {
        client.testInit(0, 0, 2, 0);
        client.getBina();
        client.getBina();

        StationView sv = client.getInfo();
        Assert.assertEquals(sv.getAvailableBinas(), 0);
        Assert.assertEquals(sv.getFreeDocks(), 2);
        Assert.assertEquals(sv.getTotalGets(), 2);
    }

    @Test(expected = NoBinaAvail_Exception.class)
    public void noAvailGetBinasTest1() throws NoBinaAvail_Exception, BadInit_Exception {
        client.testInit(0, 0, 2, 0);
        client.getBina();
        client.getBina();
        client.getBina();
        client.getBina();
        //System.out.println("AVAILABLE  " + client.getInfo().getAvailableBinas());
    }


    // NA AULA:
    @Test
    public void successGetBinasTest2() throws NoBinaAvail_Exception, BadInit_Exception {
        client.testInit(0, 0, 100, 0);
        for(int i = 0; i < 20; ++i) {
            client.getBina();
            StationView sv = client.getInfo();
            Assert.assertEquals(sv.getAvailableBinas(), 100-i-1);
            Assert.assertEquals(sv.getFreeDocks(), i+1);
            Assert.assertEquals(sv.getTotalGets(), i+1);
        }
    }

    @Test
    public void successGetBinasTest3() throws NoBinaAvail_Exception, BadInit_Exception {
        client.testInit(0, 0, 20, 0);
        for(int i = 0; i < 20; ++i) {
            client.getBina();
            StationView sv = client.getInfo();
            Assert.assertEquals(sv.getAvailableBinas(), 20-i-1);
            Assert.assertEquals(sv.getFreeDocks(), i+1);
            Assert.assertEquals(sv.getTotalGets(), i+1);
        }
    }


    @Test(expected = NoBinaAvail_Exception.class)
    public void noAvailGetBinasTest2() throws NoBinaAvail_Exception, BadInit_Exception {
        client.testInit(0, 0, 10, 0);
        for (int i = 0; i < 20; ++i) {
            client.getBina();
        }
    }

    @Override
    @After
    public void clear() {
        super.clear();
    }

}
