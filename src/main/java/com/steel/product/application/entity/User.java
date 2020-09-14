package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.steel.product.application.entity.InwardEntry;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "userid")
  private int userId;
  
  @Column(name = "username")
  private String userName;
  
  @OneToMany(mappedBy = "createdBy", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  @JsonIgnoreProperties({"createdBy"})
  private List<InwardEntry> inwardEntry;
  
  @OneToMany(mappedBy = "updatedBy", cascade = {CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
  @JsonIgnoreProperties({"updatedBy"})
  private List<InwardEntry> inwardEntry1;
  
  public int getUserId() {
    return this.userId;
  }
  
  public void setUserId(int userId) {
    this.userId = userId;
  }
  
  public String getUserName() {
    return this.userName;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }
}
