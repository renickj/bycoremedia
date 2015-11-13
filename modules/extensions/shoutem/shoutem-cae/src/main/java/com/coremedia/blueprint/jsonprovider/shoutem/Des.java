package com.coremedia.blueprint.jsonprovider.shoutem;

import com.coremedia.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Encryption for Shoutem credentials.
 */
final class Des {

  private static final String ERROR_MESSAGE_ENCRYPTION = "Error during DES encrypting: ";
  private static final String ERROR_MESSAGE_DECRYPTION = "Error during DES decrypting: ";

  /**
   * Hide Utility Class Constructor
   */
  private Des() {

  }

  private static final Logger LOG = LoggerFactory.getLogger(Des.class);

  private static final String DES = "DES";
  private static final String ENCODING = "UTF8";
  private static final String PASS = "7593jf86";

  public static String encrypt(String str) {
    try {
      Key key = new SecretKeySpec(PASS.getBytes(), DES);
      Cipher cipher = Cipher.getInstance(DES);
      cipher.init(Cipher.ENCRYPT_MODE, key);

      // Encode the string into bytes using utf-8
      byte[] utf8 = str.getBytes(ENCODING);

      // Encrypt
      byte[] enc = cipher.doFinal(utf8);

      // Encode bytes to base64 to get a string
      return Base64.encode(enc);
    } catch (Exception e) {//NOSONAR
      LOG.error(ERROR_MESSAGE_ENCRYPTION + e.getMessage(), e);
    }
    return null;
  }

  public static String decrypt(String str) {
    try {
      Key key = new SecretKeySpec(PASS.getBytes(), DES);
      Cipher cipher = Cipher.getInstance(DES);
      cipher.init(Cipher.DECRYPT_MODE, key);

      // Decode base64 to get bytes
      String formattedValue = str.replaceAll("\\\\n", "\\\n");
      formattedValue = formattedValue.replaceAll("\\\\r", "\\\r");
      byte[] dec = Base64.decode(formattedValue);

      // Decrypt
      byte[] utf8 = cipher.doFinal(dec);

      // Decode using utf-8
      return new String(utf8, ENCODING);
    } catch (NoSuchPaddingException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    } catch (UnsupportedEncodingException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    } catch (NoSuchAlgorithmException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    } catch (BadPaddingException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    } catch (IllegalBlockSizeException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    } catch (InvalidKeyException e) {
      LOG.error(ERROR_MESSAGE_DECRYPTION + e.getMessage(), e);
    }
    return null;
  }
}
