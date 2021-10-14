package org.example;

import org.example.state.NFT.NFT;

public interface IERC721 {

  int balanceOf(ERC721Context ctx, String owner);

  String ownerOf(ERC721Context ctx, String tokenId);

  // void safeTransferFrom(ERC721Context ctx, String from, String to, String tokenId, String data);

  void safeTransferFrom(ERC721Context ctx, String from, String to, String tokenId);

  void transferFrom(ERC721Context ctx, String from, String to, String tokenId);

  void approve(ERC721Context ctx, String to, String tokenId);

  void setApprovalForAll(ERC721Context ctx, String operator, boolean approved);

  String getApproved(ERC721Context ctx, String tokenId);

  boolean isApprovedForAll(ERC721Context ctx, String owner, String operator);

  NFT mint(ERC721Context ctx, String to, String tokenId);

  void burn(ERC721Context ctx,String tokenId);

}