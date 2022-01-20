package lou.sizzo.kotlininapppurchasetesting.extensions

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String, lenght: Int = Toast.LENGTH_LONG){
    Toast.makeText(this, message, lenght).show()
}

fun View.snackbar(message: String, lenght: Int = Snackbar.LENGTH_LONG){
    Snackbar.make(this, message, lenght).show()
}