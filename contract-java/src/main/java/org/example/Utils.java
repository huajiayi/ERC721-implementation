package org.example;

import org.hyperledger.fabric.shim.ChaincodeException;

public class Utils {
  public static void require(boolean condition, String message, String payload) {
    if(!condition) {
      System.out.println(message);
      throw new ChaincodeException(message, payload);
    }
  }

  public static void require(boolean condition, String message) {
    if(!condition) {
      System.out.println(message);
      throw new ChaincodeException(message);
    }
  }

  public static String address(int address) {
    if(address == 0) {
      return "";
    }

    return Integer.toString(address);
  }
}
