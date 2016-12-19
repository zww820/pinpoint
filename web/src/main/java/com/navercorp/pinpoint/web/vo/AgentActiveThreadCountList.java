/*
 *
 *  * Copyright 2014 NAVER Corp.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 *
 */

package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.navercorp.pinpoint.thrift.dto.command.TCmdActiveThreadCountRes;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @Author Taejin Koo
 */
@JsonSerialize(using = AgentActiveThreadCountListSerializer.class)
public class AgentActiveThreadCountList {

    private final List<AgentActiveThreadCount> agentActiveThreadRepository;

    public AgentActiveThreadCountList() {
        agentActiveThreadRepository = new ArrayList<>();
    }

    public AgentActiveThreadCountList(int initialCapacity) {
        agentActiveThreadRepository = new ArrayList<>(initialCapacity);
    }

    public void add(AgentActiveThreadCount agentActiveThreadStatus) {
        agentActiveThreadRepository.add(agentActiveThreadStatus);
    }

    public List<AgentActiveThreadCount> getAgentActiveThreadRepository() {
        // sort agentId
        Collections.sort(agentActiveThreadRepository, new Comparator<AgentActiveThreadCount>() {
            @Override
            public int compare(AgentActiveThreadCount o1, AgentActiveThreadCount o2) {
                final String agentId1 = StringUtils.defaultString(o1.getAgentId(), "");
                final String agentId2 = StringUtils.defaultString(o2.getAgentId(), "");
                return agentId1.compareTo(agentId2);
            }
        });
        return agentActiveThreadRepository;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentActiveThreadCountList{");
        sb.append("agentActiveThreadRepository=").append(agentActiveThreadRepository);
        sb.append('}');
        return sb.toString();
    }
}

class AgentActiveThreadCountListSerializer extends JsonSerializer<AgentActiveThreadCountList>
{
    @Override
    public void serialize(AgentActiveThreadCountList agentActiveThreadStatusList, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        List<AgentActiveThreadCount> agentActiveThreadRepository = agentActiveThreadStatusList.getAgentActiveThreadRepository();

        jgen.writeStartObject();

        for (AgentActiveThreadCount agentActiveThread : agentActiveThreadRepository) {
            jgen.writeFieldName(agentActiveThread.getAgentId());
            jgen.writeStartObject();

            jgen.writeNumberField("code", agentActiveThread.getCode());
            jgen.writeStringField("message", agentActiveThread.getCodeMessage());

            TCmdActiveThreadCountRes activeThreadCount = agentActiveThread.getActiveThreadCount();
            if (activeThreadCount != null) {
                if (activeThreadCount.getActiveThreadCountSize() >= 4) {
                    List<Integer> values = activeThreadCount.getActiveThreadCount();

                    jgen.writeFieldName("status");
                    jgen.writeStartArray();
                    jgen.writeNumber(values.get(0));
                    jgen.writeNumber(values.get(1));
                    jgen.writeNumber(values.get(2));
                    jgen.writeNumber(values.get(3));
                    jgen.writeEndArray();
                }
            }

            jgen.writeEndObject();
        }

        jgen.writeEndObject();
    }
}
