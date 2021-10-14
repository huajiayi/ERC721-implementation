package org.example.event;

public class TransferEvent extends Event {

  private String from;

  private String to;

  private String tokenId;

  public String getFrom() {
    return from;
  }

  public TransferEvent setFrom(String from) {
    this.from = from;
    return this;
  }

  public String getTo() {
    return to;
  }

  public TransferEvent setTo(String to) {
    this.to = to;
    return this;
  }

  public String getTokenId() {
    return tokenId;
  }

  public TransferEvent setTokenId(String tokenId) {
    this.tokenId = tokenId;
    return this;
  }

}
