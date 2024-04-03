package com.app.movieviewr.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.app.movieviewr.R
import com.app.movieviewr.R.drawable
import com.app.movieviewr.ui.dialog.LoadingDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

open class BaseActivity: AppCompatActivity() {
    lateinit var activity: BaseActivity
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        loadingDialog = LoadingDialog(activity)

    }

    fun showSnackbar(view: View?, message: String?, isError: Boolean) {
        snackbar = Snackbar.make(view!!, message!!, Snackbar.LENGTH_SHORT)
            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
        snackbar.getView().background = ContextCompat.getDrawable(this, drawable.snackbar_background)
        snackbar.setBackgroundTint(resources.getColor(if (isError) R.color.md_red_300 else R.color.md_green_A400))
        snackbar.show()
    }

    fun showLoadingDialog() {
        loadingDialog.show()
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoadingDialog()
    }
}