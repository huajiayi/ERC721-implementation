/*
SPDX-License-Identifier: Apache-2.0
*/
package org.example;

import java.util.List;
import java.util.logging.Logger;

import org.example.constant.ContractConstant;
import org.example.event.ApprovalEvent;
import org.example.event.ApprovalForAllEvent;
import org.example.event.TransferEvent;
import org.example.ledgerapi.State;
import org.example.state.Balance.Balance;
import org.example.state.NFT.NFT;
import org.example.state.OperatorApproval.OperatorApproval;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ChaincodeException;

@Contract(
        name = "basic",
        info = @Info(
                title = "Asset Transfer",
                description = "The hyperlegendary asset transfer",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "a.transfer@example.com",
                        name = "Adrian Transfer",
                        url = "https://hyperledger.example.com")))
@Default
public class BasicContract implements ContractInterface, IERC721 {

    // use the classname for the logger, this way you can refactor
    private final static Logger LOG = Logger.getLogger(BasicContract.class.getName());

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new ERC721Context(stub);
    }

    public BasicContract() {

    }

    @Transaction
    public void instantiate(ERC721Context ctx) {
        // No implementation required with this example
        // It could be where data migration is performed, if necessary
        LOG.info("No data migration to perform");
    }

    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String msgSender(ERC721Context ctx) {
        return ctx.getClientIdentity().getId();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public int balanceOf(ERC721Context ctx, String owner) {
        require(owner != Utils.address(0), "balance query for the zero address");
        Balance ownerBalance = ctx.balanceList.getBalance(owner);
        if(null == ownerBalance) {
            return 0;
        }

        return ownerBalance.getBalance();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String ownerOf(ERC721Context ctx, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();
        require(owner != Utils.address(0), "balance query for the zero address");
        return owner;
    }

    // fabric不允许方法重载
    // @Override
    // @Transaction(intent = Transaction.TYPE.SUBMIT)
    // public void safeTransferFrom(ERC721Context ctx, String from, String to, String tokenId, String data) {
    //     String msgSender = ctx.getClientIdentity().getId();
    //     require(to != Utils.address(0), "transfer to the zero address");
    //     require(_isApprovedOrOwner(ctx, msgSender, tokenId), "transfer caller is not owner nor approved");

    //    _transferFrom(ctx, from, to, tokenId, data);
    // }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void safeTransferFrom(ERC721Context ctx, String from, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        require(to != Utils.address(0), "transfer to the zero address");
        require(_isApprovedOrOwner(ctx, msgSender, tokenId), "transfer caller is not owner nor approved");

        _transferFrom(ctx, from, to, tokenId, "");
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void transferFrom(ERC721Context ctx, String from, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        require(to != Utils.address(0), "transfer to the zero address");
        require(_isApprovedOrOwner(ctx, msgSender, tokenId), "transfer caller is not owner nor approved");

        _transferFrom(ctx, from, to, tokenId, "");
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void approve(ERC721Context ctx, String to, String tokenId) {
        String msgSender = ctx.getClientIdentity().getId();
        NFT nft = ctx.nftList.getNFT(tokenId);
        String owner = nft.getOwner();
        
        require(to != owner, "approval to current owner");

        require(
            msgSender.equals(owner) || isApprovedForAll(ctx, owner, msgSender),
            "approve caller is not owner nor approved for all"
        );
       
        _approve(ctx, to, tokenId);
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void setApprovalForAll(ERC721Context ctx, String operator, boolean approved) {
        String msgSender = ctx.getClientIdentity().getId();
        require(msgSender != operator, "approve to caller");

        OperatorApproval operatorApproval = OperatorApproval.createInstance(msgSender, operator, approved);
        ctx.operatorApprovalList.updateOperatorApproval(operatorApproval);

        ApprovalForAllEvent event = new ApprovalForAllEvent().setOwner(msgSender).setOperator(operator).setApproved(approved);
        ctx.getStub().setEvent("Approval", event.serialize());
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String getApproved(ERC721Context ctx, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        return nft.getTokenApproval();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean isApprovedForAll(ERC721Context ctx, String owner, String operator) {
        String key = OperatorApproval.makeKey(owner, operator);
        OperatorApproval operatorApproval = ctx.operatorApprovalList.getOperatorApproval(key);
        if(null == operatorApproval) {
            return false;
        }

        return operatorApproval.isApproved();
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public NFT mint(ERC721Context ctx, String to, String tokenId) {
        require(to != Utils.address(0), "mint to the zero address");
        require(
            !_exists(ctx, tokenId),
            String.format("token %s already minted", tokenId)
        );

        NFT nft = NFT.createInstance(tokenId, to, to, "");
        ctx.nftList.addNFT(nft);

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null == toBalance) {
            Balance newBalance = Balance.createInstance(to, 1);
            ctx.balanceList.addBalance(newBalance);
        } else {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }

        TransferEvent event = new TransferEvent().setFrom(Utils.address(0)).setTo(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());

        return nft;
    }

    @Override
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void burn(ERC721Context ctx, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();

        _approve(ctx, Utils.address(0), tokenId);

        Balance ownerBalance = ctx.balanceList.getBalance(owner);
        ownerBalance.setBalance(ownerBalance.getBalance() - 1);
        ctx.balanceList.updateBalance(ownerBalance);
        
        nft.setOwner(Utils.address(0));
        ctx.nftList.updateNFT(nft);

        TransferEvent event = new TransferEvent().setFrom(owner).setTo(Utils.address(0)).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());
    }

    private NFT _getNft(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        require(
            null != nft,
            String.format("token %s does not exist", tokenId)
        );

        return nft;
    }

    private void _transferFrom(ERC721Context ctx, String from, String to, String tokenId, String data) {
        NFT nft = _getNft(ctx, tokenId);
        require(
            from.equals(nft.getOwner()),
            String.format("%s is not owner of token %s", from, tokenId)
        );

        // Clear approvals from the previous owner
        _approve(ctx, "", tokenId);

        Balance fromBalance = ctx.balanceList.getBalance(from);
        if(null != fromBalance) {
            fromBalance.setBalance(fromBalance.getBalance() - 1);
            ctx.balanceList.updateBalance(fromBalance);
        }

        Balance toBalance = ctx.balanceList.getBalance(to);
        if(null != toBalance) {
            toBalance.setBalance(toBalance.getBalance() + 1);
            ctx.balanceList.updateBalance(toBalance);
        }

        nft.setOwner(to);
        ctx.nftList.updateNFT(nft);

        TransferEvent event = new TransferEvent().setFrom(from).setTo(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Transfer", event.serialize());
    }

    private void _approve(ERC721Context ctx, String to, String tokenId) {
        NFT nft = _getNft(ctx, tokenId);
        nft.setTokenApproval(to);
        ctx.nftList.updateNFT(nft);

        ApprovalEvent event = new ApprovalEvent().setOwner(nft.getOwner()).setApproved(to).setTokenId(tokenId);
        ctx.getStub().setEvent("Approval", event.serialize());
    }

    private boolean _isApprovedOrOwner(ERC721Context ctx, String spender, String tokenId) {
        require(_exists(ctx, tokenId), "operator query for nonexistent token");
        NFT nft = _getNft(ctx, tokenId);
        String owner = nft.getOwner();

        return (spender.equals(owner) || getApproved(ctx, tokenId).equals(spender) || isApprovedForAll(ctx, owner, spender));
    }

    private boolean _exists(ERC721Context ctx, String tokenId) {
        NFT nft = ctx.nftList.getNFT(tokenId);
        return null != nft && Utils.address(0) != nft.getOwner();
    }

    public void require(boolean condition, String message) {
        if(!condition) {
            System.out.println(message);
            throw new ChaincodeException(message);
        }
    }
}
