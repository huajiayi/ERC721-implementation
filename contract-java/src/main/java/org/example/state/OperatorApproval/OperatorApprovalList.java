/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example.state.OperatorApproval;

import java.util.List;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class OperatorApprovalList {

    private StateList stateList;

    public OperatorApprovalList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, OperatorApprovalList.class.getSimpleName(), OperatorApproval::deserialize);
    }

    public OperatorApprovalList addOperatorApproval(OperatorApproval OperatorApproval) {
        stateList.addState(OperatorApproval);
        return this;
    }

    public OperatorApproval getOperatorApproval(String key) {
        return (OperatorApproval) this.stateList.getState(key);
    }

    public List<OperatorApproval> getOperatorApprovalByRange(String startKey, String endKey) {
        return (List<OperatorApproval>)(List<?>) this.stateList.getStateByRange(startKey, endKey);
    }

    public OperatorApprovalList updateOperatorApproval(OperatorApproval OperatorApproval) {
        this.stateList.updateState(OperatorApproval);
        return this;
    }

    public OperatorApprovalList deleteOperatorApproval(String platformCode) {
        this.stateList.deleteState(platformCode);
        return this;
    }
}
