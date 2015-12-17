package com.cep.jms;

import com.cep.event.generators.ReadEventsGenerator;
import com.cep.event.generators.ShareEventGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PublisherTest {

    @Mock
    private ShareEventGenerator shareEventGenerator;

    @Mock
    private ReadEventsGenerator readEventsGenerator;

    private Publisher testee;

    @Before
    public void initMocks(){
        testee = new Publisher(readEventsGenerator, shareEventGenerator);
    }

    @Test
    public void testMain() {
        int readNo = 5;
        int shareNo = 6;
        testee.generateEvents(readNo, shareNo);

        verify(shareEventGenerator, times(1)).generateEvents(shareNo);
        verify(readEventsGenerator, times(1)).generateEvents(readNo);
    }
}
