package org.example.domain;

import jakarta.persistence.Entity;

import java.util.Objects;

@Entity
public class AgentCredit extends AngajatIMC {
    private String agentCode;

    public AgentCredit() {
    }

    public AgentCredit(String agentCode) {
        this.agentCode = agentCode;
    }

    public String getAgentCode() {
        return agentCode;
    }

    public void setAgentCode(String agentCode) {
        this.agentCode = agentCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AgentCredit that = (AgentCredit) o;
        return Objects.equals(agentCode, that.agentCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(agentCode);
    }
}
