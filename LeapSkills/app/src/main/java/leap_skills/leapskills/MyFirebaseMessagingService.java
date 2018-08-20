package leap_skills.leapskills;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String message_body, message_title, click_action;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("FROM", remoteMessage.getFrom());

        if(remoteMessage.getData().size()>0){
            Log.e("Message Data", remoteMessage.getData().toString());
            Map<String, String> params = remoteMessage.getData();
            JSONObject object = new JSONObject(params);
            Log.e("JSON_OBJECT", object.toString());
            try {
                 message_body = object.getString("body");
                 Log.e("BODY", message_body);
                 message_title = object.getString("title");
                Log.e("TITLE", message_title);
                 click_action = object.getString("click_action");
                Log.e("CLICK", click_action);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            sendNotification(message_body, message_title, click_action);

        }



        /*if(remoteMessage.getNotification()!=null){
            Log.e("Message Body", remoteMessage.getNotification().getBody());
            String click_action = remoteMessage.getNotification().getClickAction();
            Log.e("Click", click_action);
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(), click_action);
        }*/
    }

    private void sendNotification(String body, String title, String click) {
        show.title1 = title;
        show.message1 = body;
        Intent intent = null;
        if(click.equals("show")){
        intent = new Intent(this, show.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else {
            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logof)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(notificationSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0/*ID of Notification*/, notifiBuilder.build());
    }
}
