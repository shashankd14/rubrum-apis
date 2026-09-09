package com.steel.product.application.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "audit_reqresp_tracker")
@Getter
@Setter
@NoArgsConstructor
public class AuditReqRespTrackerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ip_address")
    private String ipAddress;

    @Lob
    @Column(name = "req_object", columnDefinition = "TEXT")
    private String reqObject;

    @Column(name = "req_time")
    private LocalDateTime reqTime;

    @Lob
    @Column(name = "resp_object", columnDefinition = "TEXT")
    private String respObject;

    @Column(name = "resp_time")
    private LocalDateTime respTime;

    @Column(name = "response_status")
    private String responseStatus;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "request_url")
    private String requestUrl;

    @Column(name = "coil_number")
    private String coilNumber;

    @Column(name = "user_name")
    private String userName;
}
