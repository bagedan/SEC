package com.cep.event.generators;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Tkachi on 2015/12/29.
 */
public class PriceChangeEventGeneratorTest {

    private PriceChangeEventGenerator testee = new PriceChangeEventGenerator();

    @Test
    public void testUpdatePrices() throws Exception {
        Map<String, Double> prices = new HashMap<>();
        prices.put("test", 100.0);
        for(int i=0;i<1000;i++){
            testee.updatePrices(prices);
        }

        //max diff can be 1.01^1000 = 21000

        double increase = prices.get("test")/100.0;
        System.out.println(increase);
        assertTrue(increase<21000);
        assertTrue(increase>-21000);
    }
}