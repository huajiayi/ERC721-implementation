/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example.state.NFT;

import java.util.List;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class NFTList {

    private StateList stateList;

    public NFTList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, NFTList.class.getSimpleName(), NFT::deserialize);
    }

    public NFTList addNFT(NFT NFT) {
        stateList.addState(NFT);
        return this;
    }

    public NFT getNFT(String key) {
        return (NFT) this.stateList.getState(key);
    }

    public List<NFT> getNFTByRange(String startKey, String endKey) {
        return (List<NFT>)(List<?>) this.stateList.getStateByRange(startKey, endKey);
    }

    public NFTList updateNFT(NFT NFT) {
        this.stateList.updateState(NFT);
        return this;
    }

    public NFTList deleteNFT(String platformCode) {
        this.stateList.deleteState(platformCode);
        return this;
    }
}
