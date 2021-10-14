package org.example;

import org.example.state.Balance.BalanceList;
import org.example.state.NFT.NFTList;
import org.example.state.OperatorApproval.OperatorApprovalList;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class ERC721Context extends Context {

    public ERC721Context(ChaincodeStub stub) {
        super(stub);
        this.nftList = new NFTList(this);
        this.balanceList = new BalanceList(this);
        this.operatorApprovalList = new OperatorApprovalList(this);
    }

    public NFTList nftList;

    public BalanceList balanceList;

    public OperatorApprovalList operatorApprovalList;

}