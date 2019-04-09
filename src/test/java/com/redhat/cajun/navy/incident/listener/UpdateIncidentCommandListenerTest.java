package com.redhat.cajun.navy.incident.listener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import com.redhat.cajun.navy.incident.model.ReportedIncident;
import com.redhat.cajun.navy.incident.service.ReportedIncidentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

public class UpdateIncidentCommandListenerTest {

    @Mock
    private ReportedIncidentService reportedIncidentService;

    @Captor
    private ArgumentCaptor<ReportedIncident> incidentCaptor;

    private UpdateIncidentCommandListener listener;

    @Before
    public void init() {
        initMocks(this);
        listener = new UpdateIncidentCommandListener();
        setField(listener, null, reportedIncidentService, ReportedIncidentService.class);
    }

    @Test
    public void testProcessMessage() {

        String json = "{\"messageType\" : \"UpdateIncidentCommand\"," +
                "\"id\" : \"messageId\"," +
                "\"invokingService\" : \"messageSender\"," +
                "\"timestamp\" : 1521148332397," +
                "\"body\" : {" +
                "\"incident\" : {" +
                "\"id\" : \"qwerty\"," +
                "\"status\" : \"PICKEDUP\"" +
                "} " + "} " + "}";

        listener.processMessage(json);
        verify(reportedIncidentService).updateIncident(incidentCaptor.capture());
        ReportedIncident captured = incidentCaptor.getValue();
        assertThat(captured, notNullValue());
        assertThat(captured.getId(), equalTo("qwerty"));
        assertThat(captured.getStatus(), equalTo("PICKEDUP"));
        assertThat(captured.getLat(), nullValue());
        assertThat(captured.getLon(), nullValue());
        assertThat(captured.getNumberOfPeople(), nullValue());
        assertThat(captured.isMedicalNeeded(), nullValue());
        assertThat(captured.getVictimName(), nullValue());
        assertThat(captured.getVictimPhoneNumber(), nullValue());
    }

    @Test
    public void testProcessMessageWrongMessageType() {

        String json = "{\"messageType\":\"WrongType\"," +
                "\"id\":\"messageId\"," +
                "\"invokingService\":\"messageSender\"," +
                "\"timestamp\":1521148332397," +
                "\"body\":{} " +
                "}";

        listener.processMessage(json);

        verify(reportedIncidentService, never()).updateIncident(any(ReportedIncident.class));
    }

    @Test
    public void testProcessMessageWrongMessage() {
        String json = "{\"field1\":\"value1\"," +
                "\"field2\":\"value2\"}";

        listener.processMessage(json);

        verify(reportedIncidentService, never()).updateIncident(any(ReportedIncident.class));
    }

}