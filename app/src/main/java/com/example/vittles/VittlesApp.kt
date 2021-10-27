package com.example.vittles

import android.Manifest
import androidx.core.app.NotificationManagerCompat
import com.example.domain.settings.model.NotificationSchedule
import com.example.vittles.di.AppModule
import com.example.vittles.di.DaggerAppComponent
import com.example.vittles.services.notification.NotificationService
import com.example.vittles.services.notification.NotificationScheduleService
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import net.danlew.android.joda.JodaTimeAndroid
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

/**
 * Startup class.
 *
 */
class VittlesApp : DaggerApplication() {
    /** {@link AndroidInjector}*/
    override fun applicationInjector(): AndroidInjector<VittlesApp> {
        val appComponent = DaggerAppComponent.builder()
            .applicationBind(this)
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)

        return appComponent
    }

    /** {@inheritDoc}*/
    override fun onCreate() {
        super.onCreate()

        // Initialize Joda-Time
        JodaTimeAndroid.init(this)

        // Setup notification service
        NotificationService.createNotificationChannel(this@VittlesApp, NotificationManagerCompat.IMPORTANCE_DEFAULT, false,
            getString(R.string.app_name), "App notification channel.")

        // Setup notification scheduler
        NotificationScheduleService.scheduleNotificationAudit(applicationContext, NotificationSchedule.DAILY, true)

        // Necessary for creating a connection with the OFF API
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }

    companion object PermissionProperties {
        /**
        This is an arbitrary number we are using to keep track of the permission
        request. Where an app has multiple context for requesting permission,
        this can help differentiate the different contexts.
         */
        const val REQUEST_CODE_PERMISSIONS = 10

        /** This is an array of all the permission specified in the manifest. */
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}