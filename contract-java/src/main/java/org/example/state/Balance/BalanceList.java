/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example.state.Balance;

import java.util.List;

import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class BalanceList {

    private StateList stateList;

    public BalanceList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, BalanceList.class.getSimpleName(), Balance::deserialize);
    }

    public BalanceList addBalance(Balance Balance) {
        stateList.addState(Balance);
        return this;
    }

    public Balance getBalance(String key) {
        return (Balance) this.stateList.getState(key);
    }

    public List<Balance> getBalanceByRange(String startKey, String endKey) {
        return (List<Balance>)(List<?>) this.stateList.getStateByRange(startKey, endKey);
    }

    public BalanceList updateBalance(Balance Balance) {
        this.stateList.updateState(Balance);
        return this;
    }

    public BalanceList deleteBalance(String platformCode) {
        this.stateList.deleteState(platformCode);
        return this;
    }
}
