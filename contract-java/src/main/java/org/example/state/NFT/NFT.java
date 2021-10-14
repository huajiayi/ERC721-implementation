/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.example.state.NFT;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.example.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;

@DataType()
public class NFT extends State {

    @Property()
    private String tokenId;

    @Property()
    private String owner;

    @Property()
    private String tokenApproval;

    @Property()
    private String data;

    public NFT() {
        super();
    }

    public NFT setKey() {
        this.key = this.tokenId;
        return this;
    }

    public String getTokenId() {
        return tokenId;
    }

    public NFT setTokenId(String tokenId) {
        this.tokenId = tokenId;
        return this;
    }

    public String getOwner() {
        return owner;
    }

    public NFT setOwner(String owner) {
        this.owner = owner;
        return this;
    }

    public String getTokenApproval() {
        return tokenApproval;
    }

    public NFT setTokenApproval(String tokenApproval) {
        this.tokenApproval = tokenApproval;
        return this;
    }

    public String getData() {
        return data;
    }

    public NFT setData(String data) {
        this.data = data;
        return this;
    }

    /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */
    public static NFT deserialize(byte[] _data) {
        JSONObject json = new JSONObject(new String(_data, UTF_8));

        String tokenId = json.getString("tokenId");
        String owner = json.getString("owner");
        String tokenApproval = json.getString("tokenApproval");
        String data = json.getString("data");
        return createInstance(tokenId, owner, tokenApproval, data);
    }

    public static byte[] serialize(NFT paper) {
        return State.serialize(paper);
    }

    /**
     * Factory method to create a commercial paper object
     */
    public static NFT createInstance(String tokenId, String owner, String tokenApproval, String data) {
        return new NFT()
            .setTokenId(tokenId)
            .setOwner(owner)
            .setTokenApproval(tokenApproval)
            .setData(data)
            .setKey();
    }
}
