package org.example.event;

public class ApprovalEvent extends Event {

  private String owner;

  private String approved;

  private String tokenId;

  public String getOwner() {
    return owner;
  }

  public ApprovalEvent setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public String getApproved() {
    return approved;
  }

  public ApprovalEvent setApproved(String approved) {
    this.approved = approved;
    return this;
  }

  public String getTokenId() {
    return tokenId;
  }

  public ApprovalEvent setTokenId(String tokenId) {
    this.tokenId = tokenId;
    return this;
  }

}
