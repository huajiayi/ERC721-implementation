package org.example.event;

public class ApprovalForAllEvent extends Event {

  private String owner;

  private String operator;

  private boolean approved;

  public String getOwner() {
    return owner;
  }

  public ApprovalForAllEvent setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public String getOperator() {
    return operator;
  }

  public ApprovalForAllEvent setOperator(String operator) {
    this.operator = operator;
    return this;
  }

  public boolean getApproved() {
    return approved;
  }

  public ApprovalForAllEvent setApproved(boolean approved) {
    this.approved = approved;
    return this;
  }

}
