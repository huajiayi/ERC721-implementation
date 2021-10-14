package org.example.state.Balance;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class Balance extends State {

  @Property()
  private String owner;

  @Property()
  private int balance;

  public Balance() {
    super();
  }

  public Balance setKey() {
      this.key = this.owner;
      return this;
  }

  public String getOwner() {
    return owner;
  }

  public Balance setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public int getBalance() {
    return balance;
  }

  public Balance setBalance(int balance) {
    this.balance = balance;
    return this;
  }

  /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */
    public static Balance deserialize(byte[] _data) {
      JSONObject json = new JSONObject(new String(_data, UTF_8));

      String owner = json.getString("owner");
      int balance = json.getInt("balance");
      return createInstance(owner, balance);
  }

  public static byte[] serialize(Balance paper) {
      return State.serialize(paper);
  }

  /**
   * Factory method to create a commercial paper object
   */
  public static Balance createInstance(String owner, int balance) {
      return new Balance()
          .setOwner(owner)
          .setBalance(balance)
          .setKey();
  }
   
}
