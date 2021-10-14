/*
 * Copyright IBM Corp. All Rights Reserved.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// Running TestApp: 
// gradle runApp 

package application.java;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.milagro.amcl.RSA2048.private_key;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;


public class App {

	private final static String appUser1 = "appUser1";
	private final static String appUser2 = "appUser2";
	private static String appUser1_id;
	private static String appUser2_id;

	static {
			System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	// helper function for getting connected to the gateway
	public static Gateway connect(String userId) throws Exception{
			// Load a file system based wallet for managing identities.
			Path walletPath = Paths.get("/root/ERC721-implementation/wallet");
			Wallet wallet = Wallets.newFileSystemWallet(walletPath);
			// load a CCP
			Path networkConfigPath = Paths.get("/root/fabric/fabric-samples/test-network/organizations/peerOrganizations/org1.example.com/connection-org1.yaml");

			Gateway.Builder builder = Gateway.createBuilder();
			builder.identity(wallet, userId).networkConfig(networkConfigPath).discovery(true);
			return builder.connect();
	}

	public static void main(String[] args) throws Exception {
			// enrolls the admin and registers the user
			try {
					EnrollAdmin.main(null);
					RegisterUser.main(appUser1);
					RegisterUser.main(appUser2);
			} catch (Exception e) {
					System.err.println(e);
			}

			// connect to the network and invoke the smart contract
			try (Gateway gateway = connect(appUser1)) {

					// get the network and contract
					Network network = gateway.getNetwork("mychannel");
					Contract contract = network.getContract("basic");

					byte[] result;

					result = contract.evaluateTransaction("msgSender", appUser1);
					appUser1_id = new String(result);

					// 添加监听事件
					contract.addContractListener((event) -> {
						String name = event.getName();
						String payload = new String(event.getPayload().get());
						System.out.println(String.format("%s has been triggered, payload is %s", name, payload));
					});

					String tokenId = "nft1";
					System.out.println("\n");
					System.out.println(String.format("create a nft, tokenId is %s", tokenId));
					contract.submitTransaction("mint", appUser1_id, tokenId);

					System.out.println("\n");
					result = contract.evaluateTransaction("balanceOf", appUser1_id);
					System.out.println(String.format("balance of %s is %s", appUser1_id, new String(result)));

					System.out.println("\n");
					result = contract.evaluateTransaction("ownerOf", tokenId);
					System.out.println(String.format("owner of %s is %s", tokenId, new String(result)));

					System.out.println("\n");
					result = contract.evaluateTransaction("getApproved", tokenId);
					System.out.println(String.format("approval of %s is %s", tokenId, new String(result)));

					gateway.close();
			}
			catch(Exception e){
					System.err.println(e);
			}

			try (Gateway gateway = connect(appUser2)) {

				// get the network and contract
				Network network = gateway.getNetwork("mychannel");
				Contract contract = network.getContract("basic");

				// 添加监听事件
				contract.addContractListener((event) -> {
					String name = event.getName();
					String payload = new String(event.getPayload().get());
					System.out.println(String.format("%s has been triggered, payload is %s", name, payload));
				});

				byte[] result;

				result = contract.evaluateTransaction("msgSender", appUser2);
				appUser2_id = new String(result);

				String tokenId = "nft2";
				System.out.println("\n");
				System.out.println(String.format("create a nft, tokenId is %s", tokenId));
				contract.submitTransaction("mint", appUser2_id, tokenId);

				System.out.println("\n");
				result = contract.evaluateTransaction("balanceOf", appUser2_id);
				System.out.println(String.format("balance of %s is %s", appUser2_id, new String(result)));

				System.out.println("\n");
				result = contract.evaluateTransaction("ownerOf", tokenId);
				System.out.println(String.format("owner of %s is %s", tokenId, new String(result)));

				System.out.println("\n");
				result = contract.evaluateTransaction("getApproved", tokenId);
				System.out.println(String.format("approval of %s is %s", tokenId, new String(result)));

				System.out.println("\n");
				System.out.println(String.format("%s approve %s to %s", appUser2_id, tokenId, appUser1_id));
				contract.submitTransaction("approve", appUser1_id, tokenId);

				System.out.println("\n");
				result = contract.evaluateTransaction("getApproved", tokenId);
				System.out.println(String.format("approval of %s is %s", tokenId, new String(result)));

				System.out.println("\n");
				System.out.println(String.format("%s setApprovalForAll %s to %s", appUser2_id, tokenId, appUser1_id));
				contract.submitTransaction("setApprovalForAll", appUser1_id, "true");

				System.out.println("\n");
				result = contract.evaluateTransaction("isApprovedForAll", appUser2_id, appUser1_id);
				System.out.println(String.format("%s %s approvedForAll of %s", appUser1_id, new String(result).equals("true") ? "has" : "do not have", appUser2_id));

				System.out.println("\n");
				System.out.println(String.format("%s safeTransferFrom %s to %s", appUser2_id, tokenId, appUser1_id));
				contract.submitTransaction("safeTransferFrom", appUser2_id, appUser1_id, tokenId);

				System.out.println("\n");
				result = contract.evaluateTransaction("balanceOf", appUser2_id);
				System.out.println(String.format("balance of %s is %s", appUser2_id, new String(result)));

				System.out.println("\n");
				result = contract.evaluateTransaction("balanceOf", appUser1_id);
				System.out.println(String.format("balance of %s is %s", appUser1_id, new String(result)));

				System.out.println("\n");
				result = contract.evaluateTransaction("ownerOf", tokenId);
				System.out.println(String.format("owner of %s is %s", tokenId, new String(result)));

				System.out.println("\n");
				System.out.println(String.format("burn %s", tokenId));
				contract.submitTransaction("burn", tokenId);

				System.out.println("\n");
				result = contract.evaluateTransaction("balanceOf", appUser1_id);
				System.out.println(String.format("balance of %s is %s", appUser1_id, new String(result)));

				System.out.println("\n");
				result = contract.evaluateTransaction("ownerOf", tokenId);
				System.out.println(String.format("owner of %s is %s", tokenId, new String(result)));
		}
		catch(Exception e){
				System.err.println(e);
		}

	}
}