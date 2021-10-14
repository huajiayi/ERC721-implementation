package org.example.ledgerapi.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.example.ledgerapi.State;
import org.example.ledgerapi.StateDeserializer;
import org.example.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

/*
SPDX-License-Identifier: Apache-2.0
*/

/**
 * StateList provides a named virtual container for a set of ledger states. Each
 * state has a unique key which associates it with the container, rather than
 * the container containing a link to the state. This minimizes collisions for
 * parallel transactions on different states.
 */
public class StateListImpl implements StateList {

    private Context ctx;
    private String name;
    private Object supportedClasses;
    private StateDeserializer deserializer;

    /**
     * Store Fabric context for subsequent API access, and name of list
     *
     * @param deserializer
     */
    public StateListImpl(Context ctx, String listName, StateDeserializer deserializer) {
        this.ctx = ctx;
        this.name = listName;
        this.deserializer = deserializer;

    }

    /**
     * Add a state to the list. Creates a new state in worldstate with appropriate
     * composite key. Note that state defines its own key. State object is
     * serialized before writing.
     */
    @Override
    public StateList addState(State state) {
        System.out.println("Adding state " + this.name);
        ChaincodeStub stub = this.ctx.getStub();
        System.out.println("Stub=" + stub);
        String ledgerKey = state.getKey();

        byte[] data = State.serialize(state);
        System.out.println("ctx" + this.ctx);
        System.out.println("stub" + this.ctx.getStub());
        this.ctx.getStub().putState(ledgerKey.toString(), data);

        return this;
    }

    /**
     * Get a state from the list using supplied keys. Form composite keys to
     * retrieve state from world state. State data is deserialized into JSON object
     * before being returned.
     */
    @Override
    public State getState(String ledgerKey) {
        byte[] data = this.ctx.getStub().getState(ledgerKey.toString());
        if (data == null || data.length == 0) {
            return null;
        } else {
            State state = this.deserializer.deserialize(data);
            return state;
        }
    }

    @Override
    public List<State> getStateByRange(String startKey, String endKey) {
        List<State> states = new ArrayList<State>();
        QueryResultsIterator<KeyValue> results = this.ctx.getStub().getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            State state = this.deserializer.deserialize(result.getValue());
            states.add(state);
        }

        return states;
    }

    /**
     * Update a state in the list. Puts the new state in world state with
     * appropriate composite key. Note that state defines its own key. A state is
     * serialized before writing. Logic is very similar to addState() but kept
     * separate becuase it is semantically distinct.
     */
    @Override
    public StateList updateState(State state) {
        String ledgerKey = state.getKey();
        byte[] data = State.serialize(state);
        this.ctx.getStub().putState(ledgerKey.toString(), data);

        return this;
    }

    @Override
    public void deleteState(String key) {
        this.ctx.getStub().delState(key);
    }

}
