package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "product_packet_classification")
public class PacketClassification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_id")
    private Integer classificationId;

    @Column(name = "classification_name")
    private String classificationName;

    @JsonIgnore
    @ManyToMany(mappedBy = "packetClassificationTags",fetch = FetchType.LAZY)
    private Set<Party> parties = new HashSet<>();

    @Column(name = "created_on",updatable = false)
    @CreationTimestamp
    private Date createdOn;

    @Column(name = "updated_on")
    @UpdateTimestamp
    private Date updatedOn;

    public PacketClassification() {
    }

    public PacketClassification(String classificationName) {
        this.classificationName = classificationName;
    }

    public void addParty(Party party){
        this.getParties().add(party);
        party.getPacketClassificationTags().add(this);
    }

    public Integer getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(Integer classificationId) {
        this.classificationId = classificationId;
    }

    public String getClassificationName() {
        return classificationName;
    }

    public void setClassificationName(String classificationName) {
        this.classificationName = classificationName;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }

    public Set<Party> getParties() {
        return parties;
    }

    public void setParties(Set<Party> parties) {
        this.parties = parties;
    }

}
