/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ngo.friendship.syncapp.config;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class TrustAllCertificates implements X509TrustManager {
    public void checkClientTrusted(X509Certificate[] chain, String authType) {}

    public void checkServerTrusted(X509Certificate[] chain, String authType) {}

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}

