package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Creates an httpClient which accepts all https certificates.
 */
class HttpClientFactory {

  protected static HttpClient createHttpClient(boolean trustAllSslCertificates) {
    return createHttpClient(trustAllSslCertificates, true, 200, -1, -1, -1);
  }

  protected static HttpClient createHttpClient(boolean trustAllSslCertificates, boolean acceptCookies, int connectionPoolSize, int socketTimeout, int connectionTimeout, int connectionRequestTimeout) {
    // Apache must not follow redirects when calling the store front. The 302 response of a storefront call
    // might contain cookies, which we need to be aware of to copy all of them into the source response
    // that returns to the browser. If apache follows those 302 responses subsequent calls might not
    // contain those cookies anymore and we will never be able to copy them over to the original response
    // from the CAE to the clients browser.
    HttpClientBuilder clientBuilder = HttpClientBuilder.create().disableRedirectHandling().useSystemProperties();
    clientBuilder.setConnectionManager(trustAllSslCertificates ? createTrustAllConnectionMgr(connectionPoolSize) :
            createDefaultConnectionMgr(connectionPoolSize));

    RequestConfig.Builder builder = RequestConfig.custom()
            .setSocketTimeout(socketTimeout)
            .setConnectTimeout(connectionTimeout)
            .setConnectionRequestTimeout(connectionRequestTimeout)
            .setStaleConnectionCheckEnabled(true);
    if (!acceptCookies) {
      builder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
    }
    clientBuilder.setDefaultRequestConfig(builder.build());

    return clientBuilder.build();
  }

  private static PoolingHttpClientConnectionManager createTrustAllConnectionMgr(int connectionPoolSize) {


    SSLContextBuilder builder = SSLContexts.custom();
    try {
      builder.loadTrustMaterial(null, new TrustStrategy() {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
          return true;
        }
      });
    } catch (NoSuchAlgorithmException | KeyStoreException e) {
      throw new CommerceException(e);
    }

    SSLContext sslContext;
    try {
      sslContext = builder.build();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new CommerceException(e);
    }
    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
            sslContext, new X509HostnameVerifier() {
      @Override
      public void verify(String host, SSLSocket ssl)
              throws IOException {
      }

      @Override
      public void verify(String host, X509Certificate cert)
              throws SSLException {
      }

      @Override
      public void verify(String host, String[] cns,
                         String[] subjectAlts) throws SSLException {
      }

      @Override
      public boolean verify(String s, SSLSession sslSession) {
        return true;
      }
    });

    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
            .<ConnectionSocketFactory>create().register("https", sslsf)
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .build();

    PoolingHttpClientConnectionManager trustAllConnectionPoolMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
    trustAllConnectionPoolMgr.setMaxTotal(connectionPoolSize);
    trustAllConnectionPoolMgr.setDefaultMaxPerRoute(connectionPoolSize);

    return trustAllConnectionPoolMgr;
  }

  private static PoolingHttpClientConnectionManager createDefaultConnectionMgr(int connectionPoolSize) {
    PoolingHttpClientConnectionManager defaultConnectionPoolMgr = new PoolingHttpClientConnectionManager();
    defaultConnectionPoolMgr.setMaxTotal(connectionPoolSize);
    defaultConnectionPoolMgr.setDefaultMaxPerRoute(connectionPoolSize);
    return defaultConnectionPoolMgr;
  }

}