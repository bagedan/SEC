package com.cep.event.generators;

import com.cep.event.Event;
import com.cep.event.ShareArticleEvent;
import com.cep.event.generators.EventPublisher;
import com.cep.event.generators.ShareEventGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ShareEventGeneratorTest {

    @Mock
    private EventPublisher eventPublisher;

    private ShareEventGenerator testee;

    @Before
    public void inti(){
        testee = new ShareEventGenerator().withPublisher(eventPublisher);
    }

    @Test
    public void should_call_publisher(){
        int count = 5;
        testee.generateEvents(count);
        verify(eventPublisher, times(count)).sendMessage(any(Event.class));
    }

    @Test
    public void message_should_have_all_fields(){
        int count = 1;
        testee.generateEvents(count);
        ArgumentCaptor<Event> captor = new ArgumentCaptor<Event>();
        verify(eventPublisher, times(1)).sendMessage(captor.capture());

        ShareArticleEvent event = (ShareArticleEvent) captor.getValue();

        assertThat(event.getArticleId(), containsString("article"));
        assertThat(event.getSharedFromUserId(), containsString("user"));
        assertThat(event.getSharedToUserId(), containsString("user"));

    }

}