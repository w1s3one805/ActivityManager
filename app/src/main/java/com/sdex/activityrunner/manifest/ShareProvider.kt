package com.sdex.activityrunner.manifest

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ShareCompat
import android.support.v4.content.FileProvider
import android.widget.Toast
import com.sdex.activityrunner.BuildConfig
import java.io.File

class ShareProvider {

  private val authority = BuildConfig.APPLICATION_ID + ".fileprovider"

  fun share(activity: Activity, packageName: String) {
    val pathResolver = ManifestPathResolver()
    val path = pathResolver.getPath(activity, packageName)
    val file = File(path)
    val uri = FileProvider.getUriForFile(activity, authority, file)

    val intent = ShareCompat.IntentBuilder.from(activity)
      .setType("text/xml")
      .setStream(uri)
      .setChooserTitle("Share AndroidManifest.xml file")
      .createChooserIntent()
      .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    } else {
      val resInfoList = activity.packageManager.queryIntentActivities(intent,
        PackageManager.MATCH_DEFAULT_ONLY)
      for (resolveInfo in resInfoList) {
        activity.grantUriPermission(resolveInfo.activityInfo.packageName, uri,
          Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
    }

    try {
      activity.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
      Toast.makeText(activity, "Failed to share file", Toast.LENGTH_SHORT).show()
    }
  }
}