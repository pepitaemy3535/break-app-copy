package com.example.breakapp.main.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.breakapp.MainActivity
import com.example.breakapp.R

object OrderNotificationHelper {

    private const val CHANNEL_ID = "break_pedidos_channel"
    private const val CHANNEL_NAME = "Estado de Pedidos"
    private const val CHANNEL_DESCRIPTION = "Avisos sobre la preparación, disponibilidad y entrega de pedidos"

    const val NOTIF_READY_ID = 1001
    const val NOTIF_CONFIRM_ID = 1002

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            manager?.createNotificationChannel(channel)
        }
    }

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    /**
     * Muestra la notificación de que el producto/pedido ya está listo para recoger
     * tras cumplirse el tiempo estimado de preparación.
     */
    fun showOrderReadyNotification(
        context: Context,
        orderId: String,
        restaurantName: String,
        pickupPoint: String
    ) {
        createNotificationChannel(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_ORDER_TRACKING", orderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_READY_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.main_img_logo)
            .setContentTitle("¡Tu pedido está listo! 🎉")
            .setContentText("Pedido $orderId de $restaurantName listo. Recógelo en: $pickupPoint")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¡Buenas noticias! Tu pedido $orderId de $restaurantName ya fue preparado y está esperando por ti. Punto de recogida: $pickupPoint.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_READY_ID, builder.build())
        } catch (_: SecurityException) {
            // Manejo de seguridad en caso de permisos no concedidos en runtime
        }
    }

    /**
     * Muestra la notificación solicitando al usuario que confirme si ya tiene el pedido en sus manos.
     */
    fun showDeliveryConfirmationNotification(
        context: Context,
        orderId: String,
        restaurantName: String
    ) {
        createNotificationChannel(context)
        if (!hasNotificationPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_ORDER_CONFIRMATION", orderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIF_CONFIRM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.main_img_logo)
            .setContentTitle("¿Ya recibiste tu pedido? 📦")
            .setContentText("Confirma en Break que ya tienes tu pedido $orderId de $restaurantName")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("¿Ya tienes tu comida en mano? Por favor abre la app y confirma la entrega de tu pedido $orderId de $restaurantName para darlo por finalizado.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        try {
            NotificationManagerCompat.from(context).notify(NOTIF_CONFIRM_ID, builder.build())
        } catch (_: SecurityException) {
            // Manejo de seguridad
        }
    }
}
