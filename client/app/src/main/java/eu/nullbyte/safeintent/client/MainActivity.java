package eu.nullbyte.safeintent.client;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


public class MainActivity extends Activity {
    private final String PACKAGE_NAME = "eu.nullbyte.safeintent.host";
    private final String SPKI_HASH = "d51e0c6452974adba474ddb729157ee2b1eaacbcafc2bf862bd3064f6b2cc188";
    private TextView mLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLog = (TextView) findViewById(R.id.txt_log);
        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent();
            }
        });
    }

    private void sendIntent() {
        final PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo = null;
        log("Checking if package", PACKAGE_NAME, "is installed...");
        try {
            packageInfo = packageManager.getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            log("The package", PACKAGE_NAME, "is not installed.");
        }

        if (packageInfo != null) {
            log(String.format("Found '%s'", packageInfo.applicationInfo.loadLabel(packageManager).toString()));
            log("Version:", String.format("%s (%d)", packageInfo.versionName, packageInfo.versionCode));
            CertificateFactory factory = null;
            try {
                factory = CertificateFactory.getInstance("X509");
            } catch (CertificateException e) {
                log("Error loading CertificateFactory.");
            }
            if (factory != null) {
                log("Certificates:");
                for (Signature s : packageInfo.signatures) {
                    try {
                        X509Certificate cert = (X509Certificate) factory.generateCertificate(new ByteArrayInputStream(s.toByteArray()));
                        log("Subject: ", cert.getSubjectDN().toString());
                        log("Issuer: ", cert.getIssuerDN().toString());
                        MessageDigest md = null;
                        try {
                            md = MessageDigest.getInstance("SHA-256");
                            String hash = new String(Hex.encodeHex(md.digest(cert.getPublicKey().getEncoded())));
                            log("Public key hash:", hash);
                            if (SPKI_HASH.equalsIgnoreCase(hash)) {
                                log("Signature matches!");
                                Intent intent = new Intent();
                                String username = "admin";
                                String password = "passlord";
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                intent.setClassName(PACKAGE_NAME, PACKAGE_NAME + ".LoginActivity");
                                log("Sending intent with extras ", String.format("{username: '%s', password: '%s'}", username, password));
                                startActivity(intent);
                            } else {
                                log("Signature does not match!");
                            }
                        } catch (NoSuchAlgorithmException e) {
                            log("Unable to hash public key, SHA-256 not available.");
                        }
                    } catch (CertificateException e) {
                        log("Unable to load certificate for signature:", s.toString());
                    }
                }
            }
        }
        log("\n\n\n");
    }

    private void log(String ... s) {
        mLog.setText(mLog.getText() + TextUtils.join(" ", s) + "\n");
    }

}
