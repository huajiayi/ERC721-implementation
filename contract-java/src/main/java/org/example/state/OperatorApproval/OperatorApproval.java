package org.example.state.OperatorApproval;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class OperatorApproval extends State {

  @Property()
  private String owner;

  @Property()
  private String operator;

  @Property()
  private boolean approved;

  public OperatorApproval() {
    super();
  }

  public static String makeKey(String owner, String operator) {
    return owner + ":" + operator;
  }

  public OperatorApproval setKey() {
      this.key = OperatorApproval.makeKey(this.owner, this.operator);
      return this;
  }

  public String getOwner() {
    return owner;
  }

  public OperatorApproval setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public String getOperator() {
    return operator;
  }

  public OperatorApproval setOperator(String operator) {
    this.operator = operator;
    return this;
  }

  public boolean isApproved() {
    return approved;
  }

  public OperatorApproval setApproved(boolean approved) {
    this.approved = approved;
    return this;
  }

  /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */
    public static OperatorApproval deserialize(byte[] _data) {
      JSONObject json = new JSONObject(new String(_data, UTF_8));

      String owner = json.getString("owner");
      String operator = json.getString("operator");
      boolean approved = json.getBoolean("approved");
      return createInstance(owner, operator, approved);
  }

  public static byte[] serialize(OperatorApproval paper) {
      return State.serialize(paper);
  }

  /**
   * Factory method to create a commercial paper object
   */
  public static OperatorApproval createInstance(String owner, String operator, boolean approved) {
      return new OperatorApproval()
          .setOwner(owner)
          .setOperator(operator)
          .setApproved(approved)
          .setKey();
  }
   
}
